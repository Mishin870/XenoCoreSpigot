package com.mishin870.core.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerUtils {
	public static String title(Player player) {
		return ChatColor.GREEN + player.getDisplayName() + ChatColor.WHITE;
	}
}
