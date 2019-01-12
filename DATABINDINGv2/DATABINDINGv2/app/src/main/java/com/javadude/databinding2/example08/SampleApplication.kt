package com.javadude.databinding2.example08

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase

class SampleApplication : Application() {
    val db = Room.databaseBuilder(this, Database::class.java, "PEOPLE")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("INSERT INTO Address (id, ownerId, street, city, state, zip) VALUES('a1', 'p1', '123 Sesame Street', 'Laurel', 'MD', '20823')")
                    db.execSQL("INSERT INTO Address (id, ownerId, street, city, state, zip) VALUES('a2', 'p2', '11 Animated Lane', 'New York', 'NY', '10011')")
                    db.execSQL("INSERT INTO Person (id, name, age) VALUES('p1', 'Scott', 51)")
                    db.execSQL("INSERT INTO Person (id, name, age) VALUES('p2', 'Alex', 24)")
                }
            })
            .build()
}