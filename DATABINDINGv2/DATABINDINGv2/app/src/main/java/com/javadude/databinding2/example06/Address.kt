package com.javadude.databinding2.example06

import android.arch.lifecycle.MutableLiveData

class Address(
        street : String,
        city : String,
        state : State,
        zip : String
) {
    val street = MutableLiveData<String>().apply { value = street }
    val city = MutableLiveData<String>().apply { value = city }
    val state = MutableLiveData<State>().apply { value = state }
    val zip = MutableLiveData<String>().apply { value = zip }

    override fun toString(): String {
        return "Address(street='$street', city='$city', state=$state, zip='$zip')"
    }
}