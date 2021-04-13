package com.mishin870.core.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class InventoryUtils {
	public static ItemStack createTitled(Material material, String title) {
		return createTitled(material, title, List.of());
	}
	
	public static ItemStack createTitled(Material material, String title, List<String> subTitle) {
		final var item = new ItemStack(material);
		final var meta = Objects.requireNonNull(item.getItemMeta());
		meta.setDisplayName(title);
		meta.setLore(subTitle);
		item.setItemMeta(meta);
		return item;
	}
}
