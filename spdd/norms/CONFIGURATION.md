# Norm: Configuration

How the service is configured and how configuration is allowed to change behaviour.

---

## Principles

- **Typed and grouped by concern.** Configuration is read through typed interfaces or classes, one group per concern (an external system, a feature, a policy). Code never reads raw keys.
- **Validated at startup.** Invalid or incompatible configuration stops the service at boot with a message that names the offending key. It never fails on first use.
- **Configuration selects behaviour; code does not branch on environments.** Variation between deployments is expressed as configuration that selects a strategy (an interface type, an auth mode, a rule set) — never as `if (env == …)`.
- **Packaged defaults are safe and minimal.** Defaults are the values that are correct everywhere. Environment-specific values are never packaged.
- **No packaged collections.** Lists and maps that a deployment is expected to define (profiles, rules, mappings) are never shipped in the packaged configuration: merging configuration sources combines them unpredictably. Ship them as documented reference files instead.
- **Externalised.** Environment-specific values and credentials come from outside the artifact (environment variables, mounted files).

---

**Spring:** typed groups are `@ConfigurationProperties` classes; startup validation is a `@Validated` binding or a validator that runs on application start.
