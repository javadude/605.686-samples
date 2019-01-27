package com.javadude.speech;

public class Item {
	private String name;
	private String description;
	private boolean canBePickedUp;
	public Item(String name, String description, boolean canBePickedUp) {
		this.name = name;
		this.description = description;
		this.canBePickedUp = canBePickedUp;
	}
	public boolean canBePickedUp() {
		return canBePickedUp;
	}
	public boolean get(Game game, String itemName) {
		return game.report("get " + itemName + " from " + name, name + " is not a container; it does not hold other items");
	}
	public boolean open(Game game) {
		return game.report("open " + name, "The " + name + " cannot be opened");
	}
	public boolean close(Game game) {
		return game.report("close " + name, "The " + name + " cannot be closed");
	}
	public boolean unlock(Game game) {
		return game.report("unlock " + name, "The " + name + " cannot be unlocked");
	}
	public boolean lock(Game game) {
		return game.report("lock " + name, "The " + name + " cannot be unlocked");
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
}
