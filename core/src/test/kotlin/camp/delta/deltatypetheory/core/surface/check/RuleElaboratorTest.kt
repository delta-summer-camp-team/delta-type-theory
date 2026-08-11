package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfaceRuleDecl
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RuleElaboratorTest {

    @Test
    fun `reports unknown variable in rule rhs`() {
        val context = ElaborationContext()

        context.addGlobal(
            GlobalBinding(
                name = GlobalName("natRec"),
                type = TypeTerm,
                value = null,
            )
        )

        context.addGlobal(
            GlobalBinding(
                name = GlobalName("zero"),
                type = TypeTerm,
                value = null,
            )
        )

        val reporter = DiagnosticReporter()
        val elaborator = RuleElaborator(context, reporter)

        val p = SurfaceNameRef(SurfaceName("P"))
        val z = SurfaceNameRef(SurfaceName("z"))
        val s = SurfaceNameRef(SurfaceName("s"))

        val lhs =
            SurfaceApp(
                SurfaceApp(
                    SurfaceApp(
                        SurfaceApp(
                            SurfaceNameRef(SurfaceName("natRec")),
                            p,
                        ),
                        z,
                    ),
                    s,
                ),
                SurfaceNameRef(SurfaceName("zero")),
            )

        val rule = SurfaceRuleDecl(
            name = SurfaceName("bad"),
            lhs = lhs,
            rhs = SurfaceNameRef(SurfaceName("x")),
            range = null,
        )

        val result = elaborator.elaborate(rule)

        assertNull(result)
        assertTrue(reporter.all().isNotEmpty())
    }
}