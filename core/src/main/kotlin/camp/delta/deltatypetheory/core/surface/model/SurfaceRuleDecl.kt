package camp.delta.deltatypetheory.core.surface.model

data class SurfaceRuleDecl(
    val name: SurfaceName,
    val lhs: SurfaceTerm,
    val rhs: SurfaceTerm,
    val range: SourceRange?,
)
