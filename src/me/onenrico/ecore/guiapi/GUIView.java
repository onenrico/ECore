package me.onenrico.ecore.guiapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.onenrico.ecore.managerapi.PlaceholderManager;

public class GUIView implements InventoryHolder {
	private GUIMenu menu;
	private UUID owner;
	private Inventory inventory;
	private List<MenuItem> menuitems;
	private GUIAction closeaction;
	private DragAction dragaction;

	public GUIView(UUID owner, GUIMenu menu, PlaceholderManager pm) {
		this.owner = owner;
		this.menu = menu;
		this.menuitems = new ArrayList<>();
		build();
	}

	public void build() {
		build(false);
	}

	public void build(boolean reset) {
		Inventory tempinv = Bukkit.createInventory(this, menu.getRow() * 9, menu.getTitle());
		tempinv.setMaxStackSize(menu.getStacksize());
		if (!reset) {
			if (inventory != null) {
				for (int x = 0; x < tempinv.getSize() && x < inventory.getSize(); x++) {
					tempinv.setItem(x, inventory.getItem(x));
				}
				for (final MenuItem mi : new ArrayList<>(menuitems)) {
					if (mi.getSlot() + 1 >= tempinv.getSize()) {
						menuitems.remove(mi);
					}
				}
			}
		}
		inventory = tempinv;
	}

	public void setBorder(ItemStack border) {
		for (int i : getBorderSlot()) {
			inventory.setItem(i, border);
		}
	}

	public void open(Runnable callback, Player player) {
		if (menu.animation == null) {

		}
	}

	private List<Integer> getBorderSlot() {
		List<Integer> temp = new ArrayList<>();
		for (int i = 0; i < menu.getRow(); i++) {
			int left = i * 9;
			int right = 8 + (i * 9);
			temp.add(left);
			temp.add(right);
			if (i == 0) {
				for (int j = 0; j < 7; j++) {
					int top = left + 1 + j;
					temp.add(top);
				}
			}
			if (i + 1 == menu.getRow()) {
				for (int j = 0; j < 7; j++) {
					int bottom = left + 1 + j;
					temp.add(bottom);
				}
			}
		}
		return temp;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public MenuItem getMenuItem(final int slot) {
		for (final MenuItem mi : menuitems) {
			if (mi.getSlot() == slot) {
				return mi;
			}
		}
		return null;
	}

	public GUIMenu getMenu() {
		return menu;
	}

	public void setMenu(GUIMenu menu) {
		this.menu = menu;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public List<MenuItem> getMenuitems() {
		return menuitems;
	}

	public void setMenuitems(List<MenuItem> menuitems) {
		this.menuitems = menuitems;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public GUIAction getCloseaction() {
		return closeaction;
	}

	public void setCloseaction(GUIAction closeaction) {
		this.closeaction = closeaction;
	}

	public DragAction getDragaction() {
		return dragaction;
	}

	public void setDragaction(DragAction dragaction) {
		this.dragaction = dragaction;
	}
}
