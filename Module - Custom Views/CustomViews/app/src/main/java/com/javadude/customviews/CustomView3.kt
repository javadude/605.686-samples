package com.javadude.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import kotlin.math.min

class CustomView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

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

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.CustomView3)
            try {
                paint.color = a.getColor(R.styleable.CustomView2_color, Color.GRAY)
            } finally {
                a.recycle()
            }
        }
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}
            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                moverThread?.interrupt()
                moverThread = null
            }
            override fun surfaceCreated(p0: SurfaceHolder?) {
                moverThread = MoverThread()
                moverThread?.start()
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cx = w/2f
        cy = h/2f
        radius = min(w, h) / 3f
    }

    fun drawBall() {
        val canvas = holder.lockCanvas()
        try {
            canvas.drawColor(Color.WHITE)
            canvas.drawCircle(cx, cy, radius, paint)

        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
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

    inner class MoverThread : Thread() {
        override fun run() {
            while(!isInterrupted) {
                try {
                    sleep(50)
                } catch (e : InterruptedException) {
                    interrupt()
                }

                move()
                drawBall()
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
