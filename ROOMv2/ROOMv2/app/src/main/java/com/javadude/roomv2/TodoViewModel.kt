package com.javadude.roomv2

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import java.util.*

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoomRepository(application)
    val selectedProject = MutableLiveData<ProjectEntity?>()
    val selectedItem = MutableLiveData<TodoItemEntity>()

    val projects : LiveData<List<ProjectEntity>> by lazy {
        repository.getProjects()
    }

    val todoItems = switchMap(selectedProject, Collections.emptyList<TodoItemEntity>(), {
        if (it !== null)
            repository.getTodoItems(it)
        else
            null
    })

    @WorkerThread
    fun save(project: ProjectEntity, todoItem: TodoItemEntity) {
        repository.save(project, todoItem)
    }
    @WorkerThread
    fun save(project: ProjectEntity) {
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