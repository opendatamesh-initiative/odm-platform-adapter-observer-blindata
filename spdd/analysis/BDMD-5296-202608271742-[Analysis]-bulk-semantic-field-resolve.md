# SPDD Analysis: Bulk Semantic Field Resolve

## Original Business Requirement

We are tasked with analyzing and designing and implementing a "bulk" endpoint for resolving physical fields, as when there are hundreds of them the API gets hundreds of different calls.
We want to implement it a way to have a max batch size where a number of fields can be all resolved at once. There already is a pattern similar to this in the observer repository, where the endpoint is async and then there's polling to see the result. First, find this.
Then, using the command @blindata-api/.cursor/commands/spdd-analysis.md we want to start analyzing how to implement this bulk API both on the API side @blindata-api and on the observer side @odm-platform-adapter-observer-blindata

## Stakeholder Clarifications

Answers collected to resolve analysis gaps (BDMD-5296):

| Topic | Answer |
|-------|--------|
| **JIRA** | **BDMD-5296** |
| **What the API resolves** | **Confirmed path-centric.** Bulk resolve takes semantic paths (+ default namespace), not physical-field identity. The observer still attaches results onto physical fields locally. |
| **Max batch size** | **500**, as an **API request argument** (not application/yml configuration). API rejects more than 500 items. Observer chunks within the current unit of work if an entity has more than 500 linked fields. |
| **Async** | **Confirmed: synchronous bulk endpoint + optional generic async wrap.** Matches the established observer pattern (see verification below). |
| **Response shape** | **Confirmed: wrapper resource class**, not a bare JSON array. Must stay extensible (counts, errors, extra metadata later). Per-item outcomes live inside that class. |
| **Batching unit** | **Confirmed: same as today — per physical entity.** One table/definition is enriched at a time; bulk replaces the per-field GETs *inside* that enrichment. Do not gather all tables of a product into one call. |

### Verification: sync bulk + optional generic async wrap

This is already how the observer talks to other collection endpoints. The Blindata APIs themselves are ordinary synchronous POST/PATCH. The observer chooses the client:

- `restUtils` when `blindata.enableAsync=false` (default)
- `asyncRestUtils` when `enableAsync=true`

`asyncRestUtils` rewrites the same URL under `/api/v1/settings/async/request/...`, then polls `/api/v1/settings/async/poll/{taskId}` until `DONE` / `FAILED`. Call sites in `BdClientImpl` that already do this:

- data-product patch
- data-product port-assets
- quality suite import-objects
- policy-evaluation upload

`getSemanticLinkElements` (today’s GET resolvefield) does **not** use this switch — it always goes through `restUtils`. Bulk resolve should follow the four call sites above, not invent a dedicated job API.

### Verification: batching unit is already per physical entity

Datastore visit walks tables one by one. For each table definition it builds one `BDPhysicalEntityRes` (including its fields) and then calls `enrichWithSemanticContext` once. Field resolve therefore already runs **per entity**, not per port or per product version. Bulk must keep that boundary: collect the paths of the entity being enriched, call bulk (chunked at 500 if needed), attach results, then move to the next table.

## Domain Concept Identification

### Domain Concept Identification

#### Existing Concepts (from codebase)

