package me.onenrico.ecore.economyapi;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.mainapi.Module;

public abstract class EconomyModule extends Module {
	
	protected Plugin economy;
	public EconomyModule(Plugin handler, Plugin economy) {
		super(handler);
		this.economy = economy;
	}

	public abstract boolean has(final OfflinePlayer player, final double amount);

	public abstract double getRawBal(final OfflinePlayer player);

	public abstract String getBal(final OfflinePlayer player);

	public abstract void addBal(final OfflinePlayer player, final double amount);

	public abstract void subtractBal(final OfflinePlayer player, final double amount);
}
