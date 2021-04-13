package com.mishin870.core.arena;

import com.mishin870.core.CoreLanguage;
import com.mishin870.core.XenoCore;
import com.mishin870.core.game.GameManager;
import com.mishin870.core.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ArenaManager {
	private static final String KEY_ARENA = "arena";
	private static final String KEY_HUB = "hub";
	private static final Map<String, Arena> arenas = new HashMap<>();
	private static final Map<UUID, Arena> wherePlayers = new HashMap<>();
	private static Location hubLocation;
	
	//region utils
	public static void load(FileConfiguration config) {
		if (config.isLocation(KEY_HUB)) {
			hubLocation = config.getLocation(KEY_HUB);
		} else {
			hubLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		
		if (!config.isConfigurationSection(KEY_ARENA)) {
			XenoCore.instance.getLogger().warning("Arena key is empty, skipping");
			return;
		}
		
		final var section = config.getConfigurationSection(KEY_ARENA);
		final var arenaKeys = Objects.requireNonNull(section).getKeys(false);
		
		for (String arenaKey : arenaKeys) {
			final var arena = createEmpty(arenaKey);
			arena.load(config);
		}
	}
	
	public static void save(FileConfiguration config) {
		config.set(KEY_HUB, hubLocation);
		
		for (Map.Entry<String, Arena> entry : arenas.entrySet()) {
			entry.getValue().save(config);
		}
	}
	
	private static Arena createEmpty(String key) {
		var arena = new Arena(String.format("%s.%s.", KEY_ARENA, key), key);
		arena.setName("Arena " + key);
		arena.setGame(GameManager.DEFAULT_GAME);
		arenas.put(key, arena);
		return arena;
	}
	
	public static Iterable<Map.Entry<String, Arena>> allEntries() {
		return arenas.entrySet();
	}
	
	public static Collection<Arena> all() {
		return arenas.values();
	}
	
	public static Arena get(String key) {
		return arenas.get(key);
	}
	
	public static void remove(String key) {
		arenas.remove(key);
	}
	//endregion
	
	public static Arena wherePlayer(Player player) {
		return wherePlayers.get(player.getUniqueId());
	}
	
	private static Arena tryFind(CommandSender sender, String arenaKey) {
		final var arena = get(arenaKey);
		if (arena == null) XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_NOT_FOUND, arenaKey);
		
		return arena;
	}
	
	public static boolean requirePlayer(CommandSender sender) {
		if (!(sender instanceof Player)) {
			XenoCore.language.send(sender, CoreLanguage.COMMAND_ONLY_PLAYERS);
			return false;
		}
		
		return true;
	}
	
	public static boolean start(Player player) {
		final var arena = wherePlayer(player);
		return arena != null && arena.start(player);
	}
	
	public static boolean start(CommandSender sender, String arenaKey) {
		final var arena = tryFind(sender, arenaKey);
		return arena != null && arena.start(sender);
	}
	
	public static boolean join(CommandSender sender, Arena arena) {
		if (!requirePlayer(sender)) return false;
		
		final var player = (Player) sender;
		final var alreadyArena = wherePlayer(player);
		if (alreadyArena != null) {
			XenoCore.language.send(player, CoreLanguage.ARENA_ERROR_ALREADY_JOINED, alreadyArena.getName());
			return false;
		}
		
		if (arena.join(player)) {
			wherePlayers.put(player.getUniqueId(), arena);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean join(CommandSender sender, String arenaKey) {
		final var arena = tryFind(sender, arenaKey);
		return arena != null && join(sender, arena);
	}
	
	public static void update() {
		for (var arena : arenas.values()) {
			arena.update();
		}
	}
	
	public static void leave(CommandSender sender) {
		if (!requirePlayer(sender)) return;
		
		final var player = (Player) sender;
		final var arena = wherePlayer(player);
		wherePlayers.remove(player.getUniqueId());
		player.teleport(hubLocation);
		resetLobbyMenu(player);
		if (arena == null) return;
		
		arena.leave(player);
	}
	
	public static void onInteract(PlayerInteractEvent event) {
		final var player = event.getPlayer();
		final var arena = wherePlayer(player);
		
		if (arena != null) {
			arena.getGame().onPlayerInteract(arena, event);
		}
	}
	
	public static void onDrop(PlayerDropItemEvent event) {
		final var player = event.getPlayer();
		final var arena = wherePlayer(player);
		
		if (arena != null) {
			arena.getGame().onPlayerDrop(arena, event);
		}
	}
	
	public static void onDeath(PlayerDeathEvent event) {
		final var player = event.getEntity();
		final var arena = wherePlayer(player);
		
		if (arena != null) {
			arena.getGame().onPlayerDeath(arena, event);
		}
	}
	
	public static void onInventoryClick(InventoryClickEvent event, Player player) {
		final var arena = wherePlayer(player);
		
		if (arena != null) {
			arena.getGame().onInventoryClick(arena, event, player);
		}
	}
	
	public static void onDamage(EntityDamageEvent event, Player player) {
		final var arena = wherePlayer(player);
		
		if (arena != null) {
			arena.getGame().onDamage(arena, event, player);
		}
	}
	
	public static void onMobDamage(EntityDamageEvent event, Entity entity) {
		if (event instanceof EntityDamageByEntityEvent) {
			final var attacker = ((EntityDamageByEntityEvent) event).getDamager();
			if (!(attacker instanceof Player)) return;
			
			final var arena = wherePlayer((Player) attacker);
			if (arena != null) {
				arena.getGame().onMobDamage(arena, event, entity);
			}
		}
	}
	
	public static void create(CommandSender sender, String arenaKey, Location lobby) {
		if (arenas.containsKey(arenaKey)) {
			XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_ALREADY_EXIST, arenaKey);
			return;
		}
		
		final var arena = createEmpty(arenaKey);
		arena.setLobby(lobby);
		
		XenoCore.language.send(sender, CoreLanguage.ARENA_EDIT_CREATED, arenaKey);
	}
	
	public static void rename(CommandSender sender, String arenaKey, String newName) {
		final var arena = get(arenaKey);
		if (arena == null) {
			XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_NOT_FOUND, arenaKey);
			return;
		}
		
		arena.setName(newName);
		XenoCore.language.send(sender, CoreLanguage.ARENA_EDIT_RENAMED, arenaKey);
	}
	
	public static void setLobby(CommandSender sender, String arenaKey, Location lobby) {
		final var arena = get(arenaKey);
		if (arena == null) {
			XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_NOT_FOUND, arenaKey);
			return;
		}
		
		arena.setLobby(lobby);
		XenoCore.language.send(sender, CoreLanguage.ARENA_EDIT_LOBBY_CHANGED, arenaKey);
	}
	
	public static void setLocation(CommandSender sender, String arenaKey, String locationKey, Location location) {
		final var arena = get(arenaKey);
		if (arena == null) {
			XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_NOT_FOUND, arenaKey);
			return;
		}
		
		arena.setLocation(locationKey, location);
		XenoCore.language.send(sender, CoreLanguage.ARENA_EDIT_LOCATION_CHANGED, locationKey, arenaKey);
	}
	
	public static void setGame(CommandSender sender, String arenaKey, String gameKey) {
		final var arena = get(arenaKey);
		if (arena == null) {
			XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_NOT_FOUND, arenaKey);
			return;
		}
		
		final var game = GameManager.get(gameKey);
		if (game == null) {
			XenoCore.language.send(sender, CoreLanguage.GAME_ERROR_NOT_FOUND, gameKey);
			return;
		}
		
		if (arena.setGame(sender, game)) {
			XenoCore.language.send(sender, CoreLanguage.ARENA_EDIT_GAME_CHANGED, gameKey, arenaKey);
		} else {
			XenoCore.language.send(sender, CoreLanguage.ARENA_ERROR_GAME_CHANGE_ERROR, gameKey, arenaKey);
		}
	}
	
	public static void fillStandardItems(Player player) {
		final var inventory = player.getInventory();
		inventory.addItem(InventoryUtils.createTitled(Material.COMPASS,
				XenoCore.language.get(player, CoreLanguage.MENU)));
		
		if (player.isOp()) {
			inventory.addItem(InventoryUtils.createTitled(Material.CLOCK,
					XenoCore.language.get(player, CoreLanguage.ADMIN)));
		}
	}
	
	public static void resetLobbyMenu(Player player) {
		final var arena = wherePlayer(player);
		if (arena != null && arena.getState() != ArenaState.LOBBY) return;
		
		for (var effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE,
				100, false, false, false));
		player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
		player.getInventory().clear();
		player.setGameMode(GameMode.ADVENTURE);
		fillStandardItems(player);
	}
	
	public static void setHub(CommandSender sender) {
		if (!requirePlayer(sender)) return;
		
		XenoCore.language.send(sender, CoreLanguage.ARENA_EDIT_HUB_CHANGED);
		final var player = (Player) sender;
		hubLocation = player.getLocation();
	}
	
	public static Location getHub() {
		return hubLocation;
	}
}
