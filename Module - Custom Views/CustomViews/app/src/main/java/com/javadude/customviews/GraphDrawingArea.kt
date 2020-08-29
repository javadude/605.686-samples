package com.javadude.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewConfiguration
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import java.util.concurrent.Executors
import kotlin.math.abs


enum class Mode {
    AddSquare, AddCircle, AddTriangle, Select, DrawLine
}

data class Thing(val type : Type, var bounds : RectF) {
    enum class Type {
        Square, Circle, Triangle
    }
}

data class Line(val end1 : Thing, val end2 : Thing)


class GraphDrawingArea @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {


    companion object {
        val selectedState = intArrayOf(android.R.attr.state_selected)
        val unselectedState = intArrayOf()
    }

    var mode = Mode.Select

    private val executor = Executors.newSingleThreadExecutor()

    private val shapeSize = resources.getDimension(R.dimen.shape_size)
    private val halfSize = shapeSize/2

    private val squareDrawable = ContextCompat.getDrawable(context, R.drawable.square) ?: throw IllegalStateException("no square found")
    private val circleDrawable = ContextCompat.getDrawable(context, R.drawable.circle) ?: throw IllegalStateException("no circle found")

    private val strokeWidth = resources.getDimension(R.dimen.stroke_width)
    private val triangleFillColor = ContextCompat.getColor(context, R.color.triangle_fill)
    private val triangleStrokeColor = ContextCompat.getColorStateList(context, R.color.stroke) ?: throw IllegalStateException("no stroke color state list found")
    private val triangleDrawable = TriangleDrawable.create(shapeSize, strokeWidth, triangleFillColor, triangleStrokeColor)

    private val lineWidth = resources.getDimension(R.dimen.line_width)

    // NOTE: To track lines and things we use a VAR immutable list rather than a VAL mutable list
    //       This avoid issues with concurrent modification when the user taps while drawing items
    private var lines = emptyList<Line>()       // lines to be drawn
    private var things = emptyList<Thing>()     // things to be drawn

    private var okToDraw = false                // make sure the surface is ready

    private var selectedThing : Thing? = null   // when selecting an item
    private var thing1 : Thing? = null          // first end of drawing a line
    private var endOfLine : Point? = null       // the point being dragged when creating a line
    private var tappedX = -1f                   // where the user tapped (for slop calculation)
    private var tappedY = -1f
    private var offsetX = 0f                    // offset of tap from center of thing
    private var offsetY = 0f

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var movedMoreThanSlop = false

    private var blinkerInProgress = false       // are we blinking - prevents multiple blink threads from being queued


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
            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
            override fun surfaceDestroyed(p0: SurfaceHolder) {
                okToDraw = false
            }
            override fun surfaceCreated(p0: SurfaceHolder) {
                okToDraw = true
                doDraw(null)
            }
        })
    }

    /**
     * Find the tapped thing - note that we find the **last** thing in the list
     * that contains the tapped point because it will be the one painted on top of
     * all other items at that point
     */
    private fun findClickedThing(x : Float, y : Float) =
        things.findLast { it.bounds.contains(x, y) }

    /**
     * Extension property (read-only) that converts a MotionEven (where the user taps/drags)
     * into a RectF that represents a square Thing
     */
    private val MotionEvent.thingBounds
        get() = RectF(
            x - halfSize + offsetX,
            y - halfSize + offsetY,
            x + halfSize + offsetX,
            y + halfSize + offsetY
        )

    /**
     * Handle user taps and drags
     */
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
                    selectedThing = null

                    // if the user tapped and released without ever moving more than touch slop,
                    //   blink the tapped item
                    // if we're currently blinking, do nothing (otherwise a bunch of fast taps
                    //   will queue up a bunch of blinker tasks in the executor, and they can cause
                    //   some really odd draw behavior that slows things down and interacts poorly
                    //   with additional user interaction
                    if (!movedMoreThanSlop) {
                        if (!blinkerInProgress) {
                            executor.execute(createBlinker(it.type))
                        }
                    } else {
                        doDraw(null)
                    }
                }

                thing1?.let { firstEnd ->
                    findClickedThing(event.x, event.y)?.let { secondEnd ->
                        lines = lines + Line(firstEnd, secondEnd)
                    }
                    thing1 = null
                    endOfLine = null
                    doDraw(null)
                }

                true
            }
            else -> super.onTouchEvent(event)
        }

    /**
     * Do the actual drawing (draw is the name of an inherited function, so I chose "doDraw" instead)
     * @param selectedThingType If we're being asked to draw to highlight things, this is the
     *                          type of the things to highlight. If null, we won't highlight anything
     */
    fun doDraw(selectedThingType: Thing.Type?) {
        if (!okToDraw) {
            return
        }

        val canvas = holder.lockCanvas()

        try {
            // fill the background white
            canvas.drawColor(Color.WHITE)

            // draw the lines
            lines.forEach {
                canvas.drawLine(
                    it.end1.bounds.centerX(),
                    it.end1.bounds.centerY(),
                    it.end2.bounds.centerX(),
                    it.end2.bounds.centerY(),
                    linePaint
                )
            }

            // draw the things
            things.forEach {
                // find the right rubber stamp based on the type of the thing
                val drawable = when (it.type) {
                    Thing.Type.Square -> squareDrawable
                    Thing.Type.Circle -> circleDrawable
                    Thing.Type.Triangle -> triangleDrawable
                }

                // move the rubber stamp to the right location
                drawable.bounds = it.bounds.toRect()

                // set the state of the stamp if we're drawing a highlight and it matches
                drawable.state =
                    if (selectedThingType == it.type) {
                        selectedState
                    } else {
                        unselectedState
                    }

                // draw the thing
                drawable.draw(canvas)
            }

            // draw the line being created (if in progress)
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

    private fun createBlinker(selectedThingType : Thing.Type) : () -> Unit = {
            try {
                blinkerInProgress = true
                repeat(3) {
                    doDraw(selectedThingType)
                    Thread.sleep(250)
                    doDraw(null)
                    Thread.sleep(250)
                }
            } finally {
                blinkerInProgress = false
            }
        }
}