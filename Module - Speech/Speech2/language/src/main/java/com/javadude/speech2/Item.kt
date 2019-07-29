package com.javadude.speech2

open class Item(val name: String,
                open val description: String,
                val canBePickedUp: Boolean) {

    open fun get(game: Game, itemName: String) =
        game.report("get $itemName from $name", "$name is not a container; it does not hold other items")

    open fun open(game: Game) = game.report("open $name", "The $name cannot be opened")
    open fun close(game: Game) = game.report("close $name", "The $name cannot be closed")
    open fun unlock(game: Game) = game.report("unlock $name", "The $name cannot be unlocked")
    open fun lock(game: Game) = game.report("lock $name", "The $name cannot be unlocked")
}
