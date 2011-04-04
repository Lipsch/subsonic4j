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

import java.util.Collections;
import java.util.List;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.SearchResult;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class SearchResultImpl extends AbstractSubsonicModelObject implements
		SearchResult {

	private final List<Song> songs;
	private final List<Directory> albums;
	private final List<Artist> artists;

	public SearchResultImpl(List<Song> songs, List<Directory> albums,
			List<Artist> artists, SubsonicService service) {
		super(service);
		StateChecker.check(songs, "songs");
		StateChecker.check(albums, "albums");
		StateChecker.check(artists, "artists");
		this.songs = songs;
		this.albums = albums;
		this.artists = artists;
	}

	@Override
	public List<Song> getSongs() {
		return Collections.unmodifiableList(songs);
	}

	@Override
	public List<Directory> getAlbums() {
		return Collections.unmodifiableList(albums);
	}

	@Override
	public List<Artist> getArtists() {
		return Collections.unmodifiableList(artists);
	}

}
