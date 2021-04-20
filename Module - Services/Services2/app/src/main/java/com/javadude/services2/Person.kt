package com.javadude.services2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// implementation of the Person to transfer across the AIDL connection
// note that this class doesn't _exactly_ need to match, but it needs to deal
//   with the parceling the same on both ends!
@Parcelize data class Person(
    val name : String,
    val age : Int
) : Parcelable //{
//    companion object {
//        @JvmField
//        val CREATOR = object : Parcelable.Creator<Person> {
//            override fun createFromParcel(parcel: Parcel) =
//                Person(
//                    parcel.readString() ?: throw IllegalStateException(),
//                    parcel.readInt()
//                )
//
//            override fun newArray(size: Int) = Array<Person?>(size) { null }
//        }
//    }
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(name)
//        parcel.writeInt(age)
//    }
//
//    override fun describeContents() = 0
//
//}
//
