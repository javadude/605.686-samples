package com.javadude.toolbarsv2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_edit.*

class TodoEditActivity : AppCompatActivity() {
    lateinit var viewModel: TodoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar2)

        val todoViewModelFactory = TodoViewModelFactory(application)

        viewModel = ViewModelProviders.of(this, todoViewModelFactory).get(TodoViewModel::class.java)

        viewModel.selectedItem.observe(this, Observer {
            it?.let {
                supportActionBar!!.title = it.name
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.action_mark_done -> {
                true
            }
            item.itemId == R.id.action_about -> {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}