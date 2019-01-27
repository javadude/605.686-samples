package com.javadude.livedatav2

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.switchMap
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import java.util.*

class TodoViewModel : ViewModel() {
    private val repository = InMemoryRepository()
    val selectedProject = MutableLiveData<Project?>()
    val selectedItem = MutableLiveData<TodoItem>()

    val todoItems = switchMap(selectedProject, Collections.emptyList<TodoItem>(), {
        if (it !== null)
            repository.getTodoItems(it)
        else
            null
    })

    @WorkerThread
    fun save(project: Project, todoItem: TodoItem) {
        repository.save(project, todoItem)
    }
    @WorkerThread
    fun save(project: Project) {
        repository.save(project)
    }

    @MainThread
    fun <X, Y> switchMap(trigger: LiveData<X>,
                         defaultValue : Y,
                         func: (X?) -> LiveData<Y>?): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(trigger, object : Observer<X> {
            internal var mSource: LiveData<Y>? = null

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
}