package com.mishin870.core.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
	public static final Game DEFAULT_GAME = new EmptyGame();
	
	private static final Map<String, Game> games = new HashMap<>();
	
	public static void registerGame(Game game) {
		games.put(game.getId(), game);
	}
	
	public static Game get(String key) {
		return games.get(key);
	}
	
	public static Collection<Game> all() {
		return games.values();
	}
}
