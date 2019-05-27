package me.onenrico.ecore.configapi;

import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.mainapi.Module;

public abstract class ConfigModule extends Module {

	private static HashMap<Plugin, ConfigModule> cache = new HashMap<>();

	public static ConfigModule request(Plugin handler) {
		return cache.getOrDefault(handler, new EConfig(handler));
	}
	
	public static void unload(Plugin handler) {
		cache.remove(handler);
		EYaml.unload(handler);
	}
	
	public ConfigModule(Plugin handler) {
		super(handler);
		cache.put(handler, this);
		enabled = true;
	}

	public abstract Locales getLocaleConfig(String locale);

	public abstract GUIConfig getGUIConfig(String locale);
	
	public abstract DatabaseConfig getDatabaseConfig();
	
}
