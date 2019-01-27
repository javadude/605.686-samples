package com.javadude.speech;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Game {
	public interface Reporter {
		void report(String message, String text);
	}
	private Room currentRoom;
	private ItemHolder inventory;
	private final Reporter reporter;
	public Game(String json, Reporter reporter) {
		this.reporter = reporter;

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject itemsMap = (JSONObject) jsonObject.get("items");
			JSONObject roomsMap = (JSONObject) jsonObject.get("rooms");

			// set up items
			Map<String, Item> items = new HashMap<String, Item>();
			for (@SuppressWarnings("unchecked")
				 Iterator<String> i = itemsMap.keys(); i.hasNext();) {
				String name = i.next();
				JSONObject o = itemsMap.getJSONObject(name);
				String description = o.getString("description");
				boolean canPickUp = o.getBoolean("canPickUp");
				if (o.has("items")) {
					ContainerItem containerItem =
							new ContainerItem(name, description, canPickUp,
									o.getBoolean("locked"),
									o.getBoolean("open"),
									o.getString("keyName"));
					items.put(name, containerItem);
				} else {
					items.put(name, new Item(name, description, canPickUp));
				}
			}

			// resolve nested items (should use recursion, but we assume a single level for simplicity in this example)
			for (@SuppressWarnings("unchecked")
				 Iterator<String> i = itemsMap.keys(); i.hasNext();) {
				String name = i.next();
				JSONObject o = itemsMap.getJSONObject(name);
				if (o.has("items")) {
					ContainerItem containerItem = (ContainerItem) items.get(name);
					JSONArray nestedItemArray = o.getJSONArray("items");
					// we'll assume only one level deep for simplicity
					for (int m = 0; m < nestedItemArray.length(); m++) {
						String nestedItemName = nestedItemArray.getString(m);
						Item nestedItem = items.get(nestedItemName);
						containerItem.add(nestedItem);
					}
				}
			}

			// set up rooms
			Map<String, Room> rooms = new HashMap<String, Room>();
			for (@SuppressWarnings("unchecked") Iterator<String> i = roomsMap.keys(); i.hasNext();) {
				String name = i.next();
				JSONObject o = roomsMap.getJSONObject(name);
				String description = o.getString("description");
				Room room = new Room(name, description);
				JSONArray itemsArray = o.getJSONArray("items");
				for (int m = 0; m < itemsArray.length(); m++) {
					Item item = items.get(itemsArray.getString(m));
					room.add(item);
				}
				rooms.put(name, room);
			}

			// need second pass over rooms to resolve forward refs on directions
			for (@SuppressWarnings("unchecked") Iterator<String> i = roomsMap.keys(); i.hasNext();) {
				String name = i.next();
				JSONObject o = roomsMap.getJSONObject(name);
				JSONObject directionsMap = o.getJSONObject("directions");
				Room room = rooms.get(name);
				for (@SuppressWarnings("unchecked") Iterator<String> j = directionsMap.keys(); j.hasNext();) {
					String directionName = j.next();
					String roomName = directionsMap.getString(directionName);
					room.connect(Direction.valueOf(directionName.toUpperCase()), rooms.get(roomName));
				}
			}

			String startRoomName = (String) jsonObject.get("startRoom");
			currentRoom = rooms.get(startRoomName);

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		inventory = new ItemHolder("inventory", "Your inventory", false);

		look();
	}
	public boolean report(String command, String message) {
		reporter.report(message, "Location: " + currentRoom.getName() + "\n\nCommand: " + command + "\n\n" + message);
		return false;
	}
	public boolean get(String itemName) {
		String command = "get " + itemName;
		if (inventory.contains(itemName))
			return report(command, "You already have the " + itemName);
		if (!currentRoom.contains(itemName)) {
			return report(command, "You don't see the " + itemName + " in the room");
		}
		Item item = currentRoom.get(itemName);
		if (!item.canBePickedUp())
			return report(command, "You can't pick up the " + itemName);
		currentRoom.remove(itemName);
		inventory.add(item);
		return report(command, "You picked up the " + itemName);
	}
	public boolean drop(String itemName) {
		String command = "drop " + itemName;
		if (!inventory.contains(itemName))
			return report(command, "You don't have the " + itemName);
		Item item = inventory.remove(itemName);
		currentRoom.add(item);
		return report(command, "You dropped the " + itemName);
	}
	public ItemHolder getInventory() {
		return inventory;
	}
	public boolean getFrom(String itemName, String containerName) {
		String command = "get " + itemName + " from " + containerName;
		if (inventory.contains(itemName))
			return report(command, "You already have the " + itemName);
		if (!currentRoom.contains(containerName)) {
			return report(command, "You don't see the " + containerName + " in the room");
		}
		Item container = currentRoom.get(containerName);
		return container.get(this, itemName);
	}
	public boolean open(String containerName) {
		String command = "open " + containerName;
		if (!currentRoom.contains(containerName)) {
			return report(command, "You don't see the " + containerName + " in the room");
		}
		Item container = currentRoom.get(containerName);
		return container.open(this);
	}
	public boolean close(String containerName) {
		String command = "close " + containerName;
		if (!currentRoom.contains(containerName)) {
			return report(command, "You don't see the " + containerName + " in the room");
		}
		Item container = currentRoom.get(containerName);
		return container.close(this);
	}
	public boolean unlock(String containerName) {
		String command = "unlock " + containerName;
		if (!currentRoom.contains(containerName)) {
			return report(command, "You don't see the " + containerName + " in the room");
		}
		Item container = currentRoom.get(containerName);
		return container.unlock(this);
	}
	public boolean lock(String containerName) {
		String command = "lock " + containerName;
		if (!currentRoom.contains(containerName)) {
			return report(command, "You don't see the " + containerName + " in the room");
		}
		Item container = currentRoom.get(containerName);
		return container.lock(this);
	}
	public boolean examine(String itemName) {
		String command = "examine " + itemName;
		if (!inventory.contains(itemName) && !currentRoom.contains(itemName)) {
			return report(command, "You don't see the " + itemName + " in the room or your inventory");
		}
		Item item = currentRoom.get(itemName);
		if (item == null)
			item = inventory.get(itemName);
		return report(command, item.getDescription());
	}
	public boolean look() {
		return report("look", currentRoom.getDescription());
	}
	public boolean inventory() {
		return report("inventory", inventory.dumpContents());
	}
	public boolean go(Direction direction) {
		System.out.println("GO");
		Room nextRoom = currentRoom.move(direction);
		if (nextRoom == null) {
			String dir = direction.name().toLowerCase();
			return report("go " + dir, "You cannot go " + dir);
		}
		currentRoom = nextRoom;
		return look();
	}
}
