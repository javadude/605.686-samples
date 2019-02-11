package com.javadude.toolbarsv2

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM TodoItemEntity ORDER BY name")
    fun getAll() : LiveData<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    fun getById(id : String) : LiveData<TodoItemEntity>

    @Query("SELECT * FROM TodoItemEntity WHERE projectId = :projectId ORDER BY name")
    fun getByProjectId(projectId : String) : LiveData<List<TodoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todoItemEntities: TodoItemEntity)
    @Update
    fun update(vararg todoItemEntities: TodoItemEntity)
    @Delete
    fun delete(vararg todoItemEntities: TodoItemEntity)
}