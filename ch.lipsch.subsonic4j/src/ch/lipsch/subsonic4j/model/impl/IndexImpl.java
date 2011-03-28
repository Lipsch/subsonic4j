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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class IndexImpl extends AbstractSubsonicModelObject implements Index {

	private final String identifier;

	private final List<Artist> artists;

	public IndexImpl(String identifier, List<Artist> artists,
			SubsonicService service) {
		super(service);
		StateChecker.check(identifier, "identifier");
		StateChecker.check(artists, "artists");
		this.identifier = identifier;
		List<Artist> temp = new ArrayList<Artist>();
		temp.addAll(artists);
		this.artists = Collections.unmodifiableList(temp);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public List<Artist> getArtists() {
		return artists;
	}
}
