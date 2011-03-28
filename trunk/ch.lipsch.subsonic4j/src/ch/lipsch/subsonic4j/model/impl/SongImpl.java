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
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class SongImpl extends AbstractSubsonicModelObject implements Song {

	private final String id;
	private final String title;

	public SongImpl(String title, String id, SubsonicService service) {
		super(service);
		StateChecker.check(title, "title");
		StateChecker.check(id, "id");
		this.title = title;
		this.id = id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
