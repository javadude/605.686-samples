package com.javadude.rest.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TodoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Map<Long, TodoItem> items = new HashMap<>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		items.put(111L, new TodoItem(111, "Wash Car", "Make it clean", 1));
		items.put(222L, new TodoItem(222, "Wash Cat", "Make it purr", 1));
		items.put(333L, new TodoItem(333, "Wash Carpet", "Make it soapy", 2));
	}

	@Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if there's an id in the request URI
		long id = getId(request);
		if (id != -1) {
			// try to get the TodoItem from our data store
			TodoItem todoItem = items.get(id);
			if (todoItem == null) {
				// if not found, return SC_NOT_FOUND (404)
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				// otherwise, write JSON for the TodoItem to the output stream
				write(todoItem, response);
			}

		} else {
			// if there's no id in the request URI
			// if there are no TodoItems in our data store, return SC_NO_CONTENT (204)
			if (items.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				// otherwise, write a JSON list of the TodoItems to the output stream
				String result = "[\n";
				String comma = "";
				for(TodoItem todoItem : items.values()) {
					result += comma + todoItem.toJsonString();
					comma = ",\n";
				}
				response.setContentType("application/json");
				response.getWriter().write(result + "\n]");
			}
		}
	}

	private void write(TodoItem todoItem, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.getWriter().write(todoItem.toJsonString());
	}

	private long getId(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			return -1;
		}
		return Long.parseLong(pathInfo.substring(1));
	}

	private static long nextId = 1000;
	@Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long id = getId(request);
		if (id != -1) {
			throw new ServletException("Must NOT specify an ID for a POST request");
		}
		TodoItem todoItem = getItem(request);
		todoItem.setId(nextId++);
		items.put(todoItem.getId(), todoItem);
		// if there's an ID in the URI, throw exception (ID not allowed on POST)
		// otherwise, create the TodoItem specified in the input stream
		response.setContentType("text/plain");
		response.getWriter().write(request.getRequestURL().toString());
		response.getWriter().write("/");
		response.getWriter().write(todoItem.getId() +"");
	}

	@Override protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long id = getId(request);
		if (id == -1) {
			throw new ServletException("Must specify an ID for a PUT request");
		}
		TodoItem todoItem = getItem(request);
		items.put(todoItem.getId(), todoItem);
		// if there's no ID in the URI, throw exception (ID required for PUT)
		// otherwise, create or update the TodoItem specified in the input stream
		response.setContentType("text/plain");
		response.getWriter().write(request.getRequestURL().toString());
		response.getWriter().write("/");
		response.getWriter().write(todoItem.getId() + "");
	}
	private TodoItem getItem(HttpServletRequest request) throws IOException, ServletException {
		BufferedReader br = null;
		String json = "";
		try {
			br = new BufferedReader(request.getReader());
			String line;
			while((line = br.readLine()) != null) {
				json += line + "\n";
			}
		} finally {
			if (br != null)
				br.close();
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
			long id = getId(request);
			String name = jsonObject.optString("name");
			String description = jsonObject.optString("description");
			int priority = jsonObject.optInt("priority");
			return new TodoItem(id, name, description, priority);
		} catch (JSONException e) {
			throw new ServletException(e);
		}
	}

	@Override protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long id = getId(request);
		if (id == -1) {
			throw new ServletException("Must specify an ID for a DELETE request");
		}
		if (items.containsKey(id)) {
			items.remove(id);
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		// if there's no ID in the URI, throw exception (ID required for DELETE)
		// otherwise, try to delete the TodoItem specified in the input stream
		// note: style decision - if not found:
		// return SC_OK (200) - was going to delete it anyway...
		// return SC_NOT_FOUND (404) - not found so cannot delete
	}
}
