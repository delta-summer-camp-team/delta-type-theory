package camp.delta.deltatypetheory.core.surface.model

@JvmInline
value class SurfaceName(
    val value: String,
) {
    init {
        kotlin.require(value.isNotBlank()) { "SurfaceName must not be blank" }
    }
}
