package com.javadude.databinding2.example06

import androidx.lifecycle.MutableLiveData

class Person(
        name : String,
        age : Int,
        address : Address) {

    val name = MutableLiveData<String>().apply { value = name }
    val age = MutableLiveData<Int>().apply { value = age }
    val address = MutableLiveData<Address>().apply { value = address }

    override fun toString(): String {
        return "Person(name='$name', age=$age, address=$address)"
    }
}


