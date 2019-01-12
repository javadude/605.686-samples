package com.javadude.databinding2.example09

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.InverseBindingListener
import android.databinding.InverseBindingMethod
import android.databinding.InverseBindingMethods
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

open class SelectionSpinner @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : Spinner(context, attrs) {

    fun getSelection() = selectedItemPosition

    companion object {
        @JvmStatic
        @BindingAdapter("selectionAttrChanged")
        fun setSelectionListener(spinner : SelectionSpinner, listener : InverseBindingListener?) {
            if (listener == null) {
                spinner.onItemSelectedListener = null
            } else {
                spinner.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        listener.onChange()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        listener.onChange()
                    }
                }
            }
        }
    }
}