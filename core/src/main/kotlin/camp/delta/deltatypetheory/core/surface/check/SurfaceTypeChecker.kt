package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.diagnostic.*
import camp.delta.deltatypetheory.core.surface.model.*

interface SurfaceTypeChecker {
    fun check(program: SurfaceProgram): SurfaceCheckResult
}

class StubSurfaceTypeChecker : SurfaceTypeChecker {

    override fun check(program: SurfaceProgram): SurfaceCheckResult {
        val usedGlobalNames = HashSet<SurfaceName>()
        val usedLocalNames = ArrayDeque<SurfaceName>()
        val reporter = DiagnosticReporter()

        for (declaration in program.declarations) {
            checkGlobal(
                declaration,
                usedGlobalNames,
                usedLocalNames,
                reporter,
            )
        }

        return SurfaceCheckResult(reporter.all())
    }

    private fun checkGlobal(
        declaration: SurfaceDecl,
        usedGlobalNames: HashSet<SurfaceName>,
        usedLocalNames: ArrayDeque<SurfaceName>,
        reporter: DiagnosticReporter,
    ) {
        val type = when (declaration) {
            is SurfaceDefDecl -> "definition"
            is SurfaceAxiomDecl -> "axiom"
        }

        val name = declaration.name

        if (name in usedGlobalNames) {
            reporter.reportError(
                "Name ${name.value} already used for another $type",
                declaration.range,
            )
        } else {
            usedGlobalNames.add(name)
        }

        checkTerm(
            declaration.type,
            usedGlobalNames,
            usedLocalNames,
            reporter,
        )

        if (declaration is SurfaceDefDecl) {
            checkTerm(
                declaration.value,
                usedGlobalNames,
                usedLocalNames,
                reporter,
            )
        }
    }

    private fun checkTerm(
        term: SurfaceTerm,
        usedGlobalNames: HashSet<SurfaceName>,
        usedLocalNames: ArrayDeque<SurfaceName>,
        reporter: DiagnosticReporter,
    ) {
        when (term) {
            is SurfacePi -> {
                checkBinder(
                    term.binder,
                    usedGlobalNames,
                    usedLocalNames,
                    reporter,
                )
                checkTerm(
                    term.body,
                    usedGlobalNames,
                    usedLocalNames,
                    reporter,
                )
            }

            is SurfaceLambda -> {
                checkBinder(
                    term.binder,
                    usedGlobalNames,
                    usedLocalNames,
                    reporter,
                )
                checkTerm(
                    term.body,
                    usedGlobalNames,
                    usedLocalNames,
                    reporter,
                )
            }

            is SurfaceApp -> {
                checkTerm(
                    term.function,
                    usedGlobalNames,
                    usedLocalNames,
                    reporter,
                )
                checkTerm(
                    term.argument,
                    usedGlobalNames,
                    usedLocalNames,
                    reporter,
                )
            }

            is SurfaceNameRef -> {
                if (term.name in usedGlobalNames) {
                    reporter.reportError(
                        "Name ${term.name.value} already used globally",
                        null,
                    )
                } else if (term.name in usedLocalNames) {
                    reporter.reportError(
                        "Name ${term.name.value} already used in an outer binder",
                        null,
                    )
                }
            }

            is SurfaceTypeTerm -> Unit
            is SurfaceMeta -> Unit
        }
    }

    private fun checkBinder(
        binder: SurfaceBinder,
        usedGlobalNames: HashSet<SurfaceName>,
        usedLocalNames: ArrayDeque<SurfaceName>,
        reporter: DiagnosticReporter,
    ) {
        val name = binder.name

        if (name == null) {
            checkTerm(
                binder.type,
                usedGlobalNames,
                usedLocalNames,
                reporter,
            )
            return
        }

        var pushed = false

        if (name in usedGlobalNames) {
            reporter.reportError(
                "Name ${name.value} already used globally",
                null,
            )
        } else if (name in usedLocalNames) {
            reporter.reportError(
                "Name ${name.value} already used in an outer binder",
                null,
            )
        } else {
            usedLocalNames.addLast(name)
            pushed = true
        }

        checkTerm(
            binder.type,
            usedGlobalNames,
            usedLocalNames,
            reporter,
        )

        if (pushed) {
            if (usedLocalNames.last() != name) {
                error("Top Stack variable does not coincide with the current binder")
            }
            usedLocalNames.removeLast()
        }
    }
}
