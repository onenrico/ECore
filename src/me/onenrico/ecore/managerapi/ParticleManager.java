//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.ecore.managerapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import me.onenrico.ecore.messageapi.MessageUT;
import me.onenrico.ecore.utilsapi.ReflectionUT;

public class ParticleManager {
	private static Class<?> packetClass;
	private static Class<?> newEnum;
	private static Class<?> playerConnection;
	private static Class<?> Packet;
	private static Method getEnum;
	private static Method sendPacket;
	private static Constructor<?> packetConstructor;
	public static boolean first;
	public static boolean newv = false;

	static {
		ParticleManager.first = true;
	}

	public static void setup() {
		ParticleManager.first = false;
		try {
			ParticleManager.packetClass = ReflectionUT.getNMSClass("PacketPlayOutWorldParticles");
			ParticleManager.newEnum = ReflectionUT.getNMSClass("EnumParticle");
			ParticleManager.playerConnection = ReflectionUT.getNMSClass("PlayerConnection");
			ParticleManager.Packet = ReflectionUT.getNMSClass("Packet");
			ParticleManager.getEnum = ParticleManager.newEnum.getMethod("valueOf", String.class);
			ParticleManager.sendPacket = ParticleManager.playerConnection.getMethod("sendPacket",
					ParticleManager.Packet);
			ParticleManager.packetConstructor = ParticleManager.packetClass.getConstructor(ParticleManager.newEnum,
					Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE,
					Integer.TYPE, int[].class);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex2) {
			MessageUT.cmsg("Using new Particle Protocol");
			newv = true;
		}
	}

	public static void sendParticles(final Player player, final String particle, final Location loc,
			final float xOffset, final float yOffset, final float zOffset, final float data, final int amount) {
		if (ParticleManager.first) {
			setup();
		}
		final float x = (float) loc.getX();
		final float y = (float) loc.getY();
		final float z = (float) loc.getZ();
		if (!newv) {
			try {
				final Object enumParticle = ParticleManager.getEnum.invoke(ParticleManager.newEnum, particle);
				final Object packet = ParticleManager.packetConstructor.newInstance(enumParticle, true, x, y, z,
						xOffset, yOffset, zOffset, data, amount, null);
				if(player == null) {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(p.getWorld().equals(loc.getWorld())) {
							ParticleManager.sendPacket.invoke(ReflectionUT.getConnection(p), packet);
						}
					}
				}else {
					ParticleManager.sendPacket.invoke(ReflectionUT.getConnection(player), packet);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | NoSuchFieldException ex2) {
				ex2.printStackTrace();
			}
		} else {
			if(player == null) {
				loc.getWorld().spawnParticle(Particle.valueOf(particle), x, y, z, amount, xOffset, yOffset, zOffset, data, true);
			}else {
				player.spawnParticle(Particle.valueOf(particle), x, y, z, amount, xOffset, yOffset, zOffset, data, true);
			}
		}
	}
}
