package com.javadude.toolbarsv2

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.todo_item.*

class TodoItemAdapter(
        val lifecycleOwner: LifecycleOwner,
        val selectedItem : MutableLiveData<TodoItemEntity>,
        val multiSelects : MutableLiveData<Set<TodoItemEntity>>,
        val unselectedBackground : Int,
        val unselectedText : Int,
        val selectedBackground : Int,
        val selectedText : Int,
        val items : LiveData<List<TodoItemEntity>>
) : androidx.recyclerview.widget.RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    init {
        items.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
        multiSelects.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
    }

    @UiThread
    override fun getItemCount(): Int {
        return items.value?.size ?: 0
    }

    @UiThread
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoItemViewHolder(view)
    }

    @UiThread
    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val item = items.value!![position]
        holder.bind(item)
    }

    inner class TodoItemViewHolder(override val containerView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            containerView.setOnLongClickListener {
                if (selectedItem.value !== null) {
                    selectedItem.value = null
                }
                val newItem = items.value!![adapterPosition]
                val existingMultiSelects = multiSelects.value
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
                multiSelects.value =
                        if (newSet.isEmpty()) {
                            null
                        } else {
                            newSet
                        }

                true
            }
            containerView.setOnClickListener {
                val newItem = items.value!![adapterPosition]
                val existingMultiSelects = multiSelects.value
                if (existingMultiSelects === null) {
                    if (selectedItem.value === newItem) {
                        selectedItem.value = null
                    } else {
                        selectedItem.value = newItem
                    }

                } else {
                    val newSet =
                        if (existingMultiSelects.contains(newItem)) {
                            existingMultiSelects - newItem
                        } else {
                            existingMultiSelects + newItem
                        }
                    multiSelects.value =
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
            val multi = multiSelects.value
            val selected = (multi !== null && multi.contains(todoItemEntity)) || selectedItem.value === todoItemEntity
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