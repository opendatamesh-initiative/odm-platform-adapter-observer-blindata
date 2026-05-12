# Design proposal: ODM observer — KQI creation from ODCS definitions (preserve current behavior)

This document specifies **intended behavior** for the **observer** that synchronizes **ODM / data product descriptors** (including **ODCS `quality`** rules) into **Blindata Quality Checks (KQI)**. Implementation may live in a **backend or agent service** outside **blindata-ui**; this repo captures the **contract** for cross-team alignment.

**Related documents**

- **Accepted approach:** [decision record — odcs31](./quality-checks-odcs-support.md) (`EXTERNAL` + **`additionalProperties`** for definition; integration uploads scores).
- **Descriptor vocabulary:** [specs-quality-annotation-builder.md](../dataops-builder/specs-quality-annotation-builder.md) (sections 2–3).
- **KQI UI:** [design-kqi-detail-contract-metadata-ui.md](./design-kqi-detail-contract-metadata-ui.md) — must consume the **`_contract.*`** keys defined in section 6.

---

## 1. Problem statement

When a data product descriptor contains **`quality`** annotations, the platform should **materialize** corresponding **Quality Suite / Quality Check** rows so the catalog and KQI UI stay aligned with the contract. That process must:

1. Apply **odcs31** mapping for contract-sourced rules: **`scoreStrategy: EXTERNAL`**, **definition** in **`additionalProperties`** using **`_contract.*`** keys (section 6), **no** reliance on built-in Blindata scoring for the initial catalog row.
2. **Preserve current behavior** for all existing flows: checks **not** created from this path (manual, older ingestion, non-contract suites) must **not** change semantics or field population rules unless explicitly versioned.

---

## 2. Scope and definitions

| Term | Meaning |
|------|---------|
| **Observer** | Component that reacts to ODM/descriptor publish or sync events and upserts catalog objects (exact deployment: worker, agent job, or synchronous API — product decision). |
| **Current behavior** | Today’s rules for creating KQI from descriptors where **legacy-style** mapping applied (e.g. `name`, `description`, `customProperties.scoreStrategy`, thresholds, `mustBe*` mapping into interval fields) — **must remain** for **checks that are not migrated** to odcs31. |
| **ODCS rule** | One object in a `quality` array on a table or column in the descriptor (see companion spec section 2). |

### 2.1 Limitations of this design package

| Area | Gap / risk |
|------|------------|
| **Bitol ODCS 3.1 field-level schema** | Rule JSON is not fully specified in this file; the **Bitol ODCS 3.1** quality annotation model must be mapped explicitly to **`_contract.*`** (section 6) in a separate matrix (operator, `metric`, `arguments`, bounds, `query`, `implementation`, etc.). |
| **Companion docs** | Vocabulary and UI contracts live in linked specs; if those drift, observer output and UI may diverge. |
| **Blindata API** | odcs31 assumes the upload API accepts **`scoreStrategy: EXTERNAL`** and keys in **`additionalProperties`** with the **`_contract.`** prefix; confirm with Blindata platform before rollout. |
| **Retrocompat** | Section 5 mandates process and sign-off; **legacy** behavior is already **fixture-backed** in this repo (**section 2.2**). Add **odcs31** fixtures and before/after diffs where behavior changes. |
| **Code vs design drift** | Section **3.5** tracks alignment of this document with the **reference implementation** in **`odm-platform-adapter-observer-blindata`** and lists known gaps. |

### 2.2 Reference implementation (this repository)

The **`odm-platform-adapter-observer-blindata`** service implements the **Datastore API** observer path: **`QualityUpload`** use case, **`DataProductPortAssetAnalyzer`**, **`PortDatastoreApiEntitiesExtractor`** (`datastoreapi` / `1.*.*`), and **`DataStoreApiStandardDefinitionVisitorImpl`** (maps descriptor **`quality`** entries to internal **`QualityCheck`**). **`docs/mapping.md`** documents the public quality mapping for stakeholders, including both **legacy** and **odcs31** paths; section 4 below is the **normative baseline** for retrocompat work.

**Legacy baseline in automated tests (authoritative for “as-is”):** Legacy descriptors and expected **`QualityCheck`** outcomes are **pinned** by existing tests — no need to duplicate that inventory in prose before implementation.

| Area | Test / resources (under repo `src/test/`) |
|------|-------------------------------------------|
| **Extractor** — mapping, **`scoreStrategy`**, thresholds, **`refName`**, issue policies | Java: **`PortDataStoreApiQualityExtractorTest`**. JSON inputs and golden expectations: **`.../schema_analyzers/datastoreapi/v1/testDataStoreApiV1QualityExtractor_*.json`** (e.g. raw port definitions, **`_expected_quality_checks.json`**, **`_with_ref`**, issue policies). |
| **`QualityUpload` use case** — suite code, prefixed check codes, campaign, issue policy wiring | Java: **`QualityUploadTest`**. Fixtures: **`.../services/usecases/quality_upload/quality_upload_data_product_version.json`**, **`quality_upload_expected_results.json`**. |
| **Validator** — invalid / incomplete quality objects | Java: **`BlindataValidatorControllerIT`**. JSON: **`.../validator/controllers/invalid_data_product_missing_quality_*.json`**. |

