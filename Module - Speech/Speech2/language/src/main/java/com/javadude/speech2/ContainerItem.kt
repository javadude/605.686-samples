package com.javadude.speech2

class ContainerItem(
    name: String,
    description: String,
    canBePickedUp: Boolean,
    private var locked: Boolean,
    private var open: Boolean,
    private val keyName: String?
) : ItemHolder(name, description, canBePickedUp) {

    override val description: String
        get() {
            var result = super.description
            if (locked)
                return "$result\n\n The $name is locked"
            if (!open)
                return "$result\n\n The $name is closed"
            result += "\n\n" + dumpContents()
            return result
        }

    override operator fun get(game: Game, itemName: String): Boolean {
        val command = "get $itemName from $name"
        if (!open)
            return game.report(command, "The $name is not open")
        val item =
            remove(itemName) ?: return game.report(command, "The $name does not contain the $itemName")
        game.inventory.add(item)
        return game.report(command, "You got the $itemName from the $name")
    }

    override fun open(game: Game): Boolean {
        val command = "open $name"
        if (open)
            return game.report(command, "The $name is already open")
        if (locked)
            return game.report(command, "The $name is locked")
        open = true
        return game.report(command, "You open the $name")
    }

    override fun close(game: Game): Boolean {
        val command = "close $name"
        if (!open)
            return game.report(command, "The $name is already closed")
        open = false
        return game.report(command, "You close the $name")
    }

    override fun lock(game: Game): Boolean {
        val command = "lock $name"
        if (keyName == null)
            return game.report(command, "The $name cannot be locked")
        if (open)
            return game.report(command, "The $name is open")
        if (locked)
            return game.report(command, "The $name is already locked")
        if (!game.inventory.contains(keyName))
            return game.report(command, "You do not have the key for this container")
        locked = true
        return game.report(command, "You lock the $name")
    }

    override fun unlock(game: Game): Boolean {
        val command = "unlock $name"
        if (!locked)
            return game.report(command, "The $name is not locked")
        if (!game.inventory.contains(keyName!!))
            return game.report(command, "You do not have the key for this container")
        locked = false
        return game.report(command, "You unlock the $name")
    }
}
