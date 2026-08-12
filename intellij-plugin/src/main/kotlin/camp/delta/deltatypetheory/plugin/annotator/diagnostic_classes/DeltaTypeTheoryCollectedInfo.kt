package camp.delta.deltatypetheory.plugin.annotator.diagnostic_classes

import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnostic
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram

data class DeltaTypeTheoryCollectedInfo(
    val program: SurfaceProgram?,
    val diagnostics: List<SurfaceDiagnostic> = emptyList(),
)