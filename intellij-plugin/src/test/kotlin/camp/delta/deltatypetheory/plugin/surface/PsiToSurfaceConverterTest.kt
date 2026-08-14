package camp.delta.deltatypetheory.plugin.surface

import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceBinder
import camp.delta.deltatypetheory.core.surface.model.SurfaceDefDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceLambda
import camp.delta.deltatypetheory.core.surface.model.SurfaceMeta
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfacePi
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceRuleDecl
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

        assertEquals("test.delta", result.fileName)
        assertEquals(1, result.declarations.size)
        assertTrue(result.rules.isEmpty())

        val declaration = result.declarations.single() as SurfaceAxiomDecl

        assertEquals(SurfaceName("Nat"), declaration.name)
        assertTrue(declaration.type is SurfaceTypeTerm)
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

        val first = result.declarations[0] as SurfaceAxiomDecl
        val second = result.declarations[1] as SurfaceAxiomDecl

        assertEquals(SurfaceName("Nat"), first.name)
        assertTrue(first.type is SurfaceTypeTerm)

        assertEquals(SurfaceName("Bool"), second.name)
        assertTrue(second.type is SurfaceTypeTerm)
    }

    fun testConvertsNameReference() {
        val file = myFixture.configureByText(
            "test.delta",
            "axiom Nat : Nat;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        val declaration = result.declarations.single() as SurfaceAxiomDecl
        val type = declaration.type as SurfaceNameRef

        assertEquals(SurfaceName("Nat"), declaration.name)
        assertEquals(SurfaceName("Nat"), type.name)
    }

    fun testConvertsRule() {
        val file = myFixture.configureByText(
            "test.delta",
            "rule beta: x ↦ x;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(1, result.rules.size)

        assertEquals(1, result.rules.size)

        val rule = result.rules.single()

        assertEquals(SurfaceName("beta"), rule.name)

        val lhs = rule.lhs as SurfaceNameRef
        val rhs = rule.rhs as SurfaceNameRef

        assertEquals(SurfaceName("x"), lhs.name)
        assertEquals(SurfaceName("x"), rhs.name)
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

    fun testConvertsArrowTypeToAnonymousBinder() {
        val file = myFixture.configureByText(
            "test.delta",
            "axiom f : Nat → Nat → Nat;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        val type = (result.declarations.single() as SurfaceAxiomDecl).type as SurfacePi

        assertEquals(null, type.binder.name)

        val firstDomain = type.binder.type as SurfaceNameRef
        assertEquals(SurfaceName("Nat"), firstDomain.name)

        val inner = type.body as SurfacePi

        assertEquals(null, inner.binder.name)

        val secondDomain = inner.binder.type as SurfaceNameRef
        assertEquals(SurfaceName("Nat"), secondDomain.name)

        val resultType = inner.body as SurfaceNameRef
        assertEquals(SurfaceName("Nat"), resultType.name)
    }

    fun testConvertsLambdaWithoutTypeToMeta() {
        val file = myFixture.configureByText(
            "test.delta",
            "def id : Nat → Nat := λ m => m;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        val declaration = result.declarations.single() as SurfaceDefDecl

        assertEquals(SurfaceName("id"), declaration.name)

        val type = declaration.type as SurfacePi
        assertEquals(null, type.binder.name)
        assertEquals(
            SurfaceName("Nat"),
            (type.binder.type as SurfaceNameRef).name,
        )
        assertEquals(
            SurfaceName("Nat"),
            (type.body as SurfaceNameRef).name,
        )

        val value = declaration.value as SurfaceLambda

        assertEquals(SurfaceName("m"), value.binder.name)

        val meta = value.binder.type as SurfaceMeta
        assertEquals(0, meta.id)

        assertEquals(
            SurfaceName("m"),
            (value.body as SurfaceNameRef).name,
        )
    }

    fun testConvertsNestedBareLambdas() {
        val file = myFixture.configureByText(
            "test.delta",
            "def plus : Nat → Nat → Nat := λ m => λ n => m;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        val value = (result.declarations.single() as SurfaceDefDecl).value as SurfaceLambda

        assertEquals(SurfaceName("m"), value.binder.name)
        assertEquals(
            0,
            (value.binder.type as SurfaceMeta).id,
        )

        val inner = value.body as SurfaceLambda

        assertEquals(SurfaceName("n"), inner.binder.name)
        assertEquals(
            1,
            (inner.binder.type as SurfaceMeta).id,
        )

        assertEquals(
            SurfaceName("m"),
            (inner.body as SurfaceNameRef).name,
        )

    }

    fun testNumbersMetasAcrossFile() {
        val file = myFixture.configureByText(
            "test.delta",
            """
            def a : Nat → Nat := λ x => x;
            def b : Nat → Nat := λ y => y;
            """.trimIndent()
        )

        val result = PsiToSurfaceConverter().convert(file)

        val defA = result.declarations[0] as SurfaceDefDecl
        val defB = result.declarations[1] as SurfaceDefDecl

        assertEquals(
            0,
            ((defA.value as SurfaceLambda).binder.type as SurfaceMeta).id,
        )

        assertEquals(
            1,
            ((defB.value as SurfaceLambda).binder.type as SurfaceMeta).id,
        )
    }
}
