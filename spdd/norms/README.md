# Code norms (`agentspecs/norms`)

This package holds **architectural and implementation norms** for Blindata backend work. Use it when generating or implementing features so new code matches agreed patterns.

**Primary consumer:** the **`/spdd-reasons-canvas`** command (REASONS-Canvas **N — Norms** section). Reference this README (and the norm files it points to) so generated prompts include concrete, checkable standards—not generic placeholders.

---

## How to use with `/spdd-reasons-canvas`

When running the command, include this package in the input so the agent loads norms before filling **## Norms**:

```
/spdd-reasons-canvas @agentspecs/norms/README.md <your business context or @requirement file>
```

**Agent workflow (Norms stage):**

1. **Read** this README and every norm file listed under [Norm files](#norm-files) that applies to the work (see [Which norm file applies?](#which-norm-file-applies)).
2. **Infer scope** from business context: CRUD-only, use-case/orchestration, or both.
3. **Populate `## Norms`** in the output prompt with:
   - Cross-cutting items from [Cross-cutting code norms](#cross-cutting-code-norms) (always relevant for backend features).
   - Norms from the applicable dedicated files (summarized below; cite file names so implementers can open the full guide).
4. **Do not** paste entire norm documents into the canvas—distill **actionable, verifiable** bullets (naming, packages, annotations, boundaries, testing).
5. Keep **Safeguards** separate: norms = *how to build*; safeguards = *boundaries and non-negotiables* from the requirement.

**Output shape** (align with the command template):

```markdown
## Norms
1. Annotation standards: …
2. Dependency injection: …
3. Exception handling: …
4. Data validation: …
5. Logging: …
6. Documentation standards: …
7. [Feature-specific] Use-case boundaries: … (when applicable)
8. [Feature-specific] CRUD template-method hooks: … (when applicable)
```

---

## Which norm file applies?

| You are building… | Read |
|-------------------|------|
| REST endpoint with orchestration, ports, commands, presenters (non-trivial workflow) | [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md) |
| Entity CRUD with optional DTO mapping and/or filtered list APIs | [`GENERIC-CRUD-GUIDELINES.md`](./GENERIC-CRUD-GUIDELINES.md) |
| Both (e.g. use case that delegates persistence to core CRUD services) | **Both** — use cases call core services **via outbound ports**, not directly from the use case class |

---

## Cross-cutting code norms

These apply to most backend work surfaced through REASONS-Canvas. Feature-specific norms in the dedicated files **extend** this list; they do not replace it.

### Layering and responsibilities

- **Thin HTTP adapter:** Controllers map HTTP only; no business rules. Services (use-case service or CRUD service) own orchestration and mapping at the application boundary.
- **Domain inside the use case package:** Commands and presenters use **entities, value types, or small domain records**—never REST `*Res` types inside `...services.usecases.*`.
- **Core vs use case:** Reusable CRUD/query logic lives in `...services.core.*` (or equivalent). Use cases reach core only through **outbound port implementations**, not by injecting core services into the use case class.
- **Pick the shallowest CRUD base** that fits (`GenericCrudServiceImpl` → mapped → filtered); do not add filter/mapping layers without need.

### Spring and dependency injection

- **`@RestController`** on `*UseCaseController` with `@RequestMapping`, `produces = APPLICATION_JSON_VALUE`.
- **`@Service`** on `*UseCasesService` and concrete CRUD service subclasses.
- **`@Component`** on the use-case **factory only** in that slice; outbound port impls are **plain Java** (`new` in the factory).
- **No Spring stereotypes** on use case classes or `*OutboundPortImpl`.
- **Constructor injection** for factories and services; use cases receive dependencies via factory constructor wiring.

### REST resources and OpenAPI

- Request/response DTOs under `rest.v2.resources...` with naming `*CommandRes`, `*ResultRes` (and related `*Res`).
- Document controllers with `@Tag`, `@Operation`, `@ApiResponses`, `@Parameter`; `@Schema` on DTO fields where useful; `@Hidden` for non-public endpoints.
- **Mappers** (`rest.v2.resources` / MapStruct) convert `*Res` ↔ entities in the **use cases service** (or CRUD `toRes` / `toEntity`), not in controllers or use case classes.

### Exceptions and API errors

- Throw domain/API exceptions (e.g. `BadRequestException`) for invalid state from use cases and validation hooks.
- **Business exceptions:** extend `RuntimeException` or a shared `BusinessException` base; include **`errorCode`** and **`errorMessage`**; provide multiple constructors; classify by business domain.
- **Unified HTTP errors:** `GlobalExceptionHandler` (`@RestControllerAdvice`) maps business, validation, and system exceptions to a consistent **`ErrorResponse`** DTO; do not leak sensitive internals in messages.
- CRUD **`validate` / `reconcile` / hooks** throw exceptions that the global handler maps to stable HTTP status codes.

### Data validation

- **CRUD:** `validate(T)` for invariants and required fields; `reconcile(T)` for loading associations and fixing graphs before save.
- **Use cases:** validate the command early in `execute()`; fail fast with clear exceptions.
- Path parameters that must override body fields: override the `*Resource` method on the CRUD service, set the field, then call `super`.

### Transactions

- **Use cases:** use shared `TransactionalOutboundPort` (`doInTransaction`, `doInTransactionWithResults`) for atomic work—do not reimplement transaction plumbing per use case.
- **CRUD:** template methods in `GenericCrudServiceImpl` use `TransactionTemplate` for writes; mapped reads may use a transactional wrapper when mapping touches lazy associations.

### Logging

- SLF4J with class-level logger; log at **info/warn** for business-significant steps and failures; **never** log secrets or credentials.
- Prefer structured context (identifiers, operation phase) consistent with surrounding modules.

### Testing

- **Use cases:** add or extend `*UseCaseControllerIT` for full-stack verification of new routes.
- **CRUD / ports:** unit-test complex `validate`/`reconcile`/spec builders or port adapters where payoff is high.
- Trace tests back to requirements when generated from a spec (comment referencing scenario or requirement id).

### Documentation in code

- Keep public API documented via OpenAPI annotations; avoid redundant Javadoc on obvious getters/setters.
- Package-private use case classes and port types unless wider visibility is required.

---

## Norm files

### [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md)

**Purpose:** Hexagonal-style use case flow from HTTP through application orchestration to outbound adapters.

**When to apply:** New or changed behavior that is more than CRUD—initialization flows, multi-step orchestration, validation + persistence + side effects (notifications, etc.).

**Flow (must appear in Structure / Operations when relevant):**

```
HTTP → *UseCaseController → *UseCasesService → *Factory → UseCase
  → OutboundPort(s) → *OutboundPortImpl → core services / clients
```

**Hard boundaries (non-negotiable in generated prompts):**

| Layer | Rule |
|-------|------|
| `rest.v2.resources` `*Res` | Only controller + use cases service; **never** in `...services.usecases.*` |
| Command | Domain types only (entities, UUIDs, enums, small domain records) |
| Presenter | Domain result types only; service maps to `*ResultRes` |
| Use case class | Implements `UseCase`; package-private; **no** Spring annotations; **no** `@Autowired` |
| Factory | Sole `@Component` in the slice; `build...(Command, Presenter)` returns `UseCase`; wires ports with `new` |
| `*OutboundPortImpl` | Plain Java; collaborators via constructor from factory |

**Package layout per use case** (`...services.usecases.<name>`):

| Artifact | Naming / role |
|----------|----------------|
| `<Name>.java` | Use case; `execute()` orchestration |
| `<Name>Command.java` | Input record |
| `<Name>Presenter.java` | Output boundary interface |
| `<Name>Factory.java` | Composition root |
| `*OutboundPort.java` / `*OutboundPortImpl.java` | Port + adapter |

**Shared utilities:** `utils.usecases.UseCase`, `TransactionalOutboundPort` (implemented by `DefaultTransactionalOutboundPortImpl`).

**Checklist for new use cases** (use in Operations section as task order):

1. Command + presenter (domain only) in `usecases.<name>`.
2. Outbound port interfaces + plain impls.
3. `@Component` factory with `new` for impls.
4. `*UseCasesService` method: `*CommandRes` → command, factory + `execute()`, presenter → `*ResultRes`.
5. `*CommandRes` / `*ResultRes` under `rest.v2.resources...usecases...`.
6. `*UseCaseController` endpoint + OpenAPI.
7. `*UseCaseControllerIT`.

**Full detail:** [USE_CASE_IMPLEMENTATION.md](./USE_CASE_IMPLEMENTATION.md)

---

### [`GENERIC-CRUD-GUIDELINES.md`](./GENERIC-CRUD-GUIDELINES.md)

**Purpose:** Template-method CRUD for JPA entities with optional API mapping and specification-based filtering.

**When to apply:** Standard create/read/update/delete (and filtered lists) on aggregates exposed as REST resources.

**Class hierarchy (choose one):**

| Layer | Types | Use when |
|-------|--------|----------|
| Entity CRUD | `GenericCrudService<T, ID>` / `GenericCrudServiceImpl` | Persistence only, no separate API type `R` |
| + mapping | `GenericMappedCrudService<R, T, ID>` / `...Impl` | Expose `*Resource` methods (`findOneResource`, `createResource`, …) |
| + filtering | `GenericMappedAndFilteredCrudService<F, R, T, ID>` / `...Impl` | List endpoints with filter object `F` → `Specification<T>` |

**Must implement (subclass):**

- `getRepository()` — CRUD + paging/sort + `JpaSpecificationExecutor` when filtered.
- `validate(T)` — invariants before create/overwrite.
- `reconcile(T)` — associations and defaults before save.
- Mapped: `toRes(T)`, `toEntity(R)`.
- Filtered: `getSpecFromFilters(F)` — compose specs (often AND).

**Template method order (do not reorder without overriding intentionally):**

- **Create:** `validate` → `reconcile` → `beforeCreation` → `save` → `afterCreation` → `afterCreationCommit`
- **Overwrite:** `validate` → existence → `reconcile` → `beforeOverwrite` → `save` → `afterOverWrite` → `afterOverwriteCommit`
- **Delete:** existence → `beforeDelete` → `deleteById` → `afterDelete`

**Hooks (override when needed):** `afterFindOne`, `beforeCreation`, `afterCreation`, `beforeOverwrite`, `afterOverWrite`, `beforeDelete`, `afterDelete`, `afterDeleteCommit`, etc.

**Controllers:** Prefer `*Resource` methods; path id overrides body in overridden `*Resource` methods.

**Design tips for Norms / Safeguards:**

- Keep `validate` strict; put association loading in `reconcile`.
- Use hooks for DB-dependent rules (uniqueness excluding self) in the same transaction as `save`.
- If `findAll(Pageable)` is not allowed for an aggregate, override and throw explicit not-supported.
- Repository must support specifications when using the filtered base class.

**Full detail:** [GENERIC-CRUD-GUIDELINES.md](./GENERIC-CRUD-GUIDELINES.md)

---

## Combining norms in one feature

Typical pattern when a canvas covers both CRUD and a workflow:

1. **Structure:** CRUD service in core; use case package with ports whose impls delegate to `*CrudService` / `*Service`.
2. **Operations:** CRUD tasks follow hook and `*Resource` naming; use case tasks follow the 7-step checklist.
3. **Norms:** Merge cross-cutting list + CRUD template-method bullets + use-case boundary table.
4. **Safeguards:** Requirement-specific constraints (performance, security, idempotency)—not duplicated from norm files unless they encode a global rule.

---

## Package index

| File | Topic |
|------|--------|
| [`README.md`](./README.md) | This index, cross-cutting norms, SPDD integration |
| [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md) | Use case hexagon, REST adapter, ports, factory, testing |
| [`GENERIC-CRUD-GUIDELINES.md`](./GENERIC-CRUD-GUIDELINES.md) | Generic CRUD template method, mapping, filtering, transactions |

When adding a new norm file, document it in [Norm files](#norm-files) and extend [Which norm file applies?](#which-norm-file-applies).