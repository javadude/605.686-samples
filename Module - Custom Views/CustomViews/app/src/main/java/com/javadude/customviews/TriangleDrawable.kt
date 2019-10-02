package com.javadude.customviews

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable

class TriangleDrawable(
    strokeWidth : Float,
    fillColor : Int,
    private val strokeColor : ColorStateList
) : Drawable() {

    companion object {
        fun create(shapeSize : Float, strokeWidth: Float, triangleFillColor: Int, strokeColor: ColorStateList) =
            TriangleDrawable(strokeWidth, triangleFillColor, strokeColor).apply {
                bounds = Rect(0, 0, shapeSize.toInt(), shapeSize.toInt())
            }
    }

    private var path = Path()

    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        this.strokeWidth = strokeWidth
        color = strokeColor.getColorForState(IntArray(0), 0)
    }

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = fillColor
    }

    override fun onBoundsChange(bounds: Rect) {
        path = Path().apply {
            moveTo(bounds.left + bounds.width()/2f, bounds.top.toFloat())
            lineTo(bounds.right.toFloat(), bounds.bottom.toFloat())
            lineTo(bounds.left.toFloat(), bounds.bottom.toFloat())
            close()
        }
        super.onBoundsChange(bounds)
    }

    override fun isStateful() = true
    override fun onStateChange(stateSet: IntArray): Boolean {
        strokePaint.color = strokeColor.getColorForState(stateSet, 0)
        return true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, strokePaint)
    }

    override fun getOpacity() = PixelFormat.TRANSPARENT

    override fun setAlpha(p0: Int) {    } // ignore - really should do something...
    override fun setColorFilter(p0: ColorFilter?) {    } // ignore - really should do something...
}