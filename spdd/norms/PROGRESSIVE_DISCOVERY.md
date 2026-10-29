# Norm: Progressive discovery — navigation levels and cognitive layering

This file is the **canonical rule for how a module is laid out so that a reader can understand it one level at a time**. It applies to any module: the use cases of an aggregate, a job, a client of an external system, a parser, a shared library. Method shape and the line limit are in [`CODE_STRUCTURE.md`](./CODE_STRUCTURE.md); commands, presenters, ports and adapters are in [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md) when it applies. This norm puts them on one ladder and adds the levels above and below.

Apply to **new and touched** modules. Do not retrofit unrelated legacy code.

---

## Goal

A reader answers a question **at the highest level that can answer it**, and goes **down only**. Each level has one job and one grain of detail. No level needs a lower one to be understood.

- **Progressive discovery** is the navigation: overview → procedure → step → rule → I/O.
- **Cognitive layering** is why it stays cheap: a reader holds one procedure in working memory at a time. Overload comes from mixing grains in one place, not from the number of files.

These are navigation levels, not dependency layers. Dependency layers (inbound adapter → use case → ports → adapters) isolate change; navigation levels isolate attention. Code follows both, and neither replaces the other.

**Terms.**

- A **module** is a package tree a reader opens as a whole: an aggregate's use cases, a job, a client, a parser.
- A **unit** is a slice of a module that answers one business question: a use case, a product handled by a job, a connector type, a grammar.
- A **kind** is a group of units that do the same job on different inputs: the catalogue units of a job, the import use cases of an aggregate.
- A **step** is one named call in a unit's procedure. A **rule** is one decision inside a step.

---

## The levels

| Level | Where | Answers | Rules |
| --- | --- | --- | --- |
| **Index** | `<module>/README.md` | How to read the module: reading order, which unit does what, the canonical procedure of each kind. | Required when two or more units have an L0 README. Reading order, unit table and canonical procedures only. Links every unit README. |
| **L0** | `<unit>/README.md` | What the unit covers: inputs → outputs, the cases it handles, its rules by name, its entry and step classes. | Required when the unit has step classes. ≤ 80 lines, no code blocks. Names only types that exist. Ends with the tests that prove it. |
| **L1** | The unit's entry method: `execute()` of a use case, a job's launch method, a client's or parser's public method | The procedure. | A short script of named steps, the same for every unit of its kind. No loops, flags, data branches or I/O inline. The failure handling and presenter call that the unit's norm prescribes belong to the skeleton. |
| **L2** | One class per step, inside the unit | How one step works, in outline. | Composed method. Named after a domain noun (`DescriptorValidation`, `CopyActivityEdges`), never a technical role (`Processor`, `Manager`, `Handler`). |
| **L3** | Private methods at the bottom of the step class | The rules. | One rule per method, named after the rule or its outcome (`missingOwner`, `unresolvedParameter`). An outcome (skip, gap, failure) is recorded in the method whose name says why. |
| **L4** | Port implementations, adapters, clients, mappers, readers | I/O, wire formats, parsing, persistence, construction of external resources. | No business decision. A mapping method never decides whether to emit and never filters. |
| **D** | The module's shared types (`<module>/domain`), and the aggregate's entities when the model is persisted | The types most units speak. | Records, enums and interfaces, persistent entities excepted. Behaviour reads only the type's own fields: no I/O, no collaborators. |

Worked path, a use case that registers a data product:

```text
L0  register/README.md                         names DescriptorValidation and the tests that prove it
L1  execute()                                  validate → register → notify
L2    DescriptorValidation.issuesOf(descriptor)  for each component: check the component
L3      missingOwner(component)                  one rule, and the issue it records
        duplicatedPortName(component)
L4  RegisterPersistenceOutboundPortImpl.save(product)   persistence only, no decision
```

**Small units collapse levels, never mix them.** A step stays a private method of the entry class until it owns three or more rules, and then it becomes a step class. A unit without step classes keeps its levels in order in one file (entry method on top, then steps, then rules) and needs no L0 README; the module index, if there is one, describes it. L4 is never folded into L1–L3, and a README never stands in for a missing L1.

---

## Rules

1. **One skeleton per kind of unit.** Units of the same kind use the same step names, in the same order, in their entry methods. A unit without a step leaves that call out and keeps the order of the others. A unit never renames a step, and adds one only by adding it to the canonical procedure in the index. Learning one unit teaches all of them.
2. **Policy up, mechanics down.** A decision that changes the outcome lives in L1–L3. If swapping a library, a protocol or a format would not change the meaning, the code belongs in L4. Do not move policy into an adapter so the entry method looks simpler.
3. **Each rule lives in exactly one place.** A rule that must give the same answer in several units (an identity, naming or merge rule) lives in the shared types. It is a static method of an interface, or a method of the shared type whose fields it reads. It is never a `*Helper` / `*Utils` class and never a copy in each unit.
4. **Sharing is earned.** A type moves into the shared types only when at least three units use it, directly or through another shared type. The index may set a higher floor. Until then it stays in its units, even as two identical copies. The one exception is a rule from rule 3 that exactly two units must apply identically: share it, and name it in the structure test's allow-list. Entities are the aggregate's model and are not subject to the floor.
5. **Units are independent.** A unit never depends on another unit. Units meet only in the shared types and in the module's entry point.
6. **A README describes; the code implements.** Navigation documentation is a README, not Javadoc. A README states what a rule decides, in a sentence a domain expert recognises, and names the class that owns it. It never walks through the algorithm and it names only what exists.

```text
✅ execute() lists validate(), register(), notify(); the unit README names the step class of each case
✅ two units each keep a copy of a small record                                 (sharing not yet earned)
❌ execute() walks a payload and filters between its named steps                (L1 mixed with L4)
❌ a mapper skips an item because an option is off                              (L4 decides policy)
❌ a README explains a matching algorithm step by step                          (L0 implements L3)
❌ a shared type created for two units "because a third may need it"            (sharing not earned)
❌ one unit imports another unit's step class                                   (units coupled)
❌ two units of one kind run fetch → convert → save and load → map → publish    (no common skeleton)
❌ a step class named DataProcessor that parses, decides and uploads            (no single grain)
```

---

## Enforcement

A module with an index README has two tests in its own test package. Both are unit tests (`*Test`) and neither starts the application.

- **Structure test** (ArchUnit):
  - a unit exposes only its entry types (its use case or factory, command and presenter), and nothing outside the unit depends on its other types or sub-packages;
  - units do not depend on each other, except through the shared types;
  - step classes do not depend on adapters, client implementations or framework types;
  - adapters are reached only from the unit's root package, where its entry classes and factory live;
  - shared types are records, enums and interfaces (persistent entities excepted), depend only on other shared types, the JDK and the client model types the module lets past its clients, and are used by at least three units, apart from the allow-list of rule 4;
  - only L4 mapping code constructs the types of an external model, and any exception is an explicit allow-list in the test;
  - no `*Helper`, `*Util` or `*Utils` types.
- **README guard:**
  - every unit with step classes has a README of at most 80 lines with no code blocks;
  - every PascalCase name in inline code is a type declared in that unit or in the shared types (all-caps tokens such as `SQL` are ignored);
  - the index README links every unit README.

A module writes its own copies of these tests. It does not edit another module's tests.

---

## Checklist (new or touched module)

1. Each entry method is the canonical skeleton of its kind: named steps only, with no inline loops, flags or I/O.
2. Each step with three or more rules is a class named after a domain noun, with its rules as named private methods at the bottom.
3. Port implementations, adapters, clients and mappers decide nothing; mappers never filter.
4. Shared types are records, enums and interfaces that at least three units use; units do not depend on each other.
5. Each unit with step classes has an L0 README: at most 80 lines, no code blocks, existing types only, ending with the tests that prove it.
6. A module with two or more unit READMEs has an index README, a structure test and a README guard, and both tests pass.

---

**Spring:** both tests are plain JUnit classes. ArchUnit imports the compiled classes with `ClassFileImporter` and the README guard reads `src/main/java`, so neither is a `@SpringBootTest`. The template does not ship ArchUnit: the service adds `com.tngtech.archunit:archunit-junit5` with test scope.
