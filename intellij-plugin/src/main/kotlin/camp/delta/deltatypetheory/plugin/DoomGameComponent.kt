class DoomLauncher {

    private var process: Process? = null

    fun start() {
        if (process?.isAlive == true) {
            return
        }

        process = ProcessBuilder(
            "chocolate-doom",
            "-iwad",
            "DOOM.WAD"
        )
            .inheritIO()
            .start()
    }

    fun stop() {
        process?.destroy()
        process = null
    }
}