package me.onenrico.ecore.regionapi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.onenrico.ecore.messageapi.MessageUT;

public class RegionBeta extends RegionModule{

	
	public RegionBeta(Plugin handler, Plugin worldguard) {
		super(handler, worldguard);
	}

	@Override
	public Object registerFlag(String flag, boolean def) {
		final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
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
		RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer()
				.get(WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(loc.getWorld().getName()));
		final BlockVector3 vv = Vector3.toBlockPoint(loc.getX(), loc.getY(), loc.getZ());
		Method m;
		ApplicableRegionSet set = null;
		try {
			m = manager.getClass().getMethod("getApplicableRegions", com.sk89q.worldedit.math.BlockVector3.class);
			set = (ApplicableRegionSet)m.invoke(manager, vv);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		if (set.queryState(null, new StateFlag[] { flag }) == StateFlag.State.DENY) {
			return false;
		}
		return true;
	}

}
