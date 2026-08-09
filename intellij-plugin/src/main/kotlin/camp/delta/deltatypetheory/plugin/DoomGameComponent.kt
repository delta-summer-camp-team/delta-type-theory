package camp.delta.deltatypetheory.plugin

import com.intellij.openapi.editor.Editor
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.KeyboardFocusManager
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.Timer
import kotlin.math.cos
import kotlin.math.sin

class   DoomGameComponent(
    private val editor: Editor
) : JComponent() {

    private var running = true

    // Player position
    private var playerX = 0.0
    private var playerY = 0.0

    // Player direction
    private var angle = 0.0

    // Movement
    private var forward = false
    private var backward = false
    private var left = false
    private var right = false

    /*
     * Handles keyboard input globally while DOOM is active.
     *
     * This is more reliable than KeyListener because IntelliJ
     * normally wants to keep keyboard focus in the editor.
     */
    private val keyDispatcher = KeyEventDispatcher { event ->

        if (!running) {
            return@KeyEventDispatcher false
        }

        when (event.id) {

            KeyEvent.KEY_PRESSED -> {

                when (event.keyCode) {

                    KeyEvent.VK_W -> {
                        forward = true
                        event.consume()
                        true
                    }

                    KeyEvent.VK_S -> {
                        backward = true
                        event.consume()
                        true
                    }

                    KeyEvent.VK_A -> {
                        left = true
                        event.consume()
                        true
                    }

                    KeyEvent.VK_D -> {
                        right = true
                        event.consume()
                        true
                    }

                    KeyEvent.VK_ESCAPE -> {
                        event.consume()
                        stop()
                        true
                    }

                    else -> false
                }
            }

            KeyEvent.KEY_RELEASED -> {

                when (event.keyCode) {

                    KeyEvent.VK_W -> {
                        forward = false
                        event.consume()
                        true
                    }

                    KeyEvent.VK_S -> {
                        backward = false
                        event.consume()
                        true
                    }

                    KeyEvent.VK_A -> {
                        left = false
                        event.consume()
                        true
                    }

                    KeyEvent.VK_D -> {
                        right = false
                        event.consume()
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }
    }

    private val gameTimer = Timer(16) {
        update()
        repaint()
    }

    init {

        isFocusable = true

        background = Color.BLACK

        /*
         * Add the game over the editor.
         */
        val contentComponent = editor.contentComponent

        contentComponent.add(this)

        /*
         * Make sure the game fills the editor.
         */
        setBounds(
            0,
            0,
            contentComponent.width,
            contentComponent.height
        )

        contentComponent.addComponentListener(
            object : java.awt.event.ComponentAdapter() {
                override fun componentResized(
                    event: java.awt.event.ComponentEvent
                ) {
                    setBounds(
                        0,
                        0,
                        contentComponent.width,
                        contentComponent.height
                    )

                    repaint()
                }
            }
        )

        /*
         * Make sure the game is rendered above the editor.
         */
        contentComponent.setComponentZOrder(
            this,
            0
        )

        /*
         * Register keyboard handling.
         */
        KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(keyDispatcher)

        /*
         * Start the game loop.
         */
        gameTimer.start()

        /*
         * Make sure the game gets focus initially.
         */
        requestFocusInWindow()
    }

    private fun update() {

        if (!running) {
            return
        }

        val speed = 0.08

        if (forward) {
            playerX += cos(angle) * speed
            playerY += sin(angle) * speed
        }

        if (backward) {
            playerX -= cos(angle) * speed
            playerY -= sin(angle) * speed
        }

        if (left) {
            playerX += cos(angle - Math.PI / 2) * speed
            playerY += sin(angle - Math.PI / 2) * speed
        }

        if (right) {
            playerX += cos(angle + Math.PI / 2) * speed
            playerY += sin(angle + Math.PI / 2) * speed
        }
    }

    override fun paintComponent(g: Graphics) {

        super.paintComponent(g)

        val g2 = g.create() as Graphics2D

        val width = width
        val height = height

        /*
         * Sky
         */
        g2.color = Color(45, 45, 55)

        g2.fillRect(
            0,
            0,
            width,
            height / 2
        )

        /*
         * Floor
         */
        g2.color = Color(35, 25, 25)

        g2.fillRect(
            0,
            height / 2,
            width,
            height / 2
        )

        /*
         * Crosshair
         */
        g2.color = Color.WHITE

        val centerX = width / 2
        val centerY = height / 2

        g2.drawLine(
            centerX - 10,
            centerY,
            centerX + 10,
            centerY
        )

        g2.drawLine(
            centerX,
            centerY - 10,
            centerX,
            centerY + 10
        )

        /*
         * Information
         */
        g2.drawString(
            "DOOM MODE",
            20,
            30
        )

        g2.drawString(
            "WASD = MOVE",
            20,
            50
        )

        g2.drawString(
            "ESC = EXIT",
            20,
            70
        )

        g2.drawString(
            "Position: %.2f, %.2f".format(
                playerX,
                playerY
            ),
            20,
            90
        )

        g2.dispose()
    }

    /*
     * Stop the game completely.
     */
    fun stop() {

        if (!running) {
            return
        }

        running = false

        /*
         * Stop game loop.
         */
        gameTimer.stop()

        /*
         * Release keyboard handling.
         */
        KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
            .removeKeyEventDispatcher(keyDispatcher)

        /*
         * Release all movement keys.
         */
        forward = false
        backward = false
        left = false
        right = false

        /*
         * Remove the game from the editor.
         */
        editor.contentComponent.remove(this)

        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()

        /*
         * Remove it from DoomMode.
         */
        DoomMode.remove(editor)
    }
}
