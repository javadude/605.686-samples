package com.javadude.rest.server;

import org.json.JSONObject;

public class TodoItem {
	private long id;
	private String name;
	private String description;
	private int priority;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public TodoItem(long id, String name, String description, int priority) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "TodoItem{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", priority=" + priority +
				'}';
	}

	public String toJsonString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("name", name);
		jsonObject.put("description", description);
		jsonObject.put("priority", priority);
		return jsonObject.toString(4);
	}
}
