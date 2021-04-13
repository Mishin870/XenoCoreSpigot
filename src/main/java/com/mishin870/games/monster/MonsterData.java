package com.mishin870.games.monster;

import com.mishin870.core.XenoCore;
import com.mishin870.core.arena.Arena;
import com.mishin870.core.arena.ArenaData;
import com.mishin870.core.game.FinishCause;
import com.mishin870.core.players.EnhancedPlayersList;
import com.mishin870.core.utils.InventoryUtils;
import com.mishin870.core.utils.PlayerUtils;
import com.mishin870.games.monster.roles.Human;
import com.mishin870.games.monster.roles.MHPlayerData;
import com.mishin870.games.monster.roles.MHRole;
import com.mishin870.games.monster.roles.Monster;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.Objects;

public class MonsterData extends ArenaData {
	public static final int MAX_GAME_TIME = 300;
	public static final long NO_COOLDOWN = -1L;
	private static final String PREFIX = "game.monster.";
	private static final String NONE = "";
	private static final String SKILL = ".skill";
	private static final String DESC = ".desc";
	
	private final EnhancedPlayersList<MHRole, MHPlayerData> controller;
	private int gameTime = 2;
	private Objective timerObjective;
	
	public MonsterData(Arena arena) {
		super(arena);
		
		controller = new EnhancedPlayersList<>(players -> {
			var monstersRemain = 1;
			
			for (var player : players) {
				if (monstersRemain > 0) {
					player.role = Monster.instance;
					monstersRemain--;
				} else {
					player.role = Human.instance;
				}
			}
		}, MHPlayerData::new);
	}
	
	public void update() {
		var humansAlive = false;
		
		for (var data : controller.all()) {
			if (!humansAlive && data.role == Human.instance && !data.dead) humansAlive = true;
			
			if (data.cooldown == NO_COOLDOWN) continue;
			
			data.cooldown--;
			if (data.cooldown < 0L) {
				addSkill(data);
			}
		}
		
		if (!humansAlive) {
			win(MonsterGame.MONSTER);
			return;
		}
		
		gameTime--;
		if (gameTime <= 0) {
			win(MonsterGame.HUMAN);
		}
		
		final Score score = timerObjective.getScore("Time");
		score.setScore(gameTime);
	}
	
	private void win(String team) {
		for (var data : controller.all()) {
			showTitle(data.player, team + ".win");
		}
		arena.finish(FinishCause.GAME_END);
	}
	
	public void handleDamage(Player target, Player attacker) {
		if (controller.get(attacker).role == Monster.instance) {
			killPlayer(target);
			controller.broadcast("game.monster.killed", PlayerUtils.title(target), PlayerUtils.title(attacker));
		}
	}
	
	public void killPlayer(Player player) {
		controller.get(player).dead = true;
		player.getInventory().clear();
		player.teleport(arena.getLocation(MonsterGame.DEAD));
		showTitle(player, MonsterGame.DEAD);
	}
	
	private void resetInventory(MHPlayerData data) {
		data.player.getInventory().clear();
		data.role.addRoleItems(data);
		// ArenaManager.fillStandardItems(player);
	}
	
	public void useSkill(Player player) {
		resetInventory(controller.get(player));
		final var data = controller.get(player);
		data.cooldown = data.role.skillCooldown();
		
		player.sendMessage(XenoCore.language.get(player, "game.monster.skill_used"));
		data.role.useSkill(data, controller);
	}
	
	private String getText(Player player, String key, String suffix) {
		return XenoCore.language.get(player, PREFIX + key + suffix);
	}
	
	private void showTitle(MHPlayerData data) {
		showTitle(data.player, data.role.getKey());
	}
	
	public void showTitle(Player player, String key) {
		final var title = getText(player, key, NONE);
		final var subTitle = getText(player, key, DESC);
		
		player.sendTitle(title, subTitle, 10, 70, 20);
	}
	
	private void addSkill(MHPlayerData data) {
		final var title = getText(data.player, data.role.getKey(), SKILL);
		data.player.getInventory().addItem(InventoryUtils.createTitled(Material.BOOK, title));
	}
	
	private void teleport(MHPlayerData data) {
		data.player.teleport(arena.getLocation(data.role.getKey()));
	}
	
	public void assignRoles() {
		// gameTime = arena.getOptionInt("time");
		gameTime = MAX_GAME_TIME;
		
		final var timerBoard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
		timerObjective = timerBoard.registerNewObjective("game", "dummy", "Game");
		timerObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		controller.addAll(arena.players.all());
		controller.fillRoles();
		
		for (var data : controller.all()) {
			for (var effect : data.player.getActivePotionEffects()) {
				data.player.removePotionEffect(effect.getType());
			}
			
			data.player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 10,
					false, false, false));
			
			data.player.setGameMode(GameMode.ADVENTURE);
			data.player.setScoreboard(timerBoard);
			showTitle(data);
			resetInventory(data);
			addSkill(data);
			teleport(data);
		}
		
		var list = new ArrayList<String>();
		for (var monster : controller.byRole(Monster.instance)) {
			list.add(PlayerUtils.title(monster.player));
		}
		controller.broadcast("game.monster.list", String.join(", ", list));
	}
}