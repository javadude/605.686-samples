package com.javadude.speech2

import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Game(json: String, private val reporter: Reporter) {
    private var currentRoom: Room? = null
    val inventory: ItemHolder

    interface Reporter {
        fun report(message: String, text: String)
    }

    init {
        try {
            val jsonObject = JSONObject(json)
            val itemsMap = jsonObject.get("items") as JSONObject
            val roomsMap = jsonObject.get("rooms") as JSONObject

            // set up items
            val items = HashMap<String, Item>()
            run {
                val i = itemsMap.keys()
                while (i.hasNext()) {
                    val name = i.next()
                    val o = itemsMap.getJSONObject(name)
                    val description = o.getString("description")
                    val canPickUp = o.getBoolean("canPickUp")
                    if (o.has("items")) {
                        val containerItem = ContainerItem(
                            name, description, canPickUp,
                            o.getBoolean("locked"),
                            o.getBoolean("open"),
                            o.getString("keyName")
                        )
                        items[name] = containerItem
                    } else {
                        items[name] = Item(name, description, canPickUp)
                    }
                }
            }

            // resolve nested items (should use recursion, but we assume a single level for simplicity in this example)
            run {
                val i = itemsMap.keys()
                while (i.hasNext()) {
                    val name = i.next()
                    val o = itemsMap.getJSONObject(name)
                    if (o.has("items")) {
                        val containerItem = items[name] as ContainerItem
                        val nestedItemArray = o.getJSONArray("items")
                        // we'll assume only one level deep for simplicity
                        for (m in 0 until nestedItemArray.length()) {
                            val nestedItemName = nestedItemArray.getString(m)
                            val nestedItem = items[nestedItemName]
                            containerItem.add(nestedItem)
                        }
                    }
                }
            }

            // set up rooms
            val rooms = HashMap<String, Room>()
            run {
                val i = roomsMap.keys()
                while (i.hasNext()) {
                    val name = i.next()
                    val o = roomsMap.getJSONObject(name)
                    val description = o.getString("description")
                    val room = Room(name, description)
                    val itemsArray = o.getJSONArray("items")
                    for (m in 0 until itemsArray.length()) {
                        val item = items[itemsArray.getString(m)]
                        room.add(item)
                    }
                    rooms[name] = room
                }
            }

            // need second pass over rooms to resolve forward refs on directions
            val i = roomsMap.keys()
            while (i.hasNext()) {
                val name = i.next()
                val o = roomsMap.getJSONObject(name)
                val directionsMap = o.getJSONObject("directions")
                val room = rooms[name]
                val j = directionsMap.keys()
                while (j.hasNext()) {
                    val directionName = j.next()
                    val roomName = directionsMap.getString(directionName)
                    rooms[roomName]?.let {
                        room?.connect(Direction.valueOf(directionName.toUpperCase()), it)
                    }
                }
            }

            val startRoomName = jsonObject.get("startRoom") as String
            currentRoom = rooms[startRoomName]

        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        inventory = ItemHolder("inventory", "Your inventory", false)

        look()
    }

    fun report(command: String, message: String): Boolean {
        reporter.report(message, "Location: " + currentRoom!!.name + "\n\nCommand: " + command + "\n\n" + message)
        return false
    }

    operator fun get(itemName: String): Boolean {
        val command = "get $itemName"
        if (inventory.contains(itemName))
            return report(command, "You already have the $itemName")
        if (!currentRoom!!.contains(itemName)) {
            return report(command, "You don't see the $itemName in the room")
        }
        val item = currentRoom!![itemName]
        if (!item!!.canBePickedUp)
            return report(command, "You can't pick up the $itemName")
        currentRoom!!.remove(itemName)
        inventory.add(item)
        return report(command, "You picked up the $itemName")
    }

    fun drop(itemName: String): Boolean {
        val command = "drop $itemName"
        if (!inventory.contains(itemName))
            return report(command, "You don't have the $itemName")
        val item = inventory.remove(itemName)
        currentRoom!!.add(item!!)
        return report(command, "You dropped the $itemName")
    }

    fun getFrom(itemName: String, containerName: String): Boolean {
        val command = "get $itemName from $containerName"
        if (inventory.contains(itemName))
            return report(command, "You already have the $itemName")
        if (!currentRoom!!.contains(containerName)) {
            return report(command, "You don't see the $containerName in the room")
        }
        val container = currentRoom!![containerName]
        return container!!.get(this, itemName)
    }

    fun open(containerName: String): Boolean {
        val command = "open $containerName"
        if (!currentRoom!!.contains(containerName)) {
            return report(command, "You don't see the $containerName in the room")
        }
        val container = currentRoom!![containerName]
        return container!!.open(this)
    }

    fun close(containerName: String): Boolean {
        val command = "close $containerName"
        if (!currentRoom!!.contains(containerName)) {
            return report(command, "You don't see the $containerName in the room")
        }
        val container = currentRoom!![containerName]
        return container!!.close(this)
    }

    fun unlock(containerName: String): Boolean {
        val command = "unlock $containerName"
        if (!currentRoom!!.contains(containerName)) {
            return report(command, "You don't see the $containerName in the room")
        }
        val container = currentRoom!![containerName]
        return container!!.unlock(this)
    }

    fun lock(containerName: String): Boolean {
        val command = "lock $containerName"
        if (!currentRoom!!.contains(containerName)) {
            return report(command, "You don't see the $containerName in the room")
        }
        val container = currentRoom!![containerName]
        return container!!.lock(this)
    }

    fun examine(itemName: String): Boolean {
        val command = "examine $itemName"
        if (!inventory.contains(itemName) && !currentRoom!!.contains(itemName)) {
            return report(command, "You don't see the $itemName in the room or your inventory")
        }
        var item = currentRoom!![itemName]
        if (item == null)
            item = inventory[itemName]
        return report(command, item!!.description)
    }

    fun look(): Boolean {
        return report("look", currentRoom!!.description)
    }

    fun inventory(): Boolean {
        return report("inventory", inventory.dumpContents())
    }

    fun go(direction: Direction): Boolean {
        println("GO")
        val nextRoom = currentRoom!!.move(direction)
        if (nextRoom == null) {
            val dir = direction.name.toLowerCase()
            return report("go $dir", "You cannot go $dir")
        }
        currentRoom = nextRoom
        return look()
    }
}
