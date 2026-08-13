package camp.delta.deltatypetheory.plugin

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class BracketHandler : TypedHandlerDelegate() {

    companion object {
        private val PAIRS = mapOf(
            '(' to ')',
            '{' to '}',
            '[' to ']'
        )

        fun handleBackspace(
            editor: Editor,
            caret: Caret?,
            dataContext: DataContext?,
            originalHandler: EditorActionHandler?
        ) {
            val document = editor.document
            val offset = editor.caretModel.offset

            if (offset > 0 && offset < document.textLength) {

                val left = document.charsSequence[offset - 1]
                val right = document.charsSequence[offset]

                if (PAIRS[left] == right) {

                    document.deleteString(
                        offset - 1,
                        offset + 1
                    )

                    editor.caretModel.moveToOffset(offset - 1)

                    return
                }
            }

            originalHandler?.execute(
                editor,
                caret,
                dataContext
            )
        }

        fun handleDelete(
            editor: Editor,
            caret: Caret?,
            dataContext: DataContext?,
            originalHandler: EditorActionHandler?
        ) {
            val document = editor.document
            val offset = editor.caretModel.offset

            if (offset > 0 && offset < document.textLength) {

                val left = document.charsSequence[offset - 1]
                val right = document.charsSequence[offset]

                if (PAIRS[left] == right) {

                    document.deleteString(
                        offset - 1,
                        offset + 1
                    )

                    editor.caretModel.moveToOffset(offset - 1)

                    return
                }
            }

            originalHandler?.execute(
                editor,
                caret,
                dataContext
            )
        }
    }

    override fun charTyped(
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile
    ): Result {

        val closingBracket = PAIRS[c] ?: return Result.CONTINUE

        val document = editor.document
        val offset = editor.caretModel.offset

        document.insertString(
            offset,
            closingBracket.toString()
        )

        editor.caretModel.moveToOffset(offset)

        return Result.CONTINUE
    }
}