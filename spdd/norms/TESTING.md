# Norm: Testing

What to test at which layer, and how tests are named and run.

---

## Two kinds of test

| Kind | Suffix | Scope | Runs in |
| --- | --- | --- | --- |
| **Unit test** | `*Test` | One class or one use case slice, with collaborators replaced by fakes or mocks. **No running application.** | the unit-test phase |
| **Integration test** | `*IT` | The **whole application running** (container started, real wiring), driven through its public API with only external systems replaced. | the integration-test phase |

The suffix is a contract: a test that starts the application is an `*IT`, whatever it asserts.

## By layer

- **Use cases:** unit tests with fake ports. They cover every business rule and outcome, including the failures handed to the presenter, and need no framework.
- **Adapters, clients, mappers, validators:** unit tests at their seam, where the logic is non-trivial.
- **Endpoints:** at least one integration test per endpoint (e.g. `*UseCaseControllerIT`), covering the happy path and the main error contracts.
- **CRUD services:** unit-test complex `validate` / `reconcile` / specification logic; cover persistence behaviour in integration tests.

## Traceability

- Each scenario of a spec (Gherkin in `spdd/prompt/`) maps to **one named test**, whose documentation quotes the scenario.
- A test class references the requirement it was generated from.

## Isolation

- Tests never depend on **live external systems**, **developer-local configuration**, or **execution order**.
- External systems are replaced in **shared test configuration**, not test by test. All integration tests use the same configuration, so they share one running application instead of starting a new one per test class. Test doubles are reset between tests, not redefined.
- Test configuration lives with the tests; local overrides are scoped so they never apply to test runs.
- Each test sets up the state it needs and leaves no shared state behind.

---

**Spring:** unit tests are plain JUnit with Mockito. Integration tests use `@SpringBootTest` and replace external systems with beans from one shared `@TestConfiguration` (for example a stubbed `RestUtils` bean per external system, see [`INTEGRATIONS.md`](./INTEGRATIONS.md)), imported the same way by every `*IT`, so Spring's context cache keeps a single application context for the whole suite. Do not use `@MockBean`, `@MockitoBean`, `@DirtiesContext` or per-class property overrides: each distinct combination starts a new context. Surefire runs `*Test`; Failsafe runs `*IT`.
