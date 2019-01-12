package com.javadude.databinding2.example08

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Address(
        @PrimaryKey var id : String,
        var ownerId : String,
        var street: String,
        var city: String,
        var state: State,
        var zip: String
) {
    override fun toString(): String {
        return "Address(street='$street', city='$city', state=$state, zip='$zip')"
    }
}