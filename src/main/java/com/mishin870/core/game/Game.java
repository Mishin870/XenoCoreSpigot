package com.mishin870.core.game;

import com.mishin870.core.XenoCore;
import com.mishin870.core.arena.Arena;
import com.mishin870.core.game.config.ConfigRegistry;
import com.mishin870.core.utils.Presentable;
import com.mishin870.core.utils.PluginInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class Game implements Presentable {
	private final String baseKey;
	
	public Game() {
		this.baseKey = "game." + getId() + ".";
	}
	
	public void registerOptions(ConfigRegistry registry) {}
	
	public void collectLocationRequirements(CommandSender sender, Map<String, String> requirements) {
	}
	
	protected final void fillDefaultLocationRequirements(
			CommandSender sender, Map<String, String> requirements, Iterable<String> keys) {
		for (String key : keys) {
			requirements.put(key, locationTitle(sender, key));
		}
	}
	
	protected final String locationTitle(CommandSender sender, String key) {
		return XenoCore.language.get(sender, String.format("game.%s.location.%s", getId(), key));
	}
	
	public boolean checkArena(CommandSender sender, Arena arena) {
		final var required = new HashMap<String, String>();
		collectLocationRequirements(sender, required);
		var result = true;
		
		for (Map.Entry<String, String> entry : required.entrySet()) {
			if (!arena.hasLocation(entry.getKey())) {
				XenoCore.language.send(sender, "game.location_required", getId(), entry.getKey(), entry.getValue());
				result = false;
			}
		}
		
		return result;
	}
	
	public void onLobby(Arena arena) {}
	public void onStarting(Arena arena) {}
	public void onStarted(Arena arena) {}
	public void onUpdate(Arena arena) {}
	public void onLeave(Arena arena, Player player) {
		arena.finish(FinishCause.PLAYER_LEAVE);
	}
	public void onPlayerInteract(Arena arena, PlayerInteractEvent event) {}
	public void onPlayerDrop(Arena arena, PlayerDropItemEvent event) {}
	public void onInventoryClick(Arena arena, InventoryClickEvent event, Player player) {}
	public void onPlayerDeath(Arena arena, PlayerDeathEvent event) {}
	public void onDamage(Arena arena, EntityDamageEvent event, Player player) {}
	public void onMobDamage(Arena arena, EntityDamageEvent event, Entity entity) {}
	
	public abstract PluginInfo getPlugin();
	
	public abstract String getId();
	
	public int startingTimer() {
		return 5;
	}
	
	protected final String key(String source) {
		return baseKey + source;
	}
	
	public abstract String getTitle(Player player);
	
	@Override
	public String toString() {
		return "Game{" + getId() + "}";
	}
}
