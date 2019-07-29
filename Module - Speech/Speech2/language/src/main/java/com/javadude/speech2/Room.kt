package com.javadude.speech2

import java.util.*

class Room(name: String,
           description: String)
        : ItemHolder(name, description, false) {

    private val paths = HashMap<Direction, Room>()
    override val description: String
        get() {
            var result = super.description
            if (paths.isNotEmpty()) {
                result += "\n\n" + "You can go "
                for (d in paths.keys) {
                    result += d.name + ", "
                }
                result = result.substring(0, result.length - 2)
            }
            if (!isEmpty) {
                result += "\n\n" + "You see "
                for (itemName in itemNames) {
                    result += "a $itemName, "
                }
                result = result.substring(0, result.length - 2)
            }
            return result
        }

    fun move(direction: Direction) = paths[direction]

    fun connect(direction: Direction, room: Room) {
        paths[direction] = room
    }
}
