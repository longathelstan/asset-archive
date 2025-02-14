package dev.blend.util.alignment

import org.lwjgl.nanovg.NanoVG

enum class Alignment {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    /**
     * @param width width of the element
     * @param height height of the element
     * @return calculated x and y coordinates based on width and height of the element.
     */
    fun getPosition(width: Float, height: Float): (Float, Float) -> Pair<Float, Float> {
        return { x, y ->
            when (this) {
                TOP_LEFT -> x to y
                TOP_CENTER -> (x + (width / 2)) to y
                TOP_RIGHT -> (x + width) to y

                CENTER_LEFT -> x to (y + (height / 2))
                CENTER -> (x + (width / 2)) to (y + (height / 2))
                CENTER_RIGHT -> (x + width) to (y + (height / 2))

                BOTTOM_LEFT -> x to (y + height)
                BOTTOM_CENTER -> (x + (width / 2)) to (y + height)
                BOTTOM_RIGHT -> (x + width) to (y + height)
            }
        }
    }

    fun getFontAlignmentFlags(): Int {
        return when (this) {
                TOP_LEFT -> NanoVG.NVG_ALIGN_TOP or NanoVG.NVG_ALIGN_LEFT
                TOP_CENTER -> NanoVG.NVG_ALIGN_TOP or NanoVG.NVG_ALIGN_CENTER
                TOP_RIGHT -> NanoVG.NVG_ALIGN_TOP or NanoVG.NVG_ALIGN_RIGHT

                CENTER_LEFT -> NanoVG.NVG_ALIGN_MIDDLE or NanoVG.NVG_ALIGN_LEFT
                CENTER -> NanoVG.NVG_ALIGN_MIDDLE or NanoVG.NVG_ALIGN_CENTER
                CENTER_RIGHT -> NanoVG.NVG_ALIGN_MIDDLE or NanoVG.NVG_ALIGN_RIGHT

                BOTTOM_LEFT -> NanoVG.NVG_ALIGN_BOTTOM or NanoVG.NVG_ALIGN_LEFT
                BOTTOM_CENTER -> NanoVG.NVG_ALIGN_BOTTOM or NanoVG.NVG_ALIGN_CENTER
                BOTTOM_RIGHT -> NanoVG.NVG_ALIGN_BOTTOM or NanoVG.NVG_ALIGN_RIGHT
        }
    }

}
