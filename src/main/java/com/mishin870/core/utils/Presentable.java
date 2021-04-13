package com.mishin870.core.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Presentable {
	String getTitle(Player player);
	default List<String> getSubTitle(Player player) {
		return List.of();
	}
	default Material getMaterial() {
		return null;
	}
	default ItemStack getPresentation(Player player, Material suggestedMaterial) {
		final var material = getMaterial();
		final var title = getTitle(player);
		final var subTitle = getSubTitle(player);
		
		return InventoryUtils.createTitled(material == null ? suggestedMaterial : material, title, subTitle);
	}
}
