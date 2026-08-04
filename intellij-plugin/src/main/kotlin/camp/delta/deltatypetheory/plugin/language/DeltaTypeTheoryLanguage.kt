package camp.delta.deltatypetheory.plugin.language

import com.intellij.lang.Language

object DeltaTypeTheoryLanguage : Language("DeltaTypeTheory") {
    @Suppress("unused")
    private fun readResolve(): Any = DeltaTypeTheoryLanguage
}
