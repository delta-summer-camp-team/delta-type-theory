package camp.delta.deltatypetheory.core.kernel.model

data class CoreRule(
    val name: String,
    val lhs: CoreTerm,
    val rhs: CoreTerm,
)
