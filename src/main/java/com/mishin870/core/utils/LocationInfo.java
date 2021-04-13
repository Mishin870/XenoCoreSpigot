package com.mishin870.core.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class LocationInfo implements Presentable {
	public final String key;
	public final String title;
	
	public LocationInfo(String key, String title) {
		this.key = key;
		this.title = title;
	}
	
	@Override
	public String getTitle(Player player) {
		return key;
	}
	
	@Override
	public List<String> getSubTitle(Player player) {
		return List.of(ChatColor.WHITE + title);
	}
}
