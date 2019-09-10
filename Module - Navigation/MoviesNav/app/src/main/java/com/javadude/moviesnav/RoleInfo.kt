package com.javadude.moviesnav

data class RoleInfo(
    override val id : String,
    val actorId : String,
    val actorName : String,
    val roleName : String,
    val order : Int
) : HasId