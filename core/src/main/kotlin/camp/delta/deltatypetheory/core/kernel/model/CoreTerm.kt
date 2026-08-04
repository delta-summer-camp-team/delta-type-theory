package camp.delta.deltatypetheory.core.kernel.model

sealed interface CoreTerm
// Non-terminals / Nodes

data class Pi(
    val parameterType: CoreTerm,
    val body: CoreTerm,
) : CoreTerm

data class Lambda(
    val parameterType: CoreTerm,
    val body: CoreTerm,
) : CoreTerm

data class App(
    val function: CoreTerm,
    val argument: CoreTerm,
) : CoreTerm

// Terminals / Leaves

data object TypeTerm : CoreTerm

// TODO: speak with Anton wether we should maybe use proper environments
data class BoundVar(
    val index: Int,
) : CoreTerm

data class GlobalRef(
    val name: GlobalName,
) : CoreTerm

// String wrapper

@JvmInline
value class GlobalName(
    val value: String,
) : CoreTerm
