package com.javadude.databinding2.example04

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.javadude.databinding2.BR

class Address(
        street : String,
        city : String,
        state : State,
        zip : String
) : BaseObservable() {
    @get:Bindable
    var street = street
        set(value) {
            field = value
            notifyPropertyChanged(BR.street)
        }

    @get:Bindable
    var city = city
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }

    @get:Bindable
    var state = state
        set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    @get:Bindable
    var zip = zip
        set(value) {
            field = value
            notifyPropertyChanged(BR.zip)
        }

    override fun toString(): String {
        return "Address(street='$street', city='$city', state=$state, zip='$zip')"
    }
}