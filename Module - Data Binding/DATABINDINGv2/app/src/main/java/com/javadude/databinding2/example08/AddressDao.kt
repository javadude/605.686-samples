package com.javadude.databinding2.example08

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddressDao : IDao<Address> {
    @Query("SELECT * FROM Address WHERE id = :id")
    fun getById(id : String) : LiveData<Address>

    @Query("SELECT * FROM Address WHERE ownerId = :ownerId")
    fun getForOwnerId(ownerId : String) : LiveData<Address>
    @Query("SELECT * FROM Address WHERE ownerId = :ownerId")
    fun getForOwnerIdSync(ownerId : String) : Address

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(item : Address)
}