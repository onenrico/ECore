package me.onenrico.ecore.regionapi;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.onenrico.ecore.messageapi.MessageUT;

public class RegionLegacy extends RegionModule{

	private WorldGuardPlugin worldguard;
	
	public RegionLegacy(Plugin handler, Plugin worldguard) {
		super(handler, worldguard);
		this.worldguard = (WorldGuardPlugin) worldguard;
	}

	@Override
	public Object registerFlag(String flag, boolean def) {
		final FlagRegistry registry = worldguard.getFlagRegistry();
		StateFlag result = new StateFlag(flag,true);
		try {
			registry.register(result);
		} catch (Exception e) {
			MessageUT.cmsg("WorldGuard Flag Registered Already...");
			result = (StateFlag) registry.get(flag);
			enabled = result != null;
		}
		return result;
	}

	@Override
	public boolean canUse(Object flagraw, Player p) {
		StateFlag flag  = (StateFlag)flagraw;
		Location loc = p.getLocation();
		RegionManager manager = this.worldguard.getRegionManager(p.getWorld());
		final Vector v = new Vector().setX(loc.getX()).setY(loc.getY()).setZ(loc.getZ());
		ApplicableRegionSet set = manager.getApplicableRegions(v);
		if (set.queryState(null, new StateFlag[] { flag }) == StateFlag.State.DENY) {
			return false;
		}
		return true;
	}

}
