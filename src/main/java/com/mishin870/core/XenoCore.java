package com.mishin870.core;

import com.mishin870.core.arena.ArenaManager;
import com.mishin870.core.commands.ArenaCommand;
import com.mishin870.core.commands.GameCommand;
import com.mishin870.core.commands.TeleportWorldCommand;
import com.mishin870.core.game.GameManager;
import com.mishin870.core.menu.MenuManager;
import com.mishin870.core.utils.LanguageManager;
import com.mishin870.core.utils.PluginInfo;
import com.mishin870.games.monster.MonsterGame;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Objects;

public final class XenoCore extends JavaPlugin {
	public static XenoCore instance;
	public static LanguageManager language;
	public static PluginInfo info;
	
	@Override
	public void onEnable() {
		instance = this;
		info = new PluginInfo(new LanguageManager(XenoCore.class));
		language = info.language;
		
		loadWorlds();
		initAll();
		loadConfigs();
		registerListeners();
	}
	
	private void loadWorlds() {
		final var worldsDirectory = getServer().getWorldContainer().getAbsoluteFile();
		final var worldNames = worldsDirectory.list((current, name) -> new File(current, name).isDirectory());
		
		for (var worldName : Objects.requireNonNull(worldNames)) {
			System.out.println("Loading: " + worldName);
			WorldCreator.name(worldName).createWorld();
		}
	}
	
	private void initAll() {
		Objects.requireNonNull(getCommand("arena")).setExecutor(new ArenaCommand());
		Objects.requireNonNull(getCommand("game")).setExecutor(new GameCommand());
		Objects.requireNonNull(getCommand("tpworld")).setExecutor(new TeleportWorldCommand());
		
		GameManager.registerGame(GameManager.DEFAULT_GAME);
		GameManager.registerGame(new MonsterGame());
		
		MenuManager.init();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				ArenaManager.update();
			}
		}.runTaskTimer(this, 0L, 20L);
	}
	
	private void loadConfigs() {
		final var config = getConfig();
		
		ArenaManager.load(config);
	}
	
	private void saveConfigs() {
		final var config = getConfig();
		
		ArenaManager.save(config);
		
		saveConfig();
	}
	
	private void registerListeners() {
		final var manager = Bukkit.getServer().getPluginManager();
		manager.registerEvents(new PlayerEvents(), this);
		manager.registerEvents(new MenuManager(), this);
	}
	
	@Override
	public void onDisable() {
		saveConfigs();
	}
}
