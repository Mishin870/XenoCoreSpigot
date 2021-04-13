package com.mishin870.core.game.config.impl;

import com.mishin870.core.game.config.ConfigEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class BoolConfigEntry extends ConfigEntry<Boolean> {
	@Override
	public void load(ConfigurationSection section, String key) {
		value = section.getBoolean(key);
	}
	
	@Override
	public void save(ConfigurationSection section, String key) {
		section.set(key, value);
	}
	
	@Override
	public void setByCommand(CommandSender sender, String text) {
		try {
			value = Boolean.parseBoolean(text);
		} catch (NumberFormatException e) {
			sender.sendMessage("Incorrect boolean format");
		}
	}
}
