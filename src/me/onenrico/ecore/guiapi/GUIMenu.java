package me.onenrico.ecore.guiapi;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.mainapi.MinecraftVersion;
import me.onenrico.ecore.managerapi.PlaceholderManager;
import me.onenrico.ecore.nbtapi.NBTItem;

public class GUIMenu {
	protected Plugin handler;
	protected UUID menuuuid;
	protected int row;
	protected int stacksize;
	protected boolean storeable;
	protected boolean stealable;
	protected OpenAnimation animation;
	protected String title;
	protected String alias;
	protected HashMap<String, MenuItemContainer> configitems = new HashMap<>();
	protected HashMap<UUID, GUIView> views = new HashMap<>();
	
	public GUIView requestView(HumanEntity player,PlaceholderManager pm) {
		UUID uuid = player.getUniqueId();
		if(views.containsKey(uuid)) {
			return views.get(uuid);
		}
		return views.put(uuid, new GUIView(uuid,this,pm));
	}
	
	public void addConfigItems(String id, ItemStack item, int slot) {
		configitems.put(id, new MenuItemContainer(secure(item), slot));
	}

	protected ItemStack secure(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return item;
		}
		final NBTItem ni = new NBTItem(item);
		ni.setBoolean("nosteal", true);
		item = ni.getItem();
		return item;
	}

	public static boolean isSecured(final ItemStack item) {
		final NBTItem ni = new NBTItem(item);
		return ni.hasKey("nosteal") && ni.getBoolean("nosteal");
	}

	public GUIMenu(Plugin handler) {
		this.handler = handler;
		this.menuuuid = UUID.randomUUID();
		this.stacksize = 64;
		this.title = "Chest";
		this.alias = "";
		this.row = 6;
		this.stealable = false;
		this.storeable = false;
	}

	public GUIMenu(Plugin handler,final String name, final String title, final int row) {
		this.handler = handler;
		this.menuuuid = UUID.randomUUID();
		this.stacksize = 64;
		this.title = title;
		this.alias = name;
		this.row = row;
		this.stealable = false;
		this.storeable = false;
		if(!MinecraftVersion.getVersion().higher(MinecraftVersion.MC1_8_R3)) {
			if (this.title.length() > 32) {
				this.title = this.title.trim();
				if (this.title.length() > 32) {
					this.title = this.title.substring(0, 32);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		return menuuuid.hashCode();
	}

	public Plugin getHandler() {
		return handler;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if(!MinecraftVersion.getVersion().higher(MinecraftVersion.MC1_8_R3)) {
			if (this.title.length() > 32) {
				this.title = this.title.trim();
				if (this.title.length() > 32) {
					this.title = this.title.substring(0, 32);
				}
			}
		}
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getStacksize() {
		return stacksize;
	}

	public void setStacksize(int stacksize) {
		this.stacksize = stacksize;
	}

	public boolean isStoreable() {
		return storeable;
	}

	public void setStoreable(boolean storeable) {
		this.storeable = storeable;
	}

	public boolean isStealable() {
		return stealable;
	}

	public void setStealable(boolean stealable) {
		this.stealable = stealable;
	}

	public HashMap<String, MenuItemContainer> getConfigitems() {
		return configitems;
	}

	public OpenAnimation getAnimation() {
		return animation;
	}

	public void setAnimation(OpenAnimation animation) {
		this.animation = animation;
	}

	public UUID getMenuuuid() {
		return menuuuid;
	}

	public HashMap<UUID, GUIView> getViews() {
		return views;
	}

}
