package me.onenrico.ecore.regionapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.flags.StateFlag;

import me.onenrico.ecore.mainapi.Module;

public abstract class RegionModule extends Module {
	protected Plugin worldguard;
	
	public RegionModule(Plugin handler, Plugin worldguard) {
		super(handler);
		this.worldguard = worldguard;
		enabled = true;
	}
	
	public abstract StateFlag registerFlag(final String flag, final boolean def);

	public abstract boolean canUse(final StateFlag flag, final Player p);
}
