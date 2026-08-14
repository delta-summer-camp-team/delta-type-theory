package camp.delta.deltatypetheory.plugin.lexer

import com.intellij.lexer.FlexAdapter
import camp.delta.deltatypetheory.plugin.language.DeltaTypeTheoryLexer

class DeltaTypeTheoryJFlexLexer : FlexAdapter(DeltaTypeTheoryLexer(null)) {

}
