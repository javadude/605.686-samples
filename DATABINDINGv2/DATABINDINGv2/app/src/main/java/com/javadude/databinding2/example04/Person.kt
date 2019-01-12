package com.javadude.databinding2.example04

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.javadude.databinding2.BR

class Person(
        name : String,
        age : Int,
        address : Address) : BaseObservable() {

    @get:Bindable
    var name : String = name
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var age : Int = age
        set(value) {
            field = value
            notifyPropertyChanged(BR.age)
        }

    @get:Bindable
    var address : Address = address
        set(value) {
            field = value
            notifyPropertyChanged(BR.address)
        }

    override fun toString(): String {
        return "Person(name='$name', age=$age, address=$address)"
    }
}


