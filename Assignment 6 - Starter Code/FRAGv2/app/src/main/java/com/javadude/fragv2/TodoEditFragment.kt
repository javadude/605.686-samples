package com.javadude.fragv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
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
        viewModel = ViewModelProviders.of(activity!!).get(TodoViewModel::class.java)
    }

    inner class TextChangeListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) = save()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val textChangeListener = TextChangeListener()
    private var loading = false
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

        view.name.addTextChangedListener(textChangeListener)
        view.priority.addTextChangedListener(textChangeListener)
        view.description.addTextChangedListener(textChangeListener)
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
}