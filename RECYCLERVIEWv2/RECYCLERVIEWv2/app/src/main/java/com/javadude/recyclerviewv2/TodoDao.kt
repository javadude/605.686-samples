package com.javadude.recyclerviewv2

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM TodoItemEntity ORDER BY name")
    fun getAll() : LiveData<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE id = :arg0")
    fun getById(id : String) : LiveData<TodoItemEntity>

    @Query("SELECT * FROM TodoItemEntity WHERE projectId = :arg0 ORDER BY name")
    fun getByProjectId(projectId : String) : LiveData<List<TodoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg todoItemEntities: TodoItemEntity)
    @Update
    fun update(vararg todoItemEntities: TodoItemEntity)
    @Delete
    fun delete(vararg todoItemEntities: TodoItemEntity)
}