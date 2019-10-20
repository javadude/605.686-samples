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
private const val serverUrlBase = "http://10.0.2.2:8080/restserver/todox"

private sealed class TodoRequest {
    abstract fun toFuelRequest() : Request
}
private object GetAllTodoItems : TodoRequest() {
    override fun toFuelRequest() = Fuel.get(serverUrlBase)
}
private object CreateTodoItem : TodoRequest() {
    override fun toFuelRequest() = Fuel.post(serverUrlBase)
}
private class GetTodoItem(val id: String) : TodoRequest() {
    override fun toFuelRequest() = Fuel.get("$serverUrlBase/${id}")
}
private class UpdateTodoItem(private val item : TodoItem) : TodoRequest() {
    override fun toFuelRequest() = Fuel.put("$serverUrlBase/${item.id}").jsonBody(item.toJsonString())
}
private class DeleteTodoItem(private val item: TodoItem) : TodoRequest() {
    override fun toFuelRequest() = Fuel.post("$serverUrlBase/${item.id}")
}

class TodoRest {
    private fun TodoRequest.liveDataResponse(createResult : (ByteArray) -> TodoResponse) : LiveData<TodoResponse> =
        MediatorLiveData<TodoResponse>().apply {
            val source = this@liveDataResponse.toFuelRequest().liveDataResponse()

            addSource(source) { (response, result) ->
                removeSource(source)
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

    fun getAllTodoItems() = GetAllTodoItems.liveDataResponse { TodoItemListResult(it.toTodoItemList().sortedWith(compareBy(TodoItem::priority, TodoItem::name))) }

    @Suppress("unused") // in case we use it later it would look like this
    fun getTodoItem(id : String) = GetTodoItem(id).liveDataResponse { bytes -> bytes.toTodoItem()?.let { TodoItemResult(it) } ?: TodoItemNotFound }

    fun updateTodoItem(item: TodoItem) = UpdateTodoItem(item).liveDataResponse { TodoItemUpdated }

    fun deleteTodoItem(item: TodoItem) = DeleteTodoItem(item).liveDataResponse { TodoItemDeleted }

    fun createTodoItem() = CreateTodoItem.liveDataResponse {
        TodoItemResult(TodoItem(id = Uri.parse(String(it)).lastPathSegment!!))
    }
}