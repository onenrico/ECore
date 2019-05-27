package me.onenrico.ecore.regionapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.mainapi.Module;

public abstract class RegionModule extends Module {
	protected Plugin worldguard;
	
	public RegionModule(Plugin handler, Plugin worldguard) {
		super(handler);
		this.worldguard = worldguard;
		enabled = true;
	}
	
	public abstract Object registerFlag(final String flag, final boolean def);

	public abstract boolean canUse(final Object flag, final Player p);
}
