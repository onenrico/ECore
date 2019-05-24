package me.onenrico.ecore.mainapi;

import org.bukkit.plugin.java.JavaPlugin;

import me.onenrico.ecore.guiapi.MenuListener;

public class APICore extends JavaPlugin {
	static APICore instance;

	public static APICore getThis2() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		MinecraftVersion.getVersion();
		getServer().getPluginManager().registerEvents(new MenuListener(), this);

		if (getServer().getPluginManager().getPlugin("NeoMoreTPPlus") != null
				&& me.onenrico.neotpplus.main.Core.getThis() != null) {
			getLogger().info("NeoMoreTPPlus reloaded... !");
			me.onenrico.neotpplus.main.Core.integrate();
		}
	}
	
	
}
