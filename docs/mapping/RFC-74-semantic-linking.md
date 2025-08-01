# RFC-74 : Semantic Linking

## Summary

A semantic link is an explicit connection between a data asset exposed by a data product through one of its output ports
and one or more concepts defined in a central enterprise ontology.

## Motivation

Semantic linking is a way to bring semantics to a data product and improve its capability to be discovered, reused, and
composed with other data products belonging to different domains.

## Design and examples

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Simplified Movie Object (Compact)",
  "type": "object",
  "s-context": {
    "s-base": "https://schema.org",
    "s-type": "[Movie]",
    "movieId": null,
    "movieTitle": "[Movie].title",
    "directorName": "[Movie].director[Person].name",
    "directorCountryName": "[Movie].director[Person].country[Country].name",
    "actors": "[Movie].actor[Person].name",
    "copyright": {
      "s-type": "copyrightHolder[Organization]",
      "organizationId": null,
      "email": "contactPoint[ContactPoint].mail"
    }
  },
  "properties": {
    "movieId": {
      "type": "string"
    },
    "name": {
      "type": "string"
    },
    "directorName": {
      "type": "string"
    },
    "directorCountryName": {
      "type": "string"
    },
    "actors": {
      "type": "array",
      "items": {
        "type": "string"
      },
      "minItems": 1
    },
    "copyright": {
      "type": "object",
      "properties": {
        "organizationId": {
          "type": "string"
        },
        "legalName": {
          "type": "string"
        },
        "email": {
          "type": "string"
        }
      }
    }
  }
}
```

**Explanation:**

- `s-context`:  defines the semantic links. It can be defined inline or as an external reference
- `s-base`: the base URL used to resolve concept names
- `s-type`: the linked concept name or full concept URI enclosed in square brackets. The name before the brackets is the
  name of the parent property valorized by the linked concept. For example
  `"copyright": {"s-type": "copyrightHolder[Organization]"}` maps the property `copyright` of the schema to the property
  `copyrightHolder` of the parent concept (`Movie`) and has as value an `Organization` concept. If the name used in the
  schema is already equal to the name of the referenced concept property it can be omitted in the mapping.
- ` "movieId": null,` the schema property `movieId` is not defined in the `Movie` concept. It exists only in the
  physical data
- ` "directorName": "[Movie].director[Person].name"` : the property `directorName` in the schema is linked to the property`name`
  of the `Person` who directs the `Movie`
- ` "directorCountryName": "[Movie].director[Person].country[Country].name"` :  the property `directorCountryName` in the schema
  is linked to the property `name` of the `Country` of the `Person` who directs the `Movie`
- ` actors": "[Movie].actor[Person].name` : the property `actors` in the schema is linked to the property `name` of the `Person`
  who acts in the `Movie`. Because `actors` in the schema is an array the values are the names of the actors.

## Alternatives

The alternative to
use [REST API Linked Data Keywords](https://datatracker.ietf.org/doc/draft-polli-restapi-ld-keywords/) has been rejected
because is not possible to manage easily semantic linking in flat schemas

## Decision

- Define semantic links using a custom sub-specification
- Define semantic links as a particular type schema annotations

## Consequences

- We need to create a new specification
- We need to work on this RFC together with the RFC related to schema annotation

## References

- [Discussion Thread](https://github.com/opendatamesh-initiative/odm-specification-dpdescriptor/discussions/32)
- RFC Document (not available yet)

## Semantic Links and Relational Data Mapping

Through semantic linking, when a data asset corresponds to a relational database table, the automatic generation of
R2RML mappings is enabled. By defining semantic relationships between table fields and ontology attributes, data can be
efficiently mapped to RDF.
The semantic links can map directly to attributes in the ontology or follow more complex paths through property chains.

### Data Asset Definition and Semantic Linking

To define how a relational database maps to RDF, we begin by specifying the semantic context of the data asset, where
each field in the table is semantically linked to a corresponding ontology element.

**Example:**

Consider a **Product** data asset. Below the representation of this data asset and its semantic links:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Product",
  "type": "object",
  "s-context": {
    "s-base": "https://example.com",
    "s-type": "[Product]",
    "sup_code": "[Product].supplier[Supplier].supplierCode",
    "sup_name": "[Product].supplier[Supplier].supplierName",
    "sup_location": "[Product].supplier[Supplier].location[Country].countryName",
    "manufacturer": "[Product].manufacturer[Manufacturer].manufacturerCode",
    "prod_code": "[Product].productCode",
    "prod_name": "[Product].productName",
    "price": "[Product].productPrice"
  },
  "properties": {
    "sup_code": {
      "type": "string"
    },
    "sup_name": {
      "type": "string"
    },
    "sup_location": {
      "type": "string"
    },
    "manufacturer": {
      "type": "string"
    },
    "prod_code": {
      "type": "string"
    },
    "prod_name": {
      "type": "string"
    },
    "price": {
      "type": "number"
    }
  }
}
```

