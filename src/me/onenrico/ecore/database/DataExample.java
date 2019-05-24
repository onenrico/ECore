package me.onenrico.ecore.database;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.ecore.messageapi.MessageUT;

public class DataExample extends DataManagerBase{
	
	private ETable table_one;
	
	public DataExample(Plugin handler) {
		super(handler);
		setupTable();
	}
	
	public void setupTable() {
		table_one = new ETable(handler, "ONE", EObject.class);
		table_one.setPrimary("Identifier");
		table_one.addColumn("Identifier", "char(36)");
		table_one.addColumn("Autotake", "tinyint(1)");
		final long beginning = System.currentTimeMillis();
		ETable.create(handler, new BukkitRunnable() {
			@Override
			public void run() {
				final long after = System.currentTimeMillis();
				final double diff = (after - beginning) / 1000L;
				MessageUT.cmsg("Database successfuly load in " + diff + "s");
				MessageUT.cmsg("Total Data:");
				for (final ETable e : ETable.loaded.get(handler)) {
					MessageUT.cmsg(String.valueOf(e.getName()) + ": " + e.getLoadedData().size() + " data");
				}
			}
		}, table_one);
	}

}
