package com.javadude.fragv2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoItemEntity::class, ProjectEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getTodoDao() : TodoDao
    abstract fun getProjectDao() : ProjectDao
}