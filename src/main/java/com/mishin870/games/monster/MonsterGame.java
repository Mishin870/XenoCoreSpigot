package com.mishin870.games.monster;

import com.mishin870.core.XenoCore;
import com.mishin870.core.arena.Arena;
import com.mishin870.core.arena.ArenaState;
import com.mishin870.core.game.Game;
import com.mishin870.core.utils.PluginInfo;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;

public class MonsterGame extends Game {
	public static final String HUMAN = "human";
	public static final String MONSTER = "monster";
	public static final String DEAD = "dead";
	
	@Override
	public PluginInfo getPlugin() {
		return XenoCore.info;
	}
	
	@Override
	public String getId() {
		return "monster";
	}
	
	@Override
	public String getTitle(Player player) {
		return XenoCore.language.get(player, "game.monster.name");
	}
	
	@Override
	public List<String> getSubTitle(Player player) {
		return List.of(XenoCore.language.get(player, "game.monster.name.desc"));
	}
	
	private MonsterData data(Arena arena) {
		return ((MonsterData) arena.data);
	}
	
	@Override
	public void collectLocationRequirements(CommandSender sender, Map<String, String> requirements) {
		fillDefaultLocationRequirements(sender, requirements, List.of(HUMAN, MONSTER, DEAD));
	}
	
	@Override
	public void onUpdate(Arena arena) {
		super.onUpdate(arena);
		
		if (arena.getState() == ArenaState.STARTED) {
			data(arena).update();
		}
	}
	
	@Override
	public void onStarted(Arena arena) {
		arena.data = new MonsterData(arena);
		
		final var data = data(arena);
		data.assignRoles();
	}
	
	@Override
	public void onPlayerInteract(Arena arena, PlayerInteractEvent event) {
		if (arena.getState() != ArenaState.STARTED) return;
		final var action = event.getAction();
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() == null || event.getItem().getType() != Material.BOOK) return;
			
			final var data = data(arena);
			data.useSkill(event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerDrop(Arena arena, PlayerDropItemEvent event) {
		if (arena.getState() != ArenaState.STARTED) return;
		event.setCancelled(true);
	}
	
	@Override
	public void onInventoryClick(Arena arena, InventoryClickEvent event, Player player) {
		if (arena.getState() != ArenaState.STARTED) return;
		event.setCancelled(true);
	}
	
	@Override
	public void onDamage(Arena arena, EntityDamageEvent event, Player player) {
		event.setCancelled(true);
		if (arena.getState() != ArenaState.STARTED) return;
		
		if (event instanceof EntityDamageByEntityEvent) {
			final var attacker = ((EntityDamageByEntityEvent) event).getDamager();
			if (!(attacker instanceof Player)) return;
			
			data(arena).handleDamage(player, (Player) attacker);
		}
	}
	
	@Override
	public void onMobDamage(Arena arena, EntityDamageEvent event, Entity entity) {
		event.setCancelled(true);
	}
}
