package camp.delta.deltatypetheory.plugin

import com.intellij.openapi.editor.Editor
import java.io.File

object DoomMode {

    private val activeGames = mutableMapOf<Editor, Process>()

    private val chocolateDoomExecutable =
        """C:\Users\Viktor\Downloads\chocolate-doom-3.1.1-win64\chocolate-doom.exe"""

    private val iwadPath =
        File("""C:\Users\Viktor\Downloads\doom\DOOM.WAD""")
    fun activate(editor: Editor) {

        // Don't start two Doom processes for the same editor.
        val existingProcess = activeGames[editor]

        if (existingProcess?.isAlive == true) {
            return
        }

        activeGames.remove(editor)

        if (!iwadPath.exists()) {
            println("DOOM.WAD not found: ${iwadPath.absolutePath}")
            return
        }

        try {
            val process = ProcessBuilder(
                chocolateDoomExecutable,
                "-iwad",
                iwadPath.absolutePath
            )
                .redirectErrorStream(true)
                .start()

            activeGames[editor] = process

            /*
             * Wait for Chocolate Doom to exit.
             *
             * This happens on a background thread so we don't
             * freeze IntelliJ.
             */
            Thread {
                try {
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            println("[Chocolate Doom] $line")
                        }
                    }
                } catch (_: Exception) {
                    // Process may have been terminated.
                }

                activeGames.remove(editor)
            }.start()

        } catch (exception: Exception) {
            println(
                "Failed to start Chocolate Doom: ${exception.message}"
            )

            exception.printStackTrace()
        }
    }

    fun deactivate(editor: Editor) {

        val process = activeGames.remove(editor)
            ?: return

        if (process.isAlive) {
            process.destroy()
        }
    }

    fun remove(editor: Editor) {
        deactivate(editor)
    }

    fun isActive(editor: Editor): Boolean {
        return activeGames[editor]?.isAlive == true
    }
}
