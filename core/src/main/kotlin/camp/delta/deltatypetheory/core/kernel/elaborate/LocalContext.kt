package camp.delta.deltatypetheory.core.kernel.elaborate

import camp.delta.deltatypetheory.core.kernel.model.CoreTerm

/** As of push time. */
data class LocalBinding(
  val name: String,
  val type: CoreTerm,
)

/** Valid at lookup depth. */
data class LocalResolution(
  val deBruijnIndex: Int,
  val type: CoreTerm,
)

/** Innermost first: position = index. */
data class LocalContext(
  val bindings: List<LocalBinding> = emptyList(),
) {

  // keep outer scope
  fun push(name: String, type: CoreTerm): LocalContext =
    copy(bindings = listOf(LocalBinding(name, type)) + bindings)

  /** Null -> try globals. */
  fun resolve(name: String): LocalResolution? {
    val index = bindings.indexOfFirst { it.name == name }
    if (index < 0) return null
    // + 1 binders shallower
    return LocalResolution(index, bindings[index].type.shift(index + 1))
  }
}
