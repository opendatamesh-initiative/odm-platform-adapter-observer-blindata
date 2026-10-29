# SPDD Analysis: Data Product Domain Team Inheritance in the Blindata Observer

## Original Business Requirement

BDMD-5561 Supporting Team ACL on Data Product Domain → Data Product

In Blindata, a Data Product belongs to a Data Product Domain. Both can be linked to a Team, which is used for team-based ACL. The data product's team should follow its domain's team, unless the data product deliberately uses another team.

Scenarios (team ACL enabled):
1. The domain has a team and the data product has none: the data product inherits the domain's team.
2. The domain has a team and the data product has a different team: the data product's ACL is based on its own team, overriding the domain's.
3. The data product has a team and the domain has none: the data product's ACL is based on its own team.

The domain's team is copied onto the data product, so the data product carries it directly. A data product with the same team as its domain is considered aligned and follows the domain from then on. No change should be applied to the API.

UI:
- When a user changes a domain's team, a wizard lets them align the domain's data products to the new team. The user can skip the data products they don't want to align.
- When the domain's team is removed, aligning a data product makes it public.
- The same wizard can be opened at any time from the detail page of any domain, with or without a team, to align data products without changing the domain's team. It also opens after a domain is created with a team while data products already use its name.
- In the wizard, data products whose team differs from the domain's previous team are listed but not pre-selected. A data product the user is not allowed to update (for example because of stewardship) is reported as failed after the run.
- When a user creates a data product, from the create modal or from blueprint instantiation, the domain's team, if any, is pre-selected. The user can change it to any other team or make the data product public.
- If the user doesn't have write permission on the domain's team, it is still pre-selected and the user is warned.
- Changing the domain of an existing data product does not change its team.

ODM observer:
- When the observer creates or updates a data product, it fetches the domain's team and sets it on the data product. This behaviour is configurable with three modes:
  - **off** (default): the domain's team is not applied;
  - **if-missing**: the domain's team is applied only to data products with no team;
  - **always**: the domain's team is applied on every upload.
- The observer never clears a data product's team.
- Whether a domain that cannot be found blocks publication is configurable. By default it does not.

Constraints:
- No change to the API.
- Existing data is not migrated.
- History of team changes: whichever option needs the least work.

Out of scope for now:
- all other parent → child pairs (namespace → concept, policy suite → policy, system → physical entity, quality suite → quality check);
- governance read ACL;
- permission rules for creating, moving or deleting under a parent.

**This analysis covers only the observer part**: the domain team lookup, the three modes (off, if-missing, always; default off), never clearing a team, the configurable blocking on a missing domain, and preserving the stored team across both data product upload use cases. The UI part lives in `blindata-ui`. Cross-repo analysis: `blindata-api/spdd/analysis/BDMD-5561-202610221608-[Analysis]-team-acl-parent-child-inheritance.md`.

## Domain Concept Identification

### Existing Concepts (from codebase)

- **`DATA_PRODUCT_UPLOAD` use case** (`DataProductUpload`): runs on `DATA_PRODUCT_CREATED`, `DATA_PRODUCT_VERSION_CREATED`, `DATA_PRODUCT_ACTIVITY_COMPLETED` and the V2 `DATA_PRODUCT_INITIALIZED` / `DATA_PRODUCT_VERSION_PUBLISHED`.
  - It maps the ODM `Info` to a Blindata data product. `info.domain` is copied as-is into `dataProduct.domain`.
  - It then creates the data product (POST) or updates it (PATCH).
  - The update path rebuilds the data product from the descriptor and copies only `uuid`, `version` and the old additional properties from the stored one.
- **`DATA_PRODUCT_VERSION_UPLOAD` use case** (`DataProductPortsAndAssetsUpload`): runs right after `DATA_PRODUCT_UPLOAD` on the same event, sequentially. It reads the stored data product, sets its ports and PATCHes it, then sets the version and PATCHes it again.
- **Blindata data product resource** (`BDDataProductRes`): the observer's view of a Blindata data product. It has **no team field**, so a team returned by Blindata is dropped on read (unknown properties are ignored) and never sent back.
- **Blindata data product PATCH** (API side): fills omitted fields from the stored data product, but only for fields declared on the data product itself. The team is inherited from a base class, so a PATCH without a team stores none.
- **Data Product Domain** (Blindata): has an optional team. `GET /api/v1/dataproductsdomains?name=` filters by exact name and only returns domains whose team the caller can read. The observer has no client for it.
- **Blindata write checks** (API side): creating or updating a data product requires WRITE on its new team and, on update, on its old team.
- **Data product configuration** (`BdDataProductConfig`, `blindata.dataProducts.*`): `@Value` properties with defaults, exposed to the use case through its Blindata outbound port.
- **Validator dry-run**: `BlindataValidatorService` runs the `DataProductUpload` dry-run. The dry-run port delegates reads to the live port and stubs writes. **Any use-case warning fails the policy evaluation.**

