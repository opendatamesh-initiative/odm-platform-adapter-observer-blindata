# SPDD Analysis: Quality Probes Upload to Blindata Probe Project

## Original Business Requirement

i need to introduce support for creating quality probes inside a probe project in blindata. This use case should be optionally enabled with configuration. To look at how data quality annotation are written into blindata api please look at blindata-ui. pay attention that they might be different about how probes are sent to agent for execution. the probe project should follow the same convention as the related quality suite. use also blindata-site-docs for help center and discovering how data quality works in blindata. Assume the connection name is a custom property on the port object; presence of the connection name opts the port into probe upload, absence skips probe materialization for that port (even when library/sql quality annotations exist). be aware that at the moment only library and sql quality annotation are supported. once probe are uploaded this observer should also create a tag.  Assumption: blindata connection name is configured in blindata agent manually, scheduling is activated manually, custom properties about blindata connection name for the datasource is edited in the builder.

## Domain Concept Identification

### Domain Concept Identification

#### Existing Concepts (from codebase)

- **Quality annotation (descriptor)**: ODCS / Datastore API `quality` objects on table or column definitions (`type` library/sql/text/custom, `metric`, `query`, operators, `id`/`name`). Extracted today by `PortDatastoreApiEntitiesExtractor` → `DataStoreApiStandardDefinitionVisitorImpl`; already uploaded as Blindata Quality Checks via `QUALITY_UPLOAD`.
- **Quality Suite**: Per–data-product container created by `QualityUpload.buildQualitySuite`. Convention: `code` = `{domain} - {dataProductName}`; `name` = `{domain} - {displayName|name}`. All product checks land in this suite.
- **Quality Check (KQI)**: Blindata scoring entity associated to suite; `code` is suite-prefixed. Odcs31 path stores executable contract metadata as `_contract.*` additional properties (`ruleType`, `metric`, `query`, `operator`, `bounds`, `arguments`, …) — this is the **catalog/KQI** representation of the annotation, not the agent-executable probe.
- **QUALITY_UPLOAD use case**: Event-driven (`DATA_PRODUCT_VERSION_CREATED` / `DATA_PRODUCT_ACTIVITY_COMPLETED` / V2 `DATA_PRODUCT_VERSION_PUBLISHED`), wired via `activeUseCases` and `QualityUploadFactory`. Pattern to mirror for probes.
- **Port + extension/custom properties**: Descriptor port-level properties already flow into Blindata port `additionalProperties` via configured regex (`blindata.dataProducts.additionalPropertiesRegex`). Connection name for probes is assumed to live as a **custom property on the port object** (edited in Blindata Builder); no dedicated connection-name handling exists in the observer today.
- **Physical Entity / Physical Field**: Attached to extracted Quality Checks during schema visit (`schema` = datastore `databaseSchemaName`, entity/field `name`). Needed conceptually for probe physical binding (`schema` / `object` / `property`).
- **Blindata Quality Probe Project**: Container of probes that are scheduled/executed together (`/api/v1/data-quality/probes/projects`). Has `name` + optional `description` (no suite-style `code` field). Docs: projects first, then probes inside.
- **Blindata Quality Probe Definition**: Versioned probe under a project (`name` unique per project among last versions). Linked to a Quality Check via `checkCode`/`checkName`. Types include `CONTRACT_RULE`, `SINGLE_METRIC`, etc.
- **CONTRACT_RULE probe**: Builder/UI path for ODCS library/sql rules. Persists `queries[0].queryBody` as envelope `blindata.qualityProbe.contractRule.v1` with `rule` (ODCS execution subset) + `physicalBinding`. This is the right probe type for library/sql annotations.
- **Blindata Quality Probe Tag**: Snapshot of a project's current probe versions (`POST /api/v1/data-quality/probes/tags`). Tag name unique per project; creation auto-associates all current last-version probes. Required before scheduling (docs); scheduling itself is **out of scope** (manual).
- **Blindata Quality Probe Connection**: Named connection (`name` + `type` JDBC/MONGO/SALESFORCE) configured in Blindata / agent. Observer must **reference** connection by name, not create/configure credentials. Assumption: agent connection setup is manual.
- **Agent execution payload vs API persistence**: UI `ProbesJobConverter` flattens `queryBody` into the agent job (`type: QUALITY_PROBES`). Persisted probe definitions keep structured `queryBody`. Observer must write the **API persistence shape**, not the flattened agent job shape.

