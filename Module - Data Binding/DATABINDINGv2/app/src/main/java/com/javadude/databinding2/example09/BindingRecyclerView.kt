package com.javadude.databinding2.example09

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import com.javadude.databinding2.R

class BindingRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr) {
    private val rowLayout: Int
    private val adapter: GeneralBindingAdapter

    var items : List<*>? = null
        set(value) {
            field = value
            adapter.items = value
        }
    var model : SampleViewModel? = null
        set(value) {
            field = value
            adapter.model = value
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BindingRecyclerView, 0, 0)
        try {
            rowLayout = a.getResourceId(R.styleable.BindingRecyclerView_row_layout, -1)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = GeneralBindingAdapter(rowLayout)
            setAdapter(adapter)
        } finally {
            a.recycle()
        }
    }
}