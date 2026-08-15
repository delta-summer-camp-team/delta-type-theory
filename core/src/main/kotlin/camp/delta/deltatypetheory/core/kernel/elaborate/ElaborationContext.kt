package camp.delta.deltatypetheory.core.kernel.elaborate

import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding
import camp.delta.deltatypetheory.core.kernel.model.CoreRule

class ElaborationContext {
    val globals: MutableMap<String, GlobalBinding> = mutableMapOf()
    val rules: MutableList<CoreRule> = mutableListOf()

    fun addGlobal(binding: GlobalBinding) {
        globals[binding.name.value] = binding
    }

    fun lookupGlobal(name: String): GlobalBinding? = globals[name]

    fun addRule(rule: CoreRule) {
        rules.add(rule)
    }
}
