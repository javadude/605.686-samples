package com.javadude.moviesnav

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Movie(
    @PrimaryKey override var id : String = UUID.randomUUID().toString(),
    var title : String = "",
    var description : String = "") : HasId
