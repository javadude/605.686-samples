package com.javadude.speech;

import java.util.HashMap;
import java.util.Map;

public class Room extends ItemHolder {
	private Map<Direction, Room> paths = new HashMap<Direction, Room>();
	
	public Room(String name, String description) {
		super(name, description, false);
	}
	public String getDescription() {
		String result = super.getDescription();
		if (!paths.isEmpty()) {
			result += "\n\n" + "You can go ";
			for (Direction d : paths.keySet()) {
				result += d.name() + ", ";
			}
			result = result.substring(0, result.length()-2);
		}
		if (!isEmpty()) {
			result += "\n\n" + "You see ";
			for (String itemName : getItemNames()) {
				result += "a " + itemName + ", ";
			}
			result = result.substring(0, result.length()-2);
		}
		return result;
	}
	public Room move(Direction direction) {
		return paths.get(direction);
	}
	public void connect(Direction direction, Room room) {
		paths.put(direction, room);
	}
}
