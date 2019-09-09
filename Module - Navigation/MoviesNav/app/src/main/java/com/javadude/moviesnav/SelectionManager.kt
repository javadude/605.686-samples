package com.javadude.moviesnav

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

/**
 * Factored out the common selection management for the recycler view
 */
class SelectionManager<T> {
    val selections = MutableLiveData<Set<T>>()
    val multiSelectMode = MediatorLiveData<Boolean>().apply {
        value = false
        addSource(selections) {
            value = (it?.size ?: 0) > 1
        }
    }

    fun clearSelections() = selections.postValue(emptySet())

    private val selectionSet
        get() = selections.value ?: emptySet()

    fun onClicked(item: T, singleSelectAction : () -> Unit) {
        if (multiSelectMode.value == true) { // handles "null" possibility
            onLongClicked(item) // treat like tapping an icon
        } else {
            // if in single-select mode, always replace the entire selection
            selections.value = setOf(item)
            singleSelectAction()
        }
    }

    fun onLongClicked(item : T) {
        // always add/remove selected item from selection
        var current = selectionSet
        current = if (item in current) {
            current - item
        } else {
            current + item
        }
        multiSelectMode.value = current.isNotEmpty()
        selections.value = current
    }

}