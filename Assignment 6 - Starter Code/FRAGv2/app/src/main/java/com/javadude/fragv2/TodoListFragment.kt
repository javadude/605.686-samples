package com.javadude.fragv2

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_todo.toolbar
import kotlinx.android.synthetic.main.fragment_list.project_spinner
import kotlinx.android.synthetic.main.fragment_list.view.project_spinner
import kotlinx.android.synthetic.main.fragment_list.view.todo_recycler_view
import java.util.concurrent.Executors

class TodoListFragment : Fragment() {
    lateinit var viewModel: TodoViewModel
    private var nextProjectNumber = 1
    private val executor = Executors.newSingleThreadExecutor()
    var currentActionMode : ActionMode? = null

    private fun <T> LiveData<T>.observe(observer : (T?) -> Unit) =
            observe(viewLifecycleOwner, Observer<T> {
                observer(it)
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(activity!!).get(TodoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        viewModel.multiSelects.observe {
            if (it !== null && it.isNotEmpty() && currentActionMode === null) {
                (activity as TodoActivity).toolbar.startActionMode(MultiSelectCallback())
            } else if (it === null) {
                currentActionMode?.finish()
            }
        }

        val todoItemAdapter = TodoItemAdapter(
                ResourcesCompat.getColor(resources, R.color.unselectedBackground, null),
                ResourcesCompat.getColor(resources, R.color.unselectedText, null),
                ResourcesCompat.getColor(resources, R.color.selectedBackground, null),
                ResourcesCompat.getColor(resources, R.color.selectedText, null),
                onItemSelected = {
                    viewModel.selectedItem.value = it
                    viewModel.handleEvent(TodoViewModel.Event.SelectTodoItem)
                },
                onMultiSelectChanged = {
                    viewModel.multiSelects.value = it
                })
        view.todo_recycler_view.adapter = todoItemAdapter
        viewModel.todoItems.observe {
            todoItemAdapter.items = it ?: emptyList()
        }
        viewModel.selectedItem.observe {
            todoItemAdapter.selectedItem = it
        }
        viewModel.multiSelects.observe {
            todoItemAdapter.multiSelects = it ?: emptySet()
        }
        viewModel.selectedProject.observe {
            project_spinner.setSelection(viewModel.projects.value?.indexOf(it) ?: -1)
        }

        view.todo_recycler_view.layoutManager = LinearLayoutManager(activity!!)

        ItemTouchHelper(TodoSwipeCallback(viewModel)).attachToRecyclerView(view.todo_recycler_view)

        val projectAdapter = ProjectAdapter()
        view.project_spinner.adapter = projectAdapter
        viewModel.projects.observe {
            projectAdapter.items = it ?: emptyList()
        }

        view.project_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.selectedProject.value = null
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectedProject.value = viewModel.projects.value!![position]
            }
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.action_add_todo_item -> {
                // handle add item
                viewModel.selectedItem.value = TodoItemEntity()
                viewModel.handleEvent(TodoViewModel.Event.CreateNewTodoItem)
                true
            }
            item.itemId == R.id.action_add_project -> {
                // handle add project
                val newProject = ProjectEntity()
                newProject.name = "Project $nextProjectNumber"
                nextProjectNumber++
                executor.execute {
                    viewModel.save(newProject)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    // CONTEXTUAL ACTION MODE SUPPORT
    inner class MultiSelectCallback : ActionMode.Callback {
        private val observer = Observer { t: Set<TodoItemEntity>? ->
            if (t?.isEmpty() != false) {
                currentActionMode?.finish()
            } else {
                currentActionMode?.title = t.size.toString()
            }
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return if (item.itemId == R.id.action_delete) {
                viewModel.deleteMulti()
                mode.finish()
                true
            } else {
                false
            }
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.multi_select_menu, menu)
            currentActionMode = mode
            viewModel.multiSelects.observe(viewLifecycleOwner, observer)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

        override fun onDestroyActionMode(mode: ActionMode?) {
            viewModel.multiSelects.removeObserver(observer)
            viewModel.multiSelects.value = null
            currentActionMode = null
        }
    }
 }