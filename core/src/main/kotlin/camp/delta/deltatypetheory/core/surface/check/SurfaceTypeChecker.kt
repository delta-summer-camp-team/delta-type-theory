package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.diagnostic.*
import camp.delta.deltatypetheory.core.surface.model.*

interface SurfaceTypeChecker {
    fun check(program: SurfaceProgram): SurfaceCheckResult
}

class StubSurfaceTypeChecker : SurfaceTypeChecker {
    // TODO make them hashmaps with offset to add lines / offsets to reporter
    val usedGlobalNames = HashSet<SurfaceName>()
    val usedLocalNames: ArrayDeque<SurfaceName> = ArrayDeque()
    val reporter = DiagnosticReporter()

    override fun check(program: SurfaceProgram): SurfaceCheckResult {
        // val fileName = program.fileName

        for (declaration in program.declarations) {
            checkGlobal(declaration)
        }
        return SurfaceCheckResult(reporter.all())
    }

    private fun checkGlobal(declaration: SurfaceDecl) {
        val type =
                when (declaration) {
                    is SurfaceDefDecl -> "defintion"
                    is SurfaceAxiomDecl -> "axiom"
                }
        val name = declaration.name
        if (name in usedGlobalNames) {
            reporter.reportError("Name ${name.value} already used for another $type", null)
        } else {
            usedGlobalNames.add(name)
        }
        checkTerm(declaration.type)
        if (declaration is SurfaceDefDecl) {
            checkTerm(declaration.value)
        }
    }

    private fun checkTerm(term: SurfaceTerm) {
        when (term) {
            is SurfacePi -> {
                checkBinder(term.binder)
                checkTerm(term.body)
            }
            is SurfaceLambda -> {
                checkBinder(term.binder)
                checkTerm(term.body)
            }
            is SurfaceApp -> {
                checkTerm(term.function)
                checkTerm(term.argument)
            }
            is SurfaceNameRef -> {
                if (term.name in usedGlobalNames) {
                    reporter.reportError("Name ${term.name.value} already used globally", null)
                } else if (term.name in usedLocalNames) {
                    reporter.reportError(
                            "Name ${term.name.value} already used in a outer binder",
                            null,
                    )
                }
            }
            is SurfaceTypeTerm -> {}
        }
    }

    private fun checkBinder(binder: SurfaceBinder) {
        val name = binder.name
        var pushed = false
        if (name in usedGlobalNames) {
            reporter.reportError("Name ${name.value} already used globally", null)
        } else if (name in usedLocalNames) {
            reporter.reportError("Name ${name.value} already used in a outer binder", null)
        } else {
            usedLocalNames.addLast(name)
            pushed = true
        }
        checkTerm(binder.type)
        if (pushed) {
            if (usedLocalNames.last() != name) {
                error("Top Stack variable does not coincide with the current binder")
            }
            usedLocalNames.removeLast()
        }
    }
}
