# SPDD Analysis: Quality Check Name/Code Conflict on Upload

## Original Business Requirement

When the name field of a quality object is modified in a descriptor without updating its corresponding displayName, Blindata interprets the Quality Check as a new entity during upload.

This happens because the generated code (derived from the domain, DP name, and field name) changes, causing the upload process to create a new Quality Check instead of updating the existing one. The operation then fails because two Quality Checks cannot exist with the same name.

Problem
Current behavior:

A user changes the quality.name field in a descriptor but the displayName is left unchanged.
During upload, the generated quality check code changes.
Blindata treats the Quality Check as a new resource.
A conflict occurs because a Quality Check with the same name already exists.

Solution:
For each quality check, verify that it does not exist a quality check on Blindata with the same name (in the same suite) but with different code. The error should be clearly understandable, addressing the two quality checks that generate the problem.

## Domain Concept Identification

### Domain Concept Identification

#### Existing Concepts (from codebase)

- **Quality object (descriptor)**: ODCS / Datastore API quality annotation on an entity or field (`name`, optional `id`, optional `customProperties.displayName`). Source of identity inputs for Blindata mapping.
- **Quality Check (observer internal + Blindata KQI)**: Upload payload entity with `code` (technical identity used by Blindata import upsert) and `name` (display name; unique per Quality Suite on Blindata). Mapped from the quality object in `DataStoreApiStandardDefinitionVisitorImpl`.
- **Quality Suite**: Per–data-product container; `code` = `{domain} - {dataProductName}`; `name` uses data product displayName when set. All extracted checks for a product land in this suite.
- **Quality Check code composition**: Short segment = `quality.id` when present, else `quality.name`; then prefixed as `{suiteCode} - {shortSegment}` before upload (`QualityUpload.addQualitySuiteCodeToQualityChecksCode`).
- **Quality Check display name**: Priority `customProperties.displayName` > `quality.name` > `quality.id` (`qualityCheckDisplayName`). This becomes Blindata `QualityCheck.name`.
- **QUALITY_UPLOAD use case**: Extracts checks from ports, validates locally (warnings only for missing fields/thresholds), builds suite, then calls Blindata `import-objects`. Upsert on Blindata is **by code within suite**; create fails if another check already has the same **name in the suite**.
- **Blindata name uniqueness (suite-scoped)**: Blindata enforces at most one Quality Check with a given `name` inside a given suite; code is also unique. This is the constraint that surfaces as a conflict today.
- **BdQualityClient / search option DTOs**: Client currently only uploads; `QualityCheckSearchOptions` and `QualitySuitesSearchOptions` already exist in the observer but are unused—no pre-upload lookup of existing Blindata checks/suites.

#### New Concepts Required

- **Name/code identity conflict**: Situation where a descriptor-derived Quality Check targets a Blindata suite that already has a check with the **same name** but a **different code**—typically after renaming `quality.name` without updating `customProperties.displayName` (and without a stable `quality.id`).
- **Pre-upload conflict guard**: Explicit verification step that detects the name/code mismatch and emits a clear warning identifying **both** the incoming check and the existing Blindata check—consumed by the observer validator.
- **Blindata Quality Suite/Check lookup capability** (conceptual): Ability for the observer to resolve the suite and inspect existing checks by name/code within that suite—search DTOs exist; outbound port/client methods to use them do not yet.

#### Key Business Rules

- **Upsert identity is code, not name**: Blindata import matches existing checks by `code` (+ suite). A code change is treated as a new check.
- **Display name uniqueness within suite**: Two checks in the same suite cannot share the same `name`.
- **When `id` is absent, `name` drives the code segment**: Renaming `quality.name` changes Blindata `code` while leaving Blindata `name` unchanged if `customProperties.displayName` is set and unchanged.
- **When `id` is present, renaming `name` alone does not change code**: Identity is stable via `id`; this conflict path is primarily for rules without `id` (or when `id` itself also changes).
- **Guard must compare within the same suite**: Cross-suite same names are out of scope for this uniqueness rule.
- **Warning must identify both parties**: Message should surface the conflicting pair (incoming code/name vs existing Blindata code/name) so users understand they renamed the technical name without aligning display identity.
- **Remediation guidance in the warning**: Suggest **both** updating `customProperties.displayName` (or the mapped display name) **or** using a stable `quality.id` so code no longer tracks `name`.
- **Failure mode is warn (validator-driven)**: Emit via `getUseCaseLogger().warn(...)` (same pattern as existing quality validations). In dry-run, `ValidatorUseCaseLogger` collects warnings and the observer validator sets `evaluationResult=false`—sufficient to block publish when the Blindata validator policy is active/blocking. Do **not** throw `UseCaseExecutionException` for this conflict.
- **Suite absent → no-op**: If the Quality Suite does not yet exist on Blindata (first-time upload), skip the guard; no collision is possible.
- **Collect all conflicts**: Detect and report **all** name/code conflicts in the upload (not fail-fast on the first); surface them together so the validator rawError lists every conflicting pair.

