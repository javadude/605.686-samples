package com.javadude.recyclerviewv2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_edit.view.*
import java.util.concurrent.Executors

class TodoEditActivity : AppCompatActivity() {
    lateinit var viewModel: TodoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val todoViewModelFactory = TodoViewModelFactory(application)

        viewModel = ViewModelProviders.of(this, todoViewModelFactory).get(TodoViewModel::class.java)

        viewModel.selectedItem.observe(this, Observer {
            it?.let {
                name.setText(it.name)
                description.setText(it.description)
                priority.setText(it.priority.toString())
            }
        })
    }

    @UiThread
    override fun onPause() {
        viewModel.updateSelectedItem(
                name.text.toString(),
                description.text.toString(),
                priority.text.toString())
        super.onPause()
    }
}