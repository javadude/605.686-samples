package com.javadude.restserver

import com.javadude.data.TodoItem
import com.javadude.data.toJsonString
import com.javadude.data.toTodoItem
import com.javadude.data.todoListToJsonString
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TodoServlet : HttpServlet() {
    // for this example, we're just using an in-memory data store for the server
    private val items = mutableMapOf<String, TodoItem>()

    override fun init() {
        super.init()
        addItem("Wash Car", "Make it shine", 1)
        addItem("Wash Cat", "Make it purr", 2)
        addItem("Wash Carpet", "Make it soapy", 3)
    }

    private fun addItem(name: String, description: String, priority: Int) {
        val item = TodoItem(name = name, description = description, priority = priority)
        items[item.id] = item
    }

    // fetch items from the data store
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val id = request.id

        // if there is an id, just write the one item (or set status "not found" if it doesn't exist)
        if (id != null) {
            val item = items[id]
            if (item != null) {
                response.contentType = "application/json"
                response.writer.write(item.toJsonString())

            } else {
                response.status = HttpServletResponse.SC_NOT_FOUND
            }


        // if there is no id, write all items or set status "no content" if not items exist
        } else {
            if (items.isEmpty()) {
                HttpServletResponse.SC_NO_CONTENT

            } else {
                response.contentType = "application/json"
                response.writer.write(items.values.toList().todoListToJsonString())
            }
        }
    }

    // create an item in the data store
    override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        if (request.id != null) {
            throw ServletException("Must NOT specify an id for a POST request")
        }
        val item = TodoItem()
        items[item.id] = item

        // return a URI for the new item
        response.contentType = "text/plain"
        response.writer.write("${request.requestURL}/${item.id}")
    }

    // replace an item in the data store
    override fun doPut(request: HttpServletRequest, response: HttpServletResponse) {
        val id = request.id ?: throw ServletException("Must specify an id for a PUT request")
        val item = request.reader.readText().toTodoItem()
        if (item?.id != id) {
            throw ServletException("id specified in data must match id in URI")
        }
        items[id] = item
    }

    override fun doDelete(request: HttpServletRequest, response: HttpServletResponse) {
        val id = request.id ?: throw ServletException("Must specify an id for a DELETE request")
        // taking the style of "if they want to delete it, doesn't matter if it actually existed..."
        items.remove(id)
    }

    private val HttpServletRequest.id : String?
            get() {
                val parts = requestURI.split("/")
                return if (parts.size == 4) {
                    parts[3]
                } else {
                    null
                }
            }
}