### Subject Mapping

Each data asset is primarily semantically linked to a class through the `s-type` property. This linkage provides
resource typing, ensuring that for each RDF term generated by the subject map, RDF triples with the predicate `rdf:type`
and the class specified by `s-type` as the object will be created. The `s-base` property identifies the namespace or
base IRI in which the class named in `s-type` is defined.

The class associated with `s-type` should declare an IRI template, which is resolved into an actual IRI for the subject
map. This ensures that resources corresponding to rows in the table are properly typed and linked to their semantic
class in the ontology.

**Example**

Let's assume we have the concept **"Product"** in an ontology. Each product is uniquely identified by two elements that
together form a natural key:

1. **Product Code** (`productCode`)  
   This is a direct **attribute** of the `Product` class, represented as a datatype property (`ex:productCode`). It
   holds the unique identifier for the product and is stored directly within the product’s data.

2. **Supplier Code** (`supplierCode`)  
   While it contributes to the uniqueness of the product, the `supplierCode` is not a direct attribute of the `Product`
   concept. Instead, it is part of a **semantic chain** that connects the `Product` to the `Supplier` class.
   Specifically, the `supplierCode` is an attribute (`ex:supplierCode`) of the `Supplier` concept, and we access it by
   traversing the relationship between a `Product` and its `Supplier`. In RDF terms, this is expressed through an *
   *object property** linking `Product` to `Supplier` (e.g., `ex:supplier`), followed by the **datatype property**
   `ex:supplierCode` within the `Supplier` class.

The ontology specifies an IRI template for the "Product" concept:

```
https://example.com/supplier/{supplier[Supplier].supplierCode}/inventory/product/{productCode}
```

This means the generated IRI for each product is based on both the product code and the supplier code, allowing us to
track products supplied by different suppliers uniquely.

Now, we have a relational database table (our data asset) that contains product information. The table's column names
are different from the attribute names in the ontology. Although the column names in the database differ, we know that
`prod_code` semantically maps to `productCode` in the ontology and `sup_code` maps to `supplier[Supplier].supplierCode`.
Given this semantic mapping between the table columns and the elements in the ontology, we can define the **subject map
** in R2RML. The subject map will use the IRI template declared in the ontology, and it will map the database columns to
the corresponding ontology elements thus substituting `{productCode}` and `{supplier[Supplier].supplierCode}` with the
actual column names `prod_code` and `sup_code`:

```turtle

rr:subjectMap
        
[  
  rr:template "https://example.com/supplier/{sup_code}/product/{prod_code}" ;
  rr:class ex:Product ; ] .
```

### Predicate-Object Mapping

#### Mapping a Field to a Property

In cases where a table column directly maps to a property of the corresponding ontology concept, the predicate-object
map is straightforward. The `rr:predicate` will equal the value of the semantic link, and the object map will be defined
as a column-valued map.

**Example**

For additional properties of the product, like its name and price, we can map database columns directly to RDF
properties in the ontology. For instance:

```turtle

rr:predicateObjectMap
        
[  
  rr:predicate ex:productName ;
  rr:objectMap [ rr:column "prod_name" ] ;
] .

rr:predicateObjectMap
        
[  
  rr:predicate ex:productPrice ;
  rr:objectMap [ rr:column "price" ] ;
] .
```

#### Linking Two Concepts - Mappings via Key Relationships

In cases where a table field is semantically linked to an ontology attribute via a **single** hop, and that attribute is
marked as a **key** in the ontology (an attribute is implicitly considered a key if it appears within the IRI template
for the corresponding concept), the **predicate-object map** in R2RML reflects this linkage using an IRI template.

**Example**

Consider the case of the `manufacturer` column in a product table, which is semantically linked to the attribute
`manufacturerCode` in the **Manufacturer** concept.
In the ontology, the **Manufacturer** concept defines its IRI template as
`https://example.com/manufacturer/{manufacturerCode}` implicitly designating `manufacturerCode` as the natural key of
the **Manufacturer** concept.

In this situation, the R2RML mapping will use an IRI template to generate the object map for the manufacturer field,
leveraging the known link between the **Product** and **Manufacturer** concepts. The **subject map** for the product
remains based on the product's own attributes, while the **object map** for the `manufacturer` column will reference the
**Manufacturer** IRI template. The **predicate-object** map will use the value in the column `manufacturer` to resolve
the `manufacturerCode` key from the ontology.

The resulting mapping for the predicate-object map will be:

```turtle

rr:predicateObjectMap
        
[  
  rr:predicate ex:manufacturer ;
  rr:objectMap [
    rr:template "https://example.com/manufacturer/{manufacturer}" ;
    rr:column "manufacturer" ; ] ; ] .
```

#### Linking Two Concepts - Complex Mappings via Property Chains

If a table field mapping to the ontology involves multiple relationships, a property chain axiom is defined. A *
*property chain axiom** allows for the chaining of multiple properties to form a single complex relationship, enabling
navigation through multiple hops in the ontology. While it does not introduce any new meaning or semantics to the
ontology itself, it creates a formalized path that the mapping can use for linking concepts. The property chain merely
provides a mechanism to explicitly define a sequence of existing relationships, without altering the original ontology
or adding new assertions about the data model.

This axiom is necessary because R2RML requires a predicate for each predicate-object map, which represents a single
property in the ontology. When a table field refers to a concept that is connected by multiple relationships, no single
predicate exists to represent this path. The property chain axiom serves as a temporary solution for the mapping,
linking concepts across multiple steps. By defining this chain explicitly, the mapping process ensures that the correct
relationships are used without ambiguity.

The use of property chain axioms introduces another consideration: only object properties can be employed in this
context. Property chains are designed to work with object properties, which link individuals within the ontology. They
do not function with datatype properties, which connect individuals to literals. This limitation necessitates an
additional step in the mapping process. To address this, certain datatype properties are represented as object
properties by introducing intermediate classes or individuals. For example, instead of having `:countryName` as a
datatype property of `Country`, we can introduce a set of classes representing data types (such as the classes from
Schema.org under `DataType`, along with any custom-defined classes that are subclasses of `DataType`). We would then
link `Country` to `schema:Text` through an object property named `countryName`.

**Example**

Consider the **sup_location** field in a table and suppose that this field represents the name of the country where the
supplier is located. In this case, the ontology defines a chain of properties:

- A `Product` is related to a `Supplier` through the `supplier` property.
- A `Supplier` is related to a `Country` through the `location` property.
- A `Country` has a `countryName` attribute (that, as part of the mapping process, will be trasformed in a property
  linking `Country` to `Text`).

To express these relationships, an **owl:propertyChainAxiom** is automatically generated as part of the R2RML mapping
process, chaining the properties together:

```turtle

ex:supplierLocationCountryName
    rdf:type               owl:ObjectProperty ;
    owl:propertyChainAxiom ( ex:supplier ex:location ex:countryName ) .
```

This axiom allows us to use a **predicate-object map** that refers to this property chain, mapping the `sup_location`
field in the relational database to the `name` of the supplier’s country. The **predicate** will refer to the
`supplierLocationCountryName`, and the **object map** will map the `sup_location` column to the final `countryName`.

```turtle

rr:predicateObjectMap
        
[  
  rr:predicate ex:supplierLocationCountryName ;
  rr:objectMap [ rr:column "sup_location" ] ;
] .
```

This R2RML mapping ensures that even though the `sup_location` field is associated with a concept multiple steps away
from `Product`, the correct relationship chain is established via the property chain axiom, allowing the field to be
correctly mapped in the RDF representation.

### Linking Two Concepts – Many-to-Many Tables

In a relational database, many-to-many relationships between concepts are typically managed through a bridge table.
There are two common strategies for handling such relationships when creating semantic links and generating R2RML
mappings.

#### Option 1: Choosing a Master Concept for the Bridge Table

In this strategy, the bridge table is associated with an existing concept in the ontology. The **subject map** will be
generated based on the IRI template defined for that concept, but the subject IRIs in the RDF output won't uniquely
identify the rows of the bridge table.

**Example:**

Consider a many-to-many relationship between **Products** and **Categories**, where a single product can belong to
multiple categories, and each category can contain multiple products.

The bridge table might include the following columns:

- `prod_code`: The product identifier.
- `sup_code`: The supplier identifier.
- `category_code`: The category identifier.

The following JSON structure defines the semantic mapping for this data asset:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "ProductCategoryBridge",
  "type": "object",
  "s-context": {
    "s-base": "https://example.com",
    "s-type": "[Product]",
    "prod_code": "[Product].productCode",
    "sup_code": "[Product].supplier[Supplier].supplierCode",
    "category_code": "[Product].category[Category].categoryCode"
  },
  "properties": {
    "prod_code": {
      "type": "string"
    },
    "sup_code": {
      "type": "string"
    },
    "category_code": {
      "type": "string"
    }
  }
}
```

In this setup:

- The **prod_code** and **sup_code** columns are used to generate the subject map according to the IRI template defined
  in the **Product** concept:

```turtle

rr:subjectMap
        
[  
  rr:template "https://example.com/supplier/{sup_code}/inventory/product/{prod_code}" ;
  rr:class ex:Product ; ] .
```

- The **category_code** is used to create a predicate-object map, where the predicate corresponds to the **category**
  relationship, and the object is generated using the IRI template from the **Category** concept:

```turtle

rr:predicateObjectMap
        
[  
  rr:predicate ex:category ;
  rr:objectMap [
    rr:template "https://example.com/category/{category_code}" ;
    rr:column "category_code" ; ] ; ] .
```

#### Option 2: Using a Dedicated Ontology Concept for the Bridge Table

In this approach, the bridge table itself is represented as a distinct concept in the ontology. Everything works the
same as the other cases described above, with the added benefit of explicitly modeling the many-to-many relationship.

**Example:**

Consider a new concept called **ProductCategory**, which connects **Product** and **Category** via object properties (
`ex:product` and `ex:category`). This new concept will have its own IRI template to ensure that each instance of the
many-to-many relationship is uniquely identified. The data asset will reflect this structure:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "ProductCategory",
  "type": "object",
  "s-context": {
    "s-base": "https://example.com",
    "s-type": "[ProductCategory]",
    "sup_code": "[ProductCategory].product[Product].supplier[Supplier].supplierCode",
    "prod_code": "[ProductCategory].product[Product].productCode",
    "category_code": "[ProductCategory].category[Category].categoryCode"
  },
  "properties": {
    "sup_code": {
      "type": "string"
    },
    "prod_code": {
      "type": "string"
    },
    "category_code": {
      "type": "string"
    }
  }
}
```

In this case the **ProductCategory** concept will have object properties that link it to both **Product** and **Category
** concepts:

```turtle

rr:subjectMap
        
[  
  rr:template "https://example.com/supplier/{sup_code}/inventory/product/{prod_code}/category/{category_code}" ;
  rr:class ex:ProductCategory ; ] .

rr:predicateObjectMap
        
[  
  rr:predicate ex:product ;
  rr:objectMap [
    rr:template "https://example.com/supplier/{sup_code}/inventory/product/{prod_code}" ;
  ] ; ] .

rr:predicateObjectMap
        
[  
  rr:predicate ex:category ;
  rr:objectMap [
    rr:template "https://example.com/category/{category_code}" ;
  ] ; ] .
```