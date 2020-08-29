package com.javadude.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.UUID

@Serializable
data class TodoItem(
    var id : String = UUID.randomUUID().toString(),
    var name : String = "",
    var description : String = "",
    var priority : Int = 1
)

private val json = Json { allowStructuredMapKeys = true }

// NOTE - the Kotlin Serialization lib has changed the API since the video was created
//    the code below correctly works with the 1.0 RC version of kotlin serialization
fun TodoItem.toJsonString() =
    json.encodeToString(TodoItem.serializer(), this)

fun List<TodoItem>.todoListToJsonString() =
    json.encodeToString(ListSerializer(TodoItem.serializer()), this)

fun String.toTodoItem() =
    if (this.isEmpty()) null else json.decodeFromString(TodoItem.serializer(), this)

fun String.toTodoItemList() =
    if (this.isEmpty()) emptyList() else json.decodeFromString(ListSerializer(TodoItem.serializer()), this)

fun ByteArray.toTodoItem() = String(this).toTodoItem()
fun ByteArray.toTodoItemList() = String(this).toTodoItemList()