Section **5** retrocompat **sign-off** still requires **explicit** before/after where the **new** observer differs (e.g. **odcs31** rules, **`_contract.*`**), but the **legacy** catalog is **already** these fixtures and must **stay green** under the discriminant (**`customProperties.scoreStrategy` present** → legacy path).

---

## 3. As-is analysis (gaps vs. Bitol ODCS 3.1 + odcs31)

### 3.1 Parser and domain model

- Deserialization uses the **`Quality`** / **`QualityCustomProperty`** Java model. This branch extends that model with ODCS 3.1 fields needed by the mapping: **`id`**, **`metric`**, **`arguments`**, **`query`**, **`implementation`**, **`mustNotBe`**, **`mustBeGreaterThan`**, **`mustBeLessThan`**, **`mustBeBetween`**, and **`mustNotBeBetween`**.
- Structured ODCS fields that must be retained for the UI are stored as `JsonNode` where needed and serialized into **`_contract.*`** additional properties by **`ContractAdditionalPropertiesBuilder`**.
- The pipeline still uses **`DataStoreApiParser`** plus **`DataStoreApiBlindataDefinitionConverter`** to materialize **`DataStoreApiBlindataDefinition`**; unmapped JSON fields outside the modeled quality vocabulary can still be dropped by deserialization.

### 3.2 Mapping today (legacy / legacy-style)

- **Legacy mapping:** Descriptor `dimension`, `unit`, `type`, and `engine` map to legacy additional property names such as **`dimension`**, **`unit`**, **`constraint_type`**, and **`quality_engine`**. Legacy intervals (`mustBeGreaterOrEqualTo`, `mustBeLessOrEqualTo`, `mustBe`) populate **`scoreLeftValue`**, **`scoreRightValue`**, and **`scoreExpectedValue`**. **Strategy** comes from **`customProperties.scoreStrategy`**.
- **odcs31 mapping:** Rules without **`customProperties.scoreStrategy`** emit **`scoreStrategy: EXTERNAL`** and **`_contract.*`** keys. **`implementation`** is preserved as **`_contract.implementation`**, **`query`** as **`_contract.query`**, **`metric`** as **`_contract.metric`**, **`arguments`** as **`_contract.arguments`**, and ODCS comparison keys are converted to **`_contract.operator`** / **`_contract.bounds`**.
- **Thresholds:** Both paths apply descriptor threshold overrides when present and default missing warning/success thresholds to **100 / 100** so upload validation passes.

### 3.3 Validation and strategy enum (blockers for odcs31 only)

- The observer now routes **full** rules with omitted **`customProperties.scoreStrategy`** to **odcs31**, setting **`scoreStrategy: EXTERNAL`**. Full rules with **`customProperties.scoreStrategy`** present stay on the **legacy** path.
- **`QualityUpload`** validates that **warning and success thresholds** are set (and within 0–100) for extracted checks; it applies **strategy-specific** interval rules for built-in strategies and skips those interval requirements for **`EXTERNAL`**.
- The internal **`BDQualityStrategyRes`** enum includes **`EXTERNAL`**.

### 3.4 Stable identifiers and suite

- **Quality suite:** `code` = `{domain} - {dataProductName}`; **name** uses **displayName** when set.
- **Legacy and odcs31 paths:** when **`quality[].id`** is **present**, it is used as the short segment for the Quality Check **`code`** (after the usual **`{suiteCode} - `** prefix). When **`id`** is absent, **`quality[].name`** is used. **`customProperties.displayName`** overrides the **KQI display name** only, not the **`code`** segment.
- **References:** `customProperties.refName` creates a **reference** check merged by **`code`** with the main rule’s physical links.

**Implication:** Full **Bitol ODCS 3.1** support needs a **deliberate dual path**: **legacy mapping** unchanged for non-ODCS / non-migrated descriptors, and **odcs31 mapping** for classified ODCS rules, with validation and enums updated accordingly.

### 3.5 Code alignment and implementation gaps (`odm-platform-adapter-observer-blindata`)

This subsection records how the **current codebase** aligns with the analysis above, and **gaps** to close in design, tests, or implementation. It should be updated when the adapter changes.

#### What matches the design intent

