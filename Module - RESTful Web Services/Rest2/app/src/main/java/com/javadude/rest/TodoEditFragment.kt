package com.javadude.rest


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.javadude.data.TodoItem

class TodoEditFragment : Fragment() {
    private val viewModel by activityViewModels<TodoViewModel>()
    private lateinit var nameView : EditText
    private lateinit var descriptionView : EditText
    private lateinit var priorityView : EditText
    private var currentItem : TodoItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_todo_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameView = view.findViewById(R.id.name)
        descriptionView = view.findViewById(R.id.description)
        priorityView = view.findViewById(R.id.priority)

        viewModel.selectedTodoItem.observe(viewLifecycleOwner) {
            nameView.setText(it?.name)
            descriptionView.setText(it?.description)
            priorityView.setText(it?.priority?.toString())
            currentItem = it
        }

        fun EditText.updateItemOnChange(fieldUpdate : (String) -> Unit) {
            addTextChangedListener { editable ->
                currentItem?.let {
                    fieldUpdate(editable.toString())
                    viewModel.updateTodoItem(it)
                }
            }
        }

        nameView.updateItemOnChange { currentItem?.name = it }
        descriptionView.updateItemOnChange { currentItem?.description = it }
        priorityView.updateItemOnChange { currentItem?.priority = it.toIntOrNull() ?: 1}
    }

}
