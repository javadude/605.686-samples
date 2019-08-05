
package com.javadude.fragv2

import android.app.Application
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.concurrent.Executors

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    enum class State {
        List, Edit, Exit
    }
    enum class Event {
        CreateNewTodoItem,
        SelectTodoItem,
        BackSinglePane,
        BackDualPane
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val repository = RoomRepository(application)
    val selectedProject = MutableLiveData<ProjectEntity?>()
    val multiSelects = MutableLiveData<Set<TodoItemEntity>>()
    val selectedItem = MediatorLiveData<TodoItemEntity>().apply {
        // if there are any multi-selects, clear the "single selection"
        addSource(multiSelects) {
            if (it?.isNotEmpty() == true) {
                value = null
            }
        }
    }
    private val currentState0 = MutableLiveData<State>().apply { value = State.List }
    val currentState : LiveData<State>
        get() = currentState0

    val projects : LiveData<List<ProjectEntity>> by lazy {
        repository.getProjects()
    }

    private val emptyItemListLiveData = MutableLiveData<List<TodoItemEntity>>().apply { value = emptyList() }
    val todoItems: LiveData<List<TodoItemEntity>> =
            Transformations.switchMap(selectedProject) {
                if (it !== null)
                    repository.getTodoItems(it)
                else
                    emptyItemListLiveData
            }

    @UiThread
    fun deleteMulti() {
        multiSelects.value?.let {items ->
            executor.execute {
                items.forEach { item ->
                    delete(item)
                }
            }
        }
    }
    @WorkerThread
    private fun save(project: ProjectEntity, todoItem: TodoItemEntity) =
        repository.save(project, todoItem)

    @WorkerThread
    fun save(project: ProjectEntity) =
        repository.save(project)

    @WorkerThread
    @Suppress("unused")
    fun delete(project: ProjectEntity) =
        repository.delete(project)

    @WorkerThread
    fun delete(todoItem: TodoItemEntity) =
        repository.delete(todoItem)

    @UiThread
    fun updateSelectedItem(name : String,
                           description : String,
                           priority : String) {
        executor.execute {
            selectedItem.value?.let {
                it.name = name
                it.description = description
                it.priority = priority.toIntOrNull() ?: 0
                save(selectedProject.value!!, it)
            }
        }
    }

    fun handleEvent(event: Event) {
        currentState0.value =
            when (currentState0.value) {
                State.List -> when (event) {
                    Event.CreateNewTodoItem -> State.Edit
                    Event.SelectTodoItem -> State.Edit
                    Event.BackSinglePane -> State.Exit
                    Event.BackDualPane -> State.Exit
                }
                State.Edit -> when (event) {
                    Event.CreateNewTodoItem -> State.Edit
                    Event.SelectTodoItem -> State.Edit
                    Event.BackSinglePane -> State.List
                    Event.BackDualPane -> State.Exit
                }
                State.Exit -> State.Exit
                else -> throw IllegalStateException()
            }
    }
}