| Topic | Implementation |
|-------|----------------|
| **Single quality extractor** | Only **`PortDatastoreApiEntitiesExtractor`** implements **`PortStandardDefinitionQualityExtractor`** — **Datastore API** spec **`1.*.*`** only. |
| **Dual quality mapping** | **`DataStoreApiStandardDefinitionVisitorImpl.qualityToQualityCheck`** routes **`refName`** stubs first, then **legacy** when **`customProperties.scoreStrategy`** is present, otherwise **odcs31**. |
| **Suite and check code** | **`QualityUpload.buildQualitySuite`** / **`addQualitySuiteCodeToQualityChecksCode`**. |
| **odcs31 score strategy** | **`DataStoreApiStandardDefinitionVisitorImpl.odcs31QualityToQualityCheck`** sets **`scoreStrategy: EXTERNAL`**. |
| **Validation** | **`QualityUpload.validateQualityCheckStrategyParameters`** skips built-in interval rules for **`EXTERNAL`**; thresholds validated separately. |
| **`EXTERNAL` strategy** | **`BDQualityStrategyRes`** includes **`EXTERNAL`**. |
| **Blindata upload** | **`BdClientImpl.uploadQuality`** → **`POST .../data-quality/suites/*/import-objects`** with optional **`?cleanup=true`** (see gaps below). |
| **`additionalProperties` shape** | **`BDAdditionalPropertiesRes`** is **`name` + `value` (string)**; **`_contract.*`** values must be serialized (JSON string for **`_contract.implementation`**). |

#### Gaps and follow-ups

1. **Port scope vs narrative** — **`QualityUpload.extractQualityFromPorts`** scans **input, output, control, discovery, observability** ports (not output-only). Any high-level algorithm that mentions **only output ports** should match this behavior or the code should be narrowed by product decision (section 9 updated accordingly).

2. **Parse failures = silent empty quality** — **`PortDatastoreApiEntitiesExtractor.extractQualityChecks`** returns an **empty list** on **`MismatchedInputException`** / **`IOException`** (warnings `[#115]` / `[#116]`). The use case does **not** fail; **quality rules can be omitted** without blocking upload. Retrocompat tests and runbooks should cover this; odcs31 should not rely on “fail loud” without changing this behavior.

3. **Validator dry-run does not hit Blindata** — **`BlindataValidatorService`** runs **`QualityUpload` dry-run** with **`QualityUploadBlindataOutboundPortDryRunImpl`**, which **does not** call the real upload API. The claim that **Blindata rejects** legacy checks without **`scoreStrategy`** is **not** enforced by in-repo tests; keep integration evidence in the retrocompat pack.

4. **Merge ordering (`refName` + odcs31)** — **`mergeQualityChecksWithSameCode`** uses a **`HashMap`**; merge **order** affects which **`QualityCheck`** instance wins when combining **main** and **reference** rows for the same **`code`**. For stable **legacy vs odcs31** classification after merge, require **deterministic ordering** (e.g. process **non-reference** definitions before **reference** stubs, or **`LinkedHashMap`**, or resolve **`refName`** before applying **`scoreStrategy`** routing). Document in implementation and tests (**section 8.6**).

5. **`isCheckEnabled` default** — Both mapping paths default **`isEnabled`** to **`true`** when **`customProperties.isCheckEnabled`** is omitted.

6. **Import `cleanup` query parameter** — Upload may pass **`cleanup=true`**, which can remove catalog objects not present in the import. Relate product decisions on **deletion vs disable** (**section 11.2**) to this API behavior and tenant expectations.

7. **`docs/mapping.md` vs code** — If **`mapping.md`** describes quality for **other** port specifications, only **Datastore API** quality extraction is implemented here; scope statements should stay consistent.

8. **Cross-port duplicate `code`** — Merge runs **across all ports**; the same short **`code`** from two ports could merge **physical** links — document edge cases if product allows duplicate names across ports.

9. **MapStruct** — **`QualityCheckMapper`** maps **`QualityCheck` → `BDQualityCheckRes`** with minimal overrides; **`additionalProperties`** flow through if set on the internal model — odcs31 must **populate** them before mapping.

---

## 4. Current mapping behavior (baseline for retrocompat)

This subsection is the **inventory input** for section 5 (retrocompat analysis). It describes behavior implemented by the **reference adapter** for **Datastore API v1** ports.

| Stage | Behavior |
|-------|----------|
| **Port selection** | **All** interface port kinds (**input**, **output**, **control**, **discovery**, **observability**) are scanned for definitions that expose a **standard definition** the extractor supports. |
| **Extractor selection** | First registered **`PortStandardDefinitionQualityExtractor`** whose **`supports(specification, version)`** matches (e.g. **`datastoreapi`** / **`1.*.*`**). |
| **Parsing** | Port API JSON → **`StandardDefinition`** → **`DataStoreApi`** via **`DataStoreApiParser`** and **`DataStoreApiBlindataDefinitionConverter`** (table/column definitions deserialized into **`DataStoreApiBlindataDefinition`** / properties). |
| **Traversal** | For each table: build **physical entity**; for each column, **physical field**; for each **`quality[]`** item on **table** or **column**, build one **`QualityCheck`** and attach **physical entity** and/or **physical field** references. |
| **Rule validity** | A non-reference rule must have a **`name`**; otherwise the rule is skipped with a warning. |
| **Reference rules** | **`customProperties.refName`** only (column/table pointing at another rule) → **`code`** = ref target, **`isReference`** = true; **no** `scoreStrategy` on the stub; merged with the main rule (section 4). References without a matching main definition are removed before upload. |
| **`implementation`** | **Not** mapped to **`QualityCheck`** in legacy path (dropped at parse); see section 3.2. |
| **Merge** | Checks with the same **`code`** (before suite prefix) are **merged**: physical entities and fields are unioned and deduplicated. **Ordering** can affect which object instance is retained when merging **main** and **`refName`** rows — see section **3.5**. |
| **Suite prefix** | Immediately before upload, each check’s **`code`** is prefixed with **`{suiteCode} - `**. |
| **Issue policies** | Parsed from **`customProperties.issuePolicies`** per **`docs/mapping.md`**; campaign **`Quality - {domain} - {dataProductName}`**. |

