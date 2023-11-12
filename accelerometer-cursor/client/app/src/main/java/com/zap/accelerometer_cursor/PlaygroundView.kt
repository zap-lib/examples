package com.zap.accelerometer_cursor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

data class Pointer(var x: Float, var y: Float, val paint: Paint)

class PlaygroundView : View {
    private val pointer = Pointer(0.0f, 0.0f, Paint().apply { color = Color.BLUE })

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        pointer.apply {
            x = width / 2f
            y = height / 2f
        }
    }

    override fun onDraw(canvas: Canvas) {
        pointer.apply {
            if (x < 0) x = 0f
            if (y < 0) y = 0f

            if (x > width) x = width.toFloat()
            if (y > height) y = height.toFloat()

            canvas.drawCircle(x, y, 30f, paint)
        }

        invalidate()
    }
}