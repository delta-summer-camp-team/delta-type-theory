package camp.delta.deltatypetheory.core.surface.check

import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnostic

data class SurfaceCheckResult(
    val diagnostics: List<SurfaceDiagnostic>,
)
