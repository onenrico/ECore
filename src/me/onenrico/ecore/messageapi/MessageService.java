package me.onenrico.ecore.messageapi;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.configapi.ConfigModule;
import me.onenrico.ecore.managerapi.ActionBarManager;
import me.onenrico.ecore.managerapi.PlaceholderManager;
import me.onenrico.ecore.managerapi.SoundManager;
import me.onenrico.ecore.managerapi.TitleManager;
import me.onenrico.ecore.utilsapi.JsonUT;
import me.onenrico.ecore.utilsapi.StringUT;

public class MessageService extends MessageModule {

	public MessageService(Plugin handler) {
		super(handler);
	}

	@Override
	public void send(Player player, String msg) {
		if (player == null || !player.isOnline()) {
			return;
		}
		String prefix = "";
		String main = "{prefix}";
		if(msg != null)
		msg = StringUT.t(msg);
		if (msg.contains(main)) {
			String pref = ConfigModule.request(handler).getLocaleConfig(null).getJsonpluginPrefix();
			msg = msg.replace("{prefix}", pref);
		}
		main = "<console>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			console(msg);
			return;
		}
		main = "<sound>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			SoundManager.playSound(player, msg.toUpperCase());
			return;
		}
		main = "<title>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			main = "<subtitle>";
			if (msg.contains(main)) {
				TitleManager.sendFullTitle(player, 0, 60, 20, msg.split(main)[0], msg.split(main)[1]);
				msg = StringUT.remove(msg, main);
			} else {
				TitleManager.sendTitle(player, 0, 60, 20, msg);
			}
			return;
		}
		main = "<subtitle>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			TitleManager.sendSubtitle(player, 0, 60, 20, msg);
			return;
		}
		main = "<actionbar>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			ActionBarManager.sendActionBar(player, msg);
			return;
		}
		main = "<action>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			ActionBarManager.sendActionBar(player, msg);
			return;
		}
		main = "<center>";
		if (msg.contains(main)) {
			msg = StringUT.remove(msg, main);
			msg = String.valueOf(prefix) + msg;
			if (msg.contains("<json>")) {
				pmessage(player, String.valueOf(main) + msg);
				return;
			}
			msg = StringUT.centered(msg);
			pmessage(player, msg);
		} else {
			pmessage(player, String.valueOf(prefix) + msg);
		}
	}

	public static void pmessage(final Player p, final String msg) {
		if (msg.contains("<json>")) {
			JsonUT.send(p, StringUT.t(msg));
		} else {
			p.sendMessage(StringUT.t(msg));
		}
	}

	@Override
	public void send(Player player, List<String> msgs) {
		for (String msg : msgs) {
			send(player, msg);
		}
	}

	@Override
	public void send(Player player, String msg, PlaceholderManager pm) {
		if (pm == null) {
			pm = new PlaceholderManager();
		}
		msg = pm.process(msg);
		send(player, msg);
	}

	@Override
	public void send(Player player, List<String> msgs, PlaceholderManager pm) {
		if (pm == null) {
			pm = new PlaceholderManager();
		}
		for (String msg : msgs) {
			msg = pm.process(msg);
			send(player, msg);
		}
	}

	@Override
	public void console(String msg) {
		Bukkit.getConsoleSender().sendMessage(StringUT.t(msg));
	}

	@Override
	public void console(String msg, PlaceholderManager pm) {
		if (pm == null) {
			pm = new PlaceholderManager();
		}
		msg = pm.process(msg);
		console(msg);
	}

	@Override
	public void console(List<String> msgs) {
		for (String msg : msgs) {
			Bukkit.getConsoleSender().sendMessage(StringUT.t(msg));
		}
	}

	@Override
	public void console(List<String> msgs, PlaceholderManager pm) {
		if (pm == null) {
			pm = new PlaceholderManager();
		}
		for (String msg : msgs) {
			msg = pm.process(msg);
			console(msg);
		}
	}
}