**Legacy vs. odcs31 (target):** For **legacy** rules, **thresholds** and **strategy-dependent intervals** remain as today. For **odcs31**, **`scoreStrategy`** = **`EXTERNAL`**, **interval fields** cleared, **`_contract.*`** populated from ODCS, **thresholds** per section 7.1 (defaults or overrides) — **without** rewriting existing **legacy** rows unless a migration or descriptor classification explicitly opts them in. **Code and display name** rules for odcs31 are in section 7.1 and section 3.4.

---

## 5. Retrocompatibility analysis (mandatory before implementation)

**Gate:** No observer release that changes ingestion behavior may ship until a written **retrocompatibility analysis** is completed and reviewed. This is **not** optional.

**Legacy side:** Expected **legacy** behavior for the **Datastore API** path is **already encoded** in automated tests and JSON fixtures (**section 2.2**). The analysis should **reference** those tests as the baseline and require they **remain passing** for legacy-classified rules.

The analysis must at minimum:

1. **Inventory** all existing code paths that create or update **Quality Checks** from descriptors (ODM publish, import, re-sync, tenant migrations).
2. **Catalog** current field population per path: `scoreStrategy`, **`refName`** / reference merge, interval fields, `customProperties` / descriptor `customProperties`, physical links, suite naming, **`implementation`** handling (dropped today vs **`_contract.implementation`** in odcs31) — use **section 4** as the baseline for the **Datastore API** path; cross-check **section 3.5** for **code-level** caveats (parse failures, validator dry-run, merge ordering, **`cleanup`**).
3. **Define** expected **before/after** for representative descriptors (fixtures): same descriptor processed by **current** vs **new** observer — document **diffs** for each category (new tenant, legacy tenant, mixed suite).
4. **Identify** breaking scenarios (e.g. changed **`code`**, cleared intervals for checks users relied on, changed semaphore semantics) and **mitigations** grounded in **descriptor-level** controls (**section 5.1** discriminants, migrations, versioned rollout).
5. **Test plan:** regression suite name, environments, **documented rollback** (e.g. redeploy prior observer version, descriptor revert — **section 10**).

**Outcome:** A signed-off doc or ticket linking **migration steps**, **compat matrix**, and **go/no-go** criteria.

### 5.1 Retrocompatibility strategy

**Goal:** Prefer **deterministic, data-driven classification** using **`customProperties.scoreStrategy`**, **`refName`** resolution, and optional **markers** (**section 5.1**). Rollback is **operational** (redeploy, descriptor revert — **section 10**).

#### Reference rules (`refName`) — evaluate before `scoreStrategy`

**Reference-only** `quality` objects (e.g. column-level stubs with **only** **`customProperties.refName`**, no **`name`**, no **`scoreStrategy`**) are **valid** legacy constructs: they attach a **physical field** to a **main** rule identified by **`refName`**. Test fixtures show the main rule carries **`scoreStrategy`** while the stub does **not**.

**Do not** classify a reference stub as **odcs31** **only** because **`scoreStrategy`** is absent on the stub. Apply **legacy** reference semantics (section 4): merge by **`code`** with the **main** definition; the **effective** check inherits the **main rule’s** mapping. After merge, the **uploaded** check reflects **legacy** if the **resolved** rule uses **`customProperties.scoreStrategy`** (typical).

**Classification mechanics:** Either (a) **always** route **reference-only** rows through the **legacy** reference pipeline before odcs31, or (b) resolve **`refName`** and apply the **same** classification as the **target** rule’s **`scoreStrategy`** presence/absence.

#### Primary discriminant: `customProperties.scoreStrategy` (full rule, or resolved target)

Operational evidence: **Blindata quality upload fails** when a check is produced **without** a **`scoreStrategy`** on the **legacy** mapping path. Therefore, descriptors that **successfully** use the current observer + Blindata stack **in production** effectively **always** set **`customProperties.scoreStrategy`** on **full** quality rules that are meant to stay **legacy**.

Use that signal as the **main router** for **non-reference** rules (and for **resolved** reference targets):

