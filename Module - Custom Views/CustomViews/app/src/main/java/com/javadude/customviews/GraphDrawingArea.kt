package com.javadude.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewConfiguration
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import java.util.concurrent.Executors
import kotlin.math.abs

fun createTriangle(shapeSize : Float, strokeWidth: Float, triangleFillColor: Int, strokeColor: ColorStateList) =
    TriangleDrawable(strokeWidth, triangleFillColor, strokeColor).apply {
        bounds = Rect(0, 0, shapeSize.toInt(), shapeSize.toInt())
    }

class GraphDrawingArea @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    enum class Mode {
        AddSquare, AddCircle, AddTriangle, Select, DrawLine
    }

    companion object {
        val selectedState = intArrayOf(android.R.attr.state_selected)
        val unselectedState = intArrayOf()
    }

    var mode = Mode.Select

    private val executor = Executors.newSingleThreadExecutor()

    private var okToDraw = false
    private var selectedThing : Thing? = null
    private var thing1 : Thing? = null
    private var endOfLine : Point? = null
    private var tappedX = -1f
    private var tappedY = -1f
    private var offsetX = 0f
    private var offsetY = 0f
    private var movedMoreThanSlop = false

    private var lines = emptyList<Line>()
    private var things = emptyList<Thing>()

    private val shapeSize = resources.getDimension(R.dimen.shape_size)
    private val halfSize = shapeSize/2

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val squareDrawable = ContextCompat.getDrawable(context, R.drawable.square) ?: throw IllegalStateException("no square found")
    private val circleDrawable = ContextCompat.getDrawable(context, R.drawable.circle) ?: throw IllegalStateException("no color found")

    private val strokeWidth = resources.getDimension(R.dimen.stroke_width)
    private val triangleFillColor = ContextCompat.getColor(context, R.color.triangle_fill)
    private val triangleStrokeColor = ContextCompat.getColorStateList(context, R.color.stroke) ?: throw IllegalStateException("no stroke color state list found")

    private val triangleDrawable = createTriangle(shapeSize, strokeWidth, triangleFillColor, triangleStrokeColor)

    private val lineWidth = resources.getDimension(R.dimen.line_width)

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeWidth = lineWidth
        color = ContextCompat.getColor(context, R.color.line_color)
    }

    private val newLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeWidth = lineWidth
        color = ContextCompat.getColor(context, R.color.new_line_color)
    }

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}
            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                okToDraw = false
            }
            override fun surfaceCreated(p0: SurfaceHolder?) {
                okToDraw = true
                doDraw(null)
            }
        })
    }

    private fun findClickedThing(x : Float, y : Float) =
        things.findLast { it.bounds.contains(x, y) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) =
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when (mode) {
                    Mode.AddSquare -> {
                        offsetX = 0f
                        offsetY = 0f
                        things = things + Thing(Thing.Type.Square, event.thingBounds)
                        doDraw(null)
                    }
                    Mode.AddCircle -> {
                        offsetX = 0f
                        offsetY = 0f
                        things = things + Thing(Thing.Type.Circle, event.thingBounds)
                        doDraw(null)
                    }
                    Mode.AddTriangle -> {
                        offsetX = 0f
                        offsetY = 0f
                        things = things + Thing(Thing.Type.Triangle, event.thingBounds)
                        doDraw(null)
                    }
                    Mode.Select -> {
                        findClickedThing(event.x, event.y)?.let {
                            selectedThing = it
                            tappedX = event.x
                            tappedY = event.y
                            offsetX = it.bounds.centerX() - event.x
                            offsetY = it.bounds.centerY() - event.y
                            movedMoreThanSlop = false
                            things = things - it + it // move it to the end to be drawn last (on top)
                            doDraw(null)
                        }
                    }
                    Mode.DrawLine -> {
                        thing1 = findClickedThing(event.x, event.y)
                    }
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                selectedThing?.let {
                    if (!movedMoreThanSlop &&
                        (abs(event.x - tappedX) > touchSlop || abs(event.y - tappedY) > touchSlop)) {
                        movedMoreThanSlop = true
                    }
                    if (movedMoreThanSlop) {
                        it.bounds = event.thingBounds
                        doDraw(null)
                    }
                }
                thing1?.let {
                    endOfLine = Point(event.x.toInt(), event.y.toInt())
                    doDraw(null)
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                selectedThing?.let {
                    if (!movedMoreThanSlop) {
                        if (!blinkerInProgress) {
                            executor.execute(Blinker(it.type))
                        }
                    } else {
                        doDraw(null)
                    }
                }
                thing1?.let { firstEnd ->
                    findClickedThing(event.x, event.y)?.let { secondEnd ->
                        lines = lines + Line(firstEnd, secondEnd)
                    }
                    doDraw(null)
                }
                selectedThing = null
                thing1 = null
                endOfLine = null
                true
            }
            else -> super.onTouchEvent(event)
        }

    private val MotionEvent.thingBounds
            get() = RectF(
                        x - halfSize + offsetX,
                        y - halfSize + offsetY,
                        x + halfSize + offsetX,
                        y + halfSize + offsetY
                    )

    fun doDraw(selectedThingType: Thing.Type?) {
        if (!okToDraw) {
            return
        }
        val canvas = holder.lockCanvas()
        try {
            canvas.drawColor(Color.WHITE)
            lines.forEach {
                canvas.drawLine(
                    it.end1.bounds.centerX(),
                    it.end1.bounds.centerY(),
                    it.end2.bounds.centerX(),
                    it.end2.bounds.centerY(),
                    linePaint
                )
            }

            things.forEach {
                val drawable = when (it.type) {
                    Thing.Type.Square -> squareDrawable
                    Thing.Type.Circle -> circleDrawable
                    Thing.Type.Triangle -> triangleDrawable
                }
                drawable.bounds = it.bounds.toRect()

                drawable.state =
                    if (selectedThingType == it.type) {
                        selectedState
                    } else {
                        unselectedState
                    }

                drawable.draw(canvas)
            }

            endOfLine?.let { end1 ->
                thing1?.let { thing ->
                    canvas.drawLine(
                        thing.bounds.centerX(), thing.bounds.centerY(),
                        end1.x.toFloat(), end1.y.toFloat(), newLinePaint
                    )
                }
            }

        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private var blinkerInProgress = false
    private inner class Blinker(val selectedThingType : Thing.Type) : Runnable {
        override fun run() {
            blinkerInProgress = true
            doDraw(selectedThingType)
            Thread.sleep(250)
            doDraw(null)
            Thread.sleep(250)
            doDraw(selectedThingType)
            Thread.sleep(250)
            doDraw(null)
            Thread.sleep(250)
            doDraw(selectedThingType)
            Thread.sleep(250)
            doDraw(null)
            blinkerInProgress = false
        }
    }
}