package com.javadude.livedatav2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    val executor = Executors.newSingleThreadExecutor()
    var nextItem = 0
    lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        viewModel.selectedItem.observe(this, Observer {
            item_name.text = it?.name ?: ""
            item_description.text = it?.description ?: ""
            item_priority.text = it?.priority?.toString() ?: ""
        })

        viewModel.selectedProject.observe(this, Observer {
            project_name.text = it?.name ?: ""
        })

        viewModel.todoItems.observe(this, Observer {
            if (it === null) {
                viewModel.selectedItem.value = null
            } else if (!it.isEmpty()) {
                viewModel.selectedItem.value = it[0]
            }

            val builder = StringBuilder()
            it?.forEach {
                builder.append(it.name)
                builder.append("\n")
            }
            item_dump.text = builder.toString()
        })

        val project1 = Project()
        val project2 = Project()
        project1.id = "project1"
        project1.name = "Project 1"
        project2.id = "project1"
        project2.name = "Project 2"

        executor.execute({
            viewModel.save(project1)
            viewModel.save(project2)
        })

        viewModel.selectedProject.value = project1

        show_project1_button.setOnClickListener({
            viewModel.selectedProject.value = project1
        })
        show_project2_button.setOnClickListener({
            viewModel.selectedProject.value = project2
        })
        add_item_button.setOnClickListener({
            val project = viewModel.selectedProject.value
            project?.let {
                val item = TodoItem()
                val n = nextItem++
                item.name = "Name $n"
                item.description = "Description $n"
                item.priority = n
                executor.execute({
                    viewModel.save(project, item)
                })
            }
        })
    }
}
