package camp.delta.deltatypetheory.plugin.surface

import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.test.Test
import kotlin.test.assertEquals

class PsiToSurfaceConverterTest : BasePlatformTestCase() {

    @Test
    fun `converts axiom Nat Type`() {
        val file = myFixture.configureByText(
            "test.delta",
            "axiom Nat : Type;"
        )

        val result = PsiToSurfaceConverter().convert(file)

        assertEquals(
            SurfaceProgram(
                declarations = listOf(
                    SurfaceAxiomDecl(
                        name = SurfaceName("Nat"),
                        type = SurfaceTypeTerm,
                        range = null,
                    )
                ),
                fileName = "test.delta",
            ),
            result,
        )
    }
}