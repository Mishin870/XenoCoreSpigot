package com.mishin870.core.game.config;

@FunctionalInterface
public interface ConfigRegistry {
	void register(String key, ConfigEntry<?> entry);
}