- **Semantic path**: A dotted path string that starts at a data category and walks relations to a logical field (for example `[Stock].stockQuantity` or `[Stock].refersTo[lux:ProductSku].lux:productSkuIdentifier`). The observer extracts these from a datastore descriptor’s semantic context (`s-base`, `s-type`, per-field paths). Blindata parses the same string into ordered semantic-link elements.
- **Single-path resolve-field utility**: Hidden GET under `/api/v1/logical/semanticlinking/*/resolvefield`. Given one path and one default namespace identifier, it returns the target logical field plus the resolved semantic-link header. This is the method the observer uses today, one HTTP call per physical field.
- **Logical field with semantic link**: The resolve result: the catalog logical field (identity, name, data category, namespace) plus the semantic-link header (path string, default namespace, ordered elements). The observer attaches this object onto a physical field before uploading port assets.
- **Physical field (observer side)**: Column/property extracted from a port schema. Semantic linking does not resolve *physical* fields on Blindata; it resolves *semantic paths* and then binds the result to each physical field locally. CSV “upload physical fields” on the API is a different concept (persist links onto already-catalogued physical fields).
- **Semantic link manager (observer)**: Parses a physical entity’s semantic context, looks up default namespace and referenced data categories, then for every physical field with a path calls the Blindata client to resolve that path. Used both on live upload and on the validator dry-run path. Invoked once per physical entity during datastore visit.
- **Blindata semantic-linking client (observer)**: Thin REST client. Path resolve is a synchronous GET and is **not** switched onto the async client even when `enableAsync` is true. Namespace and data-category lookups are separate list searches (also per distinct concept, not per field).
- **Generic async request/poll (the pattern to reuse)**: Blindata exposes `/api/v1/settings/async/request/**` (accept any API call, enqueue a task, return a task id) and `/api/v1/settings/async/poll/{taskId}` (IN_PROGRESS / DONE / FAILED). The observer’s `AsyncRestUtilsTemplate` submits the original URL rewritten under `async/request`, then polls with exponential backoff until DONE, then deserializes the inner response. `enableAsync` currently applies this wrapper to large writes (data-product patch, port-assets, quality import-objects, policy-evaluation upload) — **not** to resolvefield.
- **Bulk write analogs (wrapper resources, not raw lists)**: Quality suite `import-objects` uses `QualitySuiteImportRes` in and `QualitySuiteImportResultRes` out. `allpaths` returns `SemanticLinksRes` wrapping a list. CSV import returns `ResponseMessage`. Collection APIs in this codebase return a **class**, so later fields can be added without breaking clients.
- **CSV physical-field semantic-link import**: In-process bulk on the API: each CSV row with a path string already calls the same resolve-field service in a loop, catching per-row not-found/bad-request and accumulating errors. Proves per-item failure isolation is an established resolve convention; it is not an HTTP bulk API and it persists catalog links rather than returning resolved objects to a remote client.
- **Request-scoped semantic-path search context**: Introduced for `allpaths` performance. Namespace prefix lookups can be cached for the duration of one request. Resolve-field today still hits namespace / data-category / attribute services per path segment with no cross-path cache — relevant when one batch repeats the same concepts hundreds of times.
- **Default namespace (`s-base`)**: Identifier shared by all semantic paths of one physical entity in the observer. Prefixed segments (`prefix:Concept`) override namespace per path segment inside Blindata’s resolver.

#### New Concepts Required

- **Bulk resolve-field operation**: A single Blindata API call that accepts many (path, default-namespace) items, resolves each with the same semantics as today’s single-path utility, and returns a **wrapper result resource** with per-item outcomes. Sibling of the existing GET, not a replacement.
- **Max batch size (API argument, 500)**: Request-level ceiling of **500** paths. Not a Spring/yml setting. The API rejects a request that exceeds 500. The observer, still working per physical entity, splits that entity’s paths into chunks of at most 500.
- **Per-item resolve outcome**: For each requested path, either a resolved logical-field+semantic-link (same meaning as today) or an unresolved/failed outcome that does not fail the rest of the batch. Carried inside the wrapper result class so the shape can grow (e.g. error message, counts).
- **Observer resolve batching (per entity)**: Semantic-link enrichment collects all field paths **for the entity currently being visited**, optionally collapses duplicates that share path+namespace, chunks by 500, calls bulk resolve, then maps results back onto that entity’s physical fields. Same manager remains the single entry point for upload and validator.
- **Async wrapping of bulk resolve (reuse, do not invent)**: The bulk operation itself is a normal synchronous API method. Long-running batches are protected by the existing settings async request/poll infrastructure, selected on the observer with the existing `enableAsync` flag — the same way quality import and port-assets already work.

