package com.javadude.databinding2.example08

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.stetho.Stetho
import com.javadude.databinding2.R
import com.javadude.databinding2.databinding.ListItem08Binding
import kotlinx.android.synthetic.main.activity_list08.*

class ListActivity : AppCompatActivity() {
    val layout = R.layout.activity_list08

    private lateinit var viewModel : SampleViewModel
    private var items : List<Person>? = null
    private lateinit var adapter : PersonListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        viewModel = ViewModelProviders.of(this).get(SampleViewModel::class.java)
        viewModel.people.observe(this, Observer {
            items = it
            adapter.notifyDataSetChanged()
        })
        setContentView(layout)
        adapter = PersonListAdapter()
        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    inner class PersonListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<PersonViewHolder>() {
        override fun getItemCount() = items?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PersonViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_08, parent, false)

            return PersonViewHolder(view)
        }

        override fun onBindViewHolder(holder: PersonViewHolder, position: Int) = holder.bind(items!![position])
    }

    inner class PersonViewHolder(view : View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val binding : ListItem08Binding = DataBindingUtil.bind(view)!!
        var personId : String? = null

        init {
            view.setOnClickListener {
                startActivity(
                        Intent(this@ListActivity, ItemActivity::class.java)
                                .putExtra("id", personId))
            }
        }

        fun bind(person : Person) {
            this.personId = person.id
            binding.person = person
        }
    }
}
