package com.javadude.databinding2.example09

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase

class SampleApplication : Application() {
    lateinit var db : Database

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, Database::class.java, "PEOPLE")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("INSERT INTO Address (id, ownerId, street, city, state, zip) VALUES('a1', 'p1', '123 Sesame Street', 'Laurel', 'MD', '20923')")
                    db.execSQL("INSERT INTO Address (id, ownerId, street, city, state, zip) VALUES('a2', 'p2', '11 Animated Lane', 'New York', 'NY', '10011')")
                    db.execSQL("INSERT INTO Person (id, name, age) VALUES('p1', 'Scott', 51)")
                    db.execSQL("INSERT INTO Person (id, name, age) VALUES('p2', 'Alex', 24)")
                }
            })
            .build()
    }
}