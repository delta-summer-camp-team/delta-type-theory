package camp.delta.deltatypetheory.core.surface.model

sealed interface SurfaceTerm {
    val range: SourceRange?
}

// Non-terminals
data class SurfacePi(
    val binder: SurfaceBinder,
    val body: SurfaceTerm,
    override val range: SourceRange? = null,
) : SurfaceTerm

data class SurfaceLambda(
    val binder: SurfaceBinder,
    val body: SurfaceTerm,
    override val range: SourceRange? = null,
) : SurfaceTerm

data class SurfaceApp(
    val function: SurfaceTerm,
    val argument: SurfaceTerm,
    override val range: SourceRange? = null,
) : SurfaceTerm

// Terminals
data class SurfaceTypeTerm(
    override val range: SourceRange? = null,
) : SurfaceTerm

data class SurfaceNameRef(
    val name: SurfaceName,
    override val range: SourceRange? = null,
) : SurfaceTerm

data class SurfaceMeta(
    val id: Int,
    override val range: SourceRange? = null,
) : SurfaceTerm
