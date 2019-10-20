package com.javadude.rest

import com.javadude.data.TodoItem

sealed class TodoResponse

// should abstract the information more so we don't "leak" the fact that were using HTTP
data class TodoError(
    val statusCode: Int,
    val text : String
) : TodoResponse()

object TodoItemNotFound : TodoResponse()
object TodoItemUpdated : TodoResponse()
object TodoItemDeleted: TodoResponse()

data class TodoItemResult(
    val item : TodoItem
) : TodoResponse()

data class TodoItemListResult(
    val items : List<TodoItem>
) : TodoResponse()