#### New Concepts Required

- **Probes upload capability (optional use case)**: New observer behavior that, when enabled by configuration, materializes Blindata Probe Project + CONTRACT_RULE Probe Definitions from descriptor quality annotations (library/sql only), then creates a Probe Tag. Distinct from (but complementary to) `QUALITY_UPLOAD`.
- **Probe Project ↔ Quality Suite alignment**: Probe Project identity follows the related Quality Suite **code** convention (`{domain} - {dataProduct.name}`), so operators can correlate KQIs and executable probes while avoiding mutable display names.
- **Port connection-name custom property**: Port-scoped property that supplies Blindata probe `connectionName` for all probes derived from that port’s annotations. → **Decided default key: `x-blindataConnectionName`** (Builder additional property name: `blindataConnectionName`). Optionally overridable via config. Connection is **per port**.
- **Executable annotation subset**: Only odcs31 **`library`** and **`sql`** quality annotations become probes. **Legacy** rules (`customProperties.scoreStrategy` present) are **out of scope** (skipped). `text`/`custom` are not supported for probe materialization.
- **Reference stubs (`refName`)**: Stubs are **skipped** for probe materialization (`isReference()`). They do **not** become standalone probes and do **not** merge physical context onto the main rule (unlike `QUALITY_UPLOAD`). Probe physical binding uses checks **as declared** on a single table/column via `extractDeclaredQualityChecksFromPorts`.
- **Declared vs merged quality extraction**: Analyzer exposes `extractDeclaredQualityChecksFromPorts` (declared physical context) vs `extractQualityChecksFromPorts` (refName merge for KQI association). Probes upload requires the declared path because a probe executes against one physical object.
- **ContractRuleEnvelopeBuilder**: Builds CONTRACT_RULE `queryBody` envelope (`blindata.qualityProbe.contractRule.v1`), execution `rule` subset from `_contract.*`, and `PhysicalBinding` from the declared quality check.
- **BdProbesUploadConfig**: Spring `@Component` under `configurations` for `blindata.probesUpload.connectionNamePropertyKey` (default `x-blindataConnectionName`).
- **Post-upload Probe Tag**: After probes are uploaded, create a tag named after the **data product version**. If a tag with that name already exists on the project, **delete and recreate** it.

#### Key Business Rules

- **Opt-in only**: Probe upload runs only when explicitly enabled via configuration; default must not change existing deployments that only run `QUALITY_UPLOAD`.
- **Stable Probe Project naming**: Probe Project name must follow the related Quality Suite **code** convention (`{domain} - {dataProduct.name}`). It must not use `displayName`, because changing a display label must not change project identity.
- **Connection name as per-port opt-in**: Presence of `x-blindataConnectionName` (or the configured key) on a port opts that port into probe upload for its library/sql rules. Absence or blank value means **skip** those rules as probe candidates (info log); do **not** fail validation or block publish. This lets a product keep ODCS library/sql annotations for KQIs while executing quality outside Blindata probes.
- **Library and SQL only; no legacy**: Probe materialization supports odcs31 `library`/`sql` only. Legacy rules are skipped. Non-library/sql types are not materialised as probes.
- **`refName` stubs**: Skipped for probes (not standalone definitions). Physical binding comes from the declared check only — **do not** reuse `QUALITY_UPLOAD` merge semantics, because a probe runs on a single table/column.
- **KQIs and probes are different Blindata resources**: Annotations already become Quality Checks (with `_contract.*` for odcs31). Probes are separate executable definitions associated by `checkCode` to those checks. Do not confuse catalog additional properties with probe `queryBody`.
- **CONTRACT_RULE is the probe representation for library/sql**: Align with Blindata UI builder contract-test / probe modal path (`CONTRACT_RULE` + ODCS envelope), not classic `SINGLE_METRIC` SQL-only probes.
- **Annotations are technology-independent**: Observer cares about **connection name** only from the port property; it does not infer technology from the quality annotation. Connection **type** for the Blindata probe API is resolved from the named Blindata connection (lookup by name), not from the descriptor rule.
- **Connection & scheduling remain manual**: Observer does not create Blindata agent connections, does not write credentials, and does not create agent schedules. It only creates project/probes/tag.
- **Tag = data product version; delete & recreate**: After probes upload, create a tag named with the data product version. On re-upload, if that tag already exists, delete it and create it again (fresh snapshot).
- **Per-port connection**: Each port carries its own `x-blindataConnectionName`; probes derived from that port use that connection.
- **Probe name uniqueness within project**: Blindata rejects creating a second last-version probe with the same name in a project; updates go through versioned overwrite by `rootUuid`.
- **Assumptions (external process)**: Blindata connection name is configured on the agent manually; scheduling is activated manually; Builder edits port additional property `blindataConnectionName` (`x-blindataConnectionName` in the descriptor).

