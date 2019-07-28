package com.javadude.fragv2

import android.app.Application
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.Collections
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
    val selectedItem = MutableLiveData<TodoItemEntity>()
    val multiSelects = MutableLiveData<Set<TodoItemEntity>>()

    private val currentState0 = MutableLiveData<State>().apply { value = State.List }
    val currentState : LiveData<State>
        get() = currentState0


    val projects : LiveData<List<ProjectEntity>> by lazy {
        repository.getProjects()
    }

    val todoItems = switchMap(selectedProject, Collections.emptyList<TodoItemEntity>()) {
        if (it !== null)
            repository.getTodoItems(it)
        else
            null
    }

    @UiThread
    fun deleteMulti() {
        multiSelects.value?.let {
            executor.execute {
                it.forEach {
                    delete(it)
                }
            }
        }
    }
    @WorkerThread
    fun save(project: ProjectEntity, todoItem: TodoItemEntity) {
        repository.save(project, todoItem)
    }
    @WorkerThread
    fun save(project: ProjectEntity) {
        repository.save(project)
    }

    @Suppress("unused")
    @WorkerThread
    fun delete(project: ProjectEntity) {
        repository.delete(project)
    }

    @WorkerThread
    fun delete(todoItem: TodoItemEntity) {
        repository.delete(todoItem)
    }

    @MainThread
    fun <X, Y> switchMap(trigger: LiveData<X>,
                         defaultValue : Y,
                         func: (X?) -> LiveData<Y>?): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(trigger, object : Observer<X> {
            var mSource: LiveData<Y>? = null

            override fun onChanged(x: X?) {
                val newLiveData = func(x)
                if (mSource === newLiveData) {
                    return
                }
                if (mSource != null) {
                    result.removeSource(mSource!!)
                }
                mSource = newLiveData
                if (mSource != null) {
                    result.addSource(mSource!!) { y -> result.value = y }
                } else {
                    result.value = defaultValue
                }
            }
        })
        return result
    }


    @UiThread
    fun updateSelectedItem(name : String,
                           description : String,
                           priority : String) {
        executor.execute {
            selectedItem.value?.let {
                it.name = name
                it.description = description
                try {
                    it.priority = priority.toInt()
                } catch (e : NumberFormatException) {
                    it.priority = 0
                }
                save(selectedProject.value!!, it)
            }
        }

    }

    fun handleEvent(event: Event) {
        currentState0.value =
            when (currentState0.value) {
                TodoViewModel.State.List -> when (event) {
                    TodoViewModel.Event.CreateNewTodoItem -> State.Edit
                    TodoViewModel.Event.SelectTodoItem -> State.Edit
                    TodoViewModel.Event.BackSinglePane -> State.Exit
                    TodoViewModel.Event.BackDualPane -> State.Exit
                }
                TodoViewModel.State.Edit -> when (event) {
                    TodoViewModel.Event.CreateNewTodoItem -> State.Edit
                    TodoViewModel.Event.SelectTodoItem -> State.Edit
                    TodoViewModel.Event.BackSinglePane -> State.List
                    TodoViewModel.Event.BackDualPane -> State.Exit
                }
                TodoViewModel.State.Exit -> State.Exit
                else -> throw IllegalStateException()
            }
    }
}