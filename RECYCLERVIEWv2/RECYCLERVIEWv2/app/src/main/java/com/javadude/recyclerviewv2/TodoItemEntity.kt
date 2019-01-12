package com.javadude.recyclerviewv2

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class TodoItemEntity {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var projectId : String? = null
    var name : String? = null
    var description : String? = null
    var priority : Int = 1
}