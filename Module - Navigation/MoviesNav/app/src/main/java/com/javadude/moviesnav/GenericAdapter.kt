package com.javadude.moviesnav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * A Generic adapter for a RecyclerView. Makes the following assumptions:
 *   - All items implement HasId, which just gives them an "id:String" property
 *     This is used in the diffutil to determine whether two objects represent the same item
 *   - The row layout XML used to display has the following optional fields
 *      + text1 - the primary text for the item (eg: movie title or role name)
 *      + text2 - the secondary text for the item (eg: actor name)
 *      + icon - an optional icon for the item - will be clickable if present, and treated as a long click
 */
class GenericAdapter<T:HasId>(
    @LayoutRes val rowLayoutRes: Int,
    val selectionManager: SelectionManager<T>,
    val singleSelectAction : () -> Unit,
    val getText1 : (T) -> String,
    val getText2 : (T) -> String
) : RecyclerView.Adapter<GenericAdapter<T>.GenericViewHolder>() {
    var selections : Set<T> = emptySet()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(GenericDiffUtilCallback(items, items, value, field))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    var items : List<T> = emptyList()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(GenericDiffUtilCallback(value, field, selections, selections))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenericViewHolder(LayoutInflater.from(parent.context).inflate(rowLayoutRes, parent, false))

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) =
        holder.bind(items[position])

    inner class GenericViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1 = itemView.findViewById<TextView>(R.id.text1)
        private val text2 = itemView.findViewById<TextView>(R.id.text2)
        private val icon = itemView.findViewById<ImageView>(R.id.icon)
        var itemId : String? = null
        init {
            itemView.setOnClickListener {
                selectionManager.onClicked(items[adapterPosition], singleSelectAction)
            }
            icon?.setOnClickListener {
                selectionManager.onLongClicked(items[adapterPosition])
            }
            itemView.setOnLongClickListener {
                selectionManager.onLongClicked(items[adapterPosition])
                true
            }
        }

        fun bind(item : T) {
            this.itemId = item.id
            text1?.text = getText1(item)
            text2?.text = getText2(item)
            // selection state is inherited by child views! nifty!
            itemView.isSelected = item in selections
        }
    }

    inner class GenericDiffUtilCallback(
            private val newItems : List<T>,
            private val oldItems: List<T>,
            private val newSelections : Set<T>,
            private val oldSelections : Set<T>)
        : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition].id == newItems[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // we only care about the title and selection status for display purposes
            val old = oldItems[oldItemPosition]
            val new = newItems[newItemPosition]
            val oldSelected = old in oldSelections
            val newSelected = new in newSelections
            return (oldSelected == newSelected && old.text1 == new.text1 && old.text2 == new.text2)
        }
        private val T.text1 : String
            get() = getText1(this)
        private val T.text2 : String
            get() = getText2(this)
    }
}
