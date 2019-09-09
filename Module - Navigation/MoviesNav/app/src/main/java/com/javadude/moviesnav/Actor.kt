package com.javadude.moviesnav

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Actor(
    @PrimaryKey override var id : String = UUID.randomUUID().toString(),
    var name : String = ""
) : HasId