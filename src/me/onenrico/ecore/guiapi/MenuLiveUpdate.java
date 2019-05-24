package me.onenrico.ecore.guiapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.ecore.itemapi.ItemBuilder;
import me.onenrico.ecore.mainapi.APICore;
import me.onenrico.ecore.managerapi.PlaceholderManager;

public class MenuLiveUpdate {
	private static Set<GUIView> animated = new HashSet<>();

	public static void startTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (final GUIView view : new ArrayList<>(MenuLiveUpdate.animated)) {
					MenuLiveUpdate.refresh(view);
				}
			}
		}.runTaskTimer(APICore.getThis2(), 20L, 20L);
	}

	public static void addAnimated(final GUIView view) {
		MenuLiveUpdate.animated.add(view);
	}

	public static void removeAnimated(final GUIView view) {
		if (MenuLiveUpdate.animated.contains(view)) {
			MenuLiveUpdate.animated.remove(view);
		}
	}

	public static void refresh(final GUIView view) {
		if (view.getInventory().getViewers().isEmpty()) {
			removeAnimated(view);
			return;
		}
		for (final MenuItem mi : view.getMenuitems()) {
			refresh(view, mi);
		}
	}

	public static void refresh(final GUIView view, final MenuItem mi) {
		final PlaceholderManager pm = mi.getPm();
		if (pm != null) {
			if (mi.getSlot() > -1) {
				final ItemStack it = view.getInventory().getItem(mi.getSlot());
				if (it != null && !it.getType().equals(Material.AIR)) {
					ItemBuilder.changeDisplayName(it, pm.process(ItemBuilder.getName(mi.getItem())));
					ItemBuilder.changeLore(it, pm.process(ItemBuilder.getLore(mi.getItem())));
				}
			}
		}
	}
}
