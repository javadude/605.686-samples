package com.javadude.databinding2.example05

import android.databinding.ObservableField

class Address(
        street : String,
        city : String,
        state : State,
        zip : String
) {
    val street = ObservableField<String>().apply { set(street) }
    val city = ObservableField<String>().apply { set(city) }
    val state = ObservableField<State>().apply { set(state) }
    val zip = ObservableField<String>().apply { set(zip) }

    override fun toString(): String {
        return "Address(street='$street', city='$city', state=$state, zip='$zip')"
    }
}