





class DeltaTypeTheoryUnicodeInputHandler : TypedHandlerDelegate () {
    override fun charTyped (
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile
    ): Result {

        if (c!=" ") {
            if (file.virtualFile?.extension == "delta") {

                val caretOffset = editor.caretModel.offset#

                val document = editor.document

                val textBeforeCaret = document.text.substring(0, caretOffset)

                val slashIndex = textBeforeCaret.lastIndexOf('\\')

                if (slashIndex == -1) {
                    return Result.CONTINUE
                }
                val command = textBeforeCaret
                    .substring(slashIndex)
                    .trimEnd()

                val replacements = mapOf(
                    "\\to" to "→",
                    "\\L" to "λ",
                    "\\mN" to "ℕ",
                    "\\forall" to "∀",
                    "\\exists" to "∃"
                )

                val replacement = replacements[command] ?: return Result.CONTINUE


                //replaces the characters with new symbol
                WriteCommandAction.runWriteCommandAction(project) {
                    document.replaceString(
                        slashIndex,
                        caretOffset - 1,
                        replacement
                    )
                }
            }
        }

        return Result.continue
    }
}