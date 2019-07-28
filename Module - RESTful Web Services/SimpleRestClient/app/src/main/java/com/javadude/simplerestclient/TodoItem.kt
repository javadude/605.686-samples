package com.javadude.simplerestclient

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

// a parcelable object representing a single to-do item

@Serializable
@Parcelize
data class TodoItem(
    var id: Long = 0, // NEW: use an id so we can handle updates better
    var name: String? = null,
    var description: String? = null,
    var priority: Int = 0
) : Parcelable

//@Throws(JSONException::class)
//    fun toJsonString(): String {
//        val jsonObject = JSONObject()
//        jsonObject.put("id", id)
//        jsonObject.put("name", name)
//        jsonObject.put("description", description)
//        jsonObject.put("priority", priority)
//        return jsonObject.toString(4)
//    }
