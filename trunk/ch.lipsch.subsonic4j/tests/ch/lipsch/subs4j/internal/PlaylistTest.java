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
package ch.lipsch.subs4j.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.lipsch.subs4j.TestConfig;
import ch.lipsch.subsonic4j.SubsonicFactory;
import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Playlist;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.PlaylistTool;

public class PlaylistTest extends TestCase {

	private static SubsonicService subsonicService;

	@Override
	@Before
	public void setUp() throws Exception {
		subsonicService = SubsonicFactory.createService(new URL(
				TestConfig.SUBSONIC_URL), TestConfig.USER1_CREDENTIALS);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		subsonicService.disposeService();
	}

	@Test
	public void testGetName() {
		List<Playlist> playLists = subsonicService.getPlayLists();
		for (Playlist playlist : playLists) {
			assertNotNull(playlist.getName());
		}
	}

	@Test
	public void testGetId() {
		List<Playlist> playLists = subsonicService.getPlayLists();
		for (Playlist playlist : playLists) {
			assertNotNull(playlist.getId());
		}
	}

	@Test
	public void testGetSongs() {
		List<Playlist> playLists = subsonicService.getPlayLists();
		for (Playlist playlist : playLists) {
			for (Song song : playlist.getSongs()) {
				assertNotNull(song);
			}
		}
	}

	@Test
	public void testAddSongs() {
		Playlist playList = null;
		try {
			List<Song> randomSongs = subsonicService.getRandomSongs();

			playList = subsonicService.createPlaylist("ASDFasdf",
					Collections.singletonList(randomSongs.get(0)));

			Song addedSong = null;
			for (Song song : randomSongs) {
				if (!(playList.getSongs().contains(song))) {
					playList.addSongs(Collections.singletonList(song));
					addedSong = song;
					break;
				}
			}
			if (addedSong == null) {
				throw new IllegalStateException(
						"No random songs which are not yet contained in the playlist");
			}

			playList = PlaylistTool.findPlaylistIdByName("ASDFasdf",
					subsonicService);

			assertTrue(playList.getSongs().contains(addedSong));
		} finally {
			playList.delete();
		}
	}

	@Test
	public void testRemoveSongs() {
		Playlist playlist = null;
		int initialNbrOfRndSongs = 3;
		List<Song> randomSongs = new ArrayList<Song>();
		try {
			int i = 0;
			for (Song song : subsonicService.getRandomSongs()) {
				if (i < initialNbrOfRndSongs) {
					randomSongs.add(song);
					i++;
				} else {
					break;
				}
			}

			playlist = subsonicService.createPlaylist("ASDFasdf", randomSongs);

			int songCount = playlist.getSongs().size();

			playlist.removeSongs(Collections.singletonList(playlist.getSongs()
					.get(0)));

			// Check the state of the local playlist
			assertEquals(songCount - 1, playlist.getSongs().size());

			// Refetch the playlist to check online state.
			playlist = PlaylistTool.findPlaylistIdByName("ASDFasdf",
					subsonicService);
			assertEquals(songCount - 1, playlist.getSongs().size());
		} finally {
			playlist.delete();
		}
	}

	@Test
	public void testDelete() {
		Playlist playlist = subsonicService.createPlaylist("ASDFasdf",
				subsonicService.getRandomSongs());

		playlist.delete();

		playlist = PlaylistTool.findPlaylistIdByName("ASDFasdf",
				subsonicService);

		assertNull(playlist);
	}
}