| `customProperties.scoreStrategy` (on the rule under classification, or on the **`refName` target** after resolve) | Path |
|-------------------------------------------------------------------------------------------------------------------|------|
| **Absent** or **null** | **odcs31** — **new mapping**: **`scoreStrategy: EXTERNAL`**, **`_contract.*`**, code/name per section 7.1. |
| **Present** (e.g. `PERCENTAGE`, `MINIMUM`, …) | **Legacy** — **unchanged** legacy-style mapping (section 4). |

Evaluate each **`quality[]`** item in order:

1. **Reference stub:** If the item is **reference-only** (**`customProperties.refName`** and not a full inline rule per section 4) → **legacy** reference handling / merge; **do not** apply odcs31 to the stub in isolation.

2. **Primary routing (for full rules or post-merge intent):**
   - If **`customProperties.scoreStrategy`** is **present** → **legacy** mapper.
   - If **`customProperties.scoreStrategy`** is **absent** / **null** → **odcs31** mapper (**EXTERNAL**, **`_contract.*`**, section 7.1), including **full** serialization of **`implementation`** into **`_contract.implementation`** (section 3.2, section 7.1).

3. **Optional refinement:** If product needs a rule to stay **legacy** but **without** `scoreStrategy`, use an explicit **marker** in **`customProperties`** (retrocompat matrix) to force **legacy**, or require a minimal **`scoreStrategy`** for legacy semantics.

**Note:** **`quality.id`** drives **stable `code`** / display **name** under **odcs31** (section 7.1, section 3.4), not legacy vs odcs31 routing once **`scoreStrategy`** + **`refName`** rules apply.

| Rule signal | Typical classification |
|-------------|-------------------------|
| **Reference-only** (`refName` stub) | **Legacy** pipeline / inherit target (section 5.1 above); **not** odcs31 on stub alone |
| `customProperties.scoreStrategy` **present** | **Legacy** |
| `customProperties.scoreStrategy` **absent / null** (full rule) | **odcs31** |

**Mixed suites:** A single Quality Suite may contain **both** legacy-classified and odcs31–classified rules (section 7.4). Tests must cover **side-by-side** rules on the same table, **and** **main rule + `refName` column references** (fixtures: e.g. **`testDataStoreApiV1QualityExtractor_*_with_ref`**).

#### Discriminant-first

Routing is driven by **descriptor-visible** signals (**`scoreStrategy`**, **`refName`**, optional **markers**), not by **operator toggles**. That supports **reproducible** before/after diffs (gate item 3) and **version-controlled** rollout.

#### Residual risk and mitigations

- **Teams that need legacy mapping but omit `scoreStrategy`:** they will get **odcs31** on **full** rules — **mitigation:** keep **`customProperties.scoreStrategy`** on **legacy** rules until migrated; or use the **optional marker** in step 3.
- **Reference stubs without `scoreStrategy`:** mitigated by **§5.1** — **not** classified as odcs31 on the stub alone; merge with **main** rule.
- **Descriptors that never uploaded successfully without `scoreStrategy`:** may **start working** under odcs31 with **`EXTERNAL`** — validate in staging.
- **Changed `code` when `id` is introduced:** document in migration (section 3.4, section 7.1).

---

## 6. Canonical `additionalProperties` keys (observer output)

Use **dotted** keys under the **`_contract.`** prefix (leading underscore; same key **suffixes** the **UI** should consume — align [design-kqi-detail-contract-metadata-ui.md](./design-kqi-detail-contract-metadata-ui.md)):

ODCS vocabulary reference: <https://bitol-io.github.io/open-data-contract-standard/v3.1.0/data-quality/>.

| Key | Content |
|-----|---------|
| `_contract.operator` | Operator id (e.g. `mustBeBetween`). |
| `_contract.metric` | Library `metric` when `type` is `library`. |
| `_contract.bounds` | JSON: **single number** or **array** of one/two numbers (see UI spec). |
| `_contract.unit` | e.g. `rows`, `percent`. |
| `_contract.ruleType` | `library` / `sql` / `text` / `custom`. |
| `_contract.query` | **Full** SQL `query` for `sql` rules. |
| `_contract.engine` | Engine name for `custom` rules. |
| `_contract.implementation` | **Full** custom implementation body for `custom` rules (descriptor **`implementation`**; **required** in odcs31 — **not** dropped as in legacy, section 3.2). |

### 6.1 ODCS 3.1 to Blindata mapping matrix

Routing is decided before this matrix is applied. Full rules with **`customProperties.scoreStrategy`** present use the
**legacy** mapping. Full rules without **`customProperties.scoreStrategy`** use **odcs31** and **`scoreStrategy:
EXTERNAL`**. Reference-only rules with **`customProperties.refName`** keep reference / merge behavior and are not routed
to odcs31 solely because the stub lacks a score strategy.

