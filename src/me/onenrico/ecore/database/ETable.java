package me.onenrico.ecore.database;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.ecore.configapi.ConfigModule;
import me.onenrico.ecore.database.DataModule.DatabaseType;
import me.onenrico.ecore.messageapi.MessageUT;
import me.onenrico.ecore.utilsapi.Pair;

public class ETable {
	private Plugin handler;
	public static HashMap<Plugin, Set<ETable>> loaded = new HashMap<>();
	private Class<? extends EObject> object;
	private String name;
	private HashMap<String, String> columns;
	private HashMap<String, HashMap<String, String>> loadedValue;
	private Set<EObject> loadedObject;
	private HashMap<UUID, Set<EObject>> ownedObject;
	private String primary;

	public ETable(Plugin handler, final String name, final Class<? extends EObject> container) {
		columns = new HashMap<>();
		loadedValue = new HashMap<>();
		loadedObject = new HashSet<>();
		ownedObject = new HashMap<>();
		primary = "";
		this.name = name;
		object = container;
		this.handler = handler;
	}

	public ETable addColumn(final String data, final String type) {
		columns.put(data, type);
		return this;
	}

	public ETable removeColumn(final String data, final String type) {
		columns.remove(data);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public HashMap<String, String> getColumns() {
		return columns;
	}

	public void setColumns(final HashMap<String, String> columns) {
		this.columns = columns;
	}

	public Set<EObject> getLoadedData() {
		return loadedObject;
	}

	public void setLoadedData(final Set<EObject> loadedData) {
		loadedObject = loadedData;
	}

	public HashMap<String, HashMap<String, String>> getLoadedValue() {
		return loadedValue;
	}

	public void setLoadedValue(final HashMap<String, HashMap<String, String>> loadedValue) {
		this.loadedValue = loadedValue;
	}

	public String getValue(final String identifier, final String column) {
		final HashMap<String, String> value = loadedValue.getOrDefault(identifier, null);
		if (value != null) {
			return value.getOrDefault(column, "");
		}
		return "";
	}

	public Set<EObject> getOwned(final UUID owner) {
		return ownedObject.getOrDefault(owner, new HashSet<EObject>());
	}

	public void addOwned(final UUID owner, final EObject obj) {
		final Set<EObject> objs = getOwned(owner);
		objs.add(obj);
		ownedObject.put(owner, objs);
	}

	public void removeOwned(final UUID owner, final EObject obj) {
		final Set<EObject> objs = getOwned(owner);
		objs.remove(obj);
		ownedObject.put(owner, objs);
	}

	public Boolean exist(final String identifier) {
		if (loadedValue.getOrDefault(identifier, null) != null) {
			return true;
		}
		return false;
	}

	public String getPrimary() {
		return primary;
	}

	public ETable setPrimary(final String primary) {
		this.primary = primary;
		return this;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static void create(final Plugin handler, final BukkitRunnable callback,final ETable... tables) {
		for (final ETable et : tables) {
			new BukkitRunnable() {
				@Override
				public void run() {
					et.create(new BukkitRunnable() {
						@Override
						public void run() {
							Set<ETable> loaded = ETable.loaded.getOrDefault(handler, new HashSet<>());
							loaded.add(et);
							ETable.loaded.put(handler, loaded);
							if (loaded.size() == tables.length && callback != null) {
								callback.run();
							}
						}
					});
				}
			}.runTaskAsynchronously(handler);
		}
	}

	public void create(final BukkitRunnable callback) {
		DataModule dm = DataModule.request(handler);
		DatabaseType db = dm.getDbtype();
		if (!db.equals(DatabaseType.YML)) {
			final String tablesql = SQLop.createTable(getName(), getColumns(), getPrimary());
			if (db.equals(DatabaseType.MYSQL)) {
				SQLop.executeUpdate(dm.getConnection(),
						String.valueOf(tablesql.substring(0, tablesql.length() - 1)) + " CHARACTER SET = utf8;");
			} else {
				SQLop.executeUpdate(dm.getConnection(), tablesql);
			}
			for (final String column : getColumns().keySet()) {
				if (!SQLop.columnExist(dm.getConnection(), getName(), column)) {
					SQLop.addColumn(dm.getConnection(), getName(), column, getColumns().get(column));
				}
			}
			this.loadSQL(callback);
		} else {
			this.loadYML(callback);
		}
	}

	public void refresh(final UUID player, final BukkitRunnable callback, final Boolean mysql) {
		DataModule dm = DataModule.request(handler);
		DatabaseType db = dm.getDbtype();
		if (!db.equals(DataModule.DatabaseType.YML)) {
			this.loadSQL(player, callback);
		} else {
			this.loadYML(player, callback);
		}
	}

	public void loadYML(final BukkitRunnable callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final FileConfiguration cp = ConfigModule.request(handler).getDatabaseConfig().getConfig();
				ConfigurationSection cs = cp.getConfigurationSection(name);
				if (cs == null) {
					cs = cp.createSection(name);
				}
				for (final String identifier : cs.getKeys(false)) {
					final String pref = String.valueOf(identifier) + ".";
					final HashMap<String, String> value = new HashMap<>();
					for (final String column : columns.keySet()) {
						value.put(column, cs.getString(String.valueOf(pref) + column));
					}
					value.put(primary, identifier);
					loadedValue.put(value.get(primary), value);
					EObject obj;
					try {
						obj = object.getConstructor(Plugin.class,String.class).newInstance(handler,value.remove(primary));
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException ex2) {
						MessageUT.cmsg("Cannot create " + object.getSimpleName() + " Exception Occurred");
						continue;
					}
					loadedObject.add(obj);
					ETable.this.addOwned(obj.getOwner(), obj);
				}
				if (callback != null) {
					callback.run();
				}
			}
		}.runTask(handler);
	}

