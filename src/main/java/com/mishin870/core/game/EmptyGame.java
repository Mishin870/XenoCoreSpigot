package com.mishin870.core.game;

import com.mishin870.core.XenoCore;
import com.mishin870.core.utils.PluginInfo;
import org.bukkit.entity.Player;

public class EmptyGame extends Game {
	@Override
	public PluginInfo getPlugin() {
		return XenoCore.info;
	}
	
	@Override
	public String getId() {
		return "empty";
	}
	
	@Override
	public String getTitle(Player player) {
		return XenoCore.language.get(player, "game.empty.name");
	}
}