## Strategic Approach

### Strategic Approach

#### Solution Direction

Extend the **QUALITY_UPLOAD** flow so that, after quality checks are extracted and suite-prefixed codes are assigned, the observer **looks up the corresponding Quality Suite on Blindata** (when it already exists) and **detects any incoming check whose `name` already exists in that suite under a different `code`**. On detection, log clear warning(s) that name both the descriptor-side check and the existing Blindata check, suggest updating displayName **or** using a stable `quality.id`, and **collect all** such conflicts for the validator. If the suite does not exist yet, the guard is a no-op. Primary enforcement is through the **observer validator** (dry-run path): warnings fail policy evaluation. Leverage existing use-case layering (outbound port + `BdQualityClient`), existing search-option resources, and Blindata’s suite-scoped name uniqueness semantics—do not change Blindata upsert identity or descriptor mapping of code/name.

High-level data flow: descriptor ports → extract Quality Checks → build suite & prefix codes → **conflict guard against Blindata suite contents (collect all conflicts → warn)** → upload via `import-objects` (upload path continues; validator dry-run surfaces the warns as policy failure).

#### Key Design Decisions

- **Pre-upload guard vs. rely on Blindata conflict**: Blindata already rejects the create, but the message does not explain the rename scenario or identify both codes. → **Recommend** observer-side detection with an explicit warning before Blindata’s opaque API conflict.
- **Lookup strategy**: Resolve suite by suite `code`, then find checks in that suite whose `name` equals the incoming name and whose `code` differs. Search options support suite UUID + text search (name LIKE / code equality on Blindata); exact name match must be applied after retrieval to avoid false positives. → **Recommend** suite resolve + suite-scoped search/filter with exact name equality.
- **Failure mode**: → **Decided: warn**. A clear `warn` is sufficient because the observer validator collects use-case warnings via `ValidatorUseCaseLogger` and fails the policy evaluation when any warning is present. Hard `UseCaseExecutionException` is not required for this scenario.
- **Dry-run / validator**: → **Decided: in scope as the enforcement path**. Dry-run outbound port no-ops `uploadQuality` but must still run the conflict guard with read-only Blindata calls so validation catches the issue before publish.
- **Suite not yet on Blindata**: → **Decided: no-op**. First-time upload cannot collide; skip the guard when the suite is absent.
- **Multiple conflicts**: → **Decided: collect all**. Report every conflicting pair (together / as multiple warns collected by the validator), not stop at the first.
- **Remediation messaging**: → **Decided: suggest both** updating `customProperties.displayName` and introducing/keeping a stable `quality.id`.
- **Scope of mapping change**: Fixing by always deriving Blindata `name` from `quality.name` (ignoring displayName) or always requiring `id` would change mapping contracts and break intentional display overrides. → **Recommend** keeping mapping as-is; add detection only.
- **Where to place the guard**: Closest to upload orchestration in `QualityUpload` (after codes are final), with Blindata reads behind `QualityUploadBlindataOutboundPort` / `BdQualityClient`—matches existing ports pattern for issue campaigns/users; dry-run must not stub out the lookup methods.

#### Alternatives Considered

- **Hard-fail with `UseCaseExecutionException`**: Rejected—product decision is that a **warn** is sufficient; the observer validator turns warnings into a failed policy evaluation.
- **Auto-heal: treat same-name as same entity and overwrite code**: Rejected—changing Blindata identity by name silently could merge unrelated checks or surprise users; ticket asks for a clear message, not remapping.
- **Change Blindata import to upsert by name**: Rejected—cross-system identity change; out of observer scope; code remains the documented technical key.
- **Only improve docs / require `quality.id`**: Helpful long-term but does not stop the failing path when users rename without `id`; ticket asks for runtime verification.
- **Surface only Blindata’s existing ResourceConflictException**: Rejected—does not identify the two codes/names involved in the rename scenario.

