package com.javadude.databinding2.example05

import android.content.Context
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
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