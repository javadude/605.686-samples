package com.javadude.moviesnav.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Movie::class, Actor::class, Role::class], exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract val dao : MovieDao
}