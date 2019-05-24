package me.onenrico.ecore.messageapi;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.onenrico.ecore.mainapi.APICore;
import me.onenrico.ecore.managerapi.ActionBarManager;
import me.onenrico.ecore.managerapi.PlaceholderManager;
import me.onenrico.ecore.managerapi.SoundManager;
import me.onenrico.ecore.managerapi.TitleManager;
import me.onenrico.ecore.utilsapi.JsonUT;
import me.onenrico.ecore.utilsapi.StringUT;

public class MessageUT {
	static boolean debug = false;

	public static void plmessage(final Player p, final List<String> msg) {
		plmessage(p, msg, null);
	}

	public static void plmessage(final Player p, final List<String> msg, final PlaceholderManager pm) {
		for (final String m : msg) {
			plmessage(p, m, pm);
		}
	}

	public static void plmessage(final Player p, final String msg) {
		plmessage(p, msg, null);
	}

	public static void plmessage(final Player p, String msg, PlaceholderManager pm) {
		if (p == null || !p.isOnline()) {
			return;
		}
		String prefix = "&8[&b&lE&fCore&8]&f ";
		if (pm == null) {
			pm = new PlaceholderManager();
		}
		pm.add("player", p.getName());
		msg = pm.process(msg);
		String main = "<np>";
		if (msg.contains(main)) {
			prefix = "";
			msg = StringUT.remove(msg, main);
		}
		main = "<console>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			cmsg(msg);
			return;
		}
		main = "<sound>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			SoundManager.playSound(p, msg.toUpperCase());
			return;
		}
		main = "<title>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			main = "<subtitle>";
			if (msg.contains(main)) {
				TitleManager.sendFullTitle(p, 0, 60, 20, msg.split(main)[0], msg.split(main)[1]);
				msg = StringUT.remove(msg, main);
			} else {
				TitleManager.sendTitle(p, 0, 60, 20, msg);
			}
			return;
		}
		main = "<subtitle>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			TitleManager.sendSubtitle(p, 0, 60, 20, msg);
			return;
		}
		main = "<actionbar>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			ActionBarManager.sendActionBar(p, msg);
			return;
		}
		main = "<action>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			ActionBarManager.sendActionBar(p, msg);
			return;
		}
		main = "<center>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			msg = String.valueOf(prefix) + msg;
			if (msg.contains("<json>")) {
				pmessage(p, String.valueOf(main) + msg);
				return;
			}
			msg = StringUT.centered(msg);
			pmessage(p, msg);
		} else {
			pmessage(p, String.valueOf(prefix) + msg);
		}
	}

	public static void cmsg(final String msg) {
		Bukkit.getConsoleSender().sendMessage(StringUT.t("|" + APICore.getThis2().getName() + "> " + msg));
	}

	public static void cmsg(final List<String> msgs, final PlaceholderManager pm) {
		for (final String msg : msgs) {
			Bukkit.getConsoleSender()
					.sendMessage(StringUT.t("|" + APICore.getThis2().getName() + "> " + pm.process(msg)));
		}
	}

	public static void debug(final String msg) {
		if (MessageUT.debug) {
			Bukkit.getConsoleSender()
					.sendMessage(StringUT.t("&b&l|&dD]&f" + APICore.getThis2().getName() + "&7&l> &r" + msg));
		}
	}

	public static void pmessage(final Player p, final String msg) {
		if (msg.contains("<json>")) {
			JsonUT.send(p, StringUT.t(msg));
		} else {
			p.sendMessage(StringUT.t(msg));
		}
	}

	public static void jmessage(final Player p, final String msg) {
		JsonUT.send(p, StringUT.t(msg));
	}
}
