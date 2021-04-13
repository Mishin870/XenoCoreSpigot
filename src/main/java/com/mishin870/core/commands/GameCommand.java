package com.mishin870.core.commands;

import com.mishin870.core.menu.CoreMenus;
import com.mishin870.core.CoreLanguage;
import com.mishin870.core.XenoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			CoreMenus.openGamesMenu((Player) sender);
			return true;
		}
		
		XenoCore.language.send(sender, CoreLanguage.COMMAND_WRONG_ARGUMENTS);
		return false;
	}
}