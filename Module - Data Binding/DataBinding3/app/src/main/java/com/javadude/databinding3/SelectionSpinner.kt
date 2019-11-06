package com.javadude.databinding3

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

/**
 * A Custom subclass of Spinner to make it usable for Data Binding. To do this, we
 * need to have a read/write "selection" property. Android's Spinner class has a
 * rather inconsistent API - there's a [setSelection] that allows us to select the nth
 * item in the model, but to get that value we have to call [getSelectedItemPosition]
 * To fix this, we add [getSelection] that delegates to [getSelectedItemPosition].
 *
 * This class is abstract, and only handles the selection attribute/property.
 * We'll define how to deal with the adapter in two subclasses:
 *
 * StateSpinner - a subclass with a fixed adapter containing a list of state names
 *
 * BindingSpinner - a subclass that allows us to pass in a list of _anything_ via a Data Binding
 * expression and an XML layout resource id for how to display each item.
 */
abstract class SelectionSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
        // NOTE - DO NOT pass in default style! That should only be passed when you're defining
        //        a new view that you will be taking style info from. Because we're extending view,
        //        and not defining a special style, we don't want to pass in a custom spinner style
        : AppCompatSpinner(context, attrs) {

    // Add a "getSelection():Int" to match the "setSelection(Int)" so we have a consistent
    //   property that we can use for Data Binding

    // Note that if we're using LiveData for the values, we'll need to deal with converting to/from
    //   Integer - see the companion object below...
    fun getSelection() = selectedItemPosition

    companion object {
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
                spinner.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onNothingSelected(spinner: AdapterView<*>) {
                        listener.onChange()
                    }
                    override fun onItemSelected(spinner: AdapterView<*>, itemView: View, position: Int, id: Long) {
                        listener.onChange()
                    }
                }
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
}