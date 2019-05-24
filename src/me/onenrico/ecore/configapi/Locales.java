package me.onenrico.ecore.configapi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.text.StrLookup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.managerapi.PlaceholderManager;

public class Locales extends EYaml {
	// LANG.yml FORMAT
	//# Option List:
	//# <center> : Center The Message on that line
	//# <np> : Disable prefix on that line
	//# <title> : send title to player
	//# <subtitle> : send subtitle to player
	//# <actionbar> : send actionbar to player
	//# <sound> : send sound to player
	//# <console> : send message to console
	//# use <title>Text<subtitle>text : to send title and subtitle at same time
	//# 
	//# Use curly bracket { } to use placeholder in Text Line
	//# Example: {heart} will be replace by defined placeholder
	//# Don't forget to encode as UTF-8 
	//# Below is pre-made plaeholder , you can make as many as you want XD
	//pluginPrefix: "&8&l▎ &bNeoTP&fPlus &8&l▎ &7"
	//custom-placeholder:
	//  doneline: '&f&m+---------------------------------------------------+'
	//  errline: '&8&m+---------------------------------------------------+'
	//  plusline: '&7&m+---------------------------------------------------+'
	//  done: '&8&l[&f✔&8&l]'
	//  not: '&8&l[&c✘&8&l]'
	//  edit: '&8&l[&f✎&8&l]'
	//  peace: ✌
	//  heart: ❤
	//  cloud: ☁
	//  poison: ☠
	//  star: ★
	//  x: █
	//  x/: ▌
	//  x\: ▐
	//  rarrow: ►
	//  larrow: ◀
	//  diamond: ◇
	//  melody: ♫
	//  crown: ♚
	//  plane: ✈
	//  snowman: ☃
	//  snow: ❆
	//  sword: ⚔
	//  health: ✚
	//  checklist: ✔
	//  cross: ✘
	//  copyright: ©
	//  tm: ™
	//  empty: ''
	//random-placeholder:
	//  red:
	//  - "&c"
	//  - "&4"
	//  green:
	//  - "&a"
	//  - "&2"
	//  blue:
	//  - "&b"
	//  - "&3"
	//  white:
	//  - "&f"
	//  - "&7"
	//  yellow:
	//  - "&e"
	//  - "&6"
	//messages:
	//  message_key:
	//  - "<center>"
	//  - "<np><center>&a&lSold {plane}"
	//  - "<np><center>&e{sold} &7items have sold &r{plane}"
	//  - "<np><center>&7claim your money with &e/ya sold"
	//  - "<sound>ENTITY_VILLAGER_YES"
	public PlaceholderManager pm;
	public String locale;
	private String rawpluginPrefix;
	private String jsonpluginPrefix;
	private HashMap<String, List<String>> messages = new HashMap<>();

	public Locales(Plugin handler, String path, String locale) {
		super(handler, path.replace("lang", "lang_" + locale.toUpperCase()));
		this.locale = locale;
		setup();
	}

	@Override
	public void setup() {
		messages.clear();
		pm = new PlaceholderManager();
		jsonpluginPrefix = getStr("pluginPrefix", "&cNot Configured");
		rawpluginPrefix = jsonpluginPrefix.contains("<json>")
				? jsonpluginPrefix.replace("@CU:", "<ends>").split("<ends>")[0].split("<json>")[1]
				: jsonpluginPrefix;
		ConfigurationSection cs = getSection("custom-placeholder");
		if (cs != null) {
			for (String key : cs.getKeys(false)) {
				pm.add(key, getStr(cs.getName() + "." + key));
			}
		}
		cs = getSection("random-placeholder");
		if (cs != null) {
			Random rand = new Random();
			for (String key : cs.getKeys(false)) {
				List<String> values = getStrList(cs.getName() + "." + key);
				pm.add(key, new StrLookup() {
					@Override
					public String lookup(String arg0) {
						return values.get(rand.nextInt(values.size()));
					}
				});
			}
		}
		cs = getSection("messages");
		if (cs != null) {
			for (String key : cs.getKeys(false)) {
				messages.put(key, getStrList(cs.getName() + "." + key));
			}
		}
	}

	public List<String> getMessage(final String msg) {
		if (messages.get(msg) == null) {
			messages.put(msg, getStrList("messages." + msg, Arrays.asList(new String[] { "Not Set" })));
		}
		return pm.process(messages.get(msg));
	}

	public String getRawpluginPrefix() {
		return rawpluginPrefix;
	}

	public void setRawpluginPrefix(String rawpluginPrefix) {
		this.rawpluginPrefix = rawpluginPrefix;
	}

	public String getJsonpluginPrefix() {
		return jsonpluginPrefix;
	}

	public void setJsonpluginPrefix(String jsonpluginPrefix) {
		this.jsonpluginPrefix = jsonpluginPrefix;
	}

}
