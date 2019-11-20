package com.javadude.moviesnav

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.javadude.moviesnav.db.Database

abstract class MovieApplicationBase : Application() {
    private val callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m1', 'The Transporter', 'Jason Statham kicks a guy in the face')")
            db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m2', 'Transporter 2', 'Jason Statham kicks a bunch of guys in the face')")
            db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m3', 'Hobbs and Shaw', 'Cars, Explosions and Stuff')")
            db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m4', 'Jumanji', 'The Rock smolders')")


            db.execSQL("INSERT INTO Actor (id, name) VALUES('a1', 'Jason Statham')")
            db.execSQL("INSERT INTO Actor (id, name) VALUES('a2', 'The Rock')")
            db.execSQL("INSERT INTO Actor (id, name) VALUES('a3', 'Shu Qi')")
            db.execSQL("INSERT INTO Actor (id, name) VALUES('a4', 'Amber Valletta')")
            db.execSQL("INSERT INTO Actor (id, name) VALUES('a5', 'Kevin Hart')")


            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r1', 'm1', 'a1', 'Frank Martin', 1)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r2', 'm1', 'a3', 'Lai', 2)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r3', 'm2', 'a1', 'Frank Martin', 1)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r4', 'm2', 'a4', 'Audrey Billings', 2)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r5', 'm3', 'a2', 'Hobbs', 1)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r6', 'm3', 'a1', 'Shaw', 2)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r7', 'm4', 'a2', 'Spencer', 1)")
            db.execSQL("INSERT INTO Role (id, movieId, actorId, roleName, `order`) VALUES('r8', 'm4', 'a5', 'Fridge', 2)")
        }
    }

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.db = Room.databaseBuilder(this, Database::class.java, "MOVIES")
            .addCallback(callback)
            .build()
    }
}