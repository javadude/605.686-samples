package com.javadude.moviesrecyclerview

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["movieId", "order"],
    indices = [Index("actorId")], // for @Relation
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
    var movieId : String,
    var actorId : String,
    var roleName : String,
    var order : Int
)