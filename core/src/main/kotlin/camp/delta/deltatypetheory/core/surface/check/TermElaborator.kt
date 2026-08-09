package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.elaborate.LocalContext
import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.kernel.model.TypedCoreTerm
import camp.delta.deltatypetheory.core.kernel.reduction.definitionallyEqual
import camp.delta.deltatypetheory.core.kernel.reduction.substituteTop
import camp.delta.deltatypetheory.core.kernel.reduction.whnf
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceLambda
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfacePi
import camp.delta.deltatypetheory.core.surface.model.SurfaceTerm
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm

class TermElaborator(
    private val elaborationContext: ElaborationContext,
    private val diagnosticReporter: DiagnosticReporter,
) {

    fun checkTerm(
        term: SurfaceTerm,
        expectedType: CoreTerm,
        localContext: LocalContext,
    ): CoreTerm? {
        val expectedWhnf = whnf(expectedType, elaborationContext)

        if (term is SurfaceLambda && expectedWhnf is Pi) {
            return checkLambdaAgainstPi(term, expectedWhnf, localContext)
        }

        val inferred = inferTerm(term, localContext) ?: return null
        if (definitionallyEqual(inferred.type, expectedType, elaborationContext)) {
            return inferred.term
        }

        diagnosticReporter.reportError(
            "Type mismatch: expected $expectedType, got ${inferred.type}",
            null,
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
                val global = elaborationContext.lookupGlobal(name)
                if (global != null) {
                    TypedCoreTerm(GlobalRef(global.name), global.type)
                } else {
                    diagnosticReporter.reportError("Name '$name' not found", null)
                    null
                }
            }
        }

        is SurfacePi -> {
            val typeA = inferTerm(term.binder.type, localContext) ?: return null
            if (!definitionallyEqual(typeA.type, TypeTerm, elaborationContext)) {
                diagnosticReporter.reportError("Expected Type for Pi parameter type", null)
                return null
            }
            val extended = localContext.push(term.binder.name.value, typeA.term)
            val typeB = inferTerm(term.body, extended) ?: return null
            if (!definitionallyEqual(typeB.type, TypeTerm, elaborationContext)) {
                diagnosticReporter.reportError("Expected Type for Pi body type", null)
                return null
            }
            TypedCoreTerm(Pi(typeA.term, typeB.term), TypeTerm)
        }

        is SurfaceLambda -> {
            val typeA = inferTerm(term.binder.type, localContext) ?: return null
            if (!definitionallyEqual(typeA.type, TypeTerm, elaborationContext)) {
                diagnosticReporter.reportError("Expected Type for Lambda parameter type", null)
                return null
            }
            val extended = localContext.push(term.binder.name.value, typeA.term)
            val body = inferTerm(term.body, extended) ?: return null
            TypedCoreTerm(Lambda(typeA.term, body.term), Pi(typeA.term, body.type))
        }

        is SurfaceApp -> {
            val function = inferTerm(term.function, localContext) ?: return null
            val functionType = whnf(function.type, elaborationContext)
            if (functionType !is Pi) {
                diagnosticReporter.reportError("Cannot apply non-function", null)
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
        localContext: LocalContext,
    ): CoreTerm? {
        val typeA = inferTerm(term.binder.type, localContext) ?: return null
        if (!definitionallyEqual(typeA.type, TypeTerm, elaborationContext)) {
            diagnosticReporter.reportError("Expected Type for Lambda parameter type", null)
            return null
        }
        if (!definitionallyEqual(typeA.term, expectedType.parameterType, elaborationContext)) {
            diagnosticReporter.reportError(
                "Lambda parameter type mismatch: expected ${expectedType.parameterType}, got ${typeA.term}",
                null,
            )
            return null
        }
        val extended = localContext.push(term.binder.name.value, typeA.term)
        val body = checkTerm(term.body, expectedType.body, extended) ?: return null
        return Lambda(typeA.term, body)
    }
}
