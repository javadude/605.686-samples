package com.javadude.rest

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.livedata.liveDataResponse
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.javadude.data.TodoItem
import com.javadude.data.toJsonString
import com.javadude.data.toTodoItem
import com.javadude.data.toTodoItemList

// should be in prefs
// NOTE: if running on a real device, you'll probably need to change firewall settings...
//       10.0.2.2 is a special address for an android emulator to access localhost on its host
//       computer
private const val serverUrlBase = "http://10.0.2.2:8080/restserver/todo"

class TodoRest {
    private fun liveDataResponse(
        createFuelRequest: () -> Request,
        createResult: (ByteArray) -> TodoResponse
    ) : LiveData<TodoResponse> =
        MediatorLiveData<TodoResponse>().apply {
            val source = createFuelRequest().liveDataResponse()

            addSource(source) { (response, result) ->
                removeSource(source) // stop observing when we get the result
                result.success {
                    value = createResult(it)
                }
                result.failure {
                    value = when (it.response.statusCode) {
                        404 -> TodoItemNotFound
                        else -> {
                            TodoError(response.statusCode, it.exception.message?: it.exception.javaClass.simpleName)
                        }
                    }
                }
            }
        }

    fun getAllTodoItems() = liveDataResponse(
        createFuelRequest = { Fuel.get(serverUrlBase) },
        createResult = { TodoItemListResult(it.toTodoItemList().sortedWith(compareBy(TodoItem::priority, TodoItem::name))) }
    )

    @Suppress("unused") // in case we use it later it would look like this
    fun getTodoItem(id : String) = liveDataResponse(
        createFuelRequest = { Fuel.get("$serverUrlBase/${id}") },
        createResult = { bytes -> bytes.toTodoItem()?.let { TodoItemResult(it) } ?: TodoItemNotFound }
    )

    fun updateTodoItem(item: TodoItem) = liveDataResponse(
        createFuelRequest = { Fuel.put("$serverUrlBase/${item.id}").jsonBody(item.toJsonString()) },
        createResult = { TodoItemUpdated }
    )

    fun deleteTodoItem(item: TodoItem) = liveDataResponse(
        createFuelRequest = { Fuel.delete("$serverUrlBase/${item.id}").jsonBody(item.toJsonString()) },
        createResult = { TodoItemDeleted }
    )

    fun createTodoItem() = liveDataResponse(
        createFuelRequest = { Fuel.post(serverUrlBase) },
        createResult = { TodoItemResult(TodoItem(id = Uri.parse(String(it)).lastPathSegment!!)) }
    )
}