package com.javadude.customviews

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children

class GraphActivity : AppCompatActivity() {
    private lateinit var drawingArea : GraphDrawingArea

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val strokeWidth = resources.getDimension(R.dimen.stroke_width)
        val triangleFillColor = ContextCompat.getColor(this, R.color.triangle_fill)
        val strokeColor = ContextCompat.getColorStateList(this, R.color.stroke) ?: throw IllegalStateException("no stroke color state list found")

        val buttonTriangle = findViewById<ImageView>(R.id.button_triangle)
        val shapeSize = resources.getDimension(R.dimen.shape_size)
        buttonTriangle.setImageDrawable(createTriangle(shapeSize, strokeWidth, triangleFillColor, strokeColor))

        drawingArea = findViewById(R.id.drawing_area)
    }

    fun onButtonPressed(view : View) {
        drawingArea.mode = when (view.id) {
            R.id.button_square -> GraphDrawingArea.Mode.AddSquare
            R.id.button_circle -> GraphDrawingArea.Mode.AddCircle
            R.id.button_triangle -> GraphDrawingArea.Mode.AddTriangle
            R.id.button_selection -> GraphDrawingArea.Mode.Select
            R.id.button_line -> GraphDrawingArea.Mode.DrawLine
            else -> throw IllegalStateException("Unknown toolbar button")
        }

        (view.parent as ViewGroup).children.forEach {
            it.isSelected = (it == view)
        }
    }
}

