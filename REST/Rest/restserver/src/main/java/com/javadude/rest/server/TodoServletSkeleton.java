package com.javadude.rest.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TodoServletSkeleton extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if there's an id in the request URI
		// try to get the TodoItem from our data store
		// if not found, return SC_NOT_FOUND (404)
		// otherwise, write JSON for the TodoItem to the output stream

		// if there's no id in the request URI
		// if there are no TodoItems in our data store, return SC_NO_CONTENT (204)
		// otherwise, write a JSON list of the TodoItems to the output stream
	}

	@Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if there's an ID in the URI, throw exception (ID not allowed on POST)
		// otherwise, create the TodoItem specified in the input stream
	}

	@Override protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if there's no ID in the URI, throw exception (ID required for PUT)
		// otherwise, create or update the TodoItem specified in the input stream
	}

	@Override protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if there's no ID in the URI, throw exception (ID required for DELETE)
		// otherwise, try to delete the TodoItem specified in the input stream
		// note: style decision - if not found:
		// return SC_OK (200) - was going to delete it anyway...
		// return SC_NOT_FOUND (404) - not found so cannot delete
	}
}
