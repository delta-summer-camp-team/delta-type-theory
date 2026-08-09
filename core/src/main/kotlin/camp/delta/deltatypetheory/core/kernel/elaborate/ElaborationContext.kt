package camp.delta.deltatypetheory.core.kernel.elaborate
import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding

class ElaborationContext {
    val globals: MutableMap<String, GlobalBinding> = mutableMapOf()

    fun addGlobal(binding: GlobalBinding) {
        globals[binding.name.value] = binding
    }

    fun lookupGlobal(name: String): GlobalBinding? = globals[name]
}
