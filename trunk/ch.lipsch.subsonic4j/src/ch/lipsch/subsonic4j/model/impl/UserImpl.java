/*
 * Copyright (C) 2011 Erwin Betschart
 * 
 * This file is part of Subsonic4J.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package ch.lipsch.subsonic4j.model.impl;

import java.util.HashSet;
import java.util.Set;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.User;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class UserImpl extends AbstractSubsonicModelObject implements User {

	private final Set<Role> roles;
	private final String username;

	public UserImpl(String username, Set<Role> roles, SubsonicService service) {
		super(service);
		StateChecker.check(username, "username");
		StateChecker.check(roles, "roles");
		StateChecker.check(service, "service");

		this.roles = new HashSet<User.Role>();
		this.roles.addAll(roles);

		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean hasRole(Role role) {
		return roles.contains(role);
	}

}
