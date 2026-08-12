package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.elaborate.LocalContext
import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.kernel.model.TypedCoreTerm
import camp.delta.deltatypetheory.core.kernel.reduction.definitionallyEqual
import camp.delta.deltatypetheory.core.kernel.reduction.substituteTop
import camp.delta.deltatypetheory.core.kernel.reduction.whnf
import camp.delta.deltatypetheory.core.surface.diagnostic.DiagnosticReporter
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceBinder
import camp.delta.deltatypetheory.core.surface.model.SurfaceLambda
import camp.delta.deltatypetheory.core.surface.model.SurfaceMeta
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
            "Type mismatch: '${formatSurfaceTerm(term)}' has type '${formatCoreTerm(inferred.type)}', " +
                "but is expected to have type '${formatCoreTerm(expectedType)}'.",
            term.range,
        )
        return null
    }

    fun inferTerm(term: SurfaceTerm, localContext: LocalContext): TypedCoreTerm? = when (term) {
        is SurfaceTypeTerm -> TypedCoreTerm(TypeTerm, TypeTerm)

        is SurfaceMeta -> {
            // TODO(M5): resolving metas is C10's job; surface-level output only.
            diagnosticReporter.reportError(
                "Cannot infer a type for metavariable '?${term.id}'.",
                term.range,
            )
            null
        }

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
                    diagnosticReporter.reportError("Unknown name '$name'.", term.range)
                    null
                }
            }
        }

        is SurfacePi -> {
            val typeA = inferTerm(term.binder.type, localContext) ?: return null
            if (!definitionallyEqual(typeA.type, TypeTerm, elaborationContext)) {
                reportExpectedType(term.binder.type, typeA.type)
                return null
            }
            val extended = localContext.push(binderName(term.binder), typeA.term)
            val typeB = inferTerm(term.body, extended) ?: return null
            if (!definitionallyEqual(typeB.type, TypeTerm, elaborationContext)) {
                reportExpectedType(term.body, typeB.type)
                return null
            }
            TypedCoreTerm(Pi(typeA.term, typeB.term), TypeTerm)
        }

        is SurfaceLambda -> {
            val typeA = inferTerm(term.binder.type, localContext) ?: return null
            if (!definitionallyEqual(typeA.type, TypeTerm, elaborationContext)) {
                reportExpectedType(term.binder.type, typeA.type)
                return null
            }
            val extended = localContext.push(binderName(term.binder), typeA.term)
            val body = inferTerm(term.body, extended) ?: return null
            TypedCoreTerm(Lambda(typeA.term, body.term), Pi(typeA.term, body.type))
        }

        is SurfaceApp -> {
            val function = inferTerm(term.function, localContext) ?: return null
            val functionType = whnf(function.type, elaborationContext)
            if (functionType !is Pi) {
                diagnosticReporter.reportError(
                    "Cannot apply '${formatSurfaceTerm(term.function)}': it has type " +
                        "'${formatCoreTerm(function.type)}', but a function type is required.",
                    term.function.range,
                )
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
        val parameterType =
            if (term.binder.type is SurfaceMeta) {
                expectedType.parameterType
            } else {
                val typeA = inferTerm(term.binder.type, localContext) ?: return null

                if (!definitionallyEqual(typeA.type, TypeTerm, elaborationContext)) {
                    reportExpectedType(term.binder.type, typeA.type)
                    return null
                }

                if (!definitionallyEqual(
                        typeA.term,
                        expectedType.parameterType,
                        elaborationContext,
                    )
                ) {
                    diagnosticReporter.reportError(
                        "Lambda parameter type mismatch: expected '${formatCoreTerm(expectedType.parameterType)}', " +
                            "but the annotation is '${formatCoreTerm(typeA.term)}'.",
                        term.binder.range ?: term.range,
                    )
                    return null
                }

                typeA.term
            }

        val extended = localContext.push(
            binderName(term.binder),
            parameterType,
        )

        val body = checkTerm(
            term.body,
            expectedType.body,
            extended,
        ) ?: return null

        return Lambda(parameterType, body)
    }

    private fun binderName(binder: SurfaceBinder): String = binder.name?.value ?: "_"

    private fun reportExpectedType(term: SurfaceTerm, actualType: CoreTerm) {
        diagnosticReporter.reportError(
            "Expected a type, but '${formatSurfaceTerm(term)}' has type '${formatCoreTerm(actualType)}'.",
            term.range,
        )
    }

    private fun formatSurfaceTerm(term: SurfaceTerm): String = when (term) {
        is SurfaceTypeTerm -> "Type"
        is SurfaceNameRef -> term.name.value
        is SurfaceMeta -> "?${term.id}"
        is SurfacePi -> "(${formatSurfaceTerm(term.binder.type)}) → ${formatSurfaceTerm(term.body)}"
        is SurfaceLambda -> "λ (${binderName(term.binder)} : ${formatSurfaceTerm(term.binder.type)}). ${formatSurfaceTerm(term.body)}"
        is SurfaceApp -> "${formatSurfaceTerm(term.function)}(${formatSurfaceTerm(term.argument)})"
    }

    private fun formatCoreTerm(term: CoreTerm): String = when (term) {
        TypeTerm -> "Type"
        is GlobalRef -> term.name.value
        is GlobalName -> term.value
        is BoundVar -> "local variable #${term.index}"
        is Pi -> "(${formatCoreTerm(term.parameterType)}) → ${formatCoreTerm(term.body)}"
        is Lambda -> "λ (_ : ${formatCoreTerm(term.parameterType)}). ${formatCoreTerm(term.body)}"
        is App -> "${formatCoreTerm(term.function)}(${formatCoreTerm(term.argument)})"
    }
}
