package co.softov.morestuff.android.presentation.priority

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt

class PriorityView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var shapes: MutableList<Circle> = mutableListOf()

    fun addCircle(centerX: Float, centerY: Float, radius: Float, @ColorInt color: Int) {
        val paint = Paint().with(color)
        Circle(centerX, centerY, radius, paint).let { shapes.add(it) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.draw(shapes)
    }
}

fun Paint.with(@ColorInt color: Int) = Paint(ANTI_ALIAS_FLAG).apply { setColor(color) }

fun Canvas.draw(shapes: List<Circle>) {
    for (circle in shapes) {
        drawCircle(circle.centerX, circle.centerY, circle.radius, circle.paint)
    }
}

data class Circle(
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val paint: Paint
)