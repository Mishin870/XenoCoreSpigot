package com.mishin870.core.menu;

import com.mishin870.core.CoreLanguage;
import com.mishin870.core.XenoCore;
import com.mishin870.core.arena.Arena;
import com.mishin870.core.arena.ArenaManager;
import com.mishin870.core.game.Game;
import com.mishin870.core.game.GameManager;
import com.mishin870.core.utils.LocationInfo;
import com.mishin870.core.utils.actions.ActionOne;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CoreMenus {
	public static void openMenu(Player player) {
		final var menu = new PaginatedMenu(player, XenoCore.language.get(player, CoreLanguage.MENU));
		final var joinedArena = ArenaManager.wherePlayer(player);
		final var isJoined = joinedArena != null;
		
		menu.addItems(player, ArenaManager.all(), (m, arena) -> {
			if (isJoined) {
				XenoCore.language.send(player, CoreLanguage.ARENA_ERROR_ALREADY_JOINED, joinedArena.getName());
			} else {
				player.closeInventory();
				ArenaManager.join(player, arena);
			}
		});
		if (isJoined) {
			menu.setSpecial(0, Material.ACACIA_DOOR, XenoCore.language.get(player, CoreLanguage.MENU_LEAVE), m -> {
				player.closeInventory();
				ArenaManager.leave(player);
			});
			menu.setSpecial(1, Material.REDSTONE_BLOCK, XenoCore.language.get(player, CoreLanguage.MENU_START), m -> {
				player.closeInventory();
				ArenaManager.start(player);
			});
		}
		
		menu.updatePage();
		menu.open(player);
	}
	
	public static void openAdmin(Player player) {
		final var menu = new PaginatedMenu(player, XenoCore.language.get(player, CoreLanguage.ADMIN));
		final var joinedArena = ArenaManager.wherePlayer(player);
		final var isJoined = joinedArena != null;
		
		menu.addItems(player, ArenaManager.all(), (m, arena) -> {
			if (isJoined) {
				XenoCore.language.send(player, CoreLanguage.ARENA_ERROR_ALREADY_JOINED, joinedArena.getName());
			} else {
				player.closeInventory();
				ArenaManager.join(player, arena);
			}
		});
		if (isJoined) {
			menu.setSpecial(0, Material.ACACIA_DOOR, XenoCore.language.get(player, CoreLanguage.MENU_LEAVE), m -> {
				player.closeInventory();
				ArenaManager.leave(player);
			});
			menu.setSpecial(1, Material.PAPER, XenoCore.language.get(player, CoreLanguage.ADMIN_EDIT), m -> {
				player.closeInventory();
				editArenaMenu(player, joinedArena);
			});
		}
		menu.setSpecial(7, Material.COMPARATOR, XenoCore.language.get(player, CoreLanguage.ADMIN_GAMES), m -> {
			player.closeInventory();
			openGamesMenu(player);
		});
		menu.setSpecial(8, Material.TNT, XenoCore.language.get(player, CoreLanguage.ADMIN_STOP), m -> {
			player.closeInventory();
			Bukkit.getServer().shutdown();
		});
		
		menu.updatePage();
		menu.open(player);
	}
	
	private static void editArenaMenu(Player player, Arena arena) {
		final var menu = new Menu(XenoCore.language.get(player, CoreLanguage.ARENA_EDIT, arena.getName()), 3);
		menu.setCommand(0, Material.DIAMOND, XenoCore.language.get(player, CoreLanguage.ARENA_EDIT_LOBBY), m -> {
			player.closeInventory();
			arena.setLobby(player.getLocation());
			XenoCore.language.send(player, CoreLanguage.ARENA_EDIT_LOBBY_CHANGED, arena.getName());
		});
		menu.setCommand(1, Material.EMERALD, XenoCore.language.get(player, CoreLanguage.ARENA_EDIT_LOCATION), m -> {
			player.closeInventory();
			openArenaLocationsMenu(player, arena, arena.makeLocations(player),
					XenoCore.language.get(player, CoreLanguage.ARENA_EDIT_LOCATION_EDIT_ALL));
		});
		menu.setCommand(2, Material.COMPARATOR, XenoCore.language.get(player, CoreLanguage.ARENA_EDIT_GAME), m -> {
			player.closeInventory();
			openGamesMenu(player, game -> {
				if (arena.setGame(player, game)) {
					XenoCore.language.send(player, CoreLanguage.ARENA_EDIT_GAME_CHANGED, game.getId(), arena.getName());
				} else {
					XenoCore.language.send(player, CoreLanguage.ARENA_ERROR_GAME_CHANGE_ERROR, game.getId(), arena.getName());
					openArenaLocationsMenu(player, arena, arena.makeMissingLocations(player, game),
							XenoCore.language.get(player, CoreLanguage.ARENA_EDIT_LOCATION_EDIT_FOR_GAME, game.getId()));
				}
			});
		});
		menu.open(player);
	}
	
	private static void openArenaLocationsMenu(Player player, Arena arena, Iterable<LocationInfo> locations, String title) {
		final var menu = new PaginatedMenu(player, title);
		
		menu.addItems(player, locations, (m, location) -> {
			player.closeInventory();
			arena.setLocation(location.key, player.getLocation());
			XenoCore.language.send(player, CoreLanguage.ARENA_EDIT_LOCATION_CHANGED, location.key, arena.getName());
		});
		
		menu.updatePage();
		menu.open(player);
	}
	
	public static void openGamesMenu(Player player) {
		openGamesMenu(player, game -> player.sendMessage(game.getId()));
	}
	
	public static void openGamesMenu(Player player, ActionOne<Game> onSelect) {
		final var menu = new PaginatedMenu(player, XenoCore.language.get(player, CoreLanguage.GAME_ALL));
		menu.addItems(player, GameManager.all(), (m, game) -> {
			player.closeInventory();
			onSelect.run(game);
		});
		menu.updatePage();
		menu.open(player);
	}
}
