package camp.delta.deltatypetheory.core.kernel.model

data class GlobalBinding(
    val name: GlobalName,
    val type: CoreTerm,
    val value: CoreTerm?
)
