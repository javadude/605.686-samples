package com.javadude.recyclerviewv2

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProjectDao {
    @Query("SELECT * FROM ProjectEntity ORDER BY name")
    fun getAll() : LiveData<List<ProjectEntity>>

    @Query("SELECT * FROM ProjectEntity WHERE id = :arg0")
    fun getById(id : String) : LiveData<ProjectEntity>

    @Insert
    fun insert(vararg projectEntities: ProjectEntity)
    @Update
    fun update(vararg projectEntities: ProjectEntity)
    @Delete
    fun delete(vararg projectEntities: ProjectEntity)
}