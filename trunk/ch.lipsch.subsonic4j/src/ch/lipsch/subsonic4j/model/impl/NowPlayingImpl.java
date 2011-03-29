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
import ch.lipsch.subsonic4j.model.NowPlaying;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class NowPlayingImpl extends AbstractSubsonicModelObject implements
		NowPlaying {

	private final String username;
	private final String playerName;
	private final int minutesAgo;
	private final Song song;

	public NowPlayingImpl(String username, String playerName, int minutesAgo,
			Song song, SubsonicService service) {
		super(service);
		StateChecker.check(username, "username");
		StateChecker.check(playerName, "playerName");
		StateChecker.checkGreaterOrEqual(minutesAgo, 0, "minutesAgo");
		StateChecker.check(song, "song");
		this.username = username;
		this.playerName = playerName;
		this.minutesAgo = minutesAgo;
		this.song = song;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public int getMinutesAgo() {
		return minutesAgo;
	}

	@Override
	public String getPlayerName() {
		return playerName;
	}

	@Override
	public Song getPlayedSong() {
		return song;
	}

}
