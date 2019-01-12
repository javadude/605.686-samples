package com.javadude.databinding2.example09

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("selection")
        fun setStateInt(spinner : StateSpinner, value : State?) {
            if (value == null) {
                spinner.setSelection(0)

            } else {
                val oldValue = spinner.getSelection()
                val newValue = value.ordinal
                if (oldValue != newValue) {
                    spinner.setSelection(newValue)
                }
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "selection")
        fun getStateInt(spinner: StateSpinner) = State.values()[spinner.getSelection()]
    }
}