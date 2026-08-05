package camp.delta.deltatypetheory.core.surface.model

sealed interface SurfaceDecl {
    val name: SurfaceName
    val type: SurfaceTerm
    val range: SourceRange?
}

data class SurfaceAxiomDecl(
    override val name: SurfaceName,
    override val type: SurfaceTerm,
    override val range: SourceRange?,
) : SurfaceDecl

data class SurfaceDefDecl(
    override val name: SurfaceName,
    override val type: SurfaceTerm,
    val value: SurfaceTerm,
    override val range: SourceRange?,
) : SurfaceDecl
