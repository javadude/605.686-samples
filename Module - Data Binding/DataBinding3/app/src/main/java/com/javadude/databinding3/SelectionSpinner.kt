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

    // NOTE - moved the binding adapters to a separate file to get rid of re-definition warnings
}