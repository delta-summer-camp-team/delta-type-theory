package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.elaborate.LocalContext
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.ElaborationContext
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceTerm

class TermElaborator(
    private val elaborationContext: ElaborationContext,
    private val diagnosticReporter: DiagnosticReporter,
) {

    fun checkTerm(
        term: SurfaceTerm,
        expectedType: CoreTerm,
        localContext: LocalContext,
    ): CoreTerm? {
        return null
    }
}
