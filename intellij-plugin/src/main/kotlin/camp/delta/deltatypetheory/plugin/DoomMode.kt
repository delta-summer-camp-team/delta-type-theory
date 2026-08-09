package camp.delta.deltatypetheory.plugin

import com.intellij.openapi.editor.Editor

object DoomMode {

    private val activeGames =
        mutableMapOf<Editor, DoomGameComponent>()

    fun activate(editor: Editor) {

        // Don't start two games in the same editor.
        if (activeGames.containsKey(editor)) {
            return
        }

        val game = DoomGameComponent(editor)

        activeGames[editor] = game

        game.parent?.setComponentZOrder(game, 0)

        game.requestFocusInWindow()
    }

    fun deactivate(editor: Editor) {
        val game = activeGames[editor] ?: return

        game.stop()
    }

    fun remove(editor: Editor) {
        activeGames.remove(editor)
    }

    fun isActive(editor: Editor): Boolean {
        return activeGames.containsKey(editor)
    }
}