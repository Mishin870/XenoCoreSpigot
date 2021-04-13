package com.mishin870.core.utils;

import com.mishin870.core.utils.containers.EmptyLanguageContainer;
import com.mishin870.core.utils.containers.ILanguageContainer;
import com.mishin870.core.utils.containers.MirrorLanguageContainer;
import com.mishin870.core.utils.containers.StandardLanguageContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class LanguageManager {
	private static final String DEFAULT_LOCALE = "en_us";
	
	private final Class<?> bundle;
	private final String basePath;
	private final String extension;
	private final Map<String, ILanguageContainer> containers = new HashMap<>();
	private final ILanguageContainer defaultContainer;
	
	public LanguageManager(Class<?> bundle, String basePath, String extension) {
		this.bundle = bundle;
		this.basePath = basePath;
		this.extension = extension;
		
		this.defaultContainer = makeContainer(DEFAULT_LOCALE);
	}
	
	public LanguageManager(Class<?> bundle) {
		this(bundle, "/lang/", ".lang");
	}
	
	private ILanguageContainer makeContainer(String locale) {
		final var path = basePath + locale + extension;
		final URL resource = bundle.getResource(path);
		
		if (resource == null) {
			if (locale.equals(DEFAULT_LOCALE)) {
				return new EmptyLanguageContainer();
			} else {
				return new MirrorLanguageContainer(defaultContainer);
			}
		} else {
			try (final var stream = resource.openStream()) {
				return new StandardLanguageContainer(StreamUtils.readLines(stream)::iterator);
			} catch (IOException e) {
				e.printStackTrace();
				return new EmptyLanguageContainer();
			}
		}
	}
	
	public String get(CommandSender sender, String key, String... args) {
		if (sender instanceof Player) {
			return get((Player) sender, key, args);
		} else {
			return get(DEFAULT_LOCALE, key, args);
		}
	}
	
	public String get(Player player, String key, String... args) {
		return get(player.getLocale().toLowerCase(Locale.ROOT), key, args);
	}
	
	public String get(String locale, String key, String... args) {
		if (!containers.containsKey(locale)) {
			containers.put(locale, makeContainer(locale));
		}
		
		return containers.get(locale).get(key, args);
	}
	
	public void send(CommandSender sender, String key, String... args) {
		if (sender instanceof Player) {
			send((Player) sender, key, args);
		} else {
			sender.sendMessage(get(DEFAULT_LOCALE, key, args));
		}
	}
	
	public void send(Player player, String key, String... args) {
		player.sendMessage(get(player, key, args));
	}
	
	public void broadcast(Iterable<? extends Player> forPlayers, String key, String... args) {
		for (Player player : forPlayers) {
			player.sendMessage(get(player, key, args));
		}
	}
	
	public void broadcast(String key, String... args) {
		broadcast(Bukkit.getOnlinePlayers(), key, args);
	}
	
	public void broadcastExcept(Player player, String key, String... args) {
		broadcast(Bukkit.getOnlinePlayers()
						.stream()
						.filter(somePlayer -> somePlayer != player)
						.collect(Collectors.toList()), key, args);
	}
}
