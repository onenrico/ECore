package me.onenrico.ecore.guiapi;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuListener implements Listener {
	public boolean isCustom(Inventory inv) {
		if (inv != null && inv.getHolder() instanceof GUIView) {
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrag(final InventoryDragEvent e) {
		final Inventory top = e.getView().getTopInventory();
		if (isCustom(top)) {
			final GUIView view = (GUIView) top.getHolder();
			if (view.getMenu().isStoreable()) {
				return;
			}
			final Inventory inv = e.getInventory();
			for (final int slot : e.getRawSlots()) {
				if (slot < inv.getSize()) {
					e.setCancelled(true);
				}
			}
			if(view.getDragaction() != null)
			view.getDragaction().act(e);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(final InventoryClickEvent e) {
		if (e.getSlotType().equals(InventoryType.SlotType.OUTSIDE) && e.getClickedInventory() == null) {
			return;
		}
		final Inventory top = e.getView().getTopInventory();
		if (!isCustom(top)) {
			return;
		}
		e.setCancelled(true);
		final GUIView view = (GUIView) top.getHolder();
		if (e.getClickedInventory().equals(e.getView().getBottomInventory())) {
			if(view.getBottomclickaction() != null){
				if(view.getBottomclickaction().valid(e.getClick())) {
					view.getBottomclickaction().act(e);
				}
			}
			if (view.getMenu().isStoreable()) {
				e.setCancelled(false);
			}
			return;
		}
		final int slot = e.getSlot();
		final MenuItem mi = view.getMenuItem(slot);
		MenuClickEvent event = new MenuClickEvent(view,mi,(Player)e.getWhoClicked());
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			return;
		}
		
		if (mi == null) {
			if (view.getMenu().isStealable()) {
				e.setCancelled(false);
			}
			return;
		}
		for (GUIAction act : new ArrayList<>(mi.getActions())) {
			if (act.valid(e.getClick())) {
				act.act(e);
			}
		}
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (GUIMenu.isSecured(e.getItemDrop().getItemStack())) {
			e.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onClose(final InventoryCloseEvent e) {
		final Inventory inv = e.getInventory();
		if (isCustom(inv)) {
			final GUIView view = (GUIView) inv.getHolder();
			if (view.getCloseaction() != null) {
				view.getCloseaction().act(e);
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					final Player p = (Player) e.getPlayer();
					if (!p.isOnline()) {
						return;
					}
					final ItemStack[] inven = p.getInventory().getContents();
					if (inven.length < 1) {
						return;
					}
					ItemStack[] array;
					for (int length = (array = inven).length, j = 0; j < length; ++j) {
						final ItemStack i = array[j];
						if (!p.isOnline()) {
							return;
						}
						if (i != null && GUIMenu.isSecured(i)) {
							e.getPlayer().getInventory().remove(i);
						}
					}
				}
			}.runTask(view.getMenu().getHandler());
		}
	}
}
