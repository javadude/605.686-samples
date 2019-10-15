package com.javadude.services2

import android.os.Parcel
import android.os.Parcelable

//import kotlinx.android.parcel.Parcelize

//@Parcelize // SHOULD WORK WITH AIDL AT SOME POINT... CURRENT KOTLIN BUG
//data class Person(
//    val name : String,
//    val age : Int
//) : Parcelable

// A person object to transfer using AIDL - must be Parcelable
// I wish the above worked with AIDL - there's a bug in the kotlin code generator
//    for @Parcelize that has the wrong return type on createFromParcel that produces a compiler
//    error in the generated stub/skeleton code for the AIDL unfortunately
// Parcelable isn't hard to implement; just annoyingly boilerplate...
data class Person(
    val name : String,
    val age : Int
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Person> {
            override fun createFromParcel(parcel: Parcel) =
                Person(
                    parcel.readString() ?: throw IllegalStateException(),
                    parcel.readInt()
                )

            override fun newArray(size: Int) = Array<Person?>(size) { null }
        }
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    override fun describeContents() = 0

}

