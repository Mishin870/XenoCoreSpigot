package com.mishin870.core.commands;

import com.mishin870.core.menu.CoreMenus;
import com.mishin870.core.CoreLanguage;
import com.mishin870.core.XenoCore;
import com.mishin870.core.arena.ArenaManager;
import com.mishin870.core.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length >= 1) {
			final var action = args[0];
			
			if (action.equals("leave")) {
				ArenaManager.leave(sender);
				return true;
			} else if (action.equals("hub")) {
				ArenaManager.setHub(sender);
				return true;
			}
			
			if (args.length >= 2) {
				final var arenaKey = args[1];
				
				switch (action) {
					case "start":
						ArenaManager.start(sender, arenaKey);
						return true;
					case "join":
						ArenaManager.join(sender, arenaKey);
						return true;
					case "create":
						if (ArenaManager.requirePlayer(sender)) {
							ArenaManager.create(sender, arenaKey, ((Player) sender).getLocation());
						}
						return true;
					case "rename":
						if (ArenaManager.requirePlayer(sender) && args.length >= 3) {
							ArenaManager.rename(sender, arenaKey, CommandUtils.merge(args, 2));
						}
						return true;
					case "lobby":
						if (ArenaManager.requirePlayer(sender)) {
							ArenaManager.setLobby(sender, arenaKey, ((Player) sender).getLocation());
						}
						return true;
					case "location":
						if (ArenaManager.requirePlayer(sender) && args.length >= 3) {
							ArenaManager.setLocation(sender, arenaKey, args[2], ((Player) sender).getLocation());
						}
						return true;
					case "game":
						if (ArenaManager.requirePlayer(sender) && args.length >= 3) {
							ArenaManager.setGame(sender, arenaKey, args[2]);
						}
						return true;
					default:
						XenoCore.language.send(sender, CoreLanguage.COMMAND_UNKNOWN_ACTION, action);
						break;
				}
			}
		}
		
		if (sender instanceof Player) {
			CoreMenus.openMenu((Player) sender);
			return true;
		}
		
		XenoCore.language.send(sender, CoreLanguage.COMMAND_WRONG_ARGUMENTS);
		return false;
	}
}