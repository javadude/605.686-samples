package com.javadude.moviesnav

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// Handler for LEFT-SWIPE = delete
class Swiper(
    context : Context,
    private val deleter : (Int) -> Unit) : ItemTouchHelper.SimpleCallback(0,
    ItemTouchHelper.LEFT
) {
    private val deleteIcon = ContextCompat.getDrawable(
        context,
        R.drawable.ic_delete_sweep_white_24dp
    )
    private val background = ColorDrawable(
        ContextCompat.getColor(
            context,
            R.color.delete_swipe_background
        )
    )
    private val deleteIconMargin = context.resources.getDimension(R.dimen.delete_icon_margin).toInt()

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false // should never get called

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        deleter(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        // get the bounds of the viewHolder
        //    (we're only allowing left swipe, so we don't need to know the left margin)
        val top = viewHolder.itemView.top
        val right = viewHolder.itemView.right
        val bottom = viewHolder.itemView.bottom

        // set the bounds and draw the red background
        //   when swiping left, dX will be the negative amount the view has been swiped so far
        background.setBounds(right+dX.toInt(), top, right, bottom)
        background.draw(c)

        // draw the trash can icon
        deleteIcon?.let {
            // make it as big as possible to fit as a square vertically within the
            //   specified margins between the top and bottom of the view
            val iconSize = bottom - top - deleteIconMargin*2
            it.setBounds(
                right - iconSize - deleteIconMargin,
                top + deleteIconMargin,
                right-deleteIconMargin,
                bottom - deleteIconMargin)
            it.draw(c)
        }
    }
}