package com.javadude.speech;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemHolder extends Item {
	private Map<String, Item> items = new HashMap<String, Item>();

	public ItemHolder(String name, String description, boolean canBePickedUp) {
		super(name, description, canBePickedUp);
	}
	
	public String dumpContents() {
		String article = "The ";
		if ("inventory".equals(getName()))
			article = "Your ";
		if (isEmpty())
			return article + getName() + " is empty";
		String result = article + getName() + " contains ";
		for (String item : items.keySet()) {
			result += "a " + item + ", ";
		}
		result = result.substring(0, result.length()-2);
		return result;
	}
	public boolean isEmpty() {
		return items.isEmpty();
	}
	public void add(Item item) {
		items.put(item.getName(), item);
	}
	public Item remove(String name) {
		return items.remove(name);
	}
	public Item get(String name) {
		return items.get(name);
	}
	public boolean contains(String name) {
		return items.containsKey(name);
	}
	public Set<String> getItemNames() {
		return items.keySet();
	}
}
