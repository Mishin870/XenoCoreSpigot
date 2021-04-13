package com.mishin870.core;

import com.mishin870.core.arena.ArenaManager;
import com.mishin870.core.menu.CoreMenus;
import com.mishin870.core.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEvents implements Listener {
	private static void broadcast(Player player, String selfKey, String key) {
		XenoCore.language.broadcastExcept(player, key, PlayerUtils.title(player));
		
		final var server = XenoCore.instance.getServer();
		server.getScheduler().scheduleSyncDelayedTask(XenoCore.instance, () -> {
			player.sendMessage(XenoCore.language.get(player, selfKey, PlayerUtils.title(player)));
			ArenaManager.resetLobbyMenu(player);
		}, 100);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage("");
		broadcast(event.getPlayer(), CoreLanguage.GENERAL_HELLO, CoreLanguage.GENERAL_JOIN);
		
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		event.getPlayer().teleport(ArenaManager.getHub());
		ArenaManager.resetLobbyMenu(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		event.setQuitMessage("");
		broadcast(event.getPlayer(), CoreLanguage.GENERAL_BYE, CoreLanguage.GENERAL_QUIT);
		
		ArenaManager.leave(event.getPlayer());
	}
	
	private boolean isMenu(ItemStack item) {
		return item != null && item.getType() == Material.COMPASS;
	}
	
	private boolean isAdmin(ItemStack item) {
		return item != null && item.getType() == Material.CLOCK;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isMenu(event.getItem())) {
			CoreMenus.openMenu(event.getPlayer());
			return;
		}
		if (isAdmin(event.getItem())) {
			CoreMenus.openAdmin(event.getPlayer());
			return;
		}
		
		ArenaManager.onInteract(event);
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		if (isMenu(event.getItemDrop().getItemStack())
			|| isAdmin(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
			return;
		}
		
		ArenaManager.onDrop(event);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		ArenaManager.onDeath(event);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		final var entity = event.getEntity();
		if (entity instanceof Player) {
			ArenaManager.onDamage(event, (Player) entity);
		} else {
			ArenaManager.onMobDamage(event, entity);
		}
	}
}
