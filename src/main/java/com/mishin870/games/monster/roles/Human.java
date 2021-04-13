package com.mishin870.games.monster.roles;

import com.mishin870.core.players.EnhancedPlayersList;
import com.mishin870.games.monster.MonsterData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Human extends MHRole {
	public static Human instance = new Human();
	
	private Human() {
	}
	
	@Override
	public String getKey() {
		return "human";
	}
	
	@Override
	public long skillCooldown() {
		return MonsterData.NO_COOLDOWN;
	}
	
	@Override
	public void useSkill(MHPlayerData data, EnhancedPlayersList<MHRole, MHPlayerData> controller) {
		for (var monster : controller.byRole(Monster.instance)) {
			monster.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 20,
					false, false, false));
		}
	}
}
