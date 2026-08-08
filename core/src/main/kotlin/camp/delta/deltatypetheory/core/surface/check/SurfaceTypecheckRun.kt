package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.model.ElaborationContext
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceDefDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram

class SurfaceTypecheckRun {

    private val elaborationContext = ElaborationContext()
    private val diagnosticReporter = DiagnosticReporter()

    private val termElaborator = TermElaborator(
        elaborationContext = elaborationContext,
        diagnosticReporter = diagnosticReporter,
    )

    fun check(program: SurfaceProgram): SurfaceCheckResult {
        for (declaration in program.declarations) {
            when (declaration) {
                is SurfaceAxiomDecl -> elaborateAxiom(declaration)
                is SurfaceDefDecl -> elaborateDef(declaration)
            }
        }

        return SurfaceCheckResult(diagnosticReporter.all())
    }

    private fun elaborateAxiom(decl: SurfaceAxiomDecl) {
    }

    private fun elaborateDef(decl: SurfaceDefDecl) {
    }
}
