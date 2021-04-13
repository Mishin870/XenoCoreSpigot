package com.mishin870.core.arena;

import com.mishin870.core.CoreLanguage;
import com.mishin870.core.game.FinishCause;
import com.mishin870.core.game.Game;
import com.mishin870.core.game.GameManager;
import com.mishin870.core.XenoCore;
import com.mishin870.core.game.config.ConfigEntry;
import com.mishin870.core.players.PlayersList;
import com.mishin870.core.utils.PlayerUtils;
import com.mishin870.core.utils.Presentable;
import com.mishin870.core.utils.LocationInfo;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public final class Arena implements Presentable {
	private static final String KEY_NAME = "name";
	private static final String KEY_LOBBY = "lobby";
	private static final String KEY_LOCATIONS = "locations";
	private static final String KEY_GAME = "game";
	private static final String KEY_GAME_CONFIG = "config";
	
	private final Map<String, Location> locations = new HashMap<>();
	private final Map<String, ConfigEntry<?>> options = new HashMap<>();
	private final String baseKey;
	private final String key;
	private String name;
	private ArenaState state = ArenaState.LOBBY;
	private Game game;
	private Location lobby;
	private int startingTimer;
	
	public final PlayersList players = new PlayersList();
	public ArenaData data;
	
	public Arena(String baseKey, String key) {
		this.baseKey = baseKey;
		this.key = key;
	}
	
	//region utils
	private void resetOptionsRegistry() {
		options.clear();
		game.registerOptions(options::put);
	}
	
	public void load(FileConfiguration config) {
		this.name = config.getString(baseKey + KEY_NAME);
		this.lobby = config.getLocation(baseKey + KEY_LOBBY);
		this.game = GameManager.get(config.getString(baseKey + KEY_GAME));
		resetOptionsRegistry();
		
		final var configSection = config.getConfigurationSection(baseKey + KEY_GAME_CONFIG);
		if (configSection != null) {
			for (var entry : options.entrySet()) {
				entry.getValue().load(configSection, entry.getKey());
			}
		}
		
		final var locationsSection = config.getConfigurationSection(baseKey + KEY_LOCATIONS);
		if (locationsSection != null) {
			for (var locationKey : locationsSection.getKeys(false)) {
				locations.put(locationKey, locationsSection.getLocation(locationKey));
			}
		}
	}
	
	public void save(FileConfiguration config) {
		config.set(baseKey + KEY_NAME, name);
		config.set(baseKey + KEY_LOBBY, lobby);
		config.set(baseKey + KEY_GAME, game.getId());
		
		final var configSection = config.getConfigurationSection(baseKey + KEY_GAME_CONFIG);
		if (configSection != null) {
			for (var entry : options.entrySet()) {
				entry.getValue().save(configSection, entry.getKey());
			}
		}
		
		final var locationsSection = config.getConfigurationSection(baseKey + KEY_LOCATIONS);
		if (locationsSection != null) {
			for (var entry : locations.entrySet()) {
				locationsSection.set(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public ArenaState getState() {
		return state;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Location getLobby() {
		return lobby;
	}
	
	public void setLobby(Location lobby) {
		this.lobby = lobby;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setGame(Game game) {
		this.game = game;
		resetOptionsRegistry();
	}
	
	public boolean setGame(CommandSender sender, Game game) {
		if (game.checkArena(sender, this)) {
			this.game = game;
			resetOptionsRegistry();
			return true;
		}
		
		return false;
	}
	
	public Location getLocation(String key) {
		return locations.get(key);
	}
	
	public boolean hasLocation(String key) {
		return locations.containsKey(key);
	}
	
	public void setLocation(String key, Location location) {
		locations.put(key, location);
	}
	
	public List<LocationInfo> makeLocations(Player player) {
		final var result = new ArrayList<LocationInfo>();
		final var gameLocations = new HashMap<String, String>();
		game.collectLocationRequirements(player, gameLocations);
		
		for (Map.Entry<String, Location> entry : locations.entrySet()) {
			result.add(new LocationInfo(entry.getKey(), gameLocations.getOrDefault(entry.getKey(), "")));
		}
		
		return result;
	}
	
	public List<LocationInfo> makeMissingLocations(Player player, Game forGame) {
		final var result = new ArrayList<LocationInfo>();
		final var gameLocations = new HashMap<String, String>();
		forGame.collectLocationRequirements(player, gameLocations);
		
		for (Map.Entry<String, String> entry : gameLocations.entrySet()) {
			if (locations.containsKey(entry.getKey())) continue;
			
			result.add(new LocationInfo(entry.getKey(), entry.getValue()));
		}
		
		return result;
	}
	//endregion
	
	public boolean join(Player player) {
		if (state != ArenaState.LOBBY) {
			XenoCore.language.send(player, CoreLanguage.ARENA_ERROR_JOIN_STARTED);
			return false;
		}
		
		player.teleport(lobby);
		ArenaManager.resetLobbyMenu(player);
		players.add(player);
		players.broadcast(CoreLanguage.ARENA_STATE_JOIN, PlayerUtils.title(player), String.valueOf(players.count()));
		return true;
	}
	
	public boolean start(CommandSender initiator) {
		if (state != ArenaState.LOBBY) {
			XenoCore.language.send(initiator, CoreLanguage.ARENA_ERROR_START_STARTED);
			return false;
		}
		
		if (initiator instanceof Player) {
			players.broadcast(CoreLanguage.ARENA_STATE_INITIATED, PlayerUtils.title((Player) initiator));
		}
		setState(ArenaState.STARTING);
		return true;
	}
	
	public void finish(FinishCause cause) {
		setState(ArenaState.LOBBY);
	}
	
	private void setState(ArenaState state) {
		if (this.state == state) {
			return;
		}
		this.state = state;
		
		switch (state) {
			case LOBBY:
				for (var player : players.all()) {
					ArenaManager.resetLobbyMenu(player);
					player.teleport(lobby);
				}
				game.onLobby(this);
				break;
			case STARTING:
				startingTimer = game.startingTimer();
				game.onStarting(this);
				break;
			case STARTED:
				players.broadcast(CoreLanguage.ARENA_STATE_ARENA_STARTED);
				game.onStarted(this);
				break;
		}
	}
	
	public void update() {
		if (state == ArenaState.STARTING) {
			players.broadcast(CoreLanguage.ARENA_STATE_TIMER, String.valueOf(startingTimer));
			startingTimer--;
			
			if (startingTimer < 0) {
				setState(ArenaState.STARTED);
			}
		}
		
		game.onUpdate(this);
	}
	
	public void leave(Player player) {
		players.broadcast(CoreLanguage.ARENA_STATE_LEAVE, PlayerUtils.title(player), String.valueOf(players.count() - 1));
		players.remove(player);
		game.onLeave(this, player);
	}
	
	//region presentation
	@Override
	public String toString() {
		return "Arena{" +
				"locations=" + locations +
				", name='" + name + '\'' +
				", state=" + state +
				", game=" + game +
				", lobby=" + lobby +
				'}';
	}
	
	@Override
	public String getTitle(Player player) {
		return name;
	}
	
	@Override
	public List<String> getSubTitle(Player player) {
		return List.of(
			XenoCore.language.get(player, CoreLanguage.ARENA_SUB_KEY, key),
			XenoCore.language.get(player, CoreLanguage.ARENA_SUB_STATE, state.getTitle(player)),
			XenoCore.language.get(player, CoreLanguage.ARENA_SUB_PLAYERS, String.valueOf(players.count())),
			XenoCore.language.get(player, CoreLanguage.ARENA_SUB_GAME, getGame().getTitle(player))
		);
	}
	//endregion
	
	public int getOptionInt(String key) {
		return (int) options.get(key).value;
	}
	
	public long getOptionLong(String key) {
		return (long) options.get(key).value;
	}
	
	public String getOptionString(String key) {
		return (String) options.get(key).value;
	}
	
	public boolean getOptionBool(String key) {
		return (boolean) options.get(key).value;
	}
}
