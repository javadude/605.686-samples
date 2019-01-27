package com.javadude.fragv2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import java.util.concurrent.Executors

//   drawing support adapted from
//     https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
class TodoSwipeCallback(
        val viewModel : TodoViewModel,
        val context: Context
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val backgroundColor: Int = Color.parseColor("#f44336")
    private val deleteIcon: Drawable
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int
    private val background = ColorDrawable()

    init {
        val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_sweep_white_24dp)!!
        this.deleteIcon = deleteIcon
        intrinsicWidth = deleteIcon.intrinsicWidth
        intrinsicHeight = deleteIcon.intrinsicHeight
    }

    private val executor = Executors.newSingleThreadExecutor()

    override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
        val item = viewModel.todoItems.value!![viewHolder.adapterPosition]
        executor.execute {
            viewModel.delete(item)
        }
    }

    override fun onChildDraw(canvas: Canvas,
                             recyclerView: androidx.recyclerview.widget.RecyclerView,
                             viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Draw the red delete background
        background.color = backgroundColor
        background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
        )
        background.draw(canvas)

        // Calculate position of delete icon
        val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val iconMargin = (itemHeight - intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + intrinsicHeight

        // Draw the delete icon
        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        deleteIcon.draw(canvas)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}