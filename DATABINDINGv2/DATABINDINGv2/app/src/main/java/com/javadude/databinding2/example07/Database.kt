package com.javadude.databinding2.example07

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(version = 1, entities = [Person::class, Address::class])
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract val personDao : PersonDao
    abstract val addressDao : AddressDao
}