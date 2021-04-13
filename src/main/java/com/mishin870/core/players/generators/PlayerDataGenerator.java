package com.mishin870.core.players.generators;

import com.mishin870.core.players.PlayerData;
import com.mishin870.core.players.Role;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerDataGenerator<R extends Role, D extends PlayerData<R>> {
	D generate(Player player);
}
