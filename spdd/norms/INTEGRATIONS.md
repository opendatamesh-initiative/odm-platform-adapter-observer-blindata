# Norm: Integrations with external systems

How the service talks to systems it does not own — outbound calls and inbound payloads defined by someone else.

---

## Clients (outbound)

- **One client per external system.** It is the only code that knows that system's protocol, paths, authentication and payload format.
- **Anti-corruption at the client.** External payloads are mapped to **domain types inside the client**. External models never travel past it.
- **Errors are translated where they enter.** Transport and remote failures become the service's own exceptions inside the client, with a message that says which system failed and why — never secrets, tokens or headers.
- **Credentials belong to the integration.** Each external system has its own credentials. They are never shared across systems and never forwarded from an inbound request.
- **Clients hold mechanics, not policy.** What to do when a remote system is unavailable (fail, degrade, report) is a use-case decision exposed through a port.

## Inbound payloads defined by another system

- The edge accepts the third-party payload as it is defined by its owner, and a **dedicated mapper** turns it into domain types before it reaches a use case.
- The payload shape is never reused as a DTO of the service's own API.

## Resilience

- **Every remote call has a timeout.**
- **Retry only what is safe to repeat** (idempotent operations, or failures that guarantee the request was not processed), with a **bounded** number of attempts and back-off.
- **Respect the remote system's limits** (rate limits, quotas) instead of working around them.
- Retry and back-off are **configuration**, not constants (see [`CONFIGURATION.md`](./CONFIGURATION.md)).

## Testability

- Clients are tested at the transport seam, against recorded or stubbed responses.
- Use cases are tested with fake ports and never see a client (see [`TESTING.md`](./TESTING.md)).

---

**Template support (module `rest-client`):** outbound HTTP goes through `RestUtils` (`utils.client`), built with `RestUtilsFactory` (`getRestUtils`, `getAuthenticatedRestUtils`, `getAsyncRestUtils`, `getAuthenticatedAsyncRestUtils`). Expose one qualified `RestUtils` bean per external system so tests can replace it. Remote errors arrive as `ClientException` / `ClientResourceMappingException` and are translated inside the client.
