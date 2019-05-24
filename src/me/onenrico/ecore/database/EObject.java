package me.onenrico.ecore.database;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class EObject {
	private DataModule datamodule;
	protected Plugin handler;
	protected String identifier;
	protected UUID owner;
	protected ETable table;


	public EObject(final Plugin handler, final String identifier) {
		this.identifier = identifier;
		this.handler = handler;
		this.datamodule = DataModule.request(handler);
	}

	public abstract HashMap<String, Object> getValues();
	
	public String getValue(String column) {
		return table.getValue(identifier, column);
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public UUID getOwner() {
		return owner;
	}

	public ETable getTable() {
		return table;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public void setOwner(final UUID owner) {
		this.owner = owner;
	}

	public void setTable(final ETable table) {
		this.table = table;
	}

	public void save() {
		new BukkitRunnable() {
			@Override
			public void run() {
				datamodule.save(EObject.this);
			}
		}.runTaskAsynchronously(handler);
	}
	public void delete() {
		new BukkitRunnable() {
			@Override
			public void run() {
				datamodule.delete(EObject.this);
			}
		}.runTaskAsynchronously(handler);
	}
}
