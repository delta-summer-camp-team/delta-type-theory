package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnosticSeverity
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceBinder
import camp.delta.deltatypetheory.core.surface.model.SurfaceDefDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfacePi
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StubSurfaceTypeCheckerTest {
    @Test
    fun `reports a reference that collides with an earlier global name`() {
        val result =
            StubSurfaceTypeChecker()
                .check(
                    SurfaceProgram(
                        declarations = listOf(
                            axiom("Nat", SurfaceTypeTerm()),
                            axiom("zero", SurfaceNameRef(name("Nat"))),
                        ),
                        rules = emptyList(),
                        fileName = "natural.delta",
                    ),
                )

        // The stub does not resolve references; it only reports a reference once its name
        // collides with a previously registered name.
        assertEquals(1, result.diagnostics.size)
        assertEquals(
            "Reference 'Nat' conflicts with a global declaration.",
            result.diagnostics.single().message,
        )
    }

    @Test
    fun `reports duplicate global declarations`() {
        val result =
            StubSurfaceTypeChecker()
                .check(
                    SurfaceProgram(
                        declarations = listOf(
                            axiom("Nat", SurfaceTypeTerm()),
                            axiom("Nat", SurfaceTypeTerm()),
                        ),
                        rules = emptyList(),
                        fileName = null,
                    ),
                )

        assertEquals(
            listOf("Duplicate axiom declaration 'Nat'."),
            result.diagnostics.map { it.message },
        )
        assertTrue(
            result.diagnostics.all { it.severity == SurfaceDiagnosticSeverity.Error },
        )
    }

    @Test
    fun `traverses both a definition type and value including applications`() {
        val result =
            StubSurfaceTypeChecker()
                .check(
                    SurfaceProgram(
                        declarations = listOf(
                            axiom("A", SurfaceTypeTerm()),
                            SurfaceDefDecl(
                                name("f"),
                                SurfaceNameRef(name("A")),
                                SurfaceApp(
                                    SurfaceNameRef(name("A")),
                                    SurfaceTypeTerm(),
                                ),
                                null,
                            ),
                        ),
                        rules = emptyList(),
                        fileName = null,
                    ),
                )

        assertEquals(
            listOf(
                "Reference 'A' conflicts with a global declaration.",
                "Reference 'A' conflicts with a global declaration.",
            ),
            result.diagnostics.map { it.message },
        )
    }

    @Test
    fun `reports a binder that collides with a global declaration`() {
        val result =
            StubSurfaceTypeChecker()
                .check(
                    SurfaceProgram(
                        declarations = listOf(
                            axiom("A", SurfaceTypeTerm()),
                            axiom(
                                "idType",
                                SurfacePi(
                                    SurfaceBinder(
                                        name("A"),
                                        SurfaceTypeTerm(),
                                    ),
                                    SurfaceTypeTerm(),
                                ),
                            ),
                        ),
                        rules = emptyList(),
                        fileName = null,
                    ),
                )

        assertEquals(
            listOf("Binder 'A' conflicts with a global declaration."),
            result.diagnostics.map { it.message },
        )
    }

    private fun axiom(
        name: String,
        type: camp.delta.deltatypetheory.core.surface.model.SurfaceTerm,
    ) = SurfaceAxiomDecl(this.name(name), type, null)

    private fun name(value: String) = SurfaceName(value)
}
