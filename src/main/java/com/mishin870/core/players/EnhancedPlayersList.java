package com.mishin870.core.players;

import com.mishin870.core.XenoCore;
import com.mishin870.core.players.generators.PlayerDataGenerator;
import com.mishin870.core.players.generators.RoleGenerator;
import org.bukkit.entity.Player;

import java.util.*;

public class EnhancedPlayersList<R extends Role, D extends PlayerData<R>> {
	private final Map<UUID, D> players = new HashMap<>();
	private final RoleGenerator<R, D> roleGenerator;
	private final PlayerDataGenerator<R, D> dataGenerator;
	
	public EnhancedPlayersList(RoleGenerator<R, D> roleGenerator, PlayerDataGenerator<R, D> dataGenerator) {
		this.roleGenerator = roleGenerator;
		this.dataGenerator = dataGenerator;
	}
	
	public void fillRoles() {
		final var randomPlayers = new ArrayList<D>(players.values());
		Collections.shuffle(randomPlayers);
		this.roleGenerator.fillRoles(randomPlayers);
	}
	
	public List<PlayerData<R>> byRole(Role role) {
		final var result = new ArrayList<PlayerData<R>>();
		
		for (var data : players.values()) {
			if (data.role == role) result.add(data);
		}
		
		return result;
	}
	
	public D get(Player player) {
		return players.get(player.getUniqueId());
	}
	
	public void clear() {
		players.clear();
	}
	
	public void add(Player player) {
		players.put(player.getUniqueId(), dataGenerator.generate(player));
	}
	
	public void addAll(Iterable<Player> players) {
		for (var player : players) {
			add(player);
		}
	}
	
	public void remove(Player player) {
		players.remove(player.getUniqueId());
	}
	
	public Collection<D> all() {
		return players.values();
	}
	
	public void broadcast(String key, String... args) {
		for (D data : players.values()) {
			XenoCore.language.send(data.player, key, args);
		}
	}
}
