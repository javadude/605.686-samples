package com.javadude.databinding3

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.javadude.databinding3.StateSpinner.StateAdapter

/**
 * A custom subclass of [SelectionSpinner] that creates an explicit adapter to display state names
 * from the State enumeration.
 *
 * This type of subclass can be used directly in the XML by only using the "selection" attribute
 * as a Data Binding expression. The list of states isn't needed to be passed in because we're
 * hard-wiring it inside the nested [StateAdapter].
 *
 * If an application has fixed sets of data (usually defined as enums), this is a simple approach
 * to defining a spinner for those data
 */
class StateSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
        // NOTE - DO NOT pass in default style! That should only be passed when you're defining
        //        a new view that you will be taking style info from. Because we're extending view,
        //        and not defining a special style, we don't want to pass in a custom spinner style
        : SelectionSpinner(context, attrs) {

    // Set up the adapter
    init {
        adapter = StateAdapter()
    }

    /**
     * An adapter for the StateSpinner that explicitly wires in state names from the State
     * enumeration.
     *
     * Note that adapters for Spinners look a little different than those for
     * [androidx.recyclerview.widget.RecyclerView]. [android.widget.Spinner] and
     * [android.widget.ListView] were the original ways of displaying lists of data in Android
     * and [androidx.recyclerview.widget.RecyclerView] was created to more efficiently handle
     * displaying data. Not sure why they didn't redo Spinner as well...
     *
     * The biggest difference is that [androidx.recyclerview.widget.RecyclerView.Adapter] separates
     * out the creation of the view instance from the binding of data to a view instance. In the
     * original adapters, the [android.widget.Spinner]/[android.widget.ListView] would pass in
     * a "convert view" parameter if they had a view they wanted to recycle. If [getView] were
     * passed a "convert view", it should reuse it; if not not, it should create it.
     *
     * A pattern developed early on where, when a view was created, the child views that were
     * to be used for displaying data would be looked up (via [findViewById]) and then held onto
     * in the "tag" property of the overall view representing the row. ("tag" is a property that
     * can hold any object) [findViewById] is rather expensive, so calling it every time we would
     * bind data to reused view was wasteful. If there were multiple child views, the programmer
     * would often create a "view holder" object to contain pointers to them and put an instance
     * of it in the "tag" property.
     *
     * That's where the concept of [androidx.recyclerview.widget.RecyclerView.ViewHolder] comes from...
     * The Android team thought the pattern was good enough to enforce it in the new way of
     * showing lists of items.
     */
    private class StateAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            val textView : TextView
            if (view == null) {
                view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
                textView = view.findViewById(android.R.id.text1)
                view.tag = textView
            } else {
                textView = view.tag as TextView
            }
            textView.text = State.values()[position].displayName
            return view!!
        }

        override fun getItem(postion: Int) = State.values()[postion]
        override fun getItemId(postion: Int) = postion.toLong()
        override fun getCount() = State.values().size
    }

    companion object {
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
    }
}