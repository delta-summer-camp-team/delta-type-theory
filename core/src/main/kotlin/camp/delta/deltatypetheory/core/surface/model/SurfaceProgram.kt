package camp.delta.deltatypetheory.core.surface.model

data class SurfaceProgram(
    val declarations: List<SurfaceDecl>,
    val rules: List<SurfaceRuleDecl>,
    val fileName: String?,
)
