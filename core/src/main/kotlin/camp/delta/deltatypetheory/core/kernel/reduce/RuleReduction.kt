package camp.delta.deltatypetheory.core.kernel.reduce

import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreRule
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm

fun applyRule(
    rule: CoreRule,
    term: CoreTerm,
): CoreTerm? {
    val substitution = mutableMapOf<GlobalName, CoreTerm>()

    if (!matchRule(
            pattern = rule.lhs,
            term = term,
            variables = rule.variables,
            substitution = substitution,
        )
    ) {
        return null
    }

    return substituteRuleVariables(
        term = rule.rhs,
        variables = rule.variables,
        substitution = substitution,
    )
}

private fun matchRule(
    pattern: CoreTerm,
    term: CoreTerm,
    variables: Set<GlobalName>,
    substitution: MutableMap<GlobalName, CoreTerm>,
): Boolean {
    if (pattern is GlobalRef && pattern.name in variables) {
        val previous = substitution[pattern.name]

        if (previous == null) {
            substitution[pattern.name] = term
            return true
        }

        return previous == term
    }

    return when {
        pattern is GlobalRef && term is GlobalRef ->
            pattern.name == term.name

        pattern is App && term is App ->
            matchRule(
                pattern.function,
                term.function,
                variables,
                substitution,
            ) &&
                matchRule(
                    pattern.argument,
                    term.argument,
                    variables,
                    substitution,
                )

        pattern is BoundVar && term is BoundVar ->
            pattern.index == term.index

        pattern is TypeTerm && term is TypeTerm ->
            true

        pattern is Lambda && term is Lambda ->
            matchRule(
                pattern.parameterType,
                term.parameterType,
                variables,
                substitution,
            ) &&
                matchRule(
                    pattern.body,
                    term.body,
                    variables,
                    substitution,
                )

        pattern is Pi && term is Pi ->
            matchRule(
                pattern.parameterType,
                term.parameterType,
                variables,
                substitution,
            ) &&
                matchRule(
                    pattern.body,
                    term.body,
                    variables,
                    substitution,
                )

        else -> false
    }
}

private fun substituteRuleVariables(
    term: CoreTerm,
    variables: Set<GlobalName>,
    substitution: Map<GlobalName, CoreTerm>,
): CoreTerm =
    when (term) {
        is GlobalRef -> {
            if (term.name in variables) {
                substitution[term.name] ?: term
            } else {
                term
            }
        }

        is App -> App(
            substituteRuleVariables(term.function, variables, substitution),
            substituteRuleVariables(term.argument, variables, substitution),
        )

        is Lambda -> Lambda(
            substituteRuleVariables(term.parameterType, variables, substitution),
            substituteRuleVariables(term.body, variables, substitution),
        )

        is Pi -> Pi(
            substituteRuleVariables(term.parameterType, variables, substitution),
            substituteRuleVariables(term.body, variables, substitution),
        )

        is BoundVar,
        is TypeTerm,
        is GlobalName -> term
    }
