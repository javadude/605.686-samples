package com.javadude.fragv2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class TodoItemEntity {
    @PrimaryKey var id : String = UUID.randomUUID().toString()
    var projectId : String? = null
    var name : String? = null
    var description : String? = null
    var priority : Int = 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TodoItemEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}