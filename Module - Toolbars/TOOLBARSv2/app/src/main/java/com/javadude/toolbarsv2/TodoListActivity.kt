package com.javadude.toolbarsv2

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_list.*
import java.util.concurrent.Executors

class TodoListActivity : AppCompatActivity() {
    lateinit var viewModel: TodoViewModel
    var nextProjectNumber = 1
    private val executor = Executors.newSingleThreadExecutor()
    var currentActionMode : ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        val todoViewModelFactory = TodoViewModelFactory(application)

        viewModel = ViewModelProviders.of(this, todoViewModelFactory).get(TodoViewModel::class.java)

        viewModel.selectedItem.observe(this, Observer {
            if (it !== null) {
                startActivity(Intent(this, TodoEditActivity::class.java))
            }
        })
        viewModel.multiSelects.observe(this, Observer {
            if (it !== null && !it.isEmpty() && currentActionMode === null) {
                startActionMode(MultiSelectCallback())
            } else if (it === null) {
                currentActionMode?.finish()
            }
        })

//        val project = ProjectEntity()
//        project.name = "Project 1"
//
//        val item1 = TodoItemEntity()
//        item1.name = "Item 1"
//        item1.description = "Description 1"
//        item1.priority = 1
//
//        val item2 = TodoItemEntity()
//        item2.name = "Item 2"
//        item2.description = "Description 2"
//        item2.priority = 2
//
        todo_recycler_view.adapter = TodoItemAdapter(this,
                viewModel.selectedItem,
                viewModel.multiSelects,
                ResourcesCompat.getColor(resources, R.color.unselectedBackground, null),
                ResourcesCompat.getColor(resources, R.color.unselectedText, null),
                ResourcesCompat.getColor(resources, R.color.selectedBackground, null),
                ResourcesCompat.getColor(resources, R.color.selectedText, null),
                viewModel.todoItems)
        todo_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        ItemTouchHelper(TodoSwipeCallback(viewModel, this)).attachToRecyclerView(todo_recycler_view)

        project_spinner.adapter = ProjectAdapter(this, viewModel.projects)
        project_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.selectedProject.value = null
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectedProject.value = viewModel.projects.value!![position]
            }
        }

//        executor.execute {
//            viewModel.save(project)
//            viewModel.save(project, item1)
//            viewModel.save(project, item2)
//        }
//        viewModel.selectedProject.value = project
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.action_add_todo_item -> {
                // handle add item
                viewModel.selectedItem.value = TodoItemEntity()
                true
            }
            item.itemId == R.id.action_add_project -> {
                // handle add project
                val newProject = ProjectEntity()
                newProject.name = "Project " + nextProjectNumber
                nextProjectNumber++
                executor.execute {
                    viewModel.save(newProject)
                }
                true
            }
            item.itemId == R.id.action_about -> {
                // handle about
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
            viewModel.multiSelects.observe(this@TodoListActivity, this)
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