package com.javadude.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sqrt

class PictographView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var drawable : Drawable? = null
        set(value) {
            field = value
            calculate(width, height)
            postInvalidate()
        }
    var count = -1f
        set(value) {
            field = value
            calculate(width, height)
            postInvalidate()
        }
    var drawableColor = -1
        set(value) {
            field = value
            calculate(width, height)
            postInvalidate()
        }

    private var drawableSize = 0.0
    private var rows = -1
    private var columns = -1
    private val preferredSize = context.resources.getDimension(R.dimen.preferred_size)
    private var hasSeenOnSizeChangedAtLeastOnce = false

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.PictographView)
            try {
                drawable = a.getDrawable(R.styleable.PictographView_drawable)?.mutate() // needed to not share drawable state!
                count = a.getFloat(R.styleable.PictographView_count, -1f)
                drawableColor = a.getColor(R.styleable.PictographView_drawable_color, -1)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hasSeenOnSizeChangedAtLeastOnce = true
        calculate(w, h)
    }

    fun calculate(w: Int, h: Int) {
        if (!hasSeenOnSizeChangedAtLeastOnce) {
            return
        }
        requireNotNull(drawable) { "PictographView must have a drawable set before it's displayed"}
        require(count >= 0) { "PictographView must have a count >= 0 set before it's displayed"}
        require(drawableColor != -1) { "PictographView must have a drawableColor set before it's displayed"}

        val usableW = w - paddingEnd - paddingStart
        val usableH = h - paddingTop - paddingBottom

        drawable!!.setTint(drawableColor) // API 21 or later only...

        val countCeil = ceil(count) // last one might be partial... we need the full square in the calculation, though

        // see math slide for description of how I got here
        val aspectRatio = usableW.toDouble()/usableH
        val doubleColumns = sqrt(countCeil * aspectRatio)
        columns = ceil(doubleColumns).toInt()
        val doubleRows = columns / aspectRatio
        rows = ceil(doubleRows).toInt()

        // double-check - sometimes it looks like round-off gets us when computing rows...
        // if we can fit everything with one less row, use one less row
        if ((rows-1) * columns >= count) {
            rows--
        }

        // NOTE - there are some odd edge cases that cause some blank rows/cols. I'm not going
        //        to run all of these to ground as this is just a fairly simple example of
        //        a more realistic custom view, and not intended for real use anyway...

        drawableSize = min(usableW.toDouble() / columns, usableH.toDouble() / rows)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) // let the superclass draw the background color

        drawable?.let {drawable ->
            val maskWidth = count - count.toInt()
            var drawn = 0
            var y = paddingTop
            repeat(rows) {
                val nextY = (y + drawableSize).toInt()
                var x = paddingStart
                repeat(columns) {
                    val nextX = (x + drawableSize).toInt()
                    if (drawn + 1 > count) { // if the last drawable
                        canvas.save()
                        canvas.clipRect(x, y, (x + drawableSize*maskWidth).toInt(), nextY)
                    }
                    drawable.setBounds(x, y, nextX, nextY)
                    drawable.draw(canvas)
                    if (drawn + 1 > count) {
                        canvas.restore()
                    }
                    drawn++
                    if (drawn > count) {
                        return
                    }
                    x = nextX
                }
                y = nextY
            }
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
}
