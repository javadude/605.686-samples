package com.javadude.fragv2

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class ProjectEntity {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var name : String? = null
}