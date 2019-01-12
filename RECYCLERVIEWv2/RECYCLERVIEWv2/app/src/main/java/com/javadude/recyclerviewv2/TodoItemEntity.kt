package com.javadude.recyclerviewv2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class TodoItemEntity {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var projectId : String? = null
    var name : String? = null
    var description : String? = null
    var priority : Int = 1
}