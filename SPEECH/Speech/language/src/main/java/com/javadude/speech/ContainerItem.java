package com.javadude.speech;

public class ContainerItem extends ItemHolder {
	private boolean open;
	private boolean locked;
	private String keyName;
	public ContainerItem(String name, String description, boolean canBePickedUp, boolean locked, boolean open, String keyName) {
		super(name, description, canBePickedUp);
		this.locked = locked;
		this.open = open;
		this.keyName = keyName;
	}
	@Override public boolean get(Game game, String itemName) {
		String command = "get " + itemName + " from " + getName();
		if (!open)
			return game.report(command, "The " + getName() + " is not open");
		Item item = remove(itemName);
		if (item == null)
			return game.report(command, "The " + getName() + " does not contain the " + itemName);
		game.getInventory().add(item);
		return game.report(command, "You got the " + itemName + " from the " + getName());
	}
	@Override public boolean open(Game game) {
		String command = "open " + getName();
		if (open)
			return game.report(command, "The " + getName() + " is already open");
		if (locked)
			return game.report(command, "The " + getName() + " is locked");
		open = true;
		return game.report(command, "You open the " + getName());
	}
	@Override public boolean close(Game game) {
		String command = "close " + getName();
		if (!open)
			return game.report(command, "The " + getName() + " is already closed");
		open = false;
		return game.report(command, "You close the " + getName());
	}
	@Override public boolean lock(Game game) {
		String command = "lock " + getName();
		if (keyName == null)
			return game.report(command, "The " + getName() + " cannot be locked");
		if (open)
			return game.report(command, "The " + getName() + " is open");
		if (locked)
			return game.report(command, "The " + getName() + " is already locked");
		if (!game.getInventory().contains(keyName))
			return game.report(command, "You do not have the key for this container");
		locked = true;
		return game.report(command, "You lock the " + getName());
	}
	@Override public boolean unlock(Game game) {
		String command = "unlock " + getName();
		if (!locked)
			return game.report(command, "The " + getName() + " is not locked");
		if (!game.getInventory().contains(keyName))
			return game.report(command, "You do not have the key for this container");
		locked = false;
		return game.report(command, "You unlock the " + getName());
	}
	
	@Override
	public String getDescription() {
		String result = super.getDescription();
		if (locked)
			return result + "\n\n The " + getName() + " is locked";
		if (!open)
			return result + "\n\n The " + getName() + " is closed";
		result += "\n\n" + dumpContents();
		return result;
	}
}
