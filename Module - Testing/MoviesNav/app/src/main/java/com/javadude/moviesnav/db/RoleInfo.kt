package com.javadude.moviesnav.db

data class RoleInfo(
    override val id : String,
    val actorId : String,
    val actorName : String,
    val roleName : String,
    val order : Int
) : HasId