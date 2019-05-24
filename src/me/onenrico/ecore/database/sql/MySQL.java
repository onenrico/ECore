package me.onenrico.ecore.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.messageapi.MessageUT;

public class MySQL extends Database {
	public static String dbname;
	public String hostname;
	public String port;
	public String database;
	public String user;
	public String password;

	public MySQL(final String hostname, final String port, final String database, final String user,
			final String password) {
		this.hostname = "";
		this.port = "";
		this.database = "";
		this.user = "";
		this.password = "";
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
	}

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
			try {
				Class.forName("com.mysql.jdbc.Driver");
				try {
					connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database
							+ "?useUnicode=true&characterEncoding=UTF8", user, password);
				} catch (Exception ex2) {
					connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database
							+ "?useUnicode=true&&characterEncoding=UTF8", user, password);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return connection;
		} catch (SQLException ex) {
			MessageUT.cmsg("G: MySQL exception on initialize");
			MessageUT.cmsg("Ex: " + ex);
			return null;
		}
	}
}
