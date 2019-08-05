package com.javadude.fragv2

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class TodoSwipeCallback(private val viewModel : TodoViewModel)
        : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val executor = Executors.newSingleThreadExecutor()

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = viewModel.todoItems.value!![viewHolder.adapterPosition]
        executor.execute {
            viewModel.delete(item)
        }
    }
}