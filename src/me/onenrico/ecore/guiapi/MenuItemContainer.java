package me.onenrico.ecore.guiapi;

import org.bukkit.inventory.ItemStack;

public class MenuItemContainer {
	public int slot;
	public ItemStack item;

	public MenuItemContainer(ItemStack item, int slot) {
		this.item = item;
		this.slot = slot;
	}

	@Override
	public ItemStack clone() {
		return item.clone();
	}

	public MenuItemContainer cloneThis() {
		return new MenuItemContainer(clone(), slot);
	}
}
