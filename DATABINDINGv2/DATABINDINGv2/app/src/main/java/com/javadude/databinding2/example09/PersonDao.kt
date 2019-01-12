package com.javadude.databinding2.example09

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface PersonDao : IDao<Person> {
    @Query("SELECT * FROM Person")
    fun getAll() : LiveData<List<Person>>

    @Query("SELECT * FROM Person WHERE id = :id")
    fun getById(id : String) : LiveData<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(item : Person)
}