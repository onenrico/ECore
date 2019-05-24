package me.onenrico.ecore.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.messageapi.MessageUT;

public abstract class Database {
	public Connection connection;
	protected Plugin handler;

	public abstract Connection getSQLConnection();

	public abstract void load(Plugin handler);

	public void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ex) {
			MessageUT.cmsg("F: " + ex);
		}
	}
}
