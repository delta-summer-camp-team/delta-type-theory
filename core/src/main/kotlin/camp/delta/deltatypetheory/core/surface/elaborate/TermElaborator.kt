package camp.delta.deltatypetheory.core.surface.elaborate

import camp.delta.deltatypetheory.core.kernel.definitionallyEqual
import camp.delta.deltatypetheory.core.kernel.substituteTop
import camp.delta.deltatypetheory.core.kernel.whnf
import camp.delta.deltatypetheory.core.kernel.elaborate.LocalContext
import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding
import camp.delta.deltatypetheory.core.kernel.model.*
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.*

class TermElaborator(
    private val ctx: ElaborationContext,
    private val reporter: DiagnosticReporter
) {

    fun checkTerm(
        term: SurfaceTerm,
        expectedType: CoreTerm,
        localContext: LocalContext
    ): CoreTerm? {
        val expectedWhnf = whnf(expectedType, ctx)

        if (term is SurfaceLambda && expectedWhnf is Pi) {
            return checkLambdaAgainstPi(term, expectedWhnf, localContext)
        }

        val inferred = inferTerm(term, localContext) ?: return null
        if (definitionallyEqual(inferred.type, expectedType, ctx)) {
            return inferred.term
        }

        reporter.reportError(
            "Type mismatch: expected ${expectedType}, got ${inferred.type}",
            null
        )
        return null
    }

    fun inferTerm(term: SurfaceTerm, localContext: LocalContext): TypedCoreTerm? = when (term) {
        is SurfaceTypeTerm -> TypedCoreTerm(TypeTerm, TypeTerm)

        is SurfaceNameRef -> {
            val name = term.name.value
            val local = localContext.resolve(name)
            if (local != null) {
                TypedCoreTerm(BoundVar(local.deBruijnIndex), local.type)
            } else {
                val global = ctx.lookupGlobal(name)
                if (global != null) {
                    TypedCoreTerm(GlobalRef(global.name), global.type)
                } else {
                    reporter.reportError("Name '$name' not found", null)
                    null
                }
            }
        }

        is SurfacePi -> {
            val typeA = inferTerm(term.binder.type, localContext) ?: return null
            if (!definitionallyEqual(typeA.type, TypeTerm, ctx)) {
                reporter.reportError("Expected Type for Pi parameter type", null)
                return null
            }
            val extended = localContext.push(term.binder.name.value, typeA.term)
            val typeB = inferTerm(term.body, extended) ?: return null
            if (!definitionallyEqual(typeB.type, TypeTerm, ctx)) {
                reporter.reportError("Expected Type for Pi body type", null)
                return null
            }
            TypedCoreTerm(Pi(typeA.term, typeB.term), TypeTerm)
        }

        is SurfaceLambda -> {
            val typeA = inferTerm(term.binder.type, localContext) ?: return null
            if (!definitionallyEqual(typeA.type, TypeTerm, ctx)) {
                reporter.reportError("Expected Type for Lambda parameter type", null)
                return null
            }
            val extended = localContext.push(term.binder.name.value, typeA.term)
            val body = inferTerm(term.body, extended) ?: return null
            TypedCoreTerm(Lambda(typeA.term, body.term), Pi(typeA.term, body.type))
        }

        is SurfaceApp -> {
            val function = inferTerm(term.function, localContext) ?: return null
            val functionType = whnf(function.type, ctx)
            if (functionType !is Pi) {
                reporter.reportError("Cannot apply non-function", null)
                return null
            }
            val argument = checkTerm(term.argument, functionType.parameterType, localContext)
                ?: return null
            val resultType = substituteTop(functionType.body, argument)
            TypedCoreTerm(App(function.term, argument), resultType)
        }
    }

    private fun checkLambdaAgainstPi(
        term: SurfaceLambda,
        expectedType: Pi,
        localContext: LocalContext
    ): CoreTerm? {
        val typeA = inferTerm(term.binder.type, localContext) ?: return null
        if (!definitionallyEqual(typeA.type, TypeTerm, ctx)) {
            reporter.reportError("Expected Type for Lambda parameter type", null)
            return null
        }
        if (!definitionallyEqual(typeA.term, expectedType.parameterType, ctx)) {
            reporter.reportError(
                "Lambda parameter type mismatch: expected ${expectedType.parameterType}, got ${typeA.term}",
                null
            )
            return null
        }
        val extended = localContext.push(term.binder.name.value, typeA.term)
        val body = checkTerm(term.body, expectedType.body, extended) ?: return null
        return Lambda(typeA.term, body)
    }
}