**Existing defect, confirmed by the code path (not reproduced at runtime):** every observer update of a data product clears its team in Blindata:
- the `DataProductUpload` update builds a PATCH with no team;
- the ports and assets upload reads the data product without its team and PATCHes it back twice;
- the API's PATCH stores an omitted team as none.

So a team set in Blindata on an observer-managed data product is lost at the next `DATA_PRODUCT_VERSION_CREATED` event.

### New Concepts Required

- **Data product team on the observer's resource**: the Blindata team of a data product, read with it and sent back on every create and update.
- **Domain team inheritance mode**: a configuration with three values:
  - **OFF** (default): no domain lookup; the stored team is preserved.
  - **IF_MISSING**: the domain's team is applied on create, and on update when the data product has no team.
  - **ALWAYS**: the domain's team is applied on every upload.
- **Data product domain lookup**: an exact-name read of the registered domain, returning its team.
- **Missing-domain blocking**: a boolean setting, `false` by default. It decides whether a domain that cannot be found is a validator warning, which blocks publication, or only an info log.

### Key Business Rules

- **R1 – Preserve the stored team**: in every mode, an update sends the team currently stored on the data product unless the mode replaces it. This applies to both upload use cases.
- **R2 – OFF changes nothing else**: no domain lookup is made, and the create path sends no team, as today.
- **R3 – IF_MISSING**: on create, and on update when the stored team is empty, the domain's team is applied. A stored team is never replaced.
- **R4 – ALWAYS**: the domain's team replaces the stored team on every upload.
- **R5 – Never clear**: a domain that is not found or has no team leaves the data product's team unchanged, in every mode.
- **R6 – Exact domain match**: the domain is the registered domain whose name equals the descriptor's `info.domain`.
- **R7 – Missing domain, configurable blocking**: when the mode is not OFF and the domain cannot be found (unregistered, or unreadable by the technical user):
  - with blocking enabled, the observer emits a validator warning, which fails the validator policy and blocks publication;
  - with blocking disabled (default), the observer only logs it as info, and publication proceeds.
  
  In both cases the team is unchanged. A domain with no team is normal and is always only logged as info.
- **R8 – Technical user permissions**: when the mode is not OFF, the observer's Blindata user must be able to READ every domain's team (for the lookup) and WRITE it (to create or update the data product with it). With ALWAYS, it also needs WRITE on the team being replaced.

## Strategic Approach

### Solution Direction

1. **Carry the team through the observer's data product resource.** Once the resource has a team field, it is read from Blindata and sent back on every PATCH. That alone fixes the ports and assets upload, which needs no other change. The `DataProductUpload` update path copies the stored team, like it already copies `uuid` and `version`.
2. **Add a domain lookup** to the Blindata data product client: an exact-name page-of-one read of `/api/v1/dataproductsdomains`, following the existing "find one by filter" client methods. It is exposed through the `DataProductUpload` Blindata outbound port, and the dry-run port delegates it.
3. **Apply the mode in `DataProductUpload`** as one named step between building the data product and writing it, on both the create and the update path.
4. **Configure the mode and the missing-domain blocking** in `BdDataProductConfig` under `blindata.dataProducts`, with OFF and `false` as defaults, and expose both through the outbound port.
5. **Document** the new property, the team mapping, the new validator code and the technical user's permissions.

Data flow: ODM event → `DataProductUpload` → descriptor info mapped → (update) stored team copied → mode applied (domain lookup when needed) → create or PATCH with team → `DataProductPortsAndAssetsUpload` → read with team → PATCH with team.

### Key Design Decisions

- **Where the mode lives**: a property under `blindata.dataProducts`, next to the existing data product settings, rather than a new use case in `activeUseCases`. The behaviour belongs to the data product upload and is not a separate upload.
  - → **Decided.**
- **Team preservation independent of the mode**: preservation is a bug fix and applies with OFF too.
  - → **Decided.** It is the only behaviour change with the default configuration.
- **No lookup when it can't change anything**: OFF never looks up; IF_MISSING skips the lookup when the data product already has a team.
  - → **Decided.** It avoids a Blindata call per upload for data products that already have a team.
- **Domain lookup on the data product client**, not a new client interface and bean: the existing `BdDataProductClient` bean already serves the data product use cases.
  - → **Decided.**
- **Mode values and YAML**: the values are `OFF`, `IF_MISSING` and `ALWAYS`. YAML 1.1 reads an unquoted `off` as the boolean `false`, so the property is read as text and parsed leniently: case-insensitive, `-` accepted for `_`, and `false` read as OFF. An unknown value stops the application at startup with a clear message.
  - → **Decided.**
- **Domain not found → configurable blocking**: with the mode not OFF, a domain that is unregistered or unreadable by the technical user leaves the product without the domain's team. A boolean property, `blindata.dataProducts.missingDomainBlocksPublication`, decides what happens:
  - `true`: `[#206]` use-case warning. The validator policy fails and publication is blocked.
  - `false` (default): the same message as an info log. Publication proceeds.
  
  Defaulting to `false` means enabling a mode never starts blocking publications by itself, which matters because Blindata allows unregistered domains unless a tenant setting denies them.
  - → **Decided.**

### Alternatives Considered

- **A dedicated use case for team alignment**: rejected. It would PATCH the same data product a third time per event, and needs the same preservation fix anyway.
- **A boolean flag instead of three modes**: rejected. The confirmed requirement has three modes.
- **Using PUT instead of PATCH to avoid the merge gap**: rejected. PUT would overwrite fields the observer does not manage, such as ports set by the second use case, and would still clear the team unless the observer sends it.
- **Fixing the API's PATCH merge**: rejected for this ticket, because no API change is allowed. Sending the team from the observer is enough.
- **Team from a descriptor extension property** (`x-team`): out of scope. The requirement asks for the domain's team.
- **A fixed choice between always warning and never warning on a missing domain**: rejected. The requirement asks for it to be configurable.

## Risk & Gap Analysis

### Requirement Ambiguities

None. Whether a missing domain blocks publication is configurable, and does not block by default (R7).

### Edge Cases

- **Domain renamed in Blindata**: not possible while data products use it (the API blocks it), so the name match stays stable.
- **Descriptor domain missing**: already warned by `[#83]`; no lookup is made and the team is unchanged.
- **Domain with no team**: info log; the team is unchanged (R5).
- **ALWAYS after a user override or a wizard skip**: the next upload replaces the user's team with the domain's. This is expected for ALWAYS and must be documented.
- **IF_MISSING after the domain's team changes**: data products that already have a team keep it. Re-alignment is done from the UI wizard.
- **Technical user without WRITE on the domain's team**: the API refuses the create or update with 403. `DataProductUpload` logs `[#84]` and the data product is not created, and the ports and assets upload then logs `[#20]`. The dry-run does not catch it, because writes are stubbed.
- **Technical user without READ on the domain's team**: the lookup returns nothing, which is handled like an unregistered domain (R7).
- **Async mode** (`blindata.enableAsync`): PATCH waits for the async task to finish, so the two use cases still run in order and the second one reads the team written by the first.

### Technical Risks

- **Behaviour change with the default configuration**: after the fix, the observer stops clearing teams on update. This is the intended fix, but it changes what operators see. It must be stated in the release notes.
- **YAML boolean coercion** of `off`: handled by lenient parsing, plus quoted values in the docs and in `application.yml`.
- **Extra Blindata call per upload**: one page-of-one GET, only when the mode can change the team.
- **Test coverage**: the use-case tests mock the outbound ports, so they cannot reproduce the API's PATCH merge. They verify the observer side: the payload passed to every update carries the team. A resource serialization test verifies the team survives a JSON read and write.

### Acceptance Criteria Coverage

| AC# | Description | Addressable? | Gaps/Notes |
|-----|-------------|--------------|------------|
| 1 | The observer fetches the domain's team and sets it on the data product | Yes | Exact-name lookup through the existing domain endpoint; applied on create and update. |
| 2 | Three modes: off (default), if-missing, always | Yes | `blindata.dataProducts` property, lenient parsing, OFF default. |
| 3 | The observer never clears a data product's team | Yes | R1 and R5; the stored team is copied on update, and a missing or team-less domain changes nothing. |
| 4 | Scenario 1: domain has a team, data product has none → inherits | Yes | IF_MISSING and ALWAYS. |
| 5 | Scenario 2: data product has a different team → keeps its own | Yes | OFF and IF_MISSING keep it; ALWAYS replaces it by design. |
| 6 | Scenario 3: data product has a team, domain has none → keeps its own | Yes | R5. |
| 7 | No API change, no migration | Yes | Only existing endpoints are used; existing products change only at their next upload. |
| 8 | Team set in Blindata survives the ports and assets upload | Yes | Fixed by carrying the team on the resource. |
| 9 | Blocking publication on a missing domain is configurable, off by default | Yes | `missingDomainBlocksPublication`: `[#206]` warning when `true`, info log when `false` (R7). |
