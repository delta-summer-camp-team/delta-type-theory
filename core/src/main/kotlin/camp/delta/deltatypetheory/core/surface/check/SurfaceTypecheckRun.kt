package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.elaborate.LocalContext
import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceDefDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceRuleDecl

class SurfaceTypecheckRun {

    private val elaborationContext = ElaborationContext()
    private val diagnosticReporter = DiagnosticReporter()

    private val termElaborator = TermElaborator(
        elaborationContext = elaborationContext,
        diagnosticReporter = diagnosticReporter,
    )

    private val ruleElaborator = RuleElaborator(
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

        for (rule in program.rules) {
            elaborateRule(rule)
        }

        return SurfaceCheckResult(diagnosticReporter.all())
    }

    private fun elaborateAxiom(declaration: SurfaceAxiomDecl) {
        val name = declaration.name.value
        if (elaborationContext.lookupGlobal(name) != null) {
            diagnosticReporter.reportError("Duplicate declaration '$name'.", declaration.range)
            return
        }
        val type = termElaborator.checkTerm(declaration.type, TypeTerm, LocalContext()) ?: return
        elaborationContext.addGlobal(GlobalBinding(GlobalName(name), type, null))
    }

    private fun elaborateDef(declaration: SurfaceDefDecl) {
        val name = declaration.name.value
        if (elaborationContext.lookupGlobal(name) != null) {
            diagnosticReporter.reportError("Duplicate declaration '$name'.", declaration.range)
            return
        }
        val type: CoreTerm = termElaborator.checkTerm(declaration.type, TypeTerm, LocalContext())
            ?: return
        val value = termElaborator.checkTerm(declaration.value, type, LocalContext()) ?: return
        elaborationContext.addGlobal(GlobalBinding(GlobalName(name), type, value))
    }

    private fun elaborateRule(rule: SurfaceRuleDecl) {
        val coreRule = ruleElaborator.elaborate(rule) ?: return
        elaborationContext.addRule(coreRule)
    }


}
