package com.mishin870.core.arena;

import com.mishin870.core.XenoCore;
import org.bukkit.entity.Player;

public enum ArenaState {
	LOBBY("arena.state.lobby"),
	STARTING("arena.state.starting"),
	STARTED("arena.state.started");
	
	private final String key;
	
	ArenaState(String key) {
		this.key = key;
	}
	
	public String getTitle(Player player) {
		return XenoCore.language.get(player, key);
	}
}
