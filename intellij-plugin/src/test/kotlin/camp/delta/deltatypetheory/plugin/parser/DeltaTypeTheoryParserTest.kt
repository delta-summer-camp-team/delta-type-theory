package camp.delta.deltatypetheory.plugin.parser

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class DeltaTypeTheoryParserTest : BasePlatformTestCase() {

    private fun assertParses(text: String) {
        val file = myFixture.configureByText("test.delta", text)

        val errors = PsiTreeUtil.collectElementsOfType(file, PsiErrorElement::class.java)

        assertTrue(
            "Unexpected parse errors: ${errors.map { it.errorDescription }}",
            errors.isEmpty()
        )
    }

    fun testParsesLambdaWithoutType() {
        assertParses("def id : Nat → Nat := λ m => m;")
    }

    fun testParsesArrowType() {
        assertParses("axiom f : Nat → Nat → Nat;")
    }

    fun testParsesNamedPiFollowedByArrow() {
        assertParses("axiom g : (x : Nat) → Nat → Nat;")
    }

    fun testParsesNestedLambdasWithoutTypes() {
        assertParses("def plus : Nat → Nat → Nat := λ m => λ n => m;")
    }

    fun testParsesLambdaWithoutTypeInsideApplication() {
        assertParses("def h : Nat := natRec(λ x => Nat)(n)(λ x => λ ih => succ(ih))(m);")
    }

    fun testParsesPlanExample() {
        assertParses(
            """
            axiom natRec :
              (P : (n : Nat) → Type) →
              (_ : P(zero)) →
              (s : (n : Nat) → (ih : P(n)) → P(succ(n))) →
              (n : Nat) →
              P(n);

            def plus : (m : Nat) → (n : Nat) → Nat :=
              λ m => λ n =>
              natRec(λ x => Nat)(n)(
              λ x => λ ih => succ(ih)
              )(m);
            """.trimIndent()
        )
    }
}
