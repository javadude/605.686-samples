package com.javadude.roomv2

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread

class RoomRepository(application: Application) {
    private val db = Room.databaseBuilder(application,
            TodoDatabase::class.java, "TODO").build()

    @WorkerThread
    fun save(projectEntity: ProjectEntity) =
        db.getProjectDao().insert(projectEntity)

//    fun save(project: Project) {
//        val projectEntity = ProjectEntity()
//        projectEntity.id = project.id
//        projectEntity.name = project.name
//        db.getProjectDao().insert(projectEntity)
//    }

    @WorkerThread
    fun save(projectEntity: ProjectEntity, todoItemEntity: TodoItemEntity) {
        todoItemEntity.projectId = projectEntity.id
        db.getTodoDao().insert(todoItemEntity)
    }

    @UiThread
    fun getTodoItems(projectEntity: ProjectEntity) : LiveData<List<TodoItemEntity>> =
        db.getTodoDao().getByProjectId(projectEntity.id)

    @UiThread
    fun getProjects() : LiveData<List<ProjectEntity>> =
        db.getProjectDao().getAll()

}