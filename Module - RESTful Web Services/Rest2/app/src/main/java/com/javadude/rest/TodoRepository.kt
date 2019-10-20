package com.javadude.rest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.javadude.data.TodoItem

// for this example we're just using a really poor in-memory cache
//   really... this is a terribly naive approach - don't use it directly...
//   it's only here as an example of the role a repository could play to cache data or look it up
// for real life, you could use a database with a better caching strategy
//   (perhaps a single request to the server to see if anything changed,
//   then clear the local cache and fetch as needed, or more granular
//   detail from the server on what has changed so you can be more optimal
//   in your caching... it really depends on the use case, how often the data
//   needs to be synced, if the user could use multiple devices to get to the same
//   data. The goal is to minimize the amount of bandwidth used based on the actual necessary
//   sync requirements. Note that you could set up a WorkManager to perform the upload syncing
//   tasks when the device is charging and and wifi)
class TodoRepository {
    private val todoRest = TodoRest()

    // initialize with all items from the rest server, the manage locally
    private val allTodoItems = MediatorLiveData<TodoResponse>()

    private fun updateTodoItems() {
        val source = todoRest.getAllTodoItems()
        allTodoItems.addSource(source) {
            allTodoItems.removeSource(source)
            allTodoItems.value = it
        }
    }

    init {
        updateTodoItems()
    }

    fun getAllTodoItems() : LiveData<TodoResponse> = allTodoItems

    // to keep this simple, whenever data changes, we update by re-reading all items from server
    // not even close to ideal...
    private fun liveDataResponse(action : () -> LiveData<TodoResponse>) =
        MediatorLiveData<TodoResponse>().apply {
            val source = action()
            addSource(source) {
                removeSource(source)
                updateTodoItems()
                value = it
            }
        }

    private fun liveDataResponse(item: TodoItem, action : (TodoItem) -> LiveData<TodoResponse>) =
        MediatorLiveData<TodoResponse>().apply {
            val source = action(item)
            addSource(source) {
                removeSource(source)
                updateTodoItems()
                value = it
            }
        }

    fun createTodoItem() = liveDataResponse(todoRest::createTodoItem)
    fun deleteTodoItem(item: TodoItem) = liveDataResponse(item, todoRest::deleteTodoItem)
    fun updateTodoItem(item: TodoItem) = liveDataResponse(item, todoRest::updateTodoItem)
}