package com.javadude.roomv2

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM TodoItemEntity")
    fun getAll() : LiveData<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE id = :arg0")
    fun getById(id : String) : LiveData<TodoItemEntity>

    @Query("SELECT * FROM TodoItemEntity WHERE projectId = :arg0")
    fun getByProjectId(projectId : String) : LiveData<List<TodoItemEntity>>

    @Insert
    fun insert(vararg todoItemEntities: TodoItemEntity)
    @Update
    fun update(vararg todoItemEntities: TodoItemEntity)
    @Delete
    fun delete(vararg todoItemEntities: TodoItemEntity)
}