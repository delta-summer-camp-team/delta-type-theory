package camp.delta.deltatypetheory.plugin.parser

import camp.delta.deltatypetheory.plugin.language.DeltaTypeTheoryLanguage
import camp.delta.deltatypetheory.plugin.lexer.DeltaTypeTheoryJFlexLexer
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryFile
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryType
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class DeltaTypeTheoryParserDefinition : ParserDefinition {

    private val fileNodeType = IFileElementType(DeltaTypeTheoryLanguage)

    override fun createLexer(project: Project?): Lexer {
        return DeltaTypeTheoryJFlexLexer()
    }

    override fun createParser(project: Project?): PsiParser = DeltaTypeTheoryParser()

    override fun getFileNodeType(): IFileElementType = IFileElementType(DeltaTypeTheoryLanguage)

    override fun getCommentTokens(): TokenSet = TokenSet.create(DeltaTypeTheoryType.COMMENT)

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = DeltaTypeTheoryType.Factory.createElement(node)
    override fun createFile(viewProvider: FileViewProvider): PsiFile = DeltaTypeTheoryFile(viewProvider)

}