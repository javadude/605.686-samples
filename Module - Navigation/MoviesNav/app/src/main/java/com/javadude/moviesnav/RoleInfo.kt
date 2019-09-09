package com.javadude.moviesnav

data class RoleInfo(
    override val id : String,
    val actorName : String,
    val roleName : String,
    val order : Int
) : HasId