| Descriptor field | Target |
|------------------|--------|
| `id` | Short segment of Quality Check `code` for odcs31 mapping when present; not an additional property. |
| `name` | KQI display name; fallback short code segment when `id` is absent. |
| `customProperties.displayName` | KQI display-name override. |
| `customProperties.scoreWarningThreshold` | `warningThreshold` override; defaults to `100` when absent. |
| `customProperties.scoreSuccessThreshold` | `successThreshold` override; defaults to `100` when absent. |
| `customProperties.isCheckEnabled` | `isEnabled` override; defaults to `true` when absent. |
| `customProperties.blindataCustomProp-*` | Blindata `additionalProperties` with the prefix removed. |
| `type` | `_contract.ruleType`. |
| `unit` | `_contract.unit`. |
| `engine` | `_contract.engine`. |
| `query` | `_contract.query`. |
| `metric` | `_contract.metric`. |
| `arguments` | `_contract.arguments` as a JSON string when present. |
| `implementation` | `_contract.implementation` as a JSON string when present. |

ODCS does not define a standalone `operator` key. Comparison is expressed with keys such as `mustBe`, `mustNotBe`,
`mustBeGreaterThan`, `mustBeGreaterOrEqualTo`, `mustBeLessThan`, `mustBeLessOrEqualTo`, `mustBeBetween`, and
`mustNotBeBetween`. The observer sets `_contract.operator` to that key id and `_contract.bounds` to the corresponding
value: a single number or JSON array. When `mustBeGreaterOrEqualTo` and `mustBeLessOrEqualTo` are both present, the
observer maps them together to `_contract.operator = mustBeBetween` with a two-value `_contract.bounds` JSON array.

---

## 7. Requirements

### 7.1 odcs31 path (contract-driven checks)

For each **`quality`** item the observer creates or updates a **Quality Check** such that:

| Quality Check field | Value |
|---------------------|--------|
| **`code`** (short segment, before suite prefix) | If **`quality.id`** is present, use it as the segment that is concatenated after **`{suiteCode} - `**. If **`id`** is absent, use **`quality.name`**. |
| **`name`** (display name) | **`customProperties.displayName`** when present, else **`quality.name`**, else **`quality.id`**. |
| **`description`** | From **`quality.description`** when present. |
| `scoreStrategy` | **`EXTERNAL`**. |
| `scoreLeftValue`, `scoreRightValue`, `scoreExpectedValue` | **Unset** or **null** (or platform default empty) — **do not** fabricate **DISTANCE** / **MINIMUM** numbers to mimic the rule. |
| `warningThreshold` / `successThreshold` | **Defaults** for pass/fail (e.g. **100 / 100**) or values from descriptor **`customProperties`** when present — **same** as today’s override pattern so **semaphore** remains consistent if integration normalizes scores to 0–100. |
| `additionalProperties` | Populated with **`_contract.*`** keys (section 6): operators, `metric`, `arguments`, `unit`, `query`, **`implementation`** (full custom body — **must** be retained; **not** dropped as in legacy, section 3.2), … |
| Physical links | Preserve existing behavior: associate **physical entity** / **physical field** from descriptor placement (table vs column **quality**). |

**Scores and semaphores** are **not** computed by the observer; they arrive via **Quality Results** from the **rule engine** or **integration**.

### 7.2 Preserving current behavior (primary approach)

**Primary:** Achieve compatibility through **design**, **migrations**, **versioned observer behavior**, and **tests** — so existing tenants and descriptors behave as intended using **descriptor-level** routing (**section 5.1**) rather than deployment toggles.

**Examples:** versioned ingest API; migration job that converts existing checks once; rules that only apply **odcs31** to new sync markers or descriptor versions — exact tactics belong in the **retrocompatibility analysis** (section 5).

### 7.3 Idempotency and updates

- **Upsert** by stable key: **`code`** (and suite linkage) so re-publish of the same descriptor **updates** the same check.
- When a rule is **removed** from the descriptor, policy is **product-defined** (soft-disable check vs delete) — document in observer release notes.

### 7.4 Coexistence

- Same **Quality Suite** may contain **legacy-mapped** checks and **EXTERNAL** checks during migration; **UI** distinguishes via **`scoreStrategy`** and **`_contract.*`** metadata (see UI proposal).

---

## 8. Implementation strategy (Bitol ODCS 3.1 + odcs31)

### 8.1 Goals

- **Full support for quality annotations** aligned with **Bitol ODCS 3.1** (lossless capture of rule definition where required for **`_contract.*`**).
- **Retrocompatible** rollout: **legacy** path remains for rules classified as legacy (section 4); **odcs31** path for ODCS-classified rules per section 7.1.

### 8.2 Classification (which path applies)

**Rules (section 5.1):**

1. **`refName`-only reference stubs** → **legacy** / inherit target — **never** odcs31 on the stub alone.
2. **`customProperties.scoreStrategy` absent / null** on a **full** rule → **odcs31** (new mapping).
3. **`customProperties.scoreStrategy` present** → **legacy** (unchanged mapping).

Optional: **explicit ODCS marker** in **`customProperties`** as documented in **section 5.1** and the retrocompat matrix.

Mixed suites (section 7.4) must be covered by fixtures: **legacy** and **EXTERNAL** checks in the same suite, **`refName`** merge cases, without cross-contamination of mapping logic.

### 8.3 Parser and model extensions

