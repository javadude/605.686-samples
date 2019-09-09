package com.javadude.moviesdb

import androidx.room.Embedded

data class RoleInfo(
    @Embedded val actor : Actor,
    val roleName : String,
    val order : Int
)