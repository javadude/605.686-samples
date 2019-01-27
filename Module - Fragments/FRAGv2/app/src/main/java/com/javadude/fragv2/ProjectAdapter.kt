package com.javadude.fragv2

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ProjectAdapter(
        val lifecycleOwner: LifecycleOwner,
        val items : LiveData<List<ProjectEntity>>

) : BaseAdapter() {

    init {
        items.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })
    }
    fun createView(layoutId : Int, position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val textView : TextView
        if (view === null) {
            view = LayoutInflater.from(parent!!.context).inflate(layoutId, parent, false)
            textView = view.findViewById(android.R.id.text1)
            view.tag = textView
        } else {
            textView = view.tag as TextView
        }
        textView.text = items.value!![position].name
        return view!!
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
            createView(android.R.layout.simple_list_item_1, position, convertView, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View =
            createView(android.R.layout.simple_dropdown_item_1line, position, convertView, parent)

    override fun getItem(position: Int): Any = items.value!![position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getCount(): Int = items.value?.size ?: 0
}