#### Key Business Rules

- **Call volume**: Resolving hundreds of semantic fields on one entity must not produce hundreds of Blindata HTTP calls. One batch = one call (plus poll if async).
- **Max batch size is 500, as an API argument**: Not configurable in application settings. Oversized requests are rejected by the API; the observer never sends more than 500 items.
- **Chunking, not truncation**: If one physical entity has more than 500 linked fields, further batches of that same entity are issued. Completeness of linking for the entity is required; dropping fields to stay under the cap is not acceptable.
- **Batching unit stays per physical entity**: Do not coalesce tables/ports/product versions into a single bulk call. Match today’s `enrichWithSemanticContext` scope.
- **Resolution semantics unchanged**: A path that resolves today must resolve the same way in bulk (including prefixed concepts and the default namespace). A path that cannot be resolved must still be treated as a warning for that field, not as a reason to skip unrelated fields.
- **Existing single-path GET remains**: UI, tests, and any other client of `*/resolvefield` keep working. Bulk is additive.
- **Do not persist from resolve**: Bulk resolve is a read/compute utility, like today’s GET. It must not write semantic links onto catalog physical fields (that is the CSV import use case).
- **Path-centric, not physical-field lookup**: The API contract is semantic path + default namespace. Physical field names stay on the observer.
- **Async is optional infrastructure, not a new contract**: Clients that already know `enableAsync` keep one code path: call the bulk URL; the wrapper rewrites to async request and polls. No dedicated “start job / get job” API for this feature.
- **Partial success inside a batch**: One bad path must not discard successful resolutions in the same batch. HTTP success for a well-formed request; failures are per item inside the wrapper result.
- **Response is a resource class**: Do not return a raw list. Use a dedicated result type so the contract can be extended.
- **Validator parity**: Dry-run validation uses the same semantic-link manager, so it must benefit from bulk resolve without a second implementation.

## Strategic Approach

### Strategic Approach

#### Solution Direction

Add a **bulk sibling** of the existing resolve-field utility on Blindata API, then change the observer’s semantic-link manager to **collect, cap at 500, and batch** path resolutions for the **current physical entity** instead of calling GET once per field.

On the API: keep GET `*/resolvefield` as-is. Introduce a collection-oriented resolve operation on the same semantic-linking utils surface that reuses the current path-parsing/resolution logic per item, takes/enforces a max of **500** via a request argument, and returns a **wrapper result class** with per-item success or failure. Internally this is still “resolve this path,” not a new glossary algorithm. The endpoint is synchronous; generic async request/poll remains available to any client, including the observer when `enableAsync` is true.

On the observer: after parsing semantic context (unchanged), gather every physical field that has a path **on that entity**, split into chunks of at most 500, POST the bulk operation (via `genericPost`, using `asyncRestUtils` when `enableAsync` is true), and attach returned links by path+namespace. Namespace and data-category existence checks that run *before* field resolve stay as they are (few distinct concepts, not hundreds of fields).

High-level flow: visit table → parse that entity’s paths → (optional dedupe) → chunk by 500 → one bulk resolve per chunk (optionally async+poll) → map outcomes onto that entity’s physical fields → next table.

#### Key Design Decisions

