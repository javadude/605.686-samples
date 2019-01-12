package com.javadude.databinding2.example09

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Method

class GeneralBindingAdapter(private val rowLayout: Int)
        : RecyclerView.Adapter<GeneralBindingAdapter.BindingHolder>() {
    var items: List<*>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var model: Any? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return BindingHolder(view)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        holder.item = items!![position]
        holder.model = model
        holder.position = position
        holder.refresh()
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    class BindingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ViewDataBinding = DataBindingUtil.bind(itemView)!!
        private var itemSetter : Method? = null
        private var modelSetter : Method? = null
        private var positionSetter : Method? = null
        init {
            binding.javaClass.methods.forEach {
                when (it.name) {
                    "setItem" -> itemSetter = it
                    "setModel" -> modelSetter = it
                    "setPosition" -> positionSetter = it
                }
            }
            if (itemSetter == null) {
                throw IllegalArgumentException("Item layout must have an 'item' variable")
            }
            if (modelSetter == null) {
                throw IllegalArgumentException("Item layout must have a 'model' variable")
            }
            if (positionSetter == null) {
                throw IllegalArgumentException("Item layout must have a 'position' variable")
            }
        }
        var item : Any? = null
            set(value) {
                field = value
                itemSetter!!.invoke(binding, value)
            }
        var model : Any? = null
            set(value) {
                field = value
                modelSetter!!.invoke(binding, value)
            }
        var position : Any? = null
            set(value) {
                field = value
                positionSetter!!.invoke(binding, value)
            }
        fun refresh() {
            binding.executePendingBindings()
        }
    }
}
