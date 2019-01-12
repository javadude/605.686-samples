package com.javadude.fragv2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_todo.*

class TodoActivity : AppCompatActivity() {
    lateinit var viewModel: TodoViewModel

    var editFragment : TodoEditFragment? = null
    var listFragment : TodoListFragment? = null
    var dualPane: Boolean = false

    override fun onPause() {
        val tx = supportFragmentManager.beginTransaction()
        try {
            editFragment?.let { tx.remove(it) }
            listFragment?.let { tx.remove(it) }
            editFragment = null
            listFragment = null

        } finally {
            tx.commit()
        }
        super.onPause()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        dualPane = (fragment_container2 !== null)

        viewModel.currentState.observe(this, Observer {
            val tx = supportFragmentManager.beginTransaction()
            try {
                when (it) {
                    TodoViewModel.State.List -> {
                        if (dualPane) {
                            editFragment = TodoEditFragment()
                            listFragment = TodoListFragment()
                            tx.replace(R.id.fragment_container2, listFragment!!)
                            tx.replace(R.id.fragment_container3, editFragment!!)
                        } else {
                            editFragment = null
                            listFragment = TodoListFragment()
                            tx.replace(R.id.fragment_container1, listFragment!!)
                        }
                    }
                    TodoViewModel.State.Edit -> {
                        if (dualPane) {
                            editFragment = TodoEditFragment()
                            listFragment = TodoListFragment()
                            tx.replace(R.id.fragment_container2, listFragment!!)
                            tx.replace(R.id.fragment_container3, editFragment!!)
                        } else {
                            listFragment = null
                            editFragment = TodoEditFragment()
                            tx.replace(R.id.fragment_container1, editFragment!!)
                        }
                    }
                    TodoViewModel.State.Exit -> finish()
                    else -> throw IllegalStateException()
                }
            } finally {
                tx.commit()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.action_about -> {
                // handle about
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        if (dualPane) {
            viewModel.handleEvent(TodoViewModel.Event.BackDualPane)
        } else {
            viewModel.handleEvent(TodoViewModel.Event.BackSinglePane)
        }
    }
 }