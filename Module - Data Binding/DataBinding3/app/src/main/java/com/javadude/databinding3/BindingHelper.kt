package com.javadude.databinding3

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.reflect.Method

/**
 * An interface describing the features that our helper delegate will provide for
 * [BindingRecyclerView] and [BindingSpinner].
 *
 * Our [BindingHelper] implements this interface, and the views will delegate these features to
 * an instance of it, making it feel like these functions are part of the views.
 */
interface BindableItemsView {
    var items: List<*>?
    var model: Any?
    fun setup(
        context: Context,
        attrs: AttributeSet?,
        styleable: IntArray,
        rowLayoutAttr: Int,
        updater: () -> Unit
    )
    fun bind(binding: Any, position: Int)
    fun inflate(parent: ViewGroup) : View
}

/**
 * The common guts for the [BindingRecyclerView] and [BindingSpinner].
 *
 * This class manages the row_layout attribute and looks up the methods that we'll
 * use to set the item, position and model variables in the binding.
 *
 * We make this class implement [BindableItemsView] so we can use Kotlin interface delegation
 * to blend this class in with the actual View. Kotlin still only allows a single superclass
 * (which will either be the RecyclerView or Spinner for the view classes) but can automatically
 * create function implementations to delegate the functions of an interface to an object such as
 * this, as a mix-in
 */
class BindingHelper : BindableItemsView {
    // properties that are set when the setup function is run
    @LayoutRes private var rowLayout: Int = 0
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var modelSetter: Method
    private lateinit var itemSetter: Method
    private lateinit var positionSetter: Method
    private lateinit var updater: () -> Unit

    // properties that are set directly from attributes in the XML
    override var items : List<*>? = null
        set(value) {
            field = value
            updater()
        }

    override var model : Any? = null
        set(value) {
            field = value
            updater()
        }

    /**
     * A helper function that passes in the XML attributes so we can load them
     */
    override fun setup(context: Context,
                       attrs: AttributeSet?,
                       styleable: IntArray,
                       rowLayoutAttr: Int,
                       updater: () -> Unit
              ) {
        val a = context.theme.obtainStyledAttributes(attrs, styleable, 0, 0)
        try {
            this.updater = updater
            layoutInflater = LayoutInflater.from(context)
            rowLayout = a.getResourceId(rowLayoutAttr, -1)

            // inflate the view temporarily so we can get the "set" functions we need
            val dummyView = layoutInflater.inflate(rowLayout, null, false)
            var tempItemSetter : Method? = null
            var tempModelSetter : Method? = null
            var tempPositionSetter : Method? = null

            val dummyBinding = DataBindingUtil.bind<ViewDataBinding>(dummyView)

            dummyBinding?.javaClass?.methods?.forEach {
                when (it.name) {
                    "setItem" -> tempItemSetter = it
                    "setModel" -> tempModelSetter = it
                    "setPosition" -> tempPositionSetter = it
                }
            }

            fun requireVariable(name: String, method: Method?) =
                requireNotNull(method) { "layout ${context.resources.getResourceName(rowLayout)} must have $name property to be used as row_layout"}

            itemSetter = requireVariable("an 'item'", tempItemSetter)
            modelSetter = requireVariable("a 'model'", tempModelSetter)
            positionSetter = requireVariable("a 'position'", tempPositionSetter)

        } finally {
            a.recycle()
        }
    }

    /**
     * Used from the views to bind the item at the specified position in the passed-in binding
     */
    override fun bind(binding: Any, position: Int) {
        modelSetter.invoke(binding, model)
        itemSetter.invoke(binding, items?.getOrNull(position))
        positionSetter.invoke(binding, position)
    }

    /**
     * Used to inflate the layout we're binding. We do this here to ensure the layout passed to
     * [bind] is actually of the right type, as we had to pass it as an [Any]
     */
    override fun inflate(parent: ViewGroup) : View = layoutInflater.inflate(rowLayout, parent, false)
}