package camp.delta.deltatypetheory.plugin.highlights

import camp.delta.deltatypetheory.plugin.lexer.DeltaTypeTheoryJFlexLexer
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryType
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.HighlighterColors.BAD_CHARACTER
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType


class DeltaTypeTheorySyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        private val KEYWORD_KEY = createTextAttributesKey("DELTA_KEYWORD", KEYWORD)
        private val COMMENT_KEY = createTextAttributesKey("DELTA_COMMENT", DOC_COMMENT)
        private val OPERATION_KEY = createTextAttributesKey("DELTA_OPERATION", INSTANCE_FIELD)
        private val IDENTIFIER_KEY = createTextAttributesKey("DELTA_IDENTIFIER", IDENTIFIER)

        private val PUNCTUATION_KEY = createTextAttributesKey("DELTA_PUNCTUATION", INSTANCE_METHOD)
        //private val SEMICOLON_KEY = createTextAttributesKey("DELTA_SEMICOLON", STRING)
        private val BAD_CHAR_KEY = createTextAttributesKey("DELTA_BAD_CHAR", BAD_CHARACTER)
        private /*impossible */ val STRING_KEY = createTextAttributesKey("DELTA_STRING", STRING)

        private val KEYWORD_PACKED = pack(KEYWORD_KEY)
        private val COMMENT_PACKED = pack(COMMENT_KEY)
        private val OPERATION_PACKED = pack(OPERATION_KEY)
        private val IDENTIFIER_PACKED = pack(IDENTIFIER_KEY)
        private val PUNCTUATION_PACKED = pack(PUNCTUATION_KEY)
        //private val SEMICOLON_PACKED = pack(SEMICOLON_KEY)
        private val BAD_CHAR_PACKED = pack(BAD_CHAR_KEY)
        private val /*impossible*/ STRING_PACKED = pack(STRING_KEY)

    }

    override fun getHighlightingLexer(): Lexer {
        return DeltaTypeTheoryJFlexLexer()
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<out TextAttributesKey?> {
        return when (tokenType) {
            //Keywords
            DeltaTypeTheoryType.AXIOM_KEYWORD, DeltaTypeTheoryType.DEF_KEYWORD, DeltaTypeTheoryType.SEMICOLON
            -> KEYWORD_PACKED
            //Identifiers
            DeltaTypeTheoryType.IDENTIFIER -> IDENTIFIER_PACKED
            //Other general punctuation
            DeltaTypeTheoryType.RPAREN, DeltaTypeTheoryType.LPAREN -> PUNCTUATION_PACKED
            //Semicolons
            // -> SEMICOLON_PACKED
            //Comments (this is technically comment-ception)
            DeltaTypeTheoryType.COMMENT -> COMMENT_PACKED
            //Operations
            DeltaTypeTheoryType.LAMBDA, DeltaTypeTheoryType.FOLLOWS,
            DeltaTypeTheoryType.EQUAL, DeltaTypeTheoryType.TO,
            DeltaTypeTheoryType.COLON -> OPERATION_PACKED
            // Bad character >:(
            TokenType.BAD_CHARACTER -> BAD_CHAR_PACKED
            //Everything else
            else -> emptyArray()
        }
    }
}