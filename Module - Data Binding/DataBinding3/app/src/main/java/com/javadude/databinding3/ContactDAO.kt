package com.javadude.databinding3

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDAO {
    @Query("SELECT * FROM Person ORDER BY name")
    fun getContacts() : LiveData<List<Person>>

    @Query("SELECT * FROM Address WHERE ownerId = :ownerId")
    fun getAddressesForOwner(ownerId : String) : LiveData<List<Address>>

    @Insert
    fun insert(item : Address)
    @Update
    fun update(item : Address)

    @Insert
    fun insert(item : Person)
    @Update
    fun update(item : Person)
}