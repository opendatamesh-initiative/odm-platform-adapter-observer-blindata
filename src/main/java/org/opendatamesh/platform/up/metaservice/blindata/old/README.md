# Backward Compatibility Package (`old`)

Temporary adapters that let the observer work with ODM services through a previous API version.
The layout mirrors the `old` package of `odm-platform-pp-registry-server` and `odm-platform-pp-devops-server`:
one `old.v1.<service>` package per service, with `V1`-suffixed names. The current client of each service lives
unsuffixed under `client`.

**This package is temporary.** Deleting it removes the V1 code paths.

## Dependency rules

- Code in `old` may depend on code outside it.
- New code outside `old` must not depend on code inside it.
- Known exception: `services.DataProductPortAssetAnalyzer` reads port standard definitions through
  `OdmRegistryClientV1`. Move that read to the Registry V2 client before deleting this package.

## Registry V1 client (`old.v1.registryservice`)

`OdmRegistryClientV1` reads the Registry V1 routes (`/api/v1/pp/registry/...`), which the Registry V2 server still
serves. It is configured by `odm.productPlane.registryService.active` and `.address`, and it does not depend on
`.apiVersion`.

The Registry V2 client is `client.odm.OdmRegistryClient`. Its `apiVersion: v2` read of data product versions is
itself temporary (BDMD-5558): it goes away once DevOps 2.0 events are enriched upstream.
