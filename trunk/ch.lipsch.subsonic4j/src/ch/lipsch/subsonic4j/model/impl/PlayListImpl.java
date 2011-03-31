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
import java.util.List;

import org.subsonic.restapi.PlaylistIdAndName;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Playlist;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class PlayListImpl extends AbstractSubsonicModelObject implements
		Playlist {

	private final String name;
	private final String id;

	/**
	 * Loads the songs of this playlist lazily. Access must be synchronized
	 * through {@link PlayListImpl} instance.
	 */
	private List<Song> songs = null;

	public PlayListImpl(PlaylistIdAndName jaxbPlaylist, SubsonicService service) {
		super(service);
		StateChecker.check(jaxbPlaylist, "jaxbPlaylist");
		name = jaxbPlaylist.getName();
		id = jaxbPlaylist.getId();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public synchronized List<Song> getSongs() {
		if (songs == null) {
			songs = new ArrayList<Song>();
			songs.addAll(getService().getPlayList(getId()));
		}

		return songs;
	}
}
