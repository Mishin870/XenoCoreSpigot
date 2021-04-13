package com.mishin870.core.commands;

import com.mishin870.core.CoreLanguage;
import com.mishin870.core.XenoCore;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportWorldCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			final var worldName = args[0];
			final var world = WorldCreator.name(worldName).createWorld();
			final var player = (Player) sender;
			
			if (world != null) {
				player.teleport(world.getSpawnLocation());
			} else {
				player.sendMessage("World is invalid");
			}
			
			return true;
		}
		
		XenoCore.language.send(sender, CoreLanguage.COMMAND_WRONG_ARGUMENTS);
		return false;
	}
}