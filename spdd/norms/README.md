# SPDD Norms — Blindata Observer Adapter

This folder holds the **engineering norms** that Blindata Observer Adapter inherits from its backend service template. They describe *how* to implement features in this codebase, not *what* to build (that lives in `spdd/prompt/` and `spdd/analysis/`).

## Ownership

The norms are **owned by the template**, not by this service. A template update renders them again, so a local edit turns into a merge conflict on the next update.

- Do not edit these files in the service. Change a norm in the template repository and roll it out with a template update.
- A rule that only this service needs is not a norm. Feature-specific rules (exact error messages, API contracts, permissions for one endpoint) belong in the prompt's **Safeguards** section.

After a template update changes a norm:

1. Adjust affected `spdd/prompt/*.md` **N — Norms** references if needed
2. Use `/spdd-sync` if generated code already exists and the prompt must reflect the new standard

## SPDD artifacts

| Folder | Purpose |
|--------|---------|
| `spdd/analysis/` | Problem analysis, proposals, and testable specs (merged) |
| `spdd/prompt/` | Implementation-ready feature prompts (Requirements, Operations, Norms, Safeguards) |
| `spdd/norms/` | Reusable engineering conventions (template-owned) |

## Who should read this

Agents and developers running **SPDD** (Structured Prompt-Driven Development) commands should load these norms when:

- Drafting or updating a REASONS Canvas prompt (**N — Norms** section)
- Generating or syncing implementation code from a prompt
- Reviewing whether new code matches existing patterns

Treat this directory as the **canonical reference** for Blindata Observer Adapter conventions referenced by prompts. Prefer linking to a norm file here over duplicating long explanations inside every prompt.

## Architectural principles

These apply to every feature. The dedicated norm files **extend** them; they never contradict them.

- **Dependencies point inward.** HTTP adapter → application service → use case → ports; adapters, core services and clients sit behind the ports. The use case never depends on HTTP, the framework or external systems, and reaches persistence only through ports.
- **JPA entities are the domain model.** There is no separate persistence model, so domain types carry JPA annotations. Inside a use case an entity is treated as a **plain object (POJO)**: the use case reads and sets its fields and passes it to ports, but never relies on persistence behaviour (lazy loading, dirty checking, cascades, flushes, an `EntityManager` or a repository). Anything that needs the database goes through a port.
- **One model per boundary.** API DTOs (`*Res`) stop at the HTTP adapter (`rest.v<N>`), which maps them to and from domain types. External-system models stop at their client. Only domain types cross layers. **The one exception** is generic CRUD: a mapped CRUD service maps its entity to the *latest* API version's `*Res` (see [`GENERIC-CRUD-GUIDELINES.md`](./GENERIC-CRUD-GUIDELINES.md)).
- **One error model.** The service has a single exception hierarchy and a single error contract at the HTTP edge. Use cases never throw the service exceptions: they hand every failure to their presenter, and the template's `UseCasePresenter` turns it into a service exception (see [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md)). Errors from external systems are translated where they enter, never passed through. Messages are descriptive and never contain secrets.
- **Observability.** A correlation id flows from the inbound request through the logs to outbound calls. Log business-significant steps and failures, with identifiers; never log secrets, headers or full bodies.

## Norms that ship with a module

Some norm files describe the code of one template module and are rendered only with that module (the **Module** column of the [Norm index](#norm-index)). The modules rendered into this service are listed under `modules` in `template.yaml`.

If a module's norm file is missing, that module is not part of this service: skip the norm, do not use the types it describes, and do not recreate them in the service. A rule in another norm file that names a module applies only when that module is rendered.

## Which norm file applies?

| You are building… | Read |
|-------------------|------|
| Any new or touched Java feature code | [Architectural principles](#architectural-principles), [`CODE_STRUCTURE.md`](./CODE_STRUCTURE.md), [`PACKAGE_STRUCTURE.md`](./PACKAGE_STRUCTURE.md), [`TESTING.md`](./TESTING.md) |
| REST endpoint with orchestration, ports, commands, presenters (non-trivial workflow) | [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md) (module `usecases`) |
| Entity CRUD with optional DTO mapping and/or filtered list APIs | [`GENERIC-CRUD-GUIDELINES.md`](./GENERIC-CRUD-GUIDELINES.md) (module `persistence`) |
| Both (e.g. use case that delegates persistence to core CRUD services) | **Both** — use cases call core services **via outbound ports**, not directly from the use case class |
| A call to, or a payload from, a system the service does not own | [`INTEGRATIONS.md`](./INTEGRATIONS.md) |
| Work that runs in the background (async calls, fan-out, heavy computation) | [`ASYNC_EXECUTION.md`](./ASYNC_EXECUTION.md) |
| New or changed configuration | [`CONFIGURATION.md`](./CONFIGURATION.md) |
| New or changed public endpoint, or a new API version | [`API_DESIGN.md`](./API_DESIGN.md) |
| A new or touched module with several units of one kind, or a unit whose steps outgrow one class (use cases of an aggregate, a job, a client, a parser) | [`PROGRESSIVE_DISCOVERY.md`](./PROGRESSIVE_DISCOVERY.md) |

## Norm index

| File | Module | Topic |
|------|--------|--------|
| [CODE_STRUCTURE.md](./CODE_STRUCTURE.md) | core | Composed method, method-body line limit (not file), no `*Utils` / `*Helper` extracts |
| [USE_CASE_IMPLEMENTATION.md](./USE_CASE_IMPLEMENTATION.md) | `usecases` | Hexagonal use-case flow: controller → use-cases service → factory → ports/adapters; failures through the presenter |
| [GENERIC-CRUD-GUIDELINES.md](./GENERIC-CRUD-GUIDELINES.md) | `persistence` | Template-method CRUD (`GenericCrud*` hierarchy), mapping to the latest API version, filtering, hooks, AOP and events |
| [PACKAGE_STRUCTURE.md](./PACKAGE_STRUCTURE.md) | core | Package by aggregate, versioned HTTP tree, version adapters, where each kind of class goes |
| [INTEGRATIONS.md](./INTEGRATIONS.md) | core | Clients per external system, anti-corruption, error translation, resilience |
| [TESTING.md](./TESTING.md) | core | `*Test` (unit) vs `*IT` (running application), tests per layer, traceability, isolation |
| [ASYNC_EXECUTION.md](./ASYNC_EXECUTION.md) | core | `AsyncExecutorService` (virtual threads, I/O) vs `PlatformThreadExecutorService` (platform threads, CPU or pinning), concurrency caps, executor configuration |
| [CONFIGURATION.md](./CONFIGURATION.md) | core | Typed, validated-at-startup configuration; behaviour selected by configuration; safe defaults |
| [API_DESIGN.md](./API_DESIGN.md) | core | Versioning, version adapters, breaking changes, OpenAPI contract, uniform responses |
| [PROGRESSIVE_DISCOVERY.md](./PROGRESSIVE_DISCOVERY.md) | core | Navigation levels of a module (index README → unit README → entry method → step class → rules → ports/adapters), one skeleton per kind of unit, earned sharing, structure test and README guard |
