package com.javadude.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CustomView1 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var halfWidth = 0f
    private var halfHeight = 0f
    private val strokeWidth = context.resources.getDimension(R.dimen.stroke_width)
    private val padding = context.resources.getDimension(R.dimen.padding)

    private val bb = ContextCompat.getDrawable(context, R.mipmap.bb)
    private val circle = ContextCompat.getDrawable(context, R.drawable.circle)
    private val android = ContextCompat.getDrawable(context, R.drawable.ic_android_white_24dp)

    private var stepX = 0f
    private var stepY = 0f

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = context.resources.getDimension(R.dimen.text_size)
        isAntiAlias = true
        setShadowLayer(30f, 0f, 0f, Color.BLACK)
    }

    private val line1 = "Hello There!"
    private val line2 = "How are you doing today?"
    private val line1Length = textPaint.measureText(line1, 0, line1.length)
    private val line2Length = textPaint.measureText(line2, 0, line2.length)
    private var line1x = 0f
    private var line2x = 0f
    private var line1baseline = 0f
    private var line2baseline = 0f


    private val linePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = this@CustomView1.strokeWidth
        style = Paint.Style.STROKE
    }
    private val stringArtPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        halfWidth = w/2f
        halfHeight = h/2f
        stepX = halfWidth / 100
        stepY = halfWidth / 100

        val quarterHeight = h/4f

        line1x = (halfWidth - line1Length) / 2
        line2x = (halfWidth - line2Length) / 2

        line1baseline = halfHeight + quarterHeight - textPaint.fontMetrics.bottom
        line2baseline = halfHeight + quarterHeight - textPaint.fontMetrics.top
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.BLACK)
        canvas.drawLine(0f, halfHeight, width.toFloat(), halfHeight, linePaint)
        canvas.drawLine(halfWidth, 0f, halfWidth, height.toFloat(), linePaint)


        bb?.setBounds(
            halfWidth.toInt() + padding.toInt(),
            padding.toInt(),
            width - padding.toInt(),
            halfHeight.toInt() - padding.toInt())          // top right
        android?.setBounds(
            halfWidth.toInt() + padding.toInt(),
            halfHeight.toInt() + padding.toInt(),
            width - padding.toInt(),
            height - padding.toInt())    // bottom right
        circle?.setBounds(
            padding.toInt(),
            halfHeight.toInt() + padding.toInt(),
            halfWidth.toInt() - padding.toInt(),
            height - padding.toInt())     // top left
        bb?.draw(canvas)
        android?.draw(canvas)
        circle?.draw(canvas)

        (0..100).forEach {                                                          // bottom right
            canvas.drawLine(
                stepX * it + padding,
                padding,
                padding, halfHeight - stepY * it - padding,
                stringArtPaint)
        }

        canvas.drawText("Hello", padding, halfHeight + padding - textPaint.fontMetrics.top, textPaint) // top left
        canvas.drawText(line1, line1x, line1baseline, textPaint)
        canvas.drawText(line2, line2x, line2baseline, textPaint)
    }
}