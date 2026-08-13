package camp.delta.deltatypetheory.core.surface.model

sealed interface SurfaceTerm

// Non-terminals
data class SurfacePi(
    val binder: SurfaceBinder,
    val body: SurfaceTerm,
    val range: SourceRange?,
) : SurfaceTerm

data class SurfaceLambda(
    val binder: SurfaceBinder,
    val body: SurfaceTerm,
    val range: SourceRange?,
) : SurfaceTerm

data class SurfaceApp(
    val function: SurfaceTerm,
    val argument: SurfaceTerm,
    val range: SourceRange?,
) : SurfaceTerm

// Terminals
data class SurfaceTypeTerm(
    val range: SourceRange?,
) : SurfaceTerm

data class SurfaceNameRef(
    val name: SurfaceName,
    val range: SourceRange?,
) : SurfaceTerm

data class SurfaceMeta(
    val id: Int,
) : SurfaceTerm
