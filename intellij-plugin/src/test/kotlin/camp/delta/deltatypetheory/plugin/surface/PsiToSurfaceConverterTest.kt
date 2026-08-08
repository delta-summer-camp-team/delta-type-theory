package camp.delta.deltatypetheory.plugin.surface

import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class PsiToSurfaceConverterTest : BasePlatformTestCase() {

    fun testConvertsAxiomNatType() {
        val file =
            myFixture.configureByText(
                "test.delta",
                "axiom Nat : Type;",
            )

        val result =
            PsiToSurfaceConverter()
                .convert(file)

        assertEquals(
            SurfaceProgram(
                declarations = listOf(
                    SurfaceAxiomDecl(
                        name = SurfaceName("Nat"),
                        type = SurfaceTypeTerm,
                        range = null,
                    ),
                ),
                fileName = "test.delta",
            ),
            result,
        )
    }
    fun testPreservesDeclarationOrder() {
        val file = myFixture.configureByText(
            "test.delta",
            """
        axiom Nat : Type;
        axiom Bool : Type;
        """.trimIndent()
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(2, result.declarations.size)

        assertEquals(
            SurfaceAxiomDecl(
                name = SurfaceName("Nat"),
                type = SurfaceTypeTerm,
                range = null,
            ),
            result.declarations[0],
        )

        assertEquals(
            SurfaceAxiomDecl(
                name = SurfaceName("Bool"),
                type = SurfaceTypeTerm,
                range = null,
            ),
            result.declarations[1],
        )
    }

    fun testConvertsNameReference() {
        val file = myFixture.configureByText(
            "test.delta",
            "axiom Nat : Nat;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(
            SurfaceAxiomDecl(
                name = SurfaceName("Nat"),
                type = camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef(
                    SurfaceName("Nat")
                ),
                range = null,
            ),
            result.declarations.single(),
        )
    }
}