## Strategic Approach

### Strategic Approach

#### Solution Direction

Introduce an **optional, configuration-gated probes upload use case** in the Blindata Observer that, on the same data-product version events used by quality upload, extracts library/sql quality annotations from ports (as declared, without `refName` merge), ensures a Blindata **Probe Project** exists using the stable **Quality Suite code convention**, upserts **CONTRACT_RULE** probe definitions (ODCS rule + declared physical binding + port connection name), and then **creates a Probe Tag** snapshot. Keep KQI upload (`QUALITY_UPLOAD`) as the source of Quality Checks; probes associate via suite-prefixed `checkCode`. Do not configure agent connections or schedules.

High-level data flow: descriptor ports → extract declared quality annotations (+ physical context + port connection property) → skip library/sql without connection (opt-out) → validate declared connections → ensure Probe Project (stable suite-code-aligned name) → upsert CONTRACT_RULE probes linked to Quality Check codes → create Probe Tag → stop (manual scheduling/ops).

Leverage existing observer patterns: use-case package with ODM/Blindata outbound ports, factory registered in notification event manager / V2 counterpart, dry-run/validator-compatible outbound stubs where appropriate, and Blindata REST clients alongside `BdQualityClient`.

#### Key Design Decisions

- **Separate use case vs fold into QUALITY_UPLOAD**: Folding couples optional execution upload to always-on (or already configured) KQI upload and complicates enablement. → **Decided** distinct use case flag `PROBES_UPLOAD` in `eventHandlers.activeUseCases`, with `BdProbesUploadConfig` for the connection-property key.
- **Probe type CONTRACT_RULE vs SINGLE_METRIC**: Builder and Blindata schema treat library/sql ODCS rules as `CONTRACT_RULE` with envelope `blindata.qualityProbe.contractRule.v1`. Classic `SINGLE_METRIC` expects JDBC `queryBody.text` scalars and does not carry ODCS library metrics. → **Decided CONTRACT_RULE**; do **not** flatten to agent job shape on write.
- **Relationship to QUALITY_UPLOAD**: Probes need stable suite-prefixed check codes matching uploaded KQIs. → **Decided** run probes upload after quality upload when both are active; checkCode = `{domain} - {dataProduct.name} - {qualityCheck.code}`.
- **Probe Project identity**: Project API has name only (no code). → **Decided** use the Quality Suite **code** convention (`{domain} - {dataProduct.name}`) as Probe Project `name`, deliberately ignoring mutable `displayName`, and resolve existing projects by exact name match after search.
- **Connection name source**: Port custom property (Builder-edited). → **Decided: per-port opt-in**; default key **`x-blindataConnectionName`** (optionally configurable). Missing/blank connection → **skip** library/sql rules on that port as probe candidates (info log; no validator block). Declared connection that is unknown or typeless → **fail** (validator warn; fail closed).
- **Connection type / technology**: Annotations are tech-independent; observer only requires **connection name**. → **Decided**: look up Blindata Probe Connection by name to obtain `connectionType` for the API payload; do not branch probe logic on JDBC/MONGO/SALESFORCE at annotation level. Fail/warn clearly if the named connection does not exist in Blindata.
- **Unsupported / legacy annotation types**: → **Decided: no legacy support** (skip when `scoreStrategy` present and no `_contract.ruleType`). Only odcs31 `library`/`sql` become probes; skip other types (info log).
- **`refName` / physical binding**: → **Decided: declared checks only**. Use `extractDeclaredQualityChecksFromPorts`; skip `isReference()` stubs; do **not** merge stub physical fields/entities onto the main probe (KQI upload continues to use merge). Rationale: a probe executes against one physical object; merge would attach wrong bindings.
- **Probe upsert identity**: Blindata enforces unique probe **name** per project (last version); updates create new versions via PUT `rootUuid`. → **Decided**: stable `name` / `checkCode` = `{domain} - {dataProduct.name} - {qualityCheck.code}`; find existing by project + name, then create or overwrite.
- **Tag naming on each upload**: → **Decided**: tag name = **data product version**. If a tag with that name already exists on the project, **delete and recreate** it after probes are uploaded. Missing version → warn and skip tag.
- **Enablement configuration**: → **Decided** event-handler `activeUseCases` entry `PROBES_UPLOAD` as primary gate; `BdProbesUploadConfig` for connection-property key (default `x-blindataConnectionName`). Sample YAML may list `PROBES_UPLOAD` for local/demo; production remains per-handler opt-in.
- **Failure / enforcement mode**: → **Decided**: use **validator warn that blocks publish** for **invalid declared** connections (unknown `[#204]`, no type `[#205]`), not a hard `UseCaseExecutionException`. Fail closed: if any opted-in candidate is invalid, skip all Blindata writes for the run (`[#201]`). Missing connection is **not** a failure (`[#200]` removed); it is the opt-out path.
- **Connection property fallback**: → **Decided**: prefer configured key; if it starts with `x-` and is absent, also try Builder-stripped name without `x-`.
- **Envelope builder**: → **Decided**: dedicated `ContractRuleEnvelopeBuilder` utility for `rule` + `physicalBinding` + schema constant.
- **Validator dry-run**: → **Decided**: `BlindataValidatorService` runs probes dry-run after quality dry-run when `PROBES_UPLOAD` is active and interface components are present; unknown/typeless declared connections warn and fail validation; missing connection does not.

