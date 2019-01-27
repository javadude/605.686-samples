package com.javadude.databinding2.example09

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 1, entities = [Person::class, Address::class])
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract val personDao : PersonDao
    abstract val addressDao : AddressDao
}