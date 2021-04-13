package com.mishin870.core.players;

import org.bukkit.entity.Player;

public abstract class PlayerData<R extends Role> {
	public final Player player;
	public R role;
	
	public PlayerData(Player player) {
		this.player = player;
	}
}