## Risk & Gap Analysis

### Risk & Gap Analysis

#### Requirement Ambiguities

- **No numbered Acceptance Criteria**: ACs below are derived from the problem/solution text plus the clarified product decisions (warn/validator, suite no-op, collect all conflicts, dual remediation messaging).
- **Exact meaning of “displayName”**: In the descriptor, KQI display override is `customProperties.displayName`, not a top-level `displayName`. Analysis assumes that field; confirm with reporters if needed.
- ~~**Behavior when suite does not yet exist**~~ → **Decided: no-op** (no warn on first-time upload).
- ~~**Multiple conflicts in one upload**~~ → **Decided: collect all** conflicting pairs for the validator output.
- ~~**Intentional same display name, different rules**~~ → **Decided: warn is correct**; messaging suggests **both** updating displayName and using a stable `quality.id`.

#### Edge Cases

- **`quality.id` present**: Renaming `name` alone does not change code; conflict may not occur. Guard still valuable if `id` changes while display name stays the same.
- **`customProperties.displayName` absent**: Blindata `name` tracks `quality.name`; renaming both code and name together usually avoids this conflict (old check orphaned by code; new check created with new name). Cleanup/`assetsCleanup` behavior may leave orphans—out of primary scope but related.
- **Fuzzy Blindata `search`**: API matches name with LIKE; short/common names risk matching multiple checks—must exact-match on `name` after fetch.
- **Live upload without prior validator**: Warn does not abort `QUALITY_UPLOAD`; if the descriptor bypasses the validator, Blindata may still return an opaque name conflict. Residual risk accepted given validator-first enforcement.
- **References (`refName`)**: References are stripped before upload; guard should run on final non-reference checks only.
- **Same conflict across ports merged by code**: Merge already collapses same short code; name/code conflict is against Blindata state, not intra-descriptor duplicates.
- **Multiple conflicts**: All pairs are reported; validator `rawError` should list every warning so users can fix the full set in one pass.

#### Technical Risks

- **Missing client read APIs**: Observer can upload but cannot yet list/search suites or checks—must add read methods on `BdQualityClient` / outbound port. Search DTOs already mirror Blindata API. Dry-run must call these reads (not stub them).
- **Performance**: N checks × Blindata lookups; mitigate by resolving suite once and loading suite checks (or batched search) rather than per-check round-trips where possible.
- **Exact name match vs. search semantics**: Relying only on Blindata LIKE search without post-filter can false-positive or miss; design must require equality on `name` within suite.
- **Warn delivery to validator**: Message must go through `getUseCaseLogger().warn` so `ValidatorUseCaseLogger` captures it; logging only via SLF4J would not fail policy evaluation.
- **Concurrency**: Two parallel uploads could still race; residual risk remains at Blindata—acceptable if documented.

#### Acceptance Criteria Coverage

| AC# | Description                                                                                                                                                                                                   | Addressable? | Gaps/Notes                                                                 |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | -------------------------------------------------------------------------- |
| 1   | When a descriptor quality object’s `name` changes but Blindata display name (`customProperties.displayName` / mapped `name`) stays the same, the conflict is detected before Blindata’s opaque create failure | Yes          | Guard detects same-name/different-code in suite                            |
| 2   | For each quality check, verify no Blindata check exists in the same suite with the same name and a different code                                                                                             | Yes          | Requires suite + check lookup capability                                   |
| 3   | On conflict, emit clear warning(s) that identify both checks involved and suggest updating displayName **or** using a stable `quality.id`; collect **all** conflicts                                          | Yes          | Warn via use-case logger; aggregate for validator rawError                 |
| 4   | Happy path: unchanged name/code or intentional name+display rename continues to upload/update by code as today                                                                                                | Yes          | Guard must not warn on matching code+name or new names                     |
| 5   | First upload / suite absent: no false warning                                                                                                                                                                 | Yes          | **Decided:** skip guard when suite not found on Blindata                   |
| 6   | Observer validator (dry-run) surfaces the conflict and fails policy evaluation when warnings are present                                                                                                      | Yes          | Decided: warn is the failure mode; validator collects warnings → fail eval |
