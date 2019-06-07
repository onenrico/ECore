package me.onenrico.ecore.mainapi;

import org.bukkit.plugin.java.JavaPlugin;

import me.onenrico.ecore.guiapi.MenuListener;
import me.onenrico.ecore.guiapi.MenuLiveUpdate;

public class APICore extends JavaPlugin {
	static APICore instance;

	public static APICore getThis2() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		MinecraftVersion.getVersion();
		MenuLiveUpdate.startTimer();
		getServer().getPluginManager().registerEvents(new MenuListener(), this);
	}

}
