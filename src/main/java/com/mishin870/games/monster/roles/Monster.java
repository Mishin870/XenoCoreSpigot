package com.mishin870.games.monster.roles;

import com.mishin870.core.XenoCore;
import com.mishin870.core.players.EnhancedPlayersList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Monster extends MHRole {
	public static Monster instance = new Monster();
	
	private Monster() {
	}
	
	@Override
	public String getKey() {
		return "monster";
	}
	
	@Override
	public long skillCooldown() {
		return 20L;
	}
	
	@Override
	public void useSkill(MHPlayerData data, EnhancedPlayersList<MHRole, MHPlayerData> controller) {
		for (var human : controller.byRole(Human.instance)) {
			human.player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20,
					1, false, false, false));
		}
	}
	
	@Override
	public void addRoleItems(MHPlayerData data) {
		final var head = new ItemStack(Material.CARVED_PUMPKIN);
		final var meta = Objects.requireNonNull(head.getItemMeta());
		meta.setDisplayName(XenoCore.language.get(data.player, "game.monster.monster.head"));
		meta.addEnchant(Enchantment.PROTECTION_FIRE, 1, true);
		head.setItemMeta(meta);
		
		data.player.getInventory().setHelmet(head);
		data.player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1,
				false, false, false));
	}
}
