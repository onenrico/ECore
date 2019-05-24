package me.onenrico.ecore.managerapi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.ecore.mainapi.APICore;
import me.onenrico.ecore.mainapi.MinecraftVersion;
import me.onenrico.ecore.utilsapi.ReflectionUT;

public class ActionBarManager {
	private static String nmsver;
	private static boolean useOldMethods;

	static {
		ActionBarManager.useOldMethods = false;
	}

	public static void sendActionBar(final Player player, final String message) {
		nmsver = ReflectionUT.VERSION;
		if (!MinecraftVersion.getVersion().higher(MinecraftVersion.MC1_9_R1)) {
			ActionBarManager.useOldMethods = true;
		}
		if (!player.isOnline()) {
			return;
		}
		try {
			final Class<?> craftPlayerClass = Class
					.forName("org.bukkit.craftbukkit." + ActionBarManager.nmsver + ".entity.CraftPlayer");
			final Object craftPlayer = craftPlayerClass.cast(player);
			final Class<?> packetPlayOutChatClass = Class
					.forName("net.minecraft.server." + ActionBarManager.nmsver + ".PacketPlayOutChat");
			final Class<?> packetClass = Class.forName("net.minecraft.server." + ActionBarManager.nmsver + ".Packet");
			Object packet;
			if (ActionBarManager.useOldMethods) {
				final Class<?> chatSerializerClass = Class
						.forName("net.minecraft.server." + ActionBarManager.nmsver + ".ChatSerializer");
				final Class<?> iChatBaseComponentClass = Class
						.forName("net.minecraft.server." + ActionBarManager.nmsver + ".IChatBaseComponent");
				final Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
				final Object cbc = iChatBaseComponentClass
						.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
				packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE).newInstance(cbc,
						(byte) 2);
			} else {
				final Class<?> chatComponentTextClass = Class
						.forName("net.minecraft.server." + ActionBarManager.nmsver + ".ChatComponentText");
				final Class<?> iChatBaseComponentClass = Class
						.forName("net.minecraft.server." + ActionBarManager.nmsver + ".IChatBaseComponent");
				try {
					final Class<?> chatMessageTypeClass = Class
							.forName("net.minecraft.server." + ActionBarManager.nmsver + ".ChatMessageType");
					final Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
					Object chatMessageType = null;
					Object[] array;
					for (int length = (array = chatMessageTypes).length, i = 0; i < length; ++i) {
						final Object obj = array[i];
						if (obj.toString().equals("GAME_INFO")) {
							chatMessageType = obj;
						}
					}
					final Object chatCompontentText = chatComponentTextClass.getConstructor(String.class)
							.newInstance(message);
					packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass)
							.newInstance(chatCompontentText, chatMessageType);
				} catch (ClassNotFoundException cnfe) {
					final Object chatCompontentText2 = chatComponentTextClass.getConstructor(String.class)
							.newInstance(message);
					packet = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, Byte.TYPE)
							.newInstance(chatCompontentText2, (byte) 2);
				}
			}
			final Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle", new Class[0]);
			final Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer, new Object[0]);
			final Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
			final Object playerConnection = playerConnectionField.get(craftPlayerHandle);
			final Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
			sendPacketMethod.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendActionBar(final Player player, final String message, int duration) {
		sendActionBar(player, message);
		if (duration >= 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					ActionBarManager.sendActionBar(player, "");
				}
			}.runTaskLater(APICore.getThis2(), duration + 1);
		}
		while (duration > 40) {
			duration -= 40;
			new BukkitRunnable() {
				@Override
				public void run() {
					ActionBarManager.sendActionBar(player, message);
				}
			}.runTaskLater(APICore.getThis2(), duration);
		}
	}

	public static void sendActionBarToAllPlayers(final String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(final String message, final int duration) {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message, duration);
		}
	}
}
