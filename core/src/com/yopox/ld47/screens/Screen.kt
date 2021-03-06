package com.yopox.ld47.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.yopox.ld47.LD47
import com.yopox.ld47.graphics.Button
import ktx.app.KtxScreen
import ktx.math.component1
import ktx.math.component2

abstract class Screen(internal val game: LD47) : InputProcessor, KtxScreen {

    internal val batch = SpriteBatch()
    internal val shapeRenderer = ShapeRenderer()
    internal val camera = OrthographicCamera().also { it.position.set(WIDTH / 2, HEIGHT / 2, 0f) }
    internal val viewport = FitViewport(WIDTH, HEIGHT, camera)

    internal val buttons = arrayListOf<Button>()

    companion object {
        const val WIDTH = 1280f
        const val HEIGHT = 720f

        internal var blockInput = false

        private fun unproject(camera: Camera, screenX: Int, screenY: Int): Vector2 {
            val pos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
            return Vector2(pos.x, pos.y)
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = this
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.update()
        camera.position.set(WIDTH / 2, HEIGHT / 2, 0f)
    }

    abstract fun reset()

    open fun inputDown(x: Float, y: Float) {
        buttons.forEach { it.click(x, y) }
    }

    open fun inputUp(x: Float, y: Float) {
        buttons.forEach { it.release(x, y) }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer > 0 || button > 0 || blockInput) return false
        val (x, y) = unproject(camera, screenX, screenY)
        inputDown(x, y)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer > 0 || button > 0 || blockInput) return false
        val (x, y) = unproject(camera, screenX, screenY)
        inputUp(x, y)
        return true
    }

    override fun keyTyped(character: Char): Boolean = false

    override fun keyDown(keycode: Int): Boolean = false
    override fun keyUp(keycode: Int): Boolean = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val (x, y) = unproject(camera, screenX, screenY)
        buttons.forEach { it.updateSelected(x, y) }
        return true
    }

    override fun scrolled(amount: Int): Boolean = false
}