package com.javadude.fragv2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Suppress("unused")
@Dao
interface ProjectDao {
    @Query("SELECT * FROM ProjectEntity ORDER BY name")
    fun getAll() : LiveData<List<ProjectEntity>>

    @Query("SELECT * FROM ProjectEntity WHERE id = :id")
    fun getById(id : String) : LiveData<ProjectEntity>

    @Insert
    fun insert(vararg projectEntities: ProjectEntity)
    @Update
    fun update(vararg projectEntities: ProjectEntity)
    @Delete
    fun delete(vararg projectEntities: ProjectEntity)
}