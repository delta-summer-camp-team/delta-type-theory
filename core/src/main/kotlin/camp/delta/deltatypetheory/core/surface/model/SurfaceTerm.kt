package camp.delta.deltatypetheory.core.surface.model

sealed interface SurfaceTerm

// Non-terminals
data class SurfacePi(
    val binder: SurfaceBinder,
    val body: SurfaceTerm,
) : SurfaceTerm

data class SurfaceLambda(
    val binder: SurfaceBinder,
    val body: SurfaceTerm,
) : SurfaceTerm

data class SurfaceApp(
    val function: SurfaceTerm,
    val argument: SurfaceTerm,
) : SurfaceTerm

// Terminals
data object SurfaceTypeTerm : SurfaceTerm

data class SurfaceNameRef(
    val name: SurfaceName,
) : SurfaceTerm
