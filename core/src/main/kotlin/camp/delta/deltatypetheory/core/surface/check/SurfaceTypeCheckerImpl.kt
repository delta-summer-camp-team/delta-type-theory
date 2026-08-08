package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram

object SurfaceTypeCheckerImpl : SurfaceTypeChecker {

    override fun check(program: SurfaceProgram): SurfaceCheckResult =
        SurfaceTypecheckRun().check(program)
}
