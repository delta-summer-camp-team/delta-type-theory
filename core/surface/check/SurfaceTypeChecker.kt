package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.diagnostic.*
import camp.delta.deltatypetheory.core.surface.model.*

interface SurfaceTypechecker {
    fun check(program: SurfaceProgram): SurfaceCheckResult
}

class StubSurfaceTypeChecker {
    // TODO make them hashmaps with offset to add lines / offsets to reporter
    val usedGlobalNames: HashSet<SurfaceName> = hashSet()
    val usedLocalNames: ArrayDeque<SurfaceName> = ArrayDeque()
    val reporter = DiagnosticReporter()

    fun check(program: SurfaceProgram): SurfaceCheckResult {
        // val fileName = program.fileName

        for (declaration in program.declarations) {
            checkGlobal(declaration)
        }
        return SurfaceCheckResult(reporter.all())
    }

    fun checkGlobal(declaration: SurfaceDecl) {
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
        declaration.type
    }

    fun checkTerm(term: SurfaceTerm){
        when (term){
            is SurfacePi{
                checkBinder(term.binder)
                checkTerm(term.body)
            }
            is SurfaceLambda{
                checkBinder(term.binder)
                checkTerm(term.body)
            }
            is SurfaceApp{
                checkTerm(term.function)
                checkTerm(term.argument)
            }
        }
    }
    
    fun checkBinder(binder: SurfaceBinder){
        val name = binder.name
        var pushed = false
        if (name in usedGlobalNames){
            reporter.reportError("Name ${name.value} already used globally", null)
        } else if(name in usedLocalNames){
            reporter.reportError("Name ${name.value} already used in a outer binder", null)
        } else {
            usedLocalNames.addLast(name)
            pushed = true
        }
        checkTerm(binder.type)
        if (pushed){
            if (usedLocalNames.last != name){
                error("Top Stack variable does not coincide with the current binder")
            }
            usedLocalNames.removeLast()
        }
    }
}

::SurfaceTypechecker