	public void loadSQL(final BukkitRunnable callback) {
		DataModule dm = DataModule.request(handler);
		new BukkitRunnable() {
			PreparedStatement ps = null;
			ResultSet rs = null;

			@Override
			public void run() {
				try {
					final String sql = SQLop.select(ETable.this.getName(), "*");
					final Pair<PreparedStatement, ResultSet> pair = SQLop.executeQuery(dm.getConnection(), sql);
					ps = pair.getLeft();
					rs = pair.getRight();
					while (rs.next()) {
						final HashMap<String, String> value = new HashMap<>();
						for (final String column : columns.keySet()) {
							value.put(column, rs.getString(column));
						}
						loadedValue.put(value.get(primary), value);
						EObject obj;
						try {
							obj = object.getConstructor(Plugin.class,String.class).
									newInstance(handler,value.remove(primary));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException ex3) {
							ex3.printStackTrace();
							continue;
						}
						if (obj.identifier == null) {
							loadedValue.remove(value.get(primary));
						} else {
							loadedObject.add(obj);
							ETable.this.addOwned(obj.getOwner(), obj);
						}
					}
					if (callback != null) {
						callback.run();
					}
				} catch (SQLException ex) {
					Bukkit.getLogger().info(ex.getMessage());
					return;
				} finally {
					ETable.this.close(ps, rs);
				}
				ETable.this.close(ps, rs);
			}
		}.runTask(handler);
	}

	public void loadSQL(final UUID player, final BukkitRunnable callback) {
		DataModule dm = DataModule.request(handler);
		new BukkitRunnable() {
			PreparedStatement ps = null;
			ResultSet rs = null;

			@Override
			public void run() {
				try {
					final HashMap<String, Object> condition = new HashMap<>();
					condition.put("Owner", player.toString());
					final String sql = SQLop.select(ETable.this.getName(), "*", condition);
					final Pair<PreparedStatement, ResultSet> pair = SQLop.executeQuery(dm.getConnection(), sql);
					ps = pair.getLeft();
					rs = pair.getRight();
					while (rs.next()) {
						final HashMap<String, String> value = new HashMap<>();
						for (final String column : columns.keySet()) {
							value.put(column, rs.getString(column));
						}
						loadedValue.put(value.remove(primary), value);
					}
					if (callback != null) {
						callback.run();
					}
				} catch (SQLException ex) {
					Bukkit.getLogger().info(ex.getMessage());
					return;
				} finally {
					ETable.this.close(ps, rs);
				}
				ETable.this.close(ps, rs);
			}
		}.runTask(handler);
	}

	public void loadYML(final UUID player, final BukkitRunnable callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final FileConfiguration cp = ConfigModule.request(handler).getDatabaseConfig().getConfig();
				ConfigurationSection cs = cp.getConfigurationSection(name);
				if (cs == null) {
					cs = cp.createSection(name);
				}
				for (final String identifier : cs.getKeys(false)) {
					if (!identifier.contains(player.toString())) {
						continue;
					}
					final String pref = String.valueOf(identifier) + ".";
					final HashMap<String, String> value = new HashMap<>();
					for (final String column : columns.keySet()) {
						value.put(column, cs.getString(String.valueOf(pref) + column));
					}
					value.put(primary, identifier);
					loadedValue.put(value.remove(primary), value);
				}
				if (callback != null) {
					callback.run();
				}
			}
		}.runTask(handler);
	}

	protected void close(PreparedStatement ps, ResultSet rs) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ps = null;
			}
			ps = null;
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			} finally {
				rs = null;
			}
			rs = null;
		}
	}

	public HashMap<UUID, Set<EObject>> getOwnedObject() {
		return ownedObject;
	}

	public Plugin getHandler() {
		return handler;
	}
}
