package com.mishin870.core.game.config;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ConfigEntry<T> {
	public T value;
	
	public abstract void load(ConfigurationSection section, String key);
	public abstract void save(ConfigurationSection section, String key);
	public abstract void setByCommand(CommandSender sender, String text);
}
