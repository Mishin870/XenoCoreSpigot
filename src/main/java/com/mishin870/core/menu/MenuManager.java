package com.mishin870.core.menu;

import com.mishin870.core.arena.ArenaManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class MenuManager implements Listener {
	public static void init() {
		Menu.init();
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event) {
		final var clickedInventory = event.getClickedInventory();
		if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER) {
			final var who = event.getWhoClicked();
			if (who instanceof Player) {
				final var player = (Player) who;
				
				ArenaManager.onInventoryClick(event, player);
			}
		}
		
		final var menu = Menu.getByInventory(event.getInventory());
		if (menu == null) return;
		
		menu.onClick(event.getSlot());
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final var menu = Menu.getByInventory(event.getInventory());
		if (menu == null) return;
		
		menu.closeEvent(event);
	}
}
