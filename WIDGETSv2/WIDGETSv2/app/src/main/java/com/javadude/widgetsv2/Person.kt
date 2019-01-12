package com.javadude.widgetsv2

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import java.util.*

@Entity
class Person {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var name : String = ""
}

@Dao
interface PersonDao {
    @Query("SELECT * FROM Person ORDER BY name")
    fun getAllLD() : LiveData<List<Person>>

    @Query("SELECT * FROM Person ORDER BY name")
    fun getAll() : List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg people : Person)
}

@Database(version = 1, entities = [Person::class])
abstract class PersonDatabase : RoomDatabase() {
    abstract val personDao : PersonDao
}
