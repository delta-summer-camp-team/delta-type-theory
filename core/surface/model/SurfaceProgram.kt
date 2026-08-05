package camp.delta.deltatypetheory.core.surface.model

data class SurfaceProgram(
    val declarations: List<SurfaceDecl>,
    val fileName: String?,
)
