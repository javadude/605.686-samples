package com.javadude.moviesnav

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    indices = [Index("actorId"), Index("movieId")], // for @Relation
    foreignKeys = [
        ForeignKey(
            entity = Movie::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Actor::class,
            parentColumns = ["id"],
            childColumns = ["actorId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Role(
    @PrimaryKey override var id : String = UUID.randomUUID().toString(),
    var movieId : String,
    var actorId : String,
    var roleName : String = "",
    var order : Int
) : HasId
