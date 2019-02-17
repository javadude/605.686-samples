package com.javadude.toolbarsv2

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import java.util.*
import java.util.concurrent.Executors

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()
    private val repository = RoomRepository(application)
    val selectedProject = MutableLiveData<ProjectEntity?>()
    val selectedItem = MutableLiveData<TodoItemEntity>()
    val multiSelects = MutableLiveData<Set<TodoItemEntity>>()

    val projects : LiveData<List<ProjectEntity>> by lazy {
        repository.getProjects()
    }

    val todoItems = switchMap(selectedProject, Collections.emptyList<TodoItemEntity>(), {
        if (it !== null)
            repository.getTodoItems(it)
        else
            null
    })

    @UiThread
    fun deleteMulti() {
        executor.execute {
            multiSelects.value?.let {
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


    @UiThread
    fun updateSelectedItem(name : String,
                           description : String,
                           priority : String) {
        executor.execute {
            selectedItem.value?.let {
                it.name = name
                it.description = description
                it.priority = priority.toInt()
                save(selectedProject.value!!, it)
            }
        }

    }
}