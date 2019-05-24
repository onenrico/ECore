package me.onenrico.ecore.configapi;

import org.bukkit.plugin.Plugin;

public class EConfig extends ConfigModule {

	public EConfig(Plugin handler) {
		super(handler);
	}

	@Override
	public Locales getLocaleConfig() {
		for(EYaml conf : EYaml.getConfigs(handler)) {
			if(conf.path.startsWith("lang")) {
				return (Locales) conf;
			}
		}
		return new Locales(handler, "lang.yml", "EN");
	}

	@Override
	public GUIConfig getGUIConfig() {
		for(EYaml conf : EYaml.getConfigs(handler)) {
			if(conf.path.startsWith("gui")) {
				return (GUIConfig) conf;
			}
		}
		return new GUIConfig(handler, "gui.yml", "EN");
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
