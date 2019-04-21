package com.javadude.fragv2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class ProjectEntity {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var name : String? = null
}