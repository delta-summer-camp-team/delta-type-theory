package camp.delta.deltatypetheory.core.kernel.elaborate

import camp.delta.deltatypetheory.core.kernel.model.CoreTerm

/** A local variable. [type] is as of push time, so it needs shifting to be read at the current depth. */
data class LocalBinding(
  val name: String,
  val type: CoreTerm,
)

/** A resolved local variable. Both fields are already correct at the lookup depth. */
data class LocalResolution(
  val deBruijnIndex: Int,
  val type: CoreTerm,
)

/** Locals in scope, innermost first — so a binding's position is its de Bruijn index. */
data class LocalContext(
  val bindings: List<LocalBinding> = emptyList(),
) {

  fun push(name: String, type: CoreTerm): LocalContext =
    copy(bindings = listOf(LocalBinding(name, type)) + bindings)

  /** Null if [name] is not local — the caller should then try globals. */
  fun resolve(name: String): LocalResolution? {
    val index = bindings.indexOfFirst { it.name == name }
    if (index < 0) return null
    // The type was stored index + 1 binders shallower than here.
    return LocalResolution(index, bindings[index].type.shift(index + 1))
  }
}
