package camp.delta.deltatypetheory.core.surface.model

sealed interface SurfaceDecl

data class SurfaceAxiomDecl(val name: SurfaceName, val type: SurfaceTerm, val range: SourceRange?)

data class SurfaceDefDecl(val name: SurfaceName, val type: SurfaceTerm, val range: SourceRange?)
