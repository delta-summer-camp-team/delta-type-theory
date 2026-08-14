package camp.delta.deltatypetheory.plugin

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class   DeltaTypeTheoryUnicodeInputHandler : TypedHandlerDelegate () {

    //replacements LaTeX to unicode symbols
    private val replacements = mapOf(
        "\\to" to "→",
        "\\l" to "λ",
        "\\N" to "ℕ",
        "\\forall" to "∀",
        "\\exists" to "∃",
        "\\rule" to "↦",
        "\\anton" to "\uD83D\uDE0D",
        "\\german" to "\uD83C\uDFC6",
        "\\plugin" to "\uD83D\uDE00",
        "\\surface" to "\uD83D\uDE28",
        "\\core" to "\uD83E\uDD2C",
        "\\konstantin" to "\uD83D\uDC10",
        "\\viktor" to "Bye\uD83E\uDD2B\uD83E\uDDCF\uD83C\uDFFB\u200D♂\uFE0FBye\uD83D\uDDFF",
        "\\pain" to "λ",
        "\\type-theory" to "☝\uFE0F\uD83E\uDD13",


    )

    override fun charTyped (
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile
    ): Result {

        if (!c.isWhitespace()){
            return Result.CONTINUE
        }
        if (file.virtualFile?.extension != "delta") {
            return Result.CONTINUE
        }

        val caretOffset = editor.caretModel.offset
        val document = editor.document
        val textBeforeCaret = document.text.substring(0, caretOffset)
        val slashIndex = textBeforeCaret.lastIndexOf('\\')

        if (slashIndex == -1) {
            return Result.CONTINUE
        }


        //extracts command
        val command = textBeforeCaret.substring(slashIndex).trimEnd()

        if (command == "\\doom") {
            document.deleteString(slashIndex, caretOffset - 1)
            DoomMode.activate(editor)
            return Result.STOP
        }

        //finds replacement if not ends the process
        val replacement = replacements[command] ?: return Result.CONTINUE


        //replaces the characters with new symbol
        WriteCommandAction.runWriteCommandAction(project) {
            document.replaceString(slashIndex,caretOffset - 1,replacement)
            val newCaretOffset = slashIndex + replacement.length + 1
            editor.caretModel.moveToOffset(newCaretOffset)
        }

        return Result.CONTINUE
    }
}
