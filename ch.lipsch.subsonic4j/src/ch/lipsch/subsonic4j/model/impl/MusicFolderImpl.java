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

import java.util.List;

import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.model.MusicFolder;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class MusicFolderImpl extends AbstractSubsonicModelObject implements
		MusicFolder {

	private final int id;
	private final String name;

	public MusicFolderImpl(int id, String name, SubsonicService service) {
		super(service);
		StateChecker.check(name, "name");

		this.id = id;
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Index> getIndexes() throws SubsonicException {
		return getService().getIndexes(this, null);
	}
}