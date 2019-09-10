package com.javadude.moviesnav

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Factored out the common selection management for the recycler view
 */
class SelectionManager<T:HasId> {
    fun selectWhenReady(source: LiveData<T>) {
        selections.addSource(source) {
            selections.value = it?.let {
                setOf(it)
            } ?: emptySet()
            selections.removeSource(source)
        }
    }
    val selections = MediatorLiveData<Set<T>>()
    val multiSelectMode = MediatorLiveData<Boolean>().apply {
        value = false
        addSource(selections) {
            value = (it?.size ?: 0) > 1
        }
    }

    fun clearSelections() = selections.postValue(emptySet())

    private val selectionSet
        get() = selections.value ?: emptySet()

    fun onClicked(multiSelectAllowed : Boolean, item: T, singleSelectAction : (T) -> Unit) {
        if (multiSelectAllowed && multiSelectMode.value == true) { // handles "null" possibility
            onLongClicked(multiSelectAllowed, item, singleSelectAction) // treat like tapping an icon
        } else {
            // if in single-select mode, always replace the entire selection
            selections.value = setOf(item)
            singleSelectAction(item)
        }
    }

    fun onLongClicked(multiSelectAllowed : Boolean, item : T, singleSelectAction : (T) -> Unit) {
        if (multiSelectAllowed) {
            // always add/remove selected item from selection
            var current = selectionSet
            current = if (item in current) {
                current - item
            } else {
                current + item
            }
            multiSelectMode.value = current.isNotEmpty()
            selections.value = current
        } else {
            // if in single-select mode, always replace the entire selection
            selections.value = setOf(item)
            singleSelectAction(item)
        }
    }

}