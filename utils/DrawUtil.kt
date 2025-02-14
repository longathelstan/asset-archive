package dev.blend.util.render

import com.mojang.blaze3d.systems.RenderSystem
import dev.blend.handler.impl.ThemeHandler
import dev.blend.util.alignment.Alignment
import dev.blend.util.interfaces.IAccessor
import dev.blend.util.misc.MiscUtil
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.system.MemoryStack
import java.awt.Color
import java.nio.ByteBuffer

@Suppress("Unused", "MemberVisibilityCanBePrivate")
object DrawUtil: IAccessor {

    private var context = -1L

    private lateinit var poppins: ByteBuffer
    private lateinit var lato: ByteBuffer

    @JvmStatic
    fun initialize() {
        // Initialize NanoVG Context
        context = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS)
        if (context == -1L) {
            throw IllegalStateException("Failed to create NanoVG Context.")
        }
        poppins = MiscUtil.getResourceAsByteBuffer("fonts/poppins.ttf")
        lato = MiscUtil.getResourceAsByteBuffer("fonts/lato.ttf")
        nvgCreateFontMem(context, "poppins", poppins, false)
        nvgCreateFontMem(context, "lato", lato, false)
    }

    @JvmStatic
    fun destroy() {
        if (context != -1L) {
            NanoVGGL3.nvgDelete(context)
        }
    }

    fun render(fr: () -> Unit) {
        MemoryStack.stackPush().use { _ ->
            preRender()
            begin()
            scale {
                fr()
            }
            end()
            postRender()
        }
    }

    fun savedState(fr: () -> Unit) {
        save()
        fr()
        restore()
    }
    fun scale(scale: Number = mc.window.scaleFactor, fr: () -> Unit) {
        save()
        scale(scale)
        fr()
        restore()
    }
    fun translate(x: Number, y: Number, fr: () -> Unit) {
        save()
        translate(x, y)
        fr()
        restore()
    }
    fun scissor(x: Number, y: Number, width: Number, height: Number, fr: () -> Unit) {
        savedState {
            scissor(x, y, width, height)
            fr()
            resetScissor()
        }
    }

    fun save() = nvgSave(context)
    fun restore() = nvgRestore(context)
    fun scale() = scale(mc.window.scaleFactor)
    fun scale(scale: Number) = scale(scale, scale)
    fun scale(x: Number, y: Number) = nvgScale(context, x.toFloat(), y.toFloat())
    fun translate(x: Number, y: Number) = nvgTranslate(context, x.toFloat(), y.toFloat())
    fun scissor(x: Number, y: Number, width: Number, height: Number) = nvgScissor(context, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    fun resetScissor() = nvgResetScissor(context)
    private fun begin() = nvgBeginFrame(context, mc.window.width.toFloat(), mc.window.height.toFloat(), 1.0f)
    private fun end() = nvgEndFrame(context)

    fun rect(x: Number, y: Number, width: Number, height: Number, color: Color, alignment: Alignment = Alignment.TOP_LEFT) {
        path {
            color.nvgColor { nvgColor ->
                val position = alignment.getPosition(width.toFloat(), height.toFloat())(x.toFloat(), y.toFloat())
                nvgRect(context, position.first, position.second, width.toFloat(), height.toFloat())
                nvgFillColor(context, nvgColor)
                nvgFill(context)
            }
        }
    }
    fun outlinedRect(x: Number, y: Number, width: Number, height: Number, stroke: Number, color: Color, alignment: Alignment = Alignment.TOP_LEFT) {
        path {
            color.nvgColor { nvgColor ->
                val position = alignment.getPosition(width.toFloat(), height.toFloat())(x.toFloat(), y.toFloat())
                nvgRect(context, position.first, position.second, width.toFloat(), height.toFloat())
                nvgStrokeWidth(context, stroke.toFloat())
                nvgStrokeColor(context, nvgColor)
                nvgStroke(context)
            }
        }
    }
    fun roundedRect(x: Number, y: Number, width: Number, height: Number, cornerRadius: Number, color: Color, alignment: Alignment = Alignment.TOP_LEFT) {
        path {
            color.nvgColor { nvgColor ->
                val position = alignment.getPosition(width.toFloat(), height.toFloat())(x.toFloat(), y.toFloat())
                nvgRoundedRect(context, position.first, position.second, width.toFloat(), height.toFloat(), cornerRadius.toFloat())
                nvgFillColor(context, nvgColor)
                nvgFill(context)
            }
        }
    }
    fun outlinedRoundedRect(x: Number, y: Number, width: Number, height: Number, cornerRadius: Number, stroke: Number, color: Color, alignment: Alignment = Alignment.TOP_LEFT) {
        path {
            color.nvgColor { nvgColor ->
                val position = alignment.getPosition(width.toFloat(), height.toFloat())(x.toFloat(), y.toFloat())
                nvgRoundedRect(context, position.first, position.second, width.toFloat(), height.toFloat(), cornerRadius.toFloat())
                nvgStrokeWidth(context, stroke.toFloat())
                nvgStrokeColor(context, nvgColor)
                nvgStroke(context)
            }
        }
    }

    fun string(text: String, x: Number, y: Number, fontSize: Number, color: Color, alignment: Alignment = Alignment.TOP_LEFT, fontName: String = ThemeHandler.fontName) {
        path {
            // shadow first
            Color(0, 0, 0, 180).nvgColor { nvgColor ->
                nvgFillColor(context, nvgColor)
            }
            nvgFontFace(context, fontName)
            nvgFontBlur(context, 2.0f)
            nvgFontSize(context, fontSize.toFloat())
            nvgTextAlign(context, alignment.getFontAlignmentFlags())
            nvgText(context, x.toFloat() + 2.0f, y.toFloat() + 2.0f, text)

            color.nvgColor { nvgColor ->
                nvgFillColor(context, nvgColor)
            }
            nvgFontFace(context, fontName)
            nvgFontBlur(context, 0.0f)
            nvgFontSize(context, fontSize.toFloat())
            nvgTextAlign(context, alignment.getFontAlignmentFlags())
            nvgText(context, x.toFloat(), y.toFloat(), text)
        }
    }

    private fun preRender() {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
    }
    private fun postRender() {
        RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()

        RenderSystem.activeTexture(GL_TEXTURE0)
        RenderSystem.bindTexture(0)
    }
    private inline fun path(fr: () -> Unit) {
        nvgBeginPath(context)
        fr()
        nvgClosePath(context)
    }
    private inline fun Color.nvgColor(fr: (NVGColor) -> Unit) {
        NVGColor
            .malloc()
            .r(red / 255f)
            .g(green / 255f)
            .b(blue / 255f)
            .a(alpha / 255f)
            .use { color ->
                fr(color)
            }
    }

}