package me.onenrico.ecore.configapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.utilsapi.StringUT;

public class EYaml {
	public Plugin handler;
	public String path = null;
	public File file = null;
	public File defaultfile = null;
	public FileConfiguration config = null;
	public FileConfiguration defaultconfig = null;


	private static HashMap<Plugin, Set<EYaml>> configs = new HashMap<>();
	public static Set<EYaml> getConfigs(Plugin handler){
		return configs.getOrDefault(handler, new HashSet<>());
	}

	public static void unload(Plugin handler){
		configs.remove(handler);
	}

	public static void reloadAll(Plugin handler) {
		for (EYaml ey : configs.get(handler)) {
			ey.reload();
			ey.setup();
		}
	}

	public EYaml(final Plugin handler, final String path) {
		this.handler = handler;
		this.path = path;
		Set<EYaml> configs = EYaml.configs.getOrDefault(handler, new HashSet<>());
		configs.add(this);
		EYaml.configs.put(handler, configs);
		file = new File(handler.getDataFolder(), path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		InputStream is = handler.getResource(path);
		if (is != null) {
			replaceDefault(is);
			if (!file.exists()) {
				handler.saveResource(path, false);
				reload();
				return;
			}
		}
		reload();

	}


	public void setup() {};
	public FileConfiguration getConfig() {
		return config;
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		file = new File(handler.getDataFolder(), path);
		config = YamlConfiguration.loadConfiguration(file);
	}

	public void replaceDefault(InputStream is) {
		try {
			defaultfile = new File(handler.getDataFolder(), path + ".temp");
			if (defaultfile.exists()) {
				defaultfile.delete();
				defaultfile.createNewFile();
			}
			Files.copy(is, defaultfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			defaultconfig = YamlConfiguration.loadConfiguration(defaultfile);
			defaultfile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ConfigurationSection getSection(final String path) {
		return config.getConfigurationSection(path);
	}

	public List<String> getStrList(final String path) {
		return config.getStringList(path);
	}

	public List<String> getStrList(final String path, List<String> def) {
		if (!config.getStringList(path).isEmpty()) {
			return config.getStringList(path);
		}
		if(defaultconfig != null) {
			if (!defaultconfig.getStringList(path).isEmpty()) {
				def = defaultconfig.getStringList(path);
			}
		}
		config.set(path, def);
		save();
		return def;
	}

	public String getStr(final String path) {
		return StringUT.t(config.getString(path));
	}

	public String getStr(final String path, String def) {
		def = StringUT.u(def);
		if (config.get(path) != null) {
			return StringUT.t(config.getString(path, def));
		}
		if(defaultconfig != null) {
			if (defaultconfig.get(path) != null) {
				def = defaultconfig.get(path).toString();
			}
		}
		config.set(path, def);
		save();
		return StringUT.t(def);
	}

	public int getInt(final String path) {
		return config.getInt(path);
	}

	public int getInt(final String path, int def) {
		if (config.get(path) != null) {
			return config.getInt(path, def);
		}
		config.set(path, def);
		save();
		return def;
	}

	public double getDouble(final String path) {
		return config.getDouble(path);
	}

	public double getDouble(final String path, double def) {
		if (config.get(path) != null) {
			return config.getDouble(path, def);
		}
		config.set(path, def);
		save();
		return def;
	}

	public Boolean getBool(final String path) {
		return config.getBoolean(path);
	}

	public Boolean getBool(final String path, final boolean def) {
		return this.getBool(path, def, null);
	}

	public Boolean getBool(final String path, boolean def, final Boolean seconddef) {
		if (config.get(path) != null) {
			return config.getBoolean(path, def);
		}
		if (seconddef != null) {
			config.set(path, seconddef);
			save();
			return seconddef;
		}
		config.set(path, def);
		save();
		return def;
	}
}