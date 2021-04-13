package com.mishin870.games.monster.roles;

import com.mishin870.core.players.PlayerData;
import com.mishin870.games.monster.MonsterData;
import org.bukkit.entity.Player;

public class MHPlayerData extends PlayerData<MHRole> {
	public long cooldown = MonsterData.NO_COOLDOWN;
	public boolean dead = false;
	
	public MHPlayerData(Player player) {
		super(player);
	}
}
