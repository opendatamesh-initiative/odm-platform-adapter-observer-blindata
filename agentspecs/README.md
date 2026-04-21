# AgentSpecs

This directory holds **specifications and proposals** used by agents (and humans) to implement features in the codebase. It is the single source of truth for *what* to build and *how* it should behave.

## High-level structure

- **`changes/`** — Work in progress. Each item here is a change or feature currently being designed or implemented.
- **`specs/`** — Consolidated specifications. Completed or released specs live here, organized by domain and feature.
- **`guidelines/`** — Architectural guidelines (how to structure layers, use cases, and related patterns). Read these when implementing so code matches the intended architecture.

When reading the codebase, check **`changes/`** for active work, **`specs/`** for stable, agreed behavior, and **`guidelines/`** for architectural rules.

## Organization

### Under `changes/`

Each change has its **own folder** (e.g. `changes/universal_search_special_characters/`). Inside that folder there must be:

| File | Purpose |
|------|--------|
| **`proposal.md`** | Context, goal, scope, and proposed technical direction. Use it as the development guide for code logic. |
| **`specs.md`** | Concrete, testable requirements (often in Gherkin). Use it to write integration/acceptance tests and to verify behavior. |

Naming: use a short, snake_case folder name that describes the change (e.g. `universal_search_special_characters`).

### Under `specs/`

Consolidated specs are organized **by domain, then by feature**:

```
specs/
  <domain>/           e.g. security, search, catalog
    <feature>/        e.g. auth, universal_search
      spec.md         single consolidated spec file for that feature
```

Only **spec** content is kept here (no `proposal.md`). The file is named **`spec.md`** (singular). Copy or merge from the `specs.md` in `changes/` when promoting a change.

### Under `guidelines/`

The **`guidelines/`** package holds **architectural guidelines** for this codebase (markdown documents describing layers, use-case structure, and similar conventions). Consult them alongside `proposal.md` when implementing so new code follows the agreed architecture.

**Entry point — what each guide contains:**

- **`USE_CASE_IMPLEMENTATION.md`** — How the use-case flow is wired from REST through controllers, use-case services, factories, domain commands, presenters, and outbound ports (and how adapters sit behind those ports).
- **`GENERIC-CRUD-GUIDELINES.md`** — How to implement CRUD with the template-method `GenericCrud*` service hierarchy (JPA entities, optional DTO mapping, optional specification-based filtering).

---

## How to work (agent guidelines)

### 1. For each change under `changes/`

- Ensure the change folder contains both **`proposal.md`** and **`specs.md`**.
- If either is missing, ask for it before implementing.

### 2. Implementing code — follow this order

Follow these steps **in this exact order**:

1. **Read** `proposal.md` and `specs.md` in the change folder.
2. **Implement integration/acceptance tests** following `specs.md` (e.g. Gherkin scenarios → test cases).  
   Add a **comment at the top of each test method** that references the corresponding requirement or scenario in the spec (e.g. section title, requirement ID, or Gherkin block), so tests can be traced back to the specification.
3. **Verify** that those tests **fail** (expected behavior not yet implemented).
4. **Implement** the code logic so the tests pass. Use `proposal.md` as the development guide.
5. **Verify** that the tests **pass**.

**Loop steps 4–5** until all tests pass.

### 3. After implementation

- **Review** the `specs.md` under `changes/` for **coherence** with the code you modified: update the spec if behavior or edge cases changed, or add missing scenarios.

### 4. When the change is finished (consolidation)

- **Move** the change into `specs/` by:
  - **Copying only the spec file(s)** (i.e. the content of `specs.md` from the change).
  - Placing it under **`specs/<domain>/<feature>/spec.md`**, following the structure: **domains first**, then **features**.
- Optionally remove or archive the folder under `changes/` once the spec is consolidated.

---

## Summary

| Location | Meaning | Contents |
|----------|--------|----------|
| **`changes/<name>/`** | Current WIP | `proposal.md` + `specs.md` |
| **`specs/<domain>/<feature>/`** | Consolidated | `spec.md` only (from specs) |
| **`guidelines/`** | Architecture | Markdown docs (e.g. use-case patterns) |

Workflow: **read proposal + specs → write ITs from specs → see tests fail → implement from proposal → see tests pass → review spec coherence → copy spec into `specs/` by domain/feature.**