#### Alternatives Considered

- **Extend QUALITY_UPLOAD to also create probes**: Rejected as primary approach — mixes optional execution concerns into KQI upload, harder to disable independently, and obscures the catalog-vs-execution distinction called out by the requirement.
- **Persist probes as SINGLE_METRIC with generated SQL for library rules**: Rejected — duplicates Blindata agent/library semantics already implemented for CONTRACT_RULE; diverges from UI builder path; brittle for library metrics.
- **Write flattened agent job payloads into Blindata definitions**: Rejected — Blindata API expects structured `queryBody`; flattening is only for agent job submission (`ProbesJobConverter`).
- **Observer creates/schedules agent connections and schedules**: Rejected — explicitly out of scope per assumptions (manual ops).
- **Reuse Quality Check `_contract.*` alone without probe entities**: Rejected — does not create executable probes/projects/tags; agent schedules operate on probe projects/tags, not KQI additional properties.

## Risk & Gap Analysis

### Risk & Gap Analysis

#### Requirement Ambiguities

- ~~**Exact port custom property key for connection name**~~ → **Decided: `x-blindataConnectionName`** (Builder name `blindataConnectionName`; optionally overridable via config).
- ~~**Tag name convention**~~ → **Decided: data product version**; on conflict **delete and recreate**.
- ~~**Unsupported rule types / legacy**~~ → **Decided: no legacy**; only odcs31 `library`/`sql`; skip others (with warn as needed).
- ~~**Multi-port connection**~~ → **Decided: per-port connection**.
- ~~**Failure mode**~~ → **Decided: validator warn that blocks publish for invalid declared connections; missing connection = opt-out (skip)**.
- ~~**Connection technology**~~ → **Decided: care about connection name only**; annotations are tech-independent; resolve Blindata connection type by name lookup for the API.
- ~~**`refName` support**~~ → **Decided: skip stubs; declared physical binding only** (not merge onto main library/sql rules; not standalone probes).
- **JIRA ticket id**: `BDMD-5372`.
- ~~**Whether `PROBES_UPLOAD` should appear in default `application.yml` samples**~~ → **Decided**: sample/dev/test YAML may include `PROBES_UPLOAD` for local simulation; production enablement remains opt-in via `activeUseCases` per handler.
- ~~**Physical binding source**~~ → **Decided**: `extractDeclaredQualityChecksFromPorts` + first declared entity/field; table-level omits `property`.
- ~~**Probe and project name composition**~~ → **Decided**: project = `{domain} - {dataProduct.name}`; probe/checkCode = `{domain} - {dataProduct.name} - {qualityCheck.code}`. All technical identities use stable product `name`, never `displayName`.
- ~~**Config class shape**~~ → **Decided**: `BdProbesUploadConfig` `@Component` under `configurations`.

#### Edge Cases

