package com.javadude.fragv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_edit.view.description
import kotlinx.android.synthetic.main.fragment_edit.view.name
import kotlinx.android.synthetic.main.fragment_edit.view.priority

class TodoEditFragment : Fragment() {
    private lateinit var viewModel: TodoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(activity!!).get(TodoViewModel::class.java)
    }

    inner class Saver : TextWatcher {
        override fun afterTextChanged(s: Editable?) = save()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val saver = Saver()
    private var loading = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        viewModel.selectedItem.observe(this, Observer {
            it?.let {item ->
                try {
                    loading = true
                    (activity as TodoActivity).supportActionBar!!.title = item.name
                    view.name.setText(item.name)
                    view.description.setText(item.description)
                    view.priority.setText(item.priority.toString())
                } finally {
                    loading = false
                }
            }
        })

        view.name.addTextChangedListener(saver)
        view.priority.addTextChangedListener(saver)
        view.description.addTextChangedListener(saver)
        return view
    }

    fun save() {
        if (!loading) {
            viewModel.updateSelectedItem(
                    view?.name?.text.toString(),
                    view?.description?.text.toString(),
                    view?.priority?.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == R.id.action_mark_done -> {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}