package com.mishin870.games.monster.roles;

import com.mishin870.core.players.EnhancedPlayersList;
import com.mishin870.core.players.Role;

public abstract class MHRole extends Role {
	public abstract long skillCooldown();
	public abstract void useSkill(MHPlayerData data, EnhancedPlayersList<MHRole, MHPlayerData> controller);
	
	public void addRoleItems(MHPlayerData data) {
	}
}
