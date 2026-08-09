package camp.delta.deltatypetheory.plugin

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import camp.delta.deltatypetheory.plugin.DeltaBracketHighlighter

class DeltaEditorListener : EditorFactoryListener {

    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val project = editor.project ?: return

        editor.caretModel.addCaretListener(
            DeltaBracketHighlighter(project)
        )
    }
}