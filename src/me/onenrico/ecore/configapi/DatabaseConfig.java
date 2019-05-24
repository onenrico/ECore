package me.onenrico.ecore.configapi;

import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.database.DataModule.DatabaseType;

public class DatabaseConfig extends EYaml {
    // DATABASE.yml FORMAT
	//	database:
	//	  type: 'sqlite'
	//	  hostname: 'unknown'
	//	  port: '3306'
	//	  database: 'database'
	//	  user: 'user'
	//	  password: 'password'
	private DatabaseType type;
	private String hostname;
	private String port;
	private String database;
	private String user;
	private String password;

			  
	public DatabaseConfig(Plugin handler, String path) {
		super(handler, path);
		setup();
	}


	@Override
	public void setup() {
		type = DatabaseType.getType(getStr("database.type", "sqlite"));
		hostname = getStr("database.hostname","unknown");
		port = getStr("database.port","3306");
		database = getStr("database.database","database");
		user = getStr("database.user","user");
		password = getStr("database.password","password");
	}


	public DatabaseType getType() {
		return type;
	}


	public String getHostname() {
		return hostname;
	}


	public String getPort() {
		return port;
	}


	public String getDatabase() {
		return database;
	}


	public String getUser() {
		return user;
	}


	public String getPassword() {
		return password;
	}
}