- ~~**When missing connection fails**~~ → **Revised**: missing/blank connection **never fails**; those library/sql rules are skipped as probe candidates (per-port opt-out). Fail only when a connection **is** declared but unknown in Blindata or has no type.
- ~~**`refName` stubs**~~ → **Decided**: skip reference stubs; do not create a separate probe; do not merge stub physical context onto the main probe.
- ~~**Table-level vs column-level binding**~~ → **Decided**: omit `property` for entity-level rules; set `property` from declared field name for column-level rules (UI accepts table-level without property).
- **Re-upload with renamed quality rules**: Probe name/checkCode drift can orphan old probes or create duplicates; identity is `qualityCheck.code` under suite prefix.
- ~~**Tag already exists for same version**~~ → **Decided: delete and recreate**.
- **Probe project name collision** with an unrelated manually created project sharing the suite-style name.
- **QUALITY_UPLOAD disabled but PROBES_UPLOAD enabled**: Probes may reference check codes not yet present as KQIs — association still works by code string, but scoring/UI may show missing checks until KQIs exist.
- ~~**Named connection missing in Blindata**~~ → **Decided**: validator warn that blocks publish; fail closed (no partial probe publish).
- **Duplicate same check code declarations**: Keep first declaration; info log; later declarations ignored for probe candidates.

#### Technical Risks

- **Two representations of the same rule**: KQI `_contract.*` vs probe envelope `rule` must stay semantically aligned with Blindata UI adapters (`internalRuleToOdcsExecution` / envelope encode) or agent results won’t match catalog expectations. Mitigation: reuse the same ODCS fields already extracted for odcs31 mapping; follow UI envelope schema literally.
- **No existing Blindata probe client in observer**: New REST client surface (`projects`, `definitions`, `tags`, optionally `connections` lookup) must follow `BdClientImpl` / RestUtils patterns; auth and paging same as quality client.
- **Versioned probe updates**: Incorrect create-vs-overwrite handling causes `ResourceConflictException` (“Probe already exists in project”).
- **Ordering with QUALITY_UPLOAD**: If both run async (`blindata.enableAsync`) or handler order is unstable, operators may briefly see probes without checks — mitigate with deterministic handler order or local code composition.
- ~~**Validator dry-run**~~ → **Decided**: run probes dry-run in `BlindataValidatorService` after quality dry-run when `PROBES_UPLOAD` is active and interface components are present; unknown/typeless **declared** connections warn and fail validation; missing connection does not.
- ~~**Schema completeness for CONTRACT_RULE**~~ → **Decided**: `ContractRuleEnvelopeBuilder` maps executable `_contract.*` subset only (type/metric/query/unit/arguments/operator+bounds); does not dump full quality customProperties into `rule`.

#### Acceptance Criteria Coverage

| AC# | Description | Addressable? | Gaps/Notes |
|-----|-------------|--------------|------------|
| 1 | Optionally enable creating quality probes in a Blindata probe project via configuration | Yes | Prefer new `activeUseCases` entry (+ config for connection property key). |
| 2 | Probe project follows the stable code convention of the related quality suite | Yes | Project name is `{domain} - {dataProduct.name}`; project API has no separate code field. |
| 3 | Use Blindata UI patterns for how quality annotations map to Blindata API; distinguish agent execution payload | Yes | Persist CONTRACT_RULE API shape; do not write flattened agent job. |
| 4 | Align with Blindata help-center DQ model (project → probes → tag → manual schedule) | Yes | Create project/probes/tag only; no schedule/connection provisioning. |
| 5 | Connection name from port custom property | Yes | **Decided: per-port `x-blindataConnectionName`** (Builder: `blindataConnectionName`; optional config override). |
| 6 | If probe upload enabled and quality annotations exist without connection name → error | Revised | **Revised: missing connection = per-port opt-out** (skip probe candidates, info log, no publish block). Error only when a declared connection is unknown or has no type. |
| 7 | Only library and sql quality annotations supported | Yes | **Decided: no legacy**; skip `refName` stubs; declared physical binding; skip unsupported types. |
| 8 | After probes uploaded, create a tag | Yes | **Decided: tag = data product version; delete & recreate** if present. |
| 9 | Connection configured on agent manually; scheduling manual; Builder edits connection custom property | Yes | Out of observer scope — document as operational assumptions. |

---

**Codebase grounding notes (internalized, not a separate section):** Spring Boot 2.7 / Java 11 observer adapter; quality path already mature (`quality_upload`, Datastore API odcs31 `_contract.*`); Blindata probe APIs and UI CONTRACT_RULE envelope confirmed; site docs confirm project/tag/schedule lifecycle and manual agent ops.
