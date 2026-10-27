# Norm: Code structure — composed method, line limit, helpers

This file is the **canonical rule for how generated service code is shaped**. Package layout, ports, and CRUD templates live in [USE_CASE_IMPLEMENTATION.md](./USE_CASE_IMPLEMENTATION.md) and [GENERIC-CRUD-GUIDELINES.md](./GENERIC-CRUD-GUIDELINES.md) when those apply.

Apply to **new and touched** feature code. Do not retrofit unrelated legacy files.

---

## Goal

A reader should narrate a public method **without opening the next method**. Names are the documentation of the step. The call _is_ the comment. Do not add a comment that restates the method name.

---

## Composed method (step-down)

Write each method as a **short outline of the procedure at one level of abstraction**. Implement each outline item as a private method one level down.

- **One abstraction per method.** Do not mix “what” (the procedure) with “how” (parsing, field walks, SQL, vendor APIs, JSON merging) in the same body.
- **Push details down; do not extract sideways.** Prefer private methods at the **bottom of the owner class** over `*Helper` / `*Util` types, extra public overloads, or unused parameters that only pass defaults.
- **Same shape at every depth.** A parent that is “for each item, validate the item” has a **child that owns that item’s checks** — not nested conditionals inlined into the parent.
- **A private method exists when it is a named step in the parent’s story**, not when it merely wraps a single `if`. Collapse overloads that exist only to supply a default.

```text
// ✅ GOOD — execute() is the script
void execute() {
  loadContext();
  enforceInvariants();
  applyChange();
}

// ❌ BAD — what mixed with how in one body
void execute() {
  // 80 lines of DTO walks, uniqueness indexing, JSON merge, then save
}
```

---

## Line limit (what to count)

These are **violations**, not hints:

1. **~200–250 lines per method body — not the class.** See below.
2. **Do not add `*Helper` / `*Util` types** (or a `utils` / `helpers` package) to shrink a class or to share a step of one owner.

Agents: **do not run `wc -l` on the `.java` file and split.** That is the wrong metric.

**Count** only the body of **one** method: from its opening `{` to **that method’s** closing `}`. Nested lambdas, anonymous classes, and local methods declared _inside_ it count.

**Do not count** (they may make the class 400–800 lines and that is fine):

- `package` / `import` lines
- fields, constructors, nested types declared _outside_ the method
- **private methods** elsewhere in the same class
- another method’s body

```text
✅ 500-line class, public method 120 lines + private steps at the bottom  → compliant
❌ 180-line class, public method 260 lines of mapping and branches       → split that method
❌ extract *Utils / *Helper because the file is 300 lines                → violation
```

**When a body is over ~250:** extract a **named private step** in the **same class**. Keep helpers at the bottom of the **owner** class. That is the only legal split for this limit.

---

## Where a helper goes

| You are adding… | Put it… |
| --- | --- |
| Predicate / merge / mapping used by one class | Private method at the bottom of that class |
| Predicate used by two classes | Duplicate if small; otherwise a named private (or package-private) method on the **owner** — never a new utils type |
| Step that is I/O or a port fulfilment | Adapter / repository / existing collaborator — still not a `*Util` |

**Do not** create `*Utils.java`, `*Helper.java`, `helpers/`, or `commons/` function dumps for new or touched code. Legacy `*Utils` types exist — do not add new ones and do not copy that pattern.

---

## Anti-patterns

- **God method:** load + validate + wire walks + persist + notify in one body.
- **File-length split:** moving code because `wc -l` on the `.java` file is over 250. Measure the **method body**.
- **Sideways extract:** `*Utils`, `*Helper`, or `commons/` functions for logic that is a step of one owner.
- **Comments that restate the name** (`// apply change` above `applyChange()`).
- **Mixing how with what** (JSON/JPA field walks inline between named steps).

---

## Checklist (new or touched feature)

1. Each public method is a short outline of named steps at one abstraction level.
2. No new `*Utils` / `*Helper` types; helpers are private methods at the bottom of the owner class.
3. No **method body** over ~200–250 lines. Classes may be much longer; do not split on `wc -l`.