1. **Extend** the **`Quality`** (and related) Java model **or** attach a **`JsonNode`** / **`Map`** to each rule for **unknown or ODCS-specific** fields (including **`id`**) so **`_contract.query`**, **`_contract.implementation`**, and structured **`arguments`** are not dropped.
2. If the shared **`DataStoreApi`** stack strips unknown properties before the observer sees them, introduce a **parallel read** of the raw port **`definition`** (e.g. **`quality`** array as **`JsonNode`**) keyed by table/column and rule identity, merged into the visitor output.
3. Implement a **`ContractAdditionalPropertiesBuilder`** (or equivalent) that maps **Bitol ODCS 3.1** rule fields → **section 6** keys, including serialization of **bounds** as required by the UI spec.

### 8.4 Mapping layers

- **Legacy:** Keep **`DataStoreApiStandardDefinitionVisitorImpl.qualityToQualityCheck`** behavior for classified legacy rules (subject to section 5).
- **Merge:** **`DataProductPortAssetAnalyzer.mergeQualityChecksWithSameCode`** must stay **deterministic** for **main vs `refName`** combinations when classification depends on merged state (**section 3.5**); adjust structure (e.g. ordering, **`LinkedHashMap`**) if needed.
- **odcs31:** New branch (or dedicated mapper) that sets **`scoreStrategy`** = **`EXTERNAL`**, clears **interval** fields, applies **default or overridden thresholds**, fills **`additionalProperties`** with **`_contract.*`** keys (including **`_contract.implementation`** from descriptor **`implementation`** — **not** dropped), and applies **section 7.1** for **`code`** (**`id`** when present) and **`name`** (display). Avoid duplicate legacy keys vs **`_contract.*`** unless the compat matrix explicitly allows them.

### 8.5 Validation and Blindata types

- Add **`EXTERNAL`** to the **strategy enum** used in upload payloads **once** Blindata confirms API support.
- Extend **`QualityUpload`** validation: for **`EXTERNAL`**, **do not** require **MINIMUM** / **MAXIMUM** / **DISTANCE** interval consistency; **do** require **thresholds** as per section 7.1 (or document explicit exceptions).

### 8.6 Observability and tests

- Log **counts** of rules processed per path (**legacy** vs **odcs31**) per run.
- **Automated tests:** golden JSON fixtures for **legacy-only**, **ODCS-only**, **mixed**; assert **diffs** on **`QualityCheck`** / upload payload; regression on **suite code**, **prefixed `code`** (including **`id`**-based segment for odcs31), **`refName`** merge (including **deterministic order** — section **3.5**), **parse-failure** warnings, and **issue policies**.

### 8.7 Implementation plan (execution order)

Execute work in the following order so dependencies and risk are managed: upstream contracts and analysis first, then parsing and mapping, then validation and hardening, then rollout.

1. **Align with platform:** Confirm Blindata upload API accepts **`scoreStrategy: EXTERNAL`**, **`_contract.*`** keys in **`additionalProperties`**, and coordinate **UI** consumption of the same key names (section 6).
2. **Freeze vocabulary:** Pin **Bitol ODCS 3.1** schema / builder spec version and publish the **ODCS → `_contract.*`** field matrix (closes open question in section 11.2 where applicable).
3. **Retrocompat baseline:** Complete **section 5** artifacts — inventory of code paths, catalog of current field population (section 4), representative **fixtures** (legacy, odcs31, mixed), and compat matrix before changing production behavior.
4. **Extend parsing:** Add **`id`** and ODCS fields to the **`Quality`** model and/or **raw `JsonNode`** (or parallel parse) for **`quality[]`** so nothing required for **`_contract.*`** is dropped (section 8.3).
5. **Classification:** Implement routing per **section 5.1** / **section 8.2** — **`refName`** reference stubs first; then **`customProperties.scoreStrategy` present** → legacy; **absent** on full rules → odcs31 — wire at the visitor / mapper entry point.
6. **Contract builder:** Implement **`ContractAdditionalPropertiesBuilder`** (or equivalent) mapping ODCS rule → **`_contract.*`** (section 6).
7. **odcs31 mapper:** Add the **odcs31 branch** — **`EXTERNAL`**, cleared intervals, thresholds per **section 7.1**, **`code`** / **`name`** per **section 7.1** and **3.4**, **`_contract.*`** population (section 8.4).
8. **Validation:** Add **`EXTERNAL`** to **`BDQualityStrategyRes`** (or equivalent) and extend **`QualityUpload`** validation for odcs31 (section 8.5).
9. **Automated tests:** Land **golden fixtures** and regression tests from **section 8.6**; keep **legacy** tests green.
10. **Observability:** Emit per-run **legacy vs odcs31** counts (section 8.6).
11. **Rollout:** Execute per **section 5** / **section 10** — staged environments, migration or tenant sequencing as signed off, **rollback** procedure documented (operational — **section 10**).

---

## 9. Algorithm (high level)

1. **Input:** Resolved descriptor (DPDS / ODM shape); scan **all** interface port kinds that expose a supported **`StandardDefinition`** (**input**, **output**, **control**, **discovery**, **observability**) — align with **section 4** / reference implementation (**section 3.5**). Extract **`quality`** arrays from **Datastore API** port definitions.
2. **For each** `quality` item at table or column level:
   - Resolve **suite** and **physical associations** (existing logic).
   - **Classify** the rule (**legacy** vs **odcs31**) per **section 5.1** and **section 8.2** (**`refName`** stub handling, then **`customProperties.scoreStrategy`**: present → legacy, absent on full rule → odcs31).
   - **Legacy:** apply existing population rules (section 4).
   - **odcs31:** build **`additionalProperties`** with **`_contract.*`** keys from Bitol ODCS 3.1 and companion spec sections; set **`scoreStrategy: EXTERNAL`**; set **`code`** / **`name`** per section 7.1; clear or omit interval fields — **subject to retrocompatibility rules** from section 5.
3. **Persist** Quality Check via existing APIs.
4. **Emit** events/logs for observability (counts of EXTERNAL vs legacy per run).

---

## 10. Non-functional

- **Backward compatibility:** Outcomes of the **retrocompatibility analysis** must be satisfied before production rollout; automated regression tests cover representative fixtures.
- **Performance:** Batch upserts; avoid N+1 when many `quality` rules exist.
- **Rollback:** Documented procedure (redeploy prior observer version, descriptor revert, tenant sequencing). **Primary** compatibility control remains **descriptor-level** discriminants (**section 5.1**), not operator toggles.

---

## 11. Open questions and decisions

### 11.1 Decisions (resolved)

| Topic | Decision |
|-------|----------|
| **Legacy vs odcs31 routing** | **`refName`-only** reference stubs → **legacy** merge path (section 5.1); **not** odcs31 on the stub alone. **Full** rules: **`customProperties.scoreStrategy` absent or null** → **odcs31**; **present** → **legacy**. See **section 5.1**. |
| **`implementation` in odcs31** | Legacy mapping **drops** **`implementation`** today (section 3.2). odcs31 **must** persist the full body in **`_contract.implementation`** (section 6, section 7.1). |
| **Rule identity in odcs31** | For the **new version (odcs31) only**, when **`quality.id`** is present, it **must** be used as the short segment of the Quality Check **`code`** (before the **`{suiteCode} - `** prefix is applied as today). When **`id`** is absent, use **`quality.name`** for that segment (legacy-compatible). |
| **Display name in odcs31** | For **odcs31 only**, **`quality.name`** is the **display name** of the check (maps to KQI **`name`**). |
| **Definition keys in `additionalProperties`** | Use the **`_contract.`** prefix (leading underscore), e.g. **`_contract.operator`**, **`_contract.metric`** — not `contract.`. |

A separate **`_contract.descriptorRuleId`** (or similar) is **not required** for primary traceability when **`id`** is already mapped into **`code`**; add only if product needs a duplicate for tools that read metadata without **`code`**.

### 11.2 Open

1. **Deletion** vs **disable** when a contract rule disappears from the descriptor — coordinate with Blindata **import** behavior when **`cleanup=true`** is used (**section 3.5**).
2. **Single source of truth** for **Bitol ODCS 3.1** JSON Schema (version pin) to freeze the **`_contract.*`** mapping matrix.

---

## 12. Acceptance criteria (Gherkin)

```gherkin
Feature: Observer creates EXTERNAL KQI from ODCS rules
  As a platform operator
  I want descriptor quality rules to become Quality Checks with EXTERNAL strategy and rich metadata
  So that KQI shows definitions while integration supplies scores.

  Scenario: Retrocompatibility analysis is completed before release
    Given a new observer version changes ingestion
    Then a retrocompatibility analysis document exists and is reviewed
    And test fixtures cover before/after behavior for legacy and new descriptors

  Scenario: odcs31 creates EXTERNAL checks with _contract.* metadata
    Given a published descriptor with at least one quality rule processed under odcs31
    When the observer processes the descriptor under the new rules
    Then each new or migrated rule yields a Quality Check with scoreStrategy EXTERNAL when required by the rollout plan
    And additionalProperties include _contract.operator, _contract.metric, _contract.bounds, and _contract.unit when present in the rule

  Scenario: odcs31 uses rule id in code when present
    Given a quality rule with id set and processed under odcs31
    When the observer builds the Quality Check code
    Then the short code segment before the suite prefix equals the rule id
    And the check display name equals the rule name

  Scenario: refName-only stubs are not classified as odcs31 in isolation
    Given a column-level quality entry with only customProperties.refName
    When the observer classifies and merges with the main rule
    Then the stub is not routed to odcs31 solely because scoreStrategy is absent on the stub

  Scenario: odcs31 preserves implementation in _contract.implementation
    Given a full quality rule without customProperties.scoreStrategy and with implementation present
    When the observer maps under odcs31
    Then additionalProperties include the full implementation body under _contract.implementation
```
