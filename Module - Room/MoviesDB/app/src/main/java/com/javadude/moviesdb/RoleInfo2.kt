package com.javadude.moviesdb

import androidx.room.Relation

data class RoleInfo2(
    @Relation(parentColumn = "actorId", entityColumn = "id")
    var actor : Actor,
    var movieId : String,
    var actorId : String,
    var roleName : String,
    var order : Int
)