package com.javadude.fragv2

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_todo.fragment_container2
import kotlinx.android.synthetic.main.activity_todo.toolbar

import android.content.Intent

class TodoActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel

    private var editFragment : TodoEditFragment? = null
    private var listFragment : TodoListFragment? = null
    private var dualPane: Boolean = false

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

    }

    override fun onResume() {
        super.onResume()
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
                startActivity(Intent(this, AboutActivity::class.java))
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