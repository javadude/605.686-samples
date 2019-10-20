package com.javadude.rest


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.javadude.data.TodoItem

class TodoListFragment : Fragment() {
    private lateinit var viewModel : TodoViewModel
    private lateinit var adapter : TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(context as AppCompatActivity).get(TodoViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_todo_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_view)

        adapter = TodoAdapter {
            viewModel.selectedTodoItem.value = it
            findNavController().navigate(R.id.action_edit)
        }

        recycler.adapter = adapter

        viewModel.todoItems.observe(viewLifecycleOwner, Observer {
            when (it) {
                is TodoItemListResult -> adapter.items = it.items
                is TodoError -> {
                    adapter.items = emptyList()
                    // do something more meaningful with error
                    Toast.makeText(context, "Error: ${it.text}", Toast.LENGTH_LONG).show()
                }
                else -> {
                    adapter.items = emptyList()
                    Toast.makeText(context, "Unexpected response: $it", Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.actionResponse.observe(viewLifecycleOwner, Observer {
            if (it is TodoItemResult) {
                // new item was created - go edit it
                viewModel.selectedTodoItem.value = it.item
                findNavController().navigate(R.id.action_edit)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_add -> {
                viewModel.createTodoItem()
                // we will see the response in actionResponse
                // note that we should disable the add button or block all input with a "force field"
                true
            } else -> super.onOptionsItemSelected(item)
        }
}

class TodoViewHolder(itemView: View, onSelect : (item: TodoItem?) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val name = itemView.findViewById<TextView>(R.id.name)
    private val priority = itemView.findViewById<TextView>(R.id.priority)
    private var currentItem : TodoItem? = null
    init {
        itemView.setOnClickListener { onSelect(currentItem) }
    }
    fun bind(item : TodoItem) {
        currentItem = item
        name.text = item.name
        priority.text = item.priority.toString()
    }
}

class TodoAdapter(private val onSelect : (item: TodoItem?) -> Unit) : RecyclerView.Adapter<TodoViewHolder>() {
    var items : List<TodoItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged() // see the RecyclerView module for using a DiffUtil for better update
        }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false), onSelect)

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) =
        holder.bind(items[position])
}