- **True bulk HTTP vs only wrapping today’s GET in async**: Wrapping N GETs with request/poll still issues N API invocations (and N tasks). That does not fix hundreds of calls. → **Bulk collection endpoint** is required; async wrapping is an additional timeout shield for each *batch*, not a substitute for batching.
- **Reuse generic async request/poll vs a dedicated async resolve job**: **Confirmed.** Observer already switches `restUtils` / `asyncRestUtils` on `enableAsync` for port-assets, quality import-objects, data-product patch, and policy-evaluation upload. Blindata APIs stay synchronous. → **Reuse**; do not add a second polling protocol for semantic linking.
- **Path-centric vs physical-field lookup**: **Confirmed.** API resolves semantic paths to logical fields. Observer binds results to physical fields. CSV `upload/physicalfields` stays out of scope.
- **Max batch size 500 as API argument, not config**: **Confirmed.** Ceiling is 500, passed/enforced as part of the API request (not yml). API rejects oversize. Observer chunks **within the current entity** if that entity exceeds 500 linked fields. Do not silently truncate.
- **Batching unit = current physical entity**: **Confirmed.** Same as `enrichWithSemanticContext` today. A product with many tables still makes one enrichment (and thus one or more bulk calls) per table, not one call for the whole product.
- **Partial results in a wrapper class**: **Confirmed.** HTTP success for a well-formed batch; per-item unresolved paths inside an extensible result resource (same family as `QualitySuiteImportResultRes` / `SemanticLinksRes` / `ResponseMessage`). Observer maps failures to `[#90]`-class warnings without aborting sibling fields.
- **Correlation of results to physical fields**: The API stays path/namespace-centric. → **Match on (path string, default namespace)**; **dedupe identical pairs** in a batch so two columns with the same path share one resolution.
- **Shared lookups inside a batch**: Hundreds of paths typically share one default namespace and a small set of concepts. Naively looping today’s resolver repeats the same namespace/category/attribute queries. → **Reuse current resolver for correctness first; add request-scoped lookup reuse in the same change if cheap (same idea as `allpaths` search context), otherwise call it out as an immediate follow-up if profiling shows DB cost still dominates after HTTP batching.**
- **When the observer uses bulk**: Always using bulk (including a batch of one) avoids two client code paths. → **Observer always uses bulk for enrichment**; GET remains for other clients.
- **`enableAsync` for resolve**: Honor the existing flag the same way as quality/port-assets. With 500 items, `enableAsync=false` can still time out; that is the same trade-off as other bulk writes.
- **Scope of namespace/data-category observer calls**: Those remain one lookup per distinct concept, not per field. Out of scope for this bulk endpoint unless they later become a bottleneck.

#### Alternatives Considered

- **Only raise timeouts / only enable async on the current GET**: Rejected — still one HTTP call (and one async task) per field; the stated problem is call volume.
- **Reuse CSV `*/upload/physicalfields`**: Rejected — that endpoint **persists** links onto catalog physical fields from a multipart CSV (overwrite mode). The observer needs **resolved objects in memory** before port-asset upload; physical fields may not even exist on Blindata yet.
- **Observer-side parallel GETs**: Rejected — still N calls, increases Blindata load, worse than sequential GETs for the API.
- **Dedicated start-job / poll-job API just for resolve**: Rejected — duplicates `settings/async`. The observer already polls that. Confirmed the house pattern is sync endpoint + optional wrap.
- **Unlimited bulk POST (no max size)**: Rejected — cap is 500 as an API argument.
- **Configurable batch size in application settings**: Rejected for now — not needed; 500 is the fixed argument/ceiling.
- **Bare JSON array as response**: Rejected — must be a resource class so the contract can grow.
- **One bulk call for the whole data product / port**: Rejected — batching unit stays per physical entity, matching today.
- **Change `allpaths` or glossary graph search to “bulk resolve”**: Rejected — wrong endpoint; `allpaths` enumerates possible paths for the UI picker. This work is parse-and-resolve of already-known path strings.

## Risk & Gap Analysis

### Risk & Gap Analysis

#### Requirement Ambiguities

All previously open ambiguities are **resolved** (see Stakeholder Clarifications). None remain blocking for API REASONS Canvas.

#### Edge Cases

- **Empty batch / no semantic paths**: Enrichment should no-op without calling Blindata (same as fields with no link today).
- **Duplicate paths** in one entity: Resolve once per unique (path, namespace) when batched together; mapping back must still populate every physical field. Cross-entity duplicates are not batched together (unit is per entity).
- **Mixed namespaces in one batch**: Unusual for one observer entity (`s-base` is shared) but valid if the bulk API is general-purpose. Must allow per-item default namespace.
- **Oversized request (>500)**: API must reject the whole request clearly; must not process a prefix of the list and omit the rest without saying so. Observer is responsible for chunking.
- **Entity with more than 500 linked fields**: Rare; observer issues a second (third, …) bulk call for the same entity until all fields are attempted.
- **All items in a batch fail to resolve**: Batch HTTP should still succeed; observer logs a warning per path and leaves fields unmodified (today’s `[#90]` intent).
- **Malformed request (missing path, missing namespace, oversize)**: These are request errors (4xx for the whole call), distinct from “this path does not exist in the glossary.”
- **Very expensive single paths**: A batch of 500 deep/prefixed paths can still be slow even after HTTP reduction (repeat attribute/parent walks). Async+poll and/or lookup caching matter here.
- **Validator vs live upload**: Validator should not write catalog data (it already doesn’t); bulk resolve is read-only so dry-run is safe, but validator latency will still depend on Blindata and `enableAsync`.

#### Technical Risks

- **HTTP timeout even after batching**: One bulk call of up to 500 sequential resolves may exceed gateway/client timeouts if `enableAsync` is false. Mitigation: same as quality/port-assets — honor async wrap; 500 is the agreed ceiling.
- **Observer `withErrorHandling` abort**: A transport/5xx failure on a bulk call still fails the whole entity’s remaining enrichment. Mitigation: treat bulk like quality upload (propagate 5xx); keep per-item glossary failures inside the wrapper payload so they no longer abort siblings.
- **Poll exhaustion**: Async poll has a finite attempt budget with backoff. A 500-item batch that runs longer than that budget fails as “polling exceeded maximum attempts.” Mitigation: request-scoped lookup reuse if profiling shows cost; do not raise the cap.
- **N+1 remaining on observer for namespaces/categories**: Field resolve was the loud N; concept lookups are still per distinct name, still per entity. Accept as boundary unless products declare huge numbers of distinct concepts.
- **Behavioral change for first-failure**: Products that previously stopped linking after the first bad path may start linking later fields on the same entity. That is intended with per-item outcomes; tests and docs should call it out.
- **Hidden utils controller**: Resolve-field lives on a `@Hidden` controller. Bulk should follow the same visibility/security as GET so it is not accidentally exposed more widely than today.
- **Cross-repo sequencing**: Observer cannot switch to bulk until the API is deployed. **API REASONS/implementation first**, then observer. GET remains so older observers keep working.

#### Acceptance Criteria Coverage

| AC# | Description | Addressable? | Gaps/Notes |
|-----|-------------|--------------|------------|
| 1 | When a data product has hundreds of semantically linked fields, Blindata is not called once per field | Yes | Per physical entity: one (or few) bulk calls instead of one GET per field. |
| 2 | A maximum of 500 paths can be resolved in one request, as an API argument | Yes | Not configurable in yml. API rejects oversize. |
| 3 | Remaining fields beyond 500 on the same entity are still resolved (further batches), not dropped | Yes | |
| 4 | Reuse observer async request + poll rather than inventing a new job API | Yes | Verified: sync endpoint + `enableAsync` wrap, same as port-assets / quality import-objects / policy upload / data-product patch. |
| 5 | Implement on Blindata API and on the observer | Yes | **This REASONS pass: API only.** Observer follows after. |
| 6 | Existing single-path resolve behavior and clients keep working | Yes | GET `*/resolvefield` stays. |
| 7 | Unresolvable paths remain non-blocking for other fields; result is an extensible wrapper class | Yes | Per-item outcomes inside a resource class, not a raw list. |
| 8 | Batching unit remains per physical entity | Yes | Same as `enrichWithSemanticContext` today. |
