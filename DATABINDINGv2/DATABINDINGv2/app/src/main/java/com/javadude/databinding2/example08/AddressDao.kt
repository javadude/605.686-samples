package com.javadude.databinding2.example08

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface AddressDao : IDao<Address> {
    @Query("SELECT * FROM Address WHERE id = :id")
    fun getById(id : String) : LiveData<Address>

    @Query("SELECT * FROM Address WHERE ownerId = :ownerId")
    fun getForOwnerId(ownerId : String) : LiveData<Address>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(item : Address)
}