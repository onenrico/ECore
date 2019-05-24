package me.onenrico.ecore.economyapi;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class EcoPoint extends EconomyModule {

	private PlayerPoints economy;

	public EcoPoint(Plugin handler, Plugin economy) {
		super(handler,economy);
		this.economy = (PlayerPoints) economy;
		enabled = true;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return getRawBal(player) >= amount;
	}

	@Override
	public double getRawBal(OfflinePlayer player) {
		return economy.getAPI().look(player.getUniqueId());
	}

	@Override
	public String getBal(OfflinePlayer player) {
		return "" + getRawBal(player);
	}

	@Override
	public void addBal(OfflinePlayer player, double amount) {
		economy.getAPI().give(player.getUniqueId(), (int) amount);
	}

	@Override
	public void subtractBal(OfflinePlayer player, double amount) {
		economy.getAPI().take(player.getUniqueId(), (int) amount);
	}

}
