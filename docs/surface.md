# Surface representation and checking

The `core/surface` package is the syntax-level boundary between the parser and the future elaborator/type checker. It deliberately represents source constructs without assigning them types or converting them to a core calculus.

## Model

`SurfaceProgram` is the main data class containing the rest of the program. It consists of declarations:

- `SurfaceAxiomDecl(name, type, range)` — a named assumption;
- `SurfaceDefDecl(name, type, value, range)` — a named definition with an annotation and value.

Both the type and value arguments are of the type `SurfaceTerm`, which is a interface with the following classes:

- `SurfaceTypeTerm` for `Type`;
- `SurfaceNameRef` for a string identifier;
- `SurfaceApp(function, argument)` for an application of a function;
- `SurfacePi(binder, body)` for a PI-function;
- `SurfaceLambda(binder, body)` for a lambda expression.

A `SurfaceBinder` couples a non-blank `SurfaceName` with its type. Declarations and binders may carry a `SourceRange` so later stages can attach diagnostics to source text. Ranges are currently optional, and the stub checker emits diagnostics without one.

Every name variable that intuitively contains a string, contains a wrapper string wrapper `SurfaceName`, that errors when empty.

## Diagnostics

`DiagnosticReporter` collects `SurfaceDiagnostic` values in encounter order. Diagnostics have a severity (`Error`, `Warning`, or `Info`), message, and optional range. `hasErrors()` is true precisely when at least one collected diagnostic has `Error` severity.
To report something through `DiagnosticReporter` write `report(diagnostic)` or `reportError(message, range)`.

## `SurfaceTypeChecker` and the current stub

`SurfaceTypeChecker.check` accepts a `SurfaceProgram` and returns a `SurfaceCheckResult` containing every diagnostic it produced. `StubSurfaceTypeChecker` is a temporary structural checker, not a type checker. It walks declaration types and definition values, including applications, Pi terms, and lambdas.

While walking, it records global declaration names and temporarily records a binder name while checking that binder's annotation. It reports:

- duplicate top-level declaration names;
- binders whose name collides with a known global name or a name temporarily being checked;
- name references whose spelling collides with a known global name or a temporarily recorded binder name.

This is intentionally a stub-era collision check: it is **not** identifier resolution. In particular, a reference to a declared name produces a collision diagnostic instead of being accepted as a resolved global reference, and binder scope is not retained for the Pi/lambda body. It does not validate type correctness, identify unknown names, infer types, or produce source ranges. A real elaborator should replace these rules while retaining the `SurfaceTypeChecker` result shape.

## Tests

The core tests live under `core/test/`. They cover model validation and diagnostic severity handling, plus the stub checker's duplicate-global, binder-collision, and recursive traversal behaviour. Run them with:

```sh
./gradlew :core:test
```
