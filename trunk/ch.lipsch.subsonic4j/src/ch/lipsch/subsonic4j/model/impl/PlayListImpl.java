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

import ch.lipsch.subsonic4j.SubsonicException;
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
			songs.addAll(getService().getPlaylistSongs(getId()));
		}

		return songs;
	}

	@Override
	public void addSongs(List<Song> songsToAdd) {
		synchronized (PlayListImpl.this) {
			List<Song> defCopy = new ArrayList<Song>(getSongs());
			defCopy.addAll(songsToAdd);

			getService().createOrUpdatePlaylist(getId(), null, defCopy);
			this.songs.addAll(songsToAdd);
		}
	}

	@Override
	public void removeSongs(List<Song> songs) {
		// Make a copy for checks before removing the songs.
		List<Song> defCopy = new ArrayList<Song>(getSongs());

		defCopy.removeAll(songs);

		if (defCopy.isEmpty()) {
			throw new IllegalStateException(
					"Cannot remove all songs of a playlist");
		} else {
			synchronized (PlayListImpl.this) {
				this.songs.removeAll(songs);
			}
		}
		// TODO this should be done in the sync block. But it is a network
		// call...

		try {
			getService().createOrUpdatePlaylist(getId(), null, getSongs());
		} catch (SubsonicException e) {
			// restore the original playlist
			List<Song> playlistSongs = getService().getPlaylistSongs(getId());
			synchronized (PlayListImpl.this) {
				this.songs.clear();
				this.songs.addAll(playlistSongs);
			}
			// TODO what should be done if the restore fails?
			throw e;
		}
	}

	@Override
	public void delete() {
		getService().deletePlaylist(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PlayListImpl)) {
			return false;
		} else {
			PlayListImpl objPlaylist = (PlayListImpl) obj;
			return objPlaylist.getId().equals(getId());
		}
	}

	@Override
	public int hashCode() {
		return 37 * getId().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Playlist (");
		sb.append(getName());
		sb.append(" / ");
		sb.append(getId());
		sb.append(")");
		return sb.toString();
	}
}
