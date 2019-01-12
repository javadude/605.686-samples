package com.javadude.fragv2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import kotlinx.android.synthetic.main.fragment_edit.view.*

class TodoEditFragment : Fragment() {
    lateinit var viewModel: TodoViewModel
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
    val saver = Saver()
    var loading = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        viewModel.selectedItem.observe(this, Observer {
            it?.let {
                try {
                    loading = true
                    (activity as TodoActivity).supportActionBar!!.title = it.name
                    view.name.setText(it.name)
                    view.description.setText(it.description)
                    view.priority.setText(it.priority.toString())
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