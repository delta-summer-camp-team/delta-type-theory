package camp.delta.deltatypetheory.plugin.surface

import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfaceRuleDecl


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
                rules = emptyList(),
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

    fun testConvertsRule() {
        val file = myFixture.configureByText(
            "test.delta",
            "rule beta: x ↦ x;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(1, result.rules.size)

        assertEquals(
            SurfaceRuleDecl(
                name = SurfaceName("beta"),
                lhs = SurfaceNameRef(
                    SurfaceName("x")
                ),
                rhs = SurfaceNameRef(
                    SurfaceName("x")
                ),
                range = null,
            ),
            result.rules.single(),
        )
    }

    fun testParsesNatRecRule() {
        val file = myFixture.configureByText(
            "test.delta",
            "rule natRec.zero: natRec(P)(z)(s)(zero) ↦ z;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(1, result.rules.size)
        assertEquals(
            SurfaceName("natRec.zero"),
            result.rules.single().name,
        )
    }

    fun testPreservesAllRules() {
        val file = myFixture.configureByText(
            "test.delta",
            """
        rule beta: x ↦ x;
        rule gamma: y ↦ y;
        """.trimIndent()
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(2, result.rules.size)
        assertEquals(SurfaceName("beta"), result.rules[0].name)
        assertEquals(SurfaceName("gamma"), result.rules[1].name)
    }

    fun testConvertsNatRecRules() {
        val file = myFixture.configureByText(
            "test.delta",
            """
        rule natRec.zero: natRec(P)(z)(s)(zero) ↦ z;
        rule natRec.succ: natRec(P)(z)(s)(succ(n)) ↦ s(n)(natRec(P)(z)(s)(n));
        """.trimIndent()
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(2, result.rules.size)

        assertEquals(
            SurfaceName("natRec.zero"),
            result.rules[0].name,
        )

        assertEquals(
            SurfaceName("natRec.succ"),
            result.rules[1].name,
        )
    }
}
