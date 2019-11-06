package com.javadude.databinding3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Address(
    @PrimaryKey var id : String,
    var ownerId : String,
    var type : String,
    var street : String,
    var city : String,
    var state : State,
    var zip : String
)