package com.javadude.speech2

open class ItemHolder(
                name: String,
                description: String,
                canBePickedUp: Boolean)
        : Item(name, description, canBePickedUp) {

    private val items = mutableMapOf<String, Item>()
    val isEmpty: Boolean
        get() = items.isEmpty()
    val itemNames: Set<String>
        get() = items.keys

    fun dumpContents(): String {
        var article = "The "
        if ("inventory" == name)
            article = "Your "
        if (isEmpty)
            return "$article$name is empty"
        var result = "$article$name contains "
        for (item in items.keys) {
            result += "a $item, "
        }
        return result.dropLast(2)
    }

    fun add(item: Item?) {
        item?.let {
            items[it.name] = it
        }
    }

    fun remove(name: String): Item? {
        return items.remove(name)
    }

    operator fun get(name: String): Item? {
        return items[name]
    }

    operator fun contains(name: String): Boolean {
        return items.containsKey(name)
    }
}
