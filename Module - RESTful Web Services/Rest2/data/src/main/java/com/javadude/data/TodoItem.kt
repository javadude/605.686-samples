package com.javadude.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.util.UUID

@Serializable
data class TodoItem(
    var id : String = UUID.randomUUID().toString(),
    var name : String = "",
    var description : String = "",
    var priority : Int = 1
)

private val json = Json(JsonConfiguration.Stable)

fun TodoItem.toJsonString() =
    json.stringify(TodoItem.serializer(), this)

fun List<TodoItem>.toJsonString() =
    json.stringify(TodoItem.serializer().list, this)

fun String.toTodoItem() =
    if (this.isEmpty()) null else json.parse(TodoItem.serializer(), this)

fun String.toTodoItemList() =
    if (this.isEmpty()) emptyList() else json.parse(TodoItem.serializer().list, this)

fun ByteArray.toTodoItem() = String(this).toTodoItem()
fun ByteArray.toTodoItemList() = String(this).toTodoItemList()

