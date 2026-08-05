package camp.delta.deltatypetheory.core.surface

import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnostic
import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnosticSeverity
import camp.delta.deltatypetheory.core.surface.model.SourceRange
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SurfaceModelAndDiagnosticTest {
    @Test
    fun `surface names reject blank identifiers`() {
        assertFailsWith<IllegalArgumentException> { SurfaceName("   ") }
    }

    @Test
    fun `reporter preserves diagnostics and recognises errors`() {
        val reporter = DiagnosticReporter()
        val range = SourceRange("example.delta", 4, 9)

        assertFalse(reporter.hasErrors())

        reporter.report(SurfaceDiagnostic(SurfaceDiagnosticSeverity.Warning, "be careful", range))
        assertFalse(reporter.hasErrors())

        reporter.reportError("bad declaration", range)

        assertTrue(reporter.hasErrors())
        assertEquals(
            listOf(
                SurfaceDiagnostic(SurfaceDiagnosticSeverity.Warning, "be careful", range),
                SurfaceDiagnostic(
                    SurfaceDiagnosticSeverity.Error,
                    "bad declaration",
                    range,
                ),
            ),
            reporter.all(),
        )
    }
}
