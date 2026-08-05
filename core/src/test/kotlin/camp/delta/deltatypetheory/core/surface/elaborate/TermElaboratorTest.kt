package camp.delta.deltatypetheory.core.surface.elaborate

import camp.delta.deltatypetheory.core.kernel.elaborate.LocalContext
import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding
import camp.delta.deltatypetheory.core.kernel.model.*
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.*
import kotlin.test.*

class TermElaboratorTest {

    private val nat = GlobalName("Nat")
    private val zero = GlobalName("zero")
    private val succ = GlobalName("succ")

    private fun makeCtx(): ElaborationContext {
        val ctx = ElaborationContext()
        ctx.addGlobal(GlobalBinding(nat, TypeTerm, null))
        ctx.addGlobal(GlobalBinding(zero, GlobalRef(nat), null))
        ctx.addGlobal(GlobalBinding(succ, Pi(GlobalRef(nat), GlobalRef(nat)), null))
        return ctx
    }

    private fun makeElab(): Pair<TermElaborator, DiagnosticReporter> {
        val reporter = DiagnosticReporter()
        val elab = TermElaborator(makeCtx(), reporter)
        return elab to reporter
    }

    // ── inferTerm ──────────────────────────────────────

    @Test
    fun inferTypeTerm() {
        val (elab, _) = makeElab()
        val result = elab.inferTerm(SurfaceTypeTerm, LocalContext())
        assertNotNull(result)
        assertEquals(TypeTerm, result.type)
    }

    @Test
    fun inferGlobalNat() {
        val (elab, _) = makeElab()
        val result = elab.inferTerm(SurfaceNameRef(SurfaceName("Nat")), LocalContext())
        assertNotNull(result)
        assertEquals(TypeTerm, result.type)
    }

    @Test
    fun inferGlobalZero() {
        val (elab, _) = makeElab()
        val result = elab.inferTerm(SurfaceNameRef(SurfaceName("zero")), LocalContext())
        assertNotNull(result)
        assertEquals(GlobalRef(nat), result.type)
    }

    @Test
    fun inferLocalVariable() {
        val (elab, _) = makeElab()
        val ctx = LocalContext().push("x", GlobalRef(nat))
        val result = elab.inferTerm(SurfaceNameRef(SurfaceName("x")), ctx)
        assertNotNull(result)
        assertEquals(BoundVar(0), result.term)
        assertEquals(GlobalRef(nat), result.type)
    }

    @Test
    fun inferNameNotFound() {
        val (elab, reporter) = makeElab()
        val result = elab.inferTerm(SurfaceNameRef(SurfaceName("unknown")), LocalContext())
        assertNull(result)
        assertTrue(reporter.hasErrors())
    }

    // ── inferTerm: Pi ─────────────────────────────────

    @Test
    fun inferPi() {
        val (elab, _) = makeElab()
        val pi = SurfacePi(
            SurfaceBinder(SurfaceName("x"), SurfaceNameRef(SurfaceName("Nat"))),
            SurfaceNameRef(SurfaceName("Nat"))
        )
        val result = elab.inferTerm(pi, LocalContext())
        assertNotNull(result)
        assertEquals(TypeTerm, result.type)
        assertEquals(Pi(GlobalRef(nat), GlobalRef(nat)), result.term)
    }

    // ── inferTerm: Lambda ─────────────────────────────

    @Test
    fun inferLambda() {
        val (elab, _) = makeElab()
        val lam = SurfaceLambda(
            SurfaceBinder(SurfaceName("x"), SurfaceNameRef(SurfaceName("Nat"))),
            SurfaceNameRef(SurfaceName("x"))
        )
        val result = elab.inferTerm(lam, LocalContext())
        assertNotNull(result)
        assertEquals(Pi(GlobalRef(nat), GlobalRef(nat)), result.type)
    }

    // ── inferTerm: App ────────────────────────────────

    @Test
    fun inferApp() {
        val (elab, _) = makeElab()
        val app = SurfaceApp(
            SurfaceNameRef(SurfaceName("succ")),
            SurfaceNameRef(SurfaceName("zero"))
        )
        val result = elab.inferTerm(app, LocalContext())
        assertNotNull(result)
        assertEquals(GlobalRef(nat), result.type)
    }

    @Test
    fun inferAppNotFunction() {
        val (elab, reporter) = makeElab()
        val app = SurfaceApp(
            SurfaceNameRef(SurfaceName("zero")),
            SurfaceNameRef(SurfaceName("zero"))
        )
        val result = elab.inferTerm(app, LocalContext())
        assertNull(result)
        assertTrue(reporter.hasErrors())
    }

    // ── checkTerm ─────────────────────────────────────

    @Test
    fun checkTypeMatches() {
        val (elab, _) = makeElab()
        val result = elab.checkTerm(SurfaceTypeTerm, TypeTerm, LocalContext())
        assertNotNull(result)
        assertEquals(TypeTerm, result)
    }

    @Test
    fun checkTypeMismatch() {
        val (elab, reporter) = makeElab()
        val result = elab.checkTerm(
            SurfaceTypeTerm,
            GlobalRef(nat),
            LocalContext()
        )
        assertNull(result)
        assertTrue(reporter.hasErrors())
    }

    // ── checkLambdaAgainstPi ──────────────────────────

    @Test
    fun checkLambdaAgainstPi() {
        val (elab, _) = makeElab()
        val lam = SurfaceLambda(
            SurfaceBinder(SurfaceName("x"), SurfaceNameRef(SurfaceName("Nat"))),
            SurfaceNameRef(SurfaceName("x"))
        )
        val pi = Pi(GlobalRef(nat), GlobalRef(nat))
        val result = elab.checkTerm(lam, pi, LocalContext())
        assertNotNull(result)
        assertEquals(Lambda(GlobalRef(nat), BoundVar(0)), result)
    }

    @Test
    fun checkLambdaAgainstPiParamMismatch() {
        val (elab, reporter) = makeElab()
        val lam = SurfaceLambda(
            SurfaceBinder(SurfaceName("x"), SurfaceNameRef(SurfaceName("Nat"))),
            SurfaceNameRef(SurfaceName("x"))
        )
        val wrongPi = Pi(TypeTerm, TypeTerm)
        val result = elab.checkTerm(lam, wrongPi, LocalContext())
        assertNull(result)
        assertTrue(reporter.hasErrors())
    }
}
