package me.onenrico.ecore.configapi;

import org.bukkit.plugin.Plugin;

public class EConfig extends ConfigModule {

	public EConfig(Plugin handler) {
		super(handler);
	}

	@Override
	public Locales getLocaleConfig(String locale) {
		for(EYaml conf : EYaml.getConfigs(handler)) {
			if(conf.path.startsWith("lang")) {
				return (Locales) conf;
			}
		}
		if(locale == null) {
			return null;
		}
		return new Locales(handler, "lang.yml", locale);
	}

	@Override
	public GUIConfig getGUIConfig(String locale) {
		for(EYaml conf : EYaml.getConfigs(handler)) {
			if(conf.path.startsWith("gui")) {
				return (GUIConfig) conf;
			}
		}
		if(locale == null) {
			return null;
		}
		return new GUIConfig(handler, "gui.yml", locale);
	}

	@Override
	public DatabaseConfig getDatabaseConfig() {
		for(EYaml conf : EYaml.getConfigs(handler)) {
			if(conf.path.startsWith("database")) {
				return (DatabaseConfig) conf;
			}
		}
		return new DatabaseConfig(handler, "database.yml");
	}

}
