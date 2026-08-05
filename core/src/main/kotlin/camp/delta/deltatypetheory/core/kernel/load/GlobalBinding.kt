package camp.delta.deltatypetheory.core.kernel.load

import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName

data class GlobalBinding(
    val name: GlobalName,
    val type: CoreTerm,
    val value: CoreTerm?
)
