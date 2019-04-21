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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_todo.toolbar
import kotlinx.android.synthetic.main.fragment_list.view.project_spinner
import kotlinx.android.synthetic.main.fragment_list.view.todo_recycler_view
import java.util.concurrent.Executors

class TodoListFragment : Fragment() {
    lateinit var viewModel: TodoViewModel
    private var nextProjectNumber = 1
    private val executor = Executors.newSingleThreadExecutor()
    var currentActionMode : ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(activity!!).get(TodoViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        viewModel.multiSelects.observe(this, Observer {
            if (it !== null && !it.isEmpty() && currentActionMode === null) {
                (activity as TodoActivity).toolbar.startActionMode(MultiSelectCallback())
            } else if (it === null) {
                currentActionMode?.finish()
            }
        })

        view.todo_recycler_view.adapter = TodoItemAdapter(this,
                viewModel,
                ResourcesCompat.getColor(resources, R.color.unselectedBackground, null),
                ResourcesCompat.getColor(resources, R.color.unselectedText, null),
                ResourcesCompat.getColor(resources, R.color.selectedBackground, null),
                ResourcesCompat.getColor(resources, R.color.selectedText, null))
        view.todo_recycler_view.layoutManager = LinearLayoutManager(activity!!)

        ItemTouchHelper(TodoSwipeCallback(viewModel, activity!!)).attachToRecyclerView(view.todo_recycler_view)

        view.project_spinner.adapter = ProjectAdapter(this, viewModel.projects)
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
    inner class MultiSelectCallback : ActionMode.Callback, Observer<Set<TodoItemEntity>> {
        override fun onChanged(t: Set<TodoItemEntity>?) {
            currentActionMode?.title = t?.size?.toString() ?: "No selection"
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
            viewModel.multiSelects.observe(this@TodoListFragment, this)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

        override fun onDestroyActionMode(mode: ActionMode?) {
            viewModel.multiSelects.removeObserver(this)
            viewModel.multiSelects.value = null
            currentActionMode = null
        }
    }
 }