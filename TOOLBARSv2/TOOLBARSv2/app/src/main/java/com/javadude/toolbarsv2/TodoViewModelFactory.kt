package com.javadude.toolbarsv2

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TodoViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    companion object {
        var todoViewModel : TodoViewModel? = null
    }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (todoViewModel === null) {
            todoViewModel = TodoViewModel(application)
        }
        @Suppress("UNCHECKED_CAST")
        return todoViewModel as T
    }

}