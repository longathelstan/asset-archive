package dev.blend.util.render

import java.awt.Color
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
object ColorUtil {

    val Color.textColor: Color get() = if (0.2126 * (red / 255) + 0.7152 * (green / 255) + 0.0722 * (blue / 255) > 0.5) Color.BLACK else Color.WHITE

    fun Color.alpha(alpha: Float): Color {
        return Color(red, green, blue, max(0, min((alpha * 255).toInt(), 255)))
    }
    fun Color.withAlpha(alpha: Int): Color {
        return Color(red, green, blue, alpha)
    }

}