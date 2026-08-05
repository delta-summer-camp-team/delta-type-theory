package camp.delta.deltatypetheory.plugin.language

import com.intellij.lang.Language

object DeltaTypeTheoryLanguage : Language("DeltaTypeTheory") {
    private fun readResolve(): Any = DeltaTypeTheoryLanguage
}