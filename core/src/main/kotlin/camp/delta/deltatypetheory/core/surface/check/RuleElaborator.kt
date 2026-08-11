package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.CoreRule
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfacePi
import camp.delta.deltatypetheory.core.surface.model.SurfaceLambda
import camp.delta.deltatypetheory.core.surface.model.SurfaceRuleDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceTerm
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import camp.delta.deltatypetheory.core.surface.model.SurfaceMeta

class RuleElaborator(
    private val elaborationContext: ElaborationContext,
    private val diagnosticReporter: DiagnosticReporter,
) {

    fun elaborate(rule: SurfaceRuleDecl): CoreRule? {
        if (elaborationContext.rules.any { it.name == rule.name.value }) {
            diagnosticReporter.reportError(
                "Rule '${rule.name.value}' already declared",
                rule.range,
            )
            return null
        }

        val variables = linkedSetOf<GlobalName>()

        val lhs = elaborateLhs(rule.lhs, variables) ?: return null
        val rhs = elaborateRhs(rule.rhs, variables) ?: return null

        return CoreRule(
            name = rule.name.value,
            lhs = lhs,
            rhs = rhs,
            variables = variables,
        )
    }

    private fun elaborateLhs(
        term: SurfaceTerm,
        variables: MutableSet<GlobalName>,
    ): CoreTerm? = when (term) {
        is SurfaceTypeTerm -> TypeTerm

        is SurfaceNameRef -> {
            val name = term.name.value
            val global = elaborationContext.lookupGlobal(name)

            if (global != null) {
                GlobalRef(global.name)
            } else {
                val variable = GlobalName(name)
                variables.add(variable)
                GlobalRef(variable)
            }
        }

        is SurfaceApp -> {
            val function = elaborateLhs(term.function, variables) ?: return null
            val argument = elaborateLhs(term.argument, variables) ?: return null
            App(function, argument)
        }

        is SurfaceLambda,
        is SurfacePi,
        is SurfaceMeta -> {
            diagnosticReporter.reportError(
                "Unsupported term in rule pattern",
                null,
            )
            null
        }
    }

    private fun elaborateRhs(
        term: SurfaceTerm,
        variables: Set<GlobalName>,
    ): CoreTerm? = when (term) {
        is SurfaceTypeTerm -> TypeTerm

        is SurfaceNameRef -> {
            val name = GlobalName(term.name.value)
            val global = elaborationContext.lookupGlobal(name.value)

            when {
                global != null -> GlobalRef(global.name)

                name in variables -> GlobalRef(name)

                else -> {
                    diagnosticReporter.reportError(
                        "Rule RHS uses unknown variable '${name.value}'",
                        null,
                    )
                    null
                }
            }
        }

        is SurfaceApp -> {
            val function = elaborateRhs(term.function, variables) ?: return null
            val argument = elaborateRhs(term.argument, variables) ?: return null
            App(function, argument)
        }

        is SurfaceLambda,
        is SurfacePi,
        is SurfaceMeta -> {
            diagnosticReporter.reportError(
                "Unsupported term in rule replacement",
                null,
            )
            null
        }
    }
}
