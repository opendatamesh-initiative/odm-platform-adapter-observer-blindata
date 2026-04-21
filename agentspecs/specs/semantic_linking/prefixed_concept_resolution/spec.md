# Spec: Prefixed concept resolution in `s-context`

Canonical spec for this feature. Trace tests via comments on `SemanticLinkManagerTest` methods.

## Feature: Leading bracket concept with namespace prefix

### Scenario: Prefixed root concept resolves in the prefix namespace

**Given** a default namespace N_default identified by `s-base`  
**And** a second namespace N_other with prefix `other`  
**And** data category `ConceptB` exists in N_other but not in N_default  
**When** `enrichWithSemanticContext` runs with a field mapping:

`"field2": "[other:ConceptB].other:attribute2FromConceptB"`

**Then** the adapter MUST look up data category name `ConceptB` using N_other’s UUID  
**And** the adapter MUST NOT look up the literal name `other:ConceptB` in N_default  

### Scenario: Unprefixed root concept unchanged

**Given** default namespace N_default  
**And** data category `ConceptA` exists in N_default  
**When** a field uses `"[ConceptA].someAttribute"` (no prefix inside brackets)  
**Then** the adapter MUST resolve `ConceptA` in N_default (existing behavior)

### Scenario: Default `s-type` still uses default namespace

**Given** `s-type` is `[ConceptA]` and `s-base` is N_default  
**When** enrichment runs  
**Then** `ConceptA` MUST be resolved in N_default  

## Feature: Namespace prefix resolution

### Scenario: Unknown prefix surfaces a clear outcome

**Given** no namespace exists with prefix `unknown`  
**When** a path references `[unknown:SomeConcept]`  
**Then** the adapter MUST NOT treat `unknown:SomeConcept` as a single concept name in the default namespace  
**And** resolution MUST fail or log a defined warning according to product rules (document actual choice in implementation)

### Scenario: Ambiguous prefix (if multiple namespaces match)

**Given** two namespaces share the same prefix (misconfiguration)  
**When** a path uses that prefix in `[prefix:Concept]`  
**Then** the adapter MUST NOT silently attach an arbitrary namespace (prefer explicit error or warn + skip)

## Feature: Blindata path passthrough

### Scenario: Semantic link resolution still receives the original path

**Given** a field path as authored in the descriptor (including `prefix:` inside brackets and on attributes where applicable)  
**When** `getSemanticLinkElements` is invoked  
**Then** the `pathString` argument MUST match the composed path expected by Blindata for that descriptor (same as today unless Blindata requires a transformation documented in the proposal)

## Non-regression

### Scenario: Stock / luxury style relative paths

**Given** the stock example with `s-type` `[Stock]` and relative segments like `refersTo[lux:ProductSku].lux:productSkuIdentifier`  
**When** enrichment runs  
**Then** behavior MUST remain aligned with existing `SemanticLinkManagerTest` expectations (paths and default namespace identifier passed to the client)

---

## Implementation notes (coherence with code)

- **Unknown / ambiguous prefix:** `BdSemanticLinkingClient.getLogicalNamespaceByPrefix` returns a namespace only when Blindata returns exactly one match (`BdClientImpl` requests up to two rows and treats zero or more than one as empty). `SemanticLinkManagerImpl` logs **`[#120]`** *No unique logical namespace found for prefix: …* and skips attaching that concept to the physical entity (no lookup of the bare concept name in the default namespace).
- **Concept not found:** **`[#89]`** when the name is not found in the resolved namespace UUID; **`[#88]`** when the default `s-type` root concept is missing after namespace resolution.
- **Default namespace UUID:** Pre-resolution uses `getUuid()` on the namespace returned for `s-base`. Test fixtures under `testSemanticLinking_mockedBlindataResponses_*.json` include a namespace **uuid** where full enrichment is asserted.
- **Unprefixed leading segment:** Covered by the film-rental integration-style test (`[Movie]`, `[Company]` distributor path, etc.) in `SemanticLinkManagerTest.testEnrichWithSemanticContext`.
