package com.javadude.rest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.javadude.data.TodoItem

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TodoRepository()

    val todoItems = repository.getAllTodoItems()
    val selectedTodoItem = MutableLiveData<TodoItem>()
    val actionResponse = MediatorLiveData<TodoResponse>()

    private fun doAction(responseLiveData: LiveData<TodoResponse>) {
        actionResponse.addSource(responseLiveData) {
            actionResponse.removeSource(responseLiveData) // only listen for single response
            actionResponse.value = it
        }
    }

    fun createTodoItem() = doAction(repository.createTodoItem())
    fun updateTodoItem(item: TodoItem) = doAction(repository.updateTodoItem(item))
    @Suppress("unused") // for later use
    fun deleteTodoItem(item: TodoItem) = doAction(repository.deleteTodoItem(item))
}