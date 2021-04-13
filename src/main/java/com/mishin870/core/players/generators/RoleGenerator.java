package com.mishin870.core.players.generators;

import com.mishin870.core.players.PlayerData;
import com.mishin870.core.players.Role;

import java.util.Collection;

@FunctionalInterface
public interface RoleGenerator<R extends Role, D extends PlayerData<R>> {
	void fillRoles(Collection<D> players);
}
