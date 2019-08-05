package com.javadude.fragv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.todo_item.name
import kotlinx.android.synthetic.main.todo_item.priority

class TodoItemAdapter(
        val unselectedBackground : Int,
        val unselectedText : Int,
        val selectedBackground : Int,
        val selectedText : Int,
        val onItemSelected : (TodoItemEntity?) -> Unit,
        val onMultiSelectChanged : (Set<TodoItemEntity>) -> Unit
) : RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    var items : List<TodoItemEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedItem : TodoItemEntity? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var multiSelects : Set<TodoItemEntity> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    @UiThread
    override fun getItemCount() = items.size

    @UiThread
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoItemViewHolder(view)
    }

    @UiThread
    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    inner class TodoItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnLongClickListener {
                val clickedItem = items[adapterPosition]
                if (clickedItem in multiSelects) {
                    onMultiSelectChanged(multiSelects - clickedItem)
                } else {
                    onMultiSelectChanged(multiSelects + clickedItem)
                }

                true
            }
            containerView.setOnClickListener {
                val clickedItem = items[adapterPosition]
                if (multiSelects.isEmpty()) {
                    if (selectedItem === clickedItem) {
                        onItemSelected(null)
                    } else {
                        onItemSelected(clickedItem)
                    }

                } else {
                    if (clickedItem in multiSelects) {
                        onMultiSelectChanged(multiSelects - clickedItem)
                    } else {
                        onMultiSelectChanged(multiSelects + clickedItem)
                    }
                }
            }
        }
        fun bind(todoItemEntity: TodoItemEntity) {
            name.text = todoItemEntity.name
            priority.text = todoItemEntity.priority.toString()
            val selected = (todoItemEntity in multiSelects) || selectedItem == todoItemEntity
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