package com.javadude.databinding2.example08

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao : IDao<Person> {
    @Query("SELECT * FROM Person ORDER BY name")
    fun getAll() : LiveData<List<Person>>

    @Query("SELECT * FROM Person WHERE id = :id")
    fun getById(id : String) : LiveData<Person>

    @Query("SELECT * FROM Person WHERE id = :id")
    fun getByIdSync(id : String) : Person

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(item : Person)
}