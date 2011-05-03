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
package ch.lipsch.subsonic4j.tools;

import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Playlist;

public final class PlaylistTool {

	/**
	 * Searches for a playlist with the given name.
	 * 
	 * @param playlistName
	 *            The name of the playlist to search for. Must not be
	 *            <code>null</code>.
	 * @param service
	 *            {@link SubsonicService} on which to search for the playlist.
	 *            Must not be <code>null</code>.
	 * @return If found a {@link Playlist} else <code>null</code>.
	 * @throws SubsonicException
	 *             In case of errors.
	 */
	public static Playlist findPlaylistIdByName(String playlistName,
			SubsonicService service) throws SubsonicException {
		StateChecker.check(playlistName, "playlistName");
		StateChecker.check(service, "service");
		Playlist foundPlaylist = null;
		for (Playlist playList : service.getPlayLists()) {
			if (playList.getName().equals(playlistName)) {
				foundPlaylist = playList;
				break;
			}
		}
		return foundPlaylist;
	}
}
