package com.javadude.sensors2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.min

class PuckView(context: Context) : View(context) {
    private var cx = 0
    private var cy = 0

    private var dx = 10
    private var dy = 20

    var ax = 0
    var ay = 0

    private var paint = Paint().apply {
        color = Color.RED
    }

    private var radius: Int = 0

    private var mover = object : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    sleep(50)
                } catch (e: InterruptedException) {
                    interrupt()
                }

                move()
                postInvalidate()
            }
        }
    }

    private fun move() {
        dx += ax
        dy += ay

        cx += dx
        cy += dy

        if (cx < radius) {
            dx = -dx / 2
            cx = radius
        }
        if (cy < radius) {
            dy = -dy / 2
            cy = radius
        }
        if (cx > width - radius) {
            dx = -dx / 2
            cx = width - radius
        }
        if (cy > height - radius) {
            dy = -dy / 2
            cy = height - radius
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = min(w, h) / 10
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mover.start()
    }

    override fun onDetachedFromWindow() {
        mover.interrupt()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)
    }
}
