package com.javadude.databinding3

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil

/**
 * A custom subclass of [SelectionSpinner] that creates an explicit adapter to display _any_ data
 * passed in. This class would be used in a Data Binding XML layout file:
 *
 *     <com.javadude.databinding3.BindingSpinner
 *         app:model="@{model}"
 *         app:items="@{model.items}"
 *         app:selection="@{model.selection}"
 *         app:row_layout="R.layout.my_item_layout"
 *         ... />
 *
 * The layout passed in for "itemLayout" must look similar to
 *
 *     <layout ...>
 *         <data>
 *             <!-- exactly the following four variables. The type of model and item can be anything -->
 *             <!-- you may add imports as needed -->
 *             <variable name="model" type="**any type**" />
 *             <variable name="position" type="int" />
 *             <variable name="item" type="**any type**" />
 *         </data>
 *
 *         <!-- views using Data Binding expressions that contain model, position, item -->
 *     </layout>
 *
 * Note that this class delegates much of its functionality to [BindingHelper], setting it up
 * in the init block
 */
class BindingSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    // NOTE - DO NOT pass in default style! That should only be passed when you're defining
    //        a new view that you will be taking style info from. Because we're extending view,
    //        and not defining a special style, we don't want to pass in a custom spinner style
        : SelectionSpinner(context, attrs), BindableItemsView by BindingHelper() {

    init {
        adapter = BindingSpinnerAdapter().apply {
            setup(context, attrs, R.styleable.BindingSpinner, R.styleable.BindingSpinner_row_layout) {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * An adapter for the StateSpinner that allows the use of **any* data to be passed in
     * as long as the view to display the items follows a few conventions.
     */
    inner class BindingSpinnerAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: inflate(parent).apply {
                tag = DataBindingUtil.bind(this)!!
            }

            bind(view.tag, position)
            return view
        }

        override fun getItem(postion: Int) = items?.getOrNull(postion)
        override fun getItemId(postion: Int) = postion.toLong()
        override fun getCount() = items?.size ?: 0
    }
}