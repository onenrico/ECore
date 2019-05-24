package me.onenrico.ecore.mainapi;

import org.bukkit.plugin.Plugin;

public class Module {
	public Plugin handler;
	protected boolean enabled = false;

	public Module(Plugin handler) {
		this.handler = handler;
	}
}
