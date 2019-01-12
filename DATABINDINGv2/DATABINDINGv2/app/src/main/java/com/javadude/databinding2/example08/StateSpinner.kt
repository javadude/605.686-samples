package com.javadude.databinding2.example08

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class StateSpinner @JvmOverloads constructor(
                context: Context, attrs: AttributeSet? = null
) : SelectionSpinner(context, attrs) {
    init {
        adapter = StatesAdapter()
    }

    class StatesAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val text : TextView
            if (view == null) {
                view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
                text = view.findViewById(android.R.id.text1)
                view.tag = text
            } else {
                text = view.tag as TextView
            }
            text.text = State.values()[position].displayName
            return view!!
        }

        override fun getItem(position: Int) = State.values()[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = State.values().size

    }
}