package com.javadude.databinding3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Person(
    @PrimaryKey var id : String,
    var name : String,
    var age : Int
)