package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnosticSeverity
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceBinder
import camp.delta.deltatypetheory.core.surface.model.SurfaceDefDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceLambda
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfacePi
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SurfaceTypecheckRunTest {

  private fun name(value: String) = SurfaceName(value)

  // Строит программу с натуральными числами:
  //   axiom Nat : Type;
  //   axiom zero : Nat;
  //   axiom succ : (n : Nat) → Nat;
  private fun natProgram(extra: List<SurfaceAxiomDecl> = emptyList()): SurfaceProgram {
    val nat = SurfaceAxiomDecl(name("Nat"), SurfaceTypeTerm(), null)
    val zero = SurfaceAxiomDecl(name("zero"), SurfaceNameRef(name("Nat")), null)
    val succType = SurfacePi(
      SurfaceBinder(name("n"), SurfaceNameRef(name("Nat"))),
      SurfaceNameRef(name("Nat")),
    )
    val succ = SurfaceAxiomDecl(name("succ"), succType, null)
    return SurfaceProgram(
      declarations = listOf(nat, zero, succ) + extra,
      rules = emptyList(),
      fileName = null,
    )
  }

  @Test
  fun validNatProgramHasNoErrors() {
    val result = SurfaceTypeCheckerImpl.check(natProgram())
    assertFalse(result.diagnostics.any { it.severity == SurfaceDiagnosticSeverity.Error })
  }

  @Test
  fun definitionWithCorrectTypeIsAccepted() {
    // def two : Nat := succ(zero);
    val two = SurfaceDefDecl(
      name("two"),
      SurfaceNameRef(name("Nat")),
      SurfaceApp(
        SurfaceNameRef(name("succ")),
        SurfaceNameRef(name("zero")),
      ),
      null,
    )
    val program = SurfaceProgram(
      declarations = natProgram().declarations + two,
      rules = emptyList(),
      fileName = null,
    )
    val result = SurfaceTypeCheckerImpl.check(program)
    assertFalse(result.diagnostics.any { it.severity == SurfaceDiagnosticSeverity.Error })
  }

  @Test
  fun dependentFunctionDefinitionWithAnonymousPiIsAccepted() {
    // def for_all_A_from_A_A : (T : Type) -> (T -> T) :=
    //   lambda (Xi : Type) => lambda (d : Xi) => d;
    val t = name("T")
    val xi = name("Xi")
    val d = name("d")
    val declaration = SurfaceDefDecl(
      name("for_all_A_from_A_A"),
      SurfacePi(
        SurfaceBinder(t, SurfaceTypeTerm()),
        SurfacePi(
          SurfaceBinder(null, SurfaceNameRef(t)),
          SurfaceNameRef(t),
        ),
      ),
      SurfaceLambda(
        SurfaceBinder(xi, SurfaceTypeTerm()),
        SurfaceLambda(
          SurfaceBinder(d, SurfaceNameRef(xi)),
          SurfaceNameRef(d),
        ),
      ),
      null,
    )
    val program = SurfaceProgram(
      declarations = listOf(declaration),
      rules = emptyList(),
      fileName = null,
    )

    val result = SurfaceTypeCheckerImpl.check(program)

    assertFalse(result.diagnostics.any { it.severity == SurfaceDiagnosticSeverity.Error })
  }

  @Test
  fun definitionWithWrongTypeIsRejected() {
    // def bad : zero := zero; -- у zero тип Nat, а не zero, поэтому ошибка
    val bad = SurfaceDefDecl(
      name("bad"),
      SurfaceNameRef(name("zero")),
      SurfaceNameRef(name("zero")),
      null,
    )
    val program = SurfaceProgram(
      declarations = natProgram().declarations + bad,
      rules = emptyList(),
      fileName = null,
    )
    val result = SurfaceTypeCheckerImpl.check(program)
    assertTrue(result.diagnostics.any { it.severity == SurfaceDiagnosticSeverity.Error })
  }

  @Test
  fun duplicateNameIsRejected() {
    val dup = SurfaceAxiomDecl(name("Nat"), SurfaceTypeTerm(), null)
    val program = SurfaceProgram(
      declarations = natProgram().declarations + dup,
      rules = emptyList(),
      fileName = null,
    )
    val result = SurfaceTypeCheckerImpl.check(program)
    assertTrue(result.diagnostics.any { it.severity == SurfaceDiagnosticSeverity.Error })
  }

  @Test
  fun unknownNameIsRejected() {
    // axiom weird : Nat; -- Nat известен, но возьмём неизвестный тип
    val weird = SurfaceAxiomDecl(name("weird"), SurfaceNameRef(name("Unknown")), null)
    val program = SurfaceProgram(
      declarations = natProgram().declarations + weird,
      rules = emptyList(),
      fileName = null,
    )
    val result = SurfaceTypeCheckerImpl.check(program)
    assertTrue(result.diagnostics.any { it.severity == SurfaceDiagnosticSeverity.Error })
  }
}
