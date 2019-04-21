package com.javadude.fragv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.todo_item.name
import kotlinx.android.synthetic.main.todo_item.priority

class TodoItemAdapter(
        lifecycleOwner: LifecycleOwner,
        val viewModel: TodoViewModel,
        val unselectedBackground : Int,
        val unselectedText : Int,
        val selectedBackground : Int,
        val selectedText : Int
) : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    init {
        viewModel.todoItems.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
        viewModel.multiSelects.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
    }

    @UiThread
    override fun getItemCount(): Int {
        return viewModel.todoItems.value?.size ?: 0
    }

    @UiThread
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoItemViewHolder(view)
    }

    @UiThread
    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val item = viewModel.todoItems.value!![position]
        holder.bind(item)
    }

    inner class TodoItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnLongClickListener {
                if (viewModel.selectedItem.value !== null) {
                    viewModel.selectedItem.value = null
                }
                val newItem = viewModel.todoItems.value!![adapterPosition]
                val existingMultiSelects = viewModel.multiSelects.value
                val newSet =
                    if (existingMultiSelects !== null) {
                        if (existingMultiSelects.contains(newItem)) {
                            existingMultiSelects - newItem
                        } else {
                            existingMultiSelects + newItem
                        }
                    } else {
                        setOf(newItem)
                    }
                viewModel.multiSelects.value =
                        if (newSet.isEmpty()) {
                            null
                        } else {
                            newSet
                        }

                true
            }
            containerView.setOnClickListener {
                val newItem = viewModel.todoItems.value!![adapterPosition]
                val existingMultiSelects = viewModel.multiSelects.value
                if (existingMultiSelects === null) {
                    if (viewModel.selectedItem.value === newItem) {
                        viewModel.selectedItem.value = null
                    } else {
                        viewModel.selectedItem.value = newItem
                        viewModel.handleEvent(TodoViewModel.Event.SelectTodoItem)
                    }

                } else {
                    val newSet =
                        if (existingMultiSelects.contains(newItem)) {
                            existingMultiSelects - newItem
                        } else {
                            existingMultiSelects + newItem
                        }
                    viewModel.multiSelects.value =
                            if (newSet.isEmpty()) {
                                null
                            } else {
                                newSet
                            }
                }
            }
        }
        fun bind(todoItemEntity: TodoItemEntity) {
            name.text = todoItemEntity.name
            priority.text = todoItemEntity.priority.toString()
            val multi = viewModel.multiSelects.value
            val selected = (multi !== null && multi.contains(todoItemEntity)) || viewModel.selectedItem.value == todoItemEntity
            if (selected) {
                containerView.setBackgroundColor(selectedBackground)
                name.setTextColor(selectedText)
                priority.setTextColor(selectedText)
            } else {
                containerView.setBackgroundColor(unselectedBackground)
                name.setTextColor(unselectedText)
                priority.setTextColor(unselectedText)
            }
        }
    }
}