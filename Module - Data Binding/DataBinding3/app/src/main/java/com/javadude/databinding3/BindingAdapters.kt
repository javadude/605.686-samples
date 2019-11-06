package com.javadude.databinding3

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

/**
 * Moved the adapters out to a separate file to get rid of the warnings about overriding
 */
object BindingAdapters {
    /**
     * Add support to notify the generated Data Binding class when the selection changes
     * so it can update the model (if the Data Binding expression looks like @={...}.
     *
     * We want this to work for the "selection" attribute/property of the Spinner. For example:
     *     <com.javadude.databinding3.SelectionSpinner
     *         app:selection="@={viewModel.selectedState}"
     *         ... />
     *
     * This requires a static function that's annotated with [BindingAdapter], named
     * "selectionAttrChanged" (the name must match the XML attribute name with "AttrChanged"
     * appended). This annotation tells the Data Binding compiler to add code to pass an
     * [InverseBindingListener] to this function along with the [SelectionSpinner]
     * that it's binding.
     *
     * It's then up to our code to notify that [InverseBindingListener] whenever the
     * selection property changes so Data Binding can update the model.
     */
    @JvmStatic
    @BindingAdapter("selectionAttrChanged")
    fun setSelectionListener(spinner : SelectionSpinner, listener : InverseBindingListener?) {
        // If Data Binding passes a "null" InverseBindingListener, we stop listening
        //   because it's no longer needed
        if (listener == null) {
            spinner.onItemSelectedListener = null

            // Otherwise, we set up an OnItemSelectedListener on the spinner to just notify
            //   the passed-in InverseBindingListener
        } else {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(spinner: AdapterView<*>) {
                    listener.onChange()
                }
                override fun onItemSelected(spinner: AdapterView<*>, itemView: View, position: Int, id: Long) {
                    listener.onChange()
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("selection")
    fun setStateInt(spinner : StateSpinner, value : State?) {
        val oldValue = spinner.getSelection()
        val newValue = value?.ordinal ?: -1
        if (oldValue != newValue) {
            spinner.setSelection(newValue)
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selection")
    fun getStateInt(spinner: StateSpinner) : State? {
        val selection = spinner.getSelection()
        return if (selection == -1) {
            null
        } else {
            State.values()[selection]
        }
    }

    // These feel crazy hacky to me. The problem is with unboxing of Integer values when we use
    //    a LiveData<Int> in kotlin and have a custom view that uses an Int property.
    //    The databinding compiler doesn't quite "get" Kotlin, so I needed to provide an
    //    explicit conversion between Integer (Java) and Int Kotlin. There may be a better
    //    way to do this, but it's not popping out at me right now.
    // Note that Kotlin is complaining about the use of Integer - normally you would not use
    //    java.lang.Integer in kotlin...
    @JvmStatic
    @BindingAdapter("selection")
    fun setIntegerInt(spinner : SelectionSpinner, value : Integer?) {
        val oldValue = spinner.getSelection()
        val newValue = value?.toInt() ?: -1
        if (oldValue != newValue) {
            spinner.setSelection(newValue)
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selection")
    fun getIntegerInt(spinner: SelectionSpinner) : Integer {
        return Integer(spinner.getSelection())
    }
}
