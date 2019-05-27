package me.onenrico.ecore.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.database.sql.Database;
import me.onenrico.ecore.mainapi.Module;
import me.onenrico.ecore.messageapi.MessageUT;

public abstract class DataModule extends Module{
	private static HashMap<Plugin, DataModule> cache = new HashMap<>();

	public static DataModule request(Plugin handler) {
		return request(handler,null);
	}
	public static DataModule request(Plugin handler, DataModule manager) {
		if(manager == null) {
			if(cache.containsKey(handler)) {
				return cache.get(handler);
			}
			return null;
		}
		if(!cache.containsKey(handler)) {
			return cache.put(handler, manager);
		}
		return cache.get(handler);
	}

	public static void unload(Plugin handler) {
		DataModule module = request(handler);
		cache.remove(handler);
		if(module == null) {
			MessageUT.cmsg("Database Module is NULL");
			return;
		}
		Connection con = module.getConnection();
		try {
			if(con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public DataModule(Plugin handler) {
		super(handler);
		cache.put(handler, this);
		enabled = true;
	}

	public abstract Connection getConnection();
	public abstract Database getDb();
	public abstract DatabaseType getDbtype();
	public abstract void setup();
	public abstract void save(EObject eo);
	public abstract void delete(EObject eo);
	public abstract void store(EObject eo);


	public enum DatabaseType {
		SQLITE("SQLITE", 0, "SQLite"), MYSQL("MYSQL", 1, "MySQL"), YML("YML", 2, "yml");

		private String alias;

		private DatabaseType(final String s, final int n, final String alias) {
			this.alias = alias;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static DatabaseType getType(final String type) {
			DatabaseType[] values;
			for (int length = (values = values()).length, i = 0; i < length; ++i) {
				final DatabaseType dt = values[i];
				if (dt.toString().equalsIgnoreCase(type)) {
					return dt;
				}
			}
			return DatabaseType.SQLITE;
		}
	}
}
