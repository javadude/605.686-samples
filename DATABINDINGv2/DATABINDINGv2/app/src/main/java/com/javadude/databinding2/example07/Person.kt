package com.javadude.databinding2.example07

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Person(
        @PrimaryKey var id : String,
        var name: String,
        var age: Int) {

    override fun toString(): String {
        return "Person(name='$name', age=$age)"
    }
}


