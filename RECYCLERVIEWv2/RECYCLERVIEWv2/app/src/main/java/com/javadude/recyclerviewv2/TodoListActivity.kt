package com.javadude.recyclerviewv2

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_list.*
import java.util.concurrent.Executors

class TodoListActivity : AppCompatActivity() {
    lateinit var viewModel: TodoViewModel
    var nextProjectNumber = 1
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val todoViewModelFactory = TodoViewModelFactory(application)

        viewModel = ViewModelProviders.of(this, todoViewModelFactory).get(TodoViewModel::class.java)

        viewModel.selectedItem.observe(this, Observer {
            if (it !== null) {
                startActivity(Intent(this, TodoEditActivity::class.java))
            }
        })
        add_todo_item_button.setOnClickListener(View.OnClickListener {
            viewModel.selectedItem.value = TodoItemEntity()
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
                viewModel.selectedItem, viewModel.todoItems)
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

        add_project_button.setOnClickListener(View.OnClickListener {
            val newProject = ProjectEntity()
            newProject.name = "Project " + nextProjectNumber
            nextProjectNumber++
            executor.execute {
                viewModel.save(newProject)
            }
        })

//        executor.execute {
//            viewModel.save(project)
//            viewModel.save(project, item1)
//            viewModel.save(project, item2)
//        }
//        viewModel.selectedProject.value = project
    }
 }