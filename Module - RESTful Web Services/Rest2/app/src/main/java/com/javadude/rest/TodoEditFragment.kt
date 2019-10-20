package com.javadude.rest


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.javadude.data.TodoItem

class TodoEditFragment : Fragment() {
    private lateinit var viewModel : TodoViewModel
    private lateinit var nameView : EditText
    private lateinit var descriptionView : EditText
    private lateinit var priorityView : EditText
    private var currentItem : TodoItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(context as AppCompatActivity).get(TodoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_todo_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameView = view.findViewById(R.id.name)
        descriptionView = view.findViewById(R.id.description)
        priorityView = view.findViewById(R.id.priority)

        viewModel.selectedTodoItem.observe(viewLifecycleOwner, Observer {
            nameView.setText(it?.name)
            descriptionView.setText(it?.description)
            priorityView.setText(it?.priority?.toString())
            currentItem = it
        })

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
