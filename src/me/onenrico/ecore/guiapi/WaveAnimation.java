package me.onenrico.ecore.guiapi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.ecore.enumsapi.EMaterial;

public class WaveAnimation extends OpenAnimation {
	public List<ItemStack> block_items;
	public List<ItemStack> wave_items;
	
	public WaveAnimation(List<ItemStack> block_items, List<ItemStack> wave_items) {
		this.block_items = block_items;
		this.wave_items = wave_items;
	}
	
	@Override
	public void open(final Runnable callback, final GUIView view, final Player p) {
		if (p.isOnline()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					final Inventory inv = view.getInventory();
					final Set<Integer> itemslots = new HashSet<>();

					// Cache all Menu Items before Start Animation
					// Turn them into block animation items
					for (final MenuItem mi : view.getMenuitems()) {
						if (mi.getItem().getType().equals(Material.AIR)) {
							continue;
						}
						if (mi.getSlot() > -1) {
							ItemStack item = null;
							final int crow = mi.getSlot() / 9 + 1;

							// If block items in config is less than inventory row
							if (block_items.size() < crow) {
								item = block_items.get(block_items.size() - 1).clone();
							} else {
								item = block_items.get(crow - 1).clone();
							}
							itemslots.add(mi.getSlot());
							inv.setItem(mi.getSlot(), item);
						}
					}

					// Open Inventory
					p.openInventory(inv);

					// Start The Animation
					new BukkitRunnable() {

						// Parameter to check wether animation is finished
						boolean finish = false;
						final int row = view.getMenu().getRow();
						int currentpos = -1;

						@Override
						public void run() {

							// cancel animation when there are no viewers
							if (!inv.getViewers().contains(p)) {
								cancel();
								return;
							}
							currentpos++;
							for (int r = 0; r < row; ++r) {
								// 9 * r is row offset position
								int rowoffset = 9 * r;
								int slot = currentpos + rowoffset - r;
								int prevslot = slot - 1;
								
								// Make Sure Slot Is On The Menu
								if (slot >= rowoffset && slot < rowoffset + 9) {
									if(itemslots.contains(prevslot)) {
										MenuItem mi = view.getMenuItem(prevslot);
										inv.setItem(prevslot, mi.getItem().clone());
									}else if(prevslot >= rowoffset){
										inv.setItem(prevslot, EMaterial.AIR.parseItem());
									}
									ItemStack item = null;
									// If wave items in config is less than inventory row
									if (wave_items.size() - 1 < r) {
										item = wave_items.get(wave_items.size() - 1).clone();
									} else {
										item = wave_items.get(r).clone();
									}
									inv.setItem(slot, item);
									finish = false;
								}
								else if(slot >= rowoffset + 9) {
									if(itemslots.contains(prevslot)) {
										MenuItem mi = view.getMenuItem(prevslot);
										inv.setItem(prevslot, mi.getItem().clone());
									}else {
										inv.setItem(prevslot, EMaterial.AIR.parseItem());
									}
									finish = true;
								}
							}
							if (finish) {
								cancel();
								if (callback != null) {
									callback.run();
								}
							}
						}
					}.runTaskTimer(view.getMenu().getHandler(), 0L, 1L);
				}
			}.runTask(view.getMenu().getHandler());
		}
	}
}
