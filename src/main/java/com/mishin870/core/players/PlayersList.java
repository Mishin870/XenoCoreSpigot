package com.mishin870.core.players;

import com.mishin870.core.XenoCore;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayersList {
	private final Map<UUID, Player> players = new HashMap<>();
	
	public void clear() {
		players.clear();
	}
	
	public void add(Player player) {
		players.put(player.getUniqueId(), player);
	}
	
	public void addAll(Iterable<Player> players) {
		for (var player : players) {
			add(player);
		}
	}
	
	public void remove(Player player) {
		players.remove(player.getUniqueId());
	}
	
	public Collection<Player> all() {
		return players.values();
	}
	
	public List<Player> allRandom() {
		final var result = new ArrayList<>(all());
		Collections.shuffle(result);
		return result;
	}
	
	public boolean contains(Player player) {
		return players.containsKey(player.getUniqueId());
	}
	
	public void broadcast(String key, String... args) {
		XenoCore.language.broadcast(all(), key, args);
	}
	
	public int count() {
		return players.size();
	}
}
