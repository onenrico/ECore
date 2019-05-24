package me.onenrico.ecore.messageapi;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.mainapi.Module;
import me.onenrico.ecore.managerapi.PlaceholderManager;

public abstract class MessageModule extends Module {

	public MessageModule(Plugin handler) {
		super(handler);
		enabled = true;
	}

	public abstract void send(Player player, List<String> msg);

	public abstract void send(Player player, String msg);

	public abstract void send(Player player, List<String> msg, PlaceholderManager pm);

	public abstract void send(Player player, String msg, PlaceholderManager pm);

	public abstract void console(List<String> msg);

	public abstract void console(String msg);

	public abstract void console(List<String> msg, PlaceholderManager pm);

	public abstract void console(String msg, PlaceholderManager pm);
}
