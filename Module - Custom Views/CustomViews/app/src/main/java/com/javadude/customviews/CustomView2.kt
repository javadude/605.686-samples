package com.javadude.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CustomView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cx = 0f
    private var cy = 0f
    private var dx = 10f
    private var dy = 20f
    private var radius = 0f

    private val preferredSize = context.resources.getDimension(R.dimen.preferred_size)
    private var moverThread : MoverThread? = null

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = context.resources.getDimension(R.dimen.stroke_width)
    }

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.CustomView2)
            try {
                paint.color = a.getColor(R.styleable.CustomView2_color, Color.GRAY)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cx = w/2f
        cy = h/2f
        radius = min(w, h) / 3f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
        canvas.drawCircle(cx, cy, radius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = determineMeasure(widthMeasureSpec)
        val height = determineMeasure(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun determineMeasure(measureSpec: Int) : Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when(mode) {
            MeasureSpec.AT_MOST -> min(size, preferredSize.toInt())
            MeasureSpec.EXACTLY -> size
            else -> preferredSize.toInt()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) {
            moverThread = MoverThread()
            moverThread?.start()
        } else {
            moverThread?.interrupt()
            moverThread = null
        }
    }

    inner class MoverThread : Thread() {
        override fun run() {
            while(!isInterrupted) {
                try {
                    sleep(50)
                } catch (e : InterruptedException) {
                    interrupt()
                }

                move()
                postInvalidate()
            }
        }
    }

    private fun move() {
        cx += dx
        cy += dy

        if (cx < radius) {
            cx = radius
            dx = -dx
        }
        if (cx > width - radius) {
            cx = width - radius
            dx = -dx
        }
        if (cy < radius) {
            cy = radius
            dy = -dy
        }
        if (cy > height - radius) {
            cy = height - radius
            dy = -dy
        }
    }
}
