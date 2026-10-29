# Norm: API design and versioning

How the public HTTP API is shaped and how it evolves.

---

## Versioning

- **The major version appears in the path and in the package:** `/api/v<N>/…` is served by `rest.v<N>`.
- **A new major version only for breaking changes:** removing or renaming an endpoint, field or enum value; changing a field's type or meaning; making an optional input required; changing status codes or the error contract. Additive changes (new endpoints, new optional fields, new enum values clients are told to tolerate) stay in the current version.
- **Versions coexist.** A previous version keeps its own controllers and DTOs until it is retired; its DTOs are not rewritten in terms of the new version's.
  - **Use-case endpoints:** the previous version's controllers map their own DTOs to the same commands and call the same `*UseCasesService`, which is version-independent.
  - **Generic CRUD endpoints:** a mapped CRUD service maps only to the **latest** version. When a new version is introduced, the previous version's CRUD controllers are re-pointed to a temporary **version adapter** (`rest.v<N>.adapters`), which maps that version's DTOs to and from entities and calls the CRUD service's entity-level methods (see [`GENERIC-CRUD-GUIDELINES.md`](./GENERIC-CRUD-GUIDELINES.md)). The adapter is deleted with the version.
- **Deprecate before removing.** A version or endpoint is marked deprecated in the API documentation before it is removed.

## Contract

- **Every public endpoint is documented in OpenAPI** (operation, parameters, request and response schemas, every status code it can return). Non-public endpoints are hidden from the documentation.
- **Uniform responses.** All endpoints share one error contract and the same conventions for paging, identifiers and timestamps.
- **The API speaks DTOs, never entities.** `*Res` types are the contract; persistence and domain types never cross the HTTP boundary.
- **Thin controllers.** A controller maps the request DTO to domain input with its version's mapper, makes one call (a `*UseCasesService` method, a CRUD service's `*Resource` method in the latest version, or a version adapter), and maps the result back. It holds no business rules.

## Contracts owned by others

- When an endpoint receives a payload defined by another system, that payload follows its owner's contract, not this norm. It is mapped at the edge (see [`INTEGRATIONS.md`](./INTEGRATIONS.md)).

---

**Spring:** controllers are `@RestController`; OpenAPI uses the `io.swagger.v3.oas.annotations` annotations (springdoc).
