package com.javadude.rest;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

// a parcelable object representing a single to-do item
public class TodoItem implements Parcelable {
	private long id; // NEW: use an id so we can handle updates better
	private String name;
	private String description;
	private int priority;

	public TodoItem() {}
	public TodoItem(long id, String name, String description, int priority) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
	}

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

	// only used by framework code for "special" cases
	// you should always return 0
	@Override
	public int describeContents() {
		return 0;
	}

	// write the data for this instance
	// note that this is significantly more efficient that java serializable, because
	//   we don't need to write metadata describing the class and fields
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeInt(priority);
	}

	// read the data and create a new instance
	// note that the field MUST be named "CREATOR", all uppercase
	public static Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
		// read the data from the parcel - note that the data read MUST be in the same
		//   order it was written in writeToParcel!
		@Override
		public TodoItem createFromParcel(Parcel source) {
			TodoItem todoItem = new TodoItem();
			todoItem.setId(source.readLong());
			todoItem.setName(source.readString());
			todoItem.setDescription(source.readString());
			todoItem.setPriority(source.readInt());
			return todoItem;
		}

		@Override
		public TodoItem[] newArray(int size) {
			return new TodoItem[size];
		}
	};
	public String toJsonString() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("name", name);
		jsonObject.put("description", description);
		jsonObject.put("priority", priority);
		return jsonObject.toString(4);
	}
}
