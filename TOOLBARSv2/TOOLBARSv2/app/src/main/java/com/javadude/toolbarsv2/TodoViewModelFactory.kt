package com.javadude.toolbarsv2

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class TodoViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    companion object {
        var todoViewModel : TodoViewModel? = null
    }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (todoViewModel === null) {
            todoViewModel = TodoViewModel(application)
        }
        return todoViewModel as T
    }

}