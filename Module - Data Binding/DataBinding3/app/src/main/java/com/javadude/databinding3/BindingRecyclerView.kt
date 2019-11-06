package com.javadude.databinding3

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * A custom subclass of [RecyclerView] that creates an explicit adapter to display _any_ data
 * passed in. This class would be used in a Data Binding XML layout file:
 *
 *     <com.javadude.databinding3.BindingRecyclerView
 *         app:items="@{model.items}"
 *         app:row_layout="R.layout.my_item_layout"
 *         ... />
 *
 * Note that this version of BindingRecyclerView doesn't handle selection. A better version of this
 * class would manage selection...
 *
 * The layout passed in for "row_layout" must look similar to
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
class BindingRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), BindableItemsView by BindingHelper() {

    init {
        adapter = BindingRecyclerViewAdapter().apply {
            setup(context, attrs, R.styleable.BindingRecyclerView, R.styleable.BindingRecyclerView_row_layout) {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * A view holder that can hold _any_ layout and allow binding to it. The [bind] function of this
     * class delegates to [BindingHelper.bind] to actually perform the data binding
     */
    inner class BindingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = DataBindingUtil.bind<ViewDataBinding>(itemView)!!
        fun bind(position: Int) = bind(binding, position)
    }

    /**
     * An adapter that can hold a list of _any_ data to bind to _any_ layout.
     */
    inner class BindingRecyclerViewAdapter : RecyclerView.Adapter<BindingViewHolder>() {
        override fun getItemCount() = items?.size ?: 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingViewHolder(inflate(parent))
        override fun onBindViewHolder(holder: BindingViewHolder, position: Int) = holder.bind(position)
    }
}