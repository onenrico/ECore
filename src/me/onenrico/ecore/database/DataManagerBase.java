package me.onenrico.ecore.database;

import java.sql.Connection;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.ecore.configapi.ConfigModule;
import me.onenrico.ecore.configapi.DatabaseConfig;
import me.onenrico.ecore.database.sql.Database;
import me.onenrico.ecore.database.sql.MySQL;
import me.onenrico.ecore.database.sql.SQLite;
import me.onenrico.ecore.messageapi.MessageUT;

public class DataManagerBase extends DataModule{

	public Database db;
	public DatabaseType databasetype;

	public DataManagerBase(Plugin handler) {
		super(handler);
		setup();
	}
	
	@Override
	public Connection getConnection() {
		return db.getSQLConnection();
	}
	
	@Override
	public void setup() {
		DatabaseConfig dc = ConfigModule.request(handler).getDatabaseConfig();
		databasetype = dc.getType();
		switch (databasetype) {
		case MYSQL: {
			final String hostname = dc.getHostname();
			if (hostname.equalsIgnoreCase("unknown")) {
				MessageUT.cmsg("["+handler.getName()+"] You are using MySQL but credential not set correctly on database.yml");
				handler.getPluginLoader().disablePlugin(handler);
				return;
			}
			final String port = dc.getPort();
			final String database = dc.getDatabase();
			final String username = dc.getUser();
			final String password = dc.getPassword();
			db = new MySQL(hostname, port, database, username, password);
			break;
		}
		case SQLITE: {
			db = new SQLite();
			break;
		}
		default:
			break;
		}
		MessageUT.cmsg("Connecting to " + databasetype + " Database...");
		if (db != null) {
			db.load(handler);
		}
		MessageUT.cmsg("Connected to " + databasetype);
	}
	@Override
	public void store(EObject data) {
		data.table.getLoadedData().add(data);
		data.table.addOwned(data.getOwner(), data);
	}
	@Override
	public synchronized void save(EObject data) {
		final HashMap<String, Object> columns = data.getValues();
		if (!databasetype.equals(DatabaseType.YML)) {
			SQLop.executeUpdate(getConnection(),SQLop.insert(data.getTable().getName(), columns));
		} else {
			final FileConfiguration fc = ConfigModule.request(handler).getDatabaseConfig().getConfig();
			final String name = data.getIdentifier();
			final String p = String.valueOf(data.getTable().getName()) + "." + name + ".";
			for (final String key : data.getTable().getColumns().keySet()) {
				if (name.equals(columns.get(key))) {
					continue;
				}
				fc.set(String.valueOf(p) + key, columns.get(key));
			}

			ConfigModule.request(handler).getDatabaseConfig().save();
		}
	}
	@Override
	public synchronized void delete(EObject data) {
		data.table.getLoadedData().remove(data);
		data.table.getLoadedValue().remove(data.getIdentifier());
		data.table.removeOwned(data.getOwner(), data);
		if (!databasetype.equals(DatabaseType.YML)) {
			final HashMap<String, Object> condition = new HashMap<>();
			condition.put(data.table.getPrimary(), data.getIdentifier());
			new BukkitRunnable() {
				@Override
				public void run() {
					SQLop.executeUpdate(getConnection(),SQLop.delete(data.table.getName(), condition));
				}
			}.runTask(handler);
		} else {
			final FileConfiguration fc = ConfigModule.request(handler).getDatabaseConfig().getConfig();
			final String name = data.getIdentifier();
			final String p = String.valueOf(data.table.getName()) + "." + name;
			fc.set(p, null);
			ConfigModule.request(handler).getDatabaseConfig().save();
		}
	}

	@Override
	public Database getDb() {
		return db;
	}

	@Override
	public DatabaseType getDbtype() {
		return databasetype;
	}



}
