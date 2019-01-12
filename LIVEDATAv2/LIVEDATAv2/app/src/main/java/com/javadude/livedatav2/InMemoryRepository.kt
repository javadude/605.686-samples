package com.javadude.livedatav2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread

class InMemoryRepository {
    private val projects = ArrayList<Project>()
    private val items = HashMap<Project, MutableList<TodoItem>>()
    private val liveDatas = HashMap<Project, MutableLiveData<List<TodoItem>>>()

    fun save(project: Project) {
        if (!projects.contains(project)) {
            projects += project
        }
    }

    @WorkerThread
    fun save(project: Project, todoItem: TodoItem) {
        synchronized(this) {
            var projectItems = items[project]
            if (projectItems === null) {
                projectItems = ArrayList()
                items[project] = projectItems
            }
            if (!projectItems.contains(todoItem)) {
                projectItems.add(todoItem)
            }
            liveDatas[project]?.postValue(projectItems)
        }
    }

    @UiThread
    fun getTodoItems(project: Project) : LiveData<List<TodoItem>> {
        synchronized(this) {
            var liveData = liveDatas[project]
            if (liveData === null) {
                liveData = MutableLiveData()
                liveDatas[project] = liveData
                var projectItems = items[project]
                if (projectItems === null) {
                    projectItems = ArrayList()
                    items[project] = projectItems
                }
                liveData.value = projectItems
            }
            return liveData
        }
    }
}