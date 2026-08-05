package camp.delta.deltatypetheory.core.surface.elaborate

import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding

class ElaborationContext {

    val globals: MutableMap<String, GlobalBinding> = mutableMapOf()

    fun addGlobal(binding: GlobalBinding) {
        globals[binding.name.value] = binding
    }

    fun lookupGlobal(name: String): GlobalBinding? {
        return globals[name]
    }
}
