package com.javadude.recyclerviewv2

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [TodoItemEntity::class, ProjectEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getTodoDao() : TodoDao
    abstract fun getProjectDao() : ProjectDao
}