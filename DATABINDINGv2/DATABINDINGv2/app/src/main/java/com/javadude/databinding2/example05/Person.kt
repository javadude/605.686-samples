package com.javadude.databinding2.example05

import android.databinding.ObservableField
import android.databinding.ObservableInt

class Person(
        name : String,
        age : Int,
        address : Address) {

    val name = ObservableField<String>().apply { set(name) }
    val age = ObservableInt().apply { set(age) }
    val address = ObservableField<Address>().apply { set(address) }

    override fun toString(): String {
        return "Person(name='$name', age=$age, address=$address)"
    }
}


