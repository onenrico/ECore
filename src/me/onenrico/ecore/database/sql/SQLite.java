package me.onenrico.ecore.database.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.messageapi.MessageUT;

public class SQLite extends Database {
	public static String dbname = "database";
	
	@Override
	public void load(Plugin handler) {
		super.handler = handler;
		connection = getSQLConnection();
	}

	@Override
	public Connection getSQLConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		final File dataFolder = new File(handler.getDataFolder() + "/data/");
		final File dataFile = new File(handler.getDataFolder() + "/data/",
				String.valueOf(SQLite.dbname) + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.mkdir();
				dataFile.createNewFile();
			} catch (IOException e) {
				MessageUT.cmsg("File write error: " + SQLite.dbname + ".db");
			}
		}
		try {
			Class.forName("org.sqlite.JDBC");
			return connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
		} catch (SQLException ex) {
			MessageUT.cmsg("G: SQLite exception on initialize");
		} catch (ClassNotFoundException ex2) {
			MessageUT.cmsg("H: SQLite exception on initialize");
		}
		return null;
	}
}
