package com.javadude.widgetsv2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
        const val EXTRA_ITEM_NAME = "EXTRA_ITEM_NAME"
    }

    private val adapter = NamesAdapter()
    private lateinit var viewModel: SampleViewModel

    var people : List<Person>? = null

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.selectedName.value = intent.getStringExtra(EXTRA_ITEM_NAME)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(SampleViewModel::class.java)

        viewModel.selectedName.value = intent.getStringExtra(EXTRA_ITEM_NAME)
        viewModel.people.observe(this, Observer {
            people = it
            adapter.notifyDataSetChanged()
        })
        viewModel.selectedName.observe(this, Observer {
            name.setText(it ?: "")
        })
        setSupportActionBar(toolbar)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                viewModel.addPerson(name.text.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class NamesAdapter : RecyclerView.Adapter<NamesViewHolder>() {
        override fun getItemCount() = people?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NamesViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return NamesViewHolder(view)
        }

        override fun onBindViewHolder(holder: NamesViewHolder, position: Int) {
            holder.nameView.text = people!![position].name
        }
    }

    inner class NamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView : TextView = itemView.findViewById(android.R.id.text1)
        init {
            itemView.setOnClickListener {
                viewModel.selectedName.value = people!![adapterPosition].name
            }
        }
    }
}
