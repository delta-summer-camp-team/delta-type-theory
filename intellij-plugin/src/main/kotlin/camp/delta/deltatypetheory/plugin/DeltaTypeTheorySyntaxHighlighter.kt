package camp.delta.deltatypetheory.plugin

import camp.delta.deltatypetheory.plugin.lexer.DeltaTypeTheoryJFlexLexer
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class DeltaTypeTheorySyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return DeltaTypeTheoryJFlexLexer()
    }
    override fun getTokenHighlights(p0: IElementType?): Array<out TextAttributesKey?> {
        TODO("Not yet implemented")
    }
}