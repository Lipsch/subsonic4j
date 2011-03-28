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

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class ArtistImpl extends AbstractSubsonicModelObject implements Artist {

	private final String name;
	private final String id;

	public ArtistImpl(String name, String id, SubsonicService service) {
		super(service);
		StateChecker.check(name, "name");
		StateChecker.check(id, "id");
		this.name = name;
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Directory getDirectory() {
		return getService().getMusicDirectory(this);
	}

}
