package camp.delta.deltatypetheory.plugin

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.markup.HighlighterTargetArea

class DeltaBracketHighlighter(
    private val project: Project
) : CaretListener {

    private val highlighters = mutableListOf<RangeHighlighter>()

    override fun caretPositionChanged(event: CaretEvent) {
        val editor = event.editor

        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)

        if (virtualFile?.extension != "delta") {
            clearHighlights(editor)
            return
        }

        highlightBrackets(editor)
    }

    private fun highlightBrackets(editor: Editor) {
        clearHighlights(editor)

        val document = editor.document
        val text = document.text
        val caretOffset = editor.caretModel.offset

        if (text.isEmpty()) return

        // Check the character immediately before the caret
        // and the character immediately after it.
        val bracketOffset = when {
            caretOffset < text.length && isBracket(text[caretOffset]) ->
                caretOffset

            caretOffset > 0 && isBracket(text[caretOffset - 1]) ->
                caretOffset - 1

            else -> return
        }

        val bracket = text[bracketOffset]
        val matchingBracket = findMatchingBracket(
            text,
            bracketOffset
        ) ?: return

        highlight(editor, bracketOffset)
        highlight(editor, matchingBracket)
    }

    private fun isBracket(c: Char): Boolean {
        return c in "()[]{}"
    }

    private fun findMatchingBracket(
        text: String,
        position: Int
    ): Int? {
        val current = text[position]

        val pairs = mapOf(
            '(' to ')',
            ')' to '(',
            '[' to ']',
            ']' to '[',
            '{' to '}',
            '}' to '{'
        )

        val target = pairs[current] ?: return null

        val direction = if (current in "([{") 1 else -1

        var depth = 0
        var i = position

        while (i in text.indices) {
            val c = text[i]

            if (c == current) {
                depth++
            } else if (c == target) {
                depth--

                if (depth == 0) {
                    return i
                }
            }

            i += direction
        }

        return null
    }

    private fun highlight(
        editor: Editor,
        offset: Int
    ) {
        val attributes = editor.colorsScheme.getAttributes(
            EditorColors.SEARCH_RESULT_ATTRIBUTES
        )

        val highlighter = editor.markupModel.addRangeHighlighter(
            offset,
            offset + 1,
            HighlighterLayer.SELECTION - 1,
            attributes,
            HighlighterTargetArea.EXACT_RANGE
        )

        highlighters.add(highlighter)
    }

    private fun clearHighlights(editor: Editor) {
        highlighters.forEach {
            editor.markupModel.removeHighlighter(it)
        }

        highlighters.clear()
    }
}
