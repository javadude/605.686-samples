package com.javadude.toolbarsv2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class ProjectEntity {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var name : String? = null
}