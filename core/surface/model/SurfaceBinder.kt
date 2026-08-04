package camp.delta.deltatypetheory.core.surface.model

data class SurfaceBinder(
        val name: SurfaceName,
        val type: SurfaceTerm,
        val range: SourceRange? = null
)

// Used for diagnostics
data class SourceRange(
        val filePath: String, // TODO: change String to a proper file path type
        val startOffset: Int,
        val endOffset: Int
)
