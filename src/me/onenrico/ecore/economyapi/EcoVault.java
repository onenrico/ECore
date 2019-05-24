package me.onenrico.ecore.economyapi;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EcoVault extends EconomyModule {

	private Economy economy;

	public EcoVault(Plugin handler, Plugin economy) {
		super(handler,economy);
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		this.economy = rsp.getProvider();
		enabled = this.economy != null;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return economy.has(player, amount);
	}

	@Override
	public double getRawBal(OfflinePlayer player) {
		return round(economy.getBalance(player), 2);
	}

	@Override
	public String getBal(OfflinePlayer player) {
		return economy.format(getRawBal(player));
	}

	@Override
	public void addBal(OfflinePlayer player, double amount) {
		economy.depositPlayer(player, amount);
	}

	@Override
	public void subtractBal(OfflinePlayer player, double amount) {
		economy.withdrawPlayer(player, amount);
	}

	private double round(final double value, final int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.DOWN);
		return bd.doubleValue();
	}
}
