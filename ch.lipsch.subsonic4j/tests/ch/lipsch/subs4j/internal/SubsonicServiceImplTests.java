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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.subsonic.restapi.SearchResult2;

import ch.lipsch.subs4j.TestConfig;
import ch.lipsch.subsonic4j.StreamListener;
import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicFactory;
import ch.lipsch.subsonic4j.internal.InternalSubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.ChatMessage;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.model.License;
import ch.lipsch.subsonic4j.model.MusicFolder;
import ch.lipsch.subsonic4j.model.NowPlaying;
import ch.lipsch.subsonic4j.model.Playlist;
import ch.lipsch.subsonic4j.model.SearchResult;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.model.User;
import ch.lipsch.subsonic4j.tools.PlaylistTool;

public class SubsonicServiceImplTests extends TestCase implements
		InternalSubsonicService {

	private static final String NEW_TEST_SYSOUT = "#####################################";

	private InternalSubsonicService subsonicServiceForUser1 = null;

	@Override
	@Before
	public void setUp() throws MalformedURLException {
		subsonicServiceForUser1 = (InternalSubsonicService) SubsonicFactory
				.createService(new URL(TestConfig.SUBSONIC_URL),
						TestConfig.USER1_CREDENTIALS);
	}

	@After
	public void teardown() {

	}

	@Test
	public void testPing() throws SubsonicException {
		ping();
	}

	@Override
	public void ping() throws SubsonicException {
		subsonicServiceForUser1.ping();
	}

	@Test
	public void testGetLicense() throws SubsonicException {
		License license = getLicense();
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("getLicense");
		System.out.println(MessageFormat.format("date: ",
				license.getIssueDate()));
		System.out.println("email: " + license.getLicenseeEmail());
		System.out.println("key: " + license.getLicenseKey());
		assertNotNull(license);
	}

	@Override
	public License getLicense() throws SubsonicException {
		return subsonicServiceForUser1.getLicense();
	}

	@Test
	public void testGetMusicFolders() throws SubsonicException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("getMusicFolders:");
		List<ch.lipsch.subsonic4j.model.MusicFolder> musicFolders = getMusicFolders();
		for (ch.lipsch.subsonic4j.model.MusicFolder folder : musicFolders) {
			System.out.println(folder.getName());
		}
		assertNotNull(musicFolders);
	}

	@Override
	public List<ch.lipsch.subsonic4j.model.MusicFolder> getMusicFolders()
			throws SubsonicException {
		return subsonicServiceForUser1.getMusicFolders();
	}

	@Test
	public void testGetNowPlaying() throws SubsonicException {
		List<NowPlaying> nowPlaying = getNowPlaying();

		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("getNowPlaying");
		System.out.println("number of entries: " + nowPlaying.size());
		for (NowPlaying entry : nowPlaying) {
			System.out.println("player name:" + entry.getPlayerName());
			System.out.println("title: " + entry.getPlayedSong().getTitle());
		}

		assertNotNull(nowPlaying);
	}

	@Override
	public List<NowPlaying> getNowPlaying() throws SubsonicException {
		return subsonicServiceForUser1.getNowPlaying();
	}

	@Test
	public void testGetIndexes() throws SubsonicException {
		List<ch.lipsch.subsonic4j.model.Index> indexes = getIndexes(null, null);
		assertNotNull(indexes);

		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("testGetIndexes:");

		for (ch.lipsch.subsonic4j.model.Index index : indexes) {
			System.out.println(MessageFormat.format("name: {0}",
					index.getIdentifier()));
		}
	}

	@Test
	public void testGetIndexesWithFolderId() throws SubsonicException {
		List<Index> indexes = getIndexes(getMusicFolders().get(0), null);
		assertNotNull(indexes);
	}

	@Test
	public void testGetIndexesWithDate() throws SubsonicException {
		throw new UnsupportedOperationException("TODO");
	}

	@Test
	public void testGetIndexesWithFolderAndDate() throws SubsonicException {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public List<ch.lipsch.subsonic4j.model.Index> getIndexes(
			MusicFolder musicFolder, Calendar ifModifiedSince)
			throws SubsonicException {
		return subsonicServiceForUser1.getIndexes(musicFolder, ifModifiedSince);
	}

	@Test
	public void testGetMusicDirectoryWithMusicFolder() throws SubsonicException {
		ch.lipsch.subsonic4j.model.Directory musicDirectory = getMusicDirectory(getMusicFolders()
				.get(0));
		assertNotNull(musicDirectory);
	}

	@Override
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(
			MusicFolder musicFolder) throws SubsonicException {
		return subsonicServiceForUser1.getMusicDirectory(musicFolder);
	}

	@Test
	public void testGetMusicDirectoryWithArtist() throws SubsonicException {
		throw new UnsupportedOperationException("reactivate");
		// Indexes indexes = getIndexes(null, null);
		// Index index = indexes.getIndex().get(0);
		//
		// Directory musicDirectory = getMusicDirectory(index.getArtist().get(0)
		// .getId());
		//
		// assertNotNull(musicDirectory);
	}

	@Override
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(Artist artist)
			throws SubsonicException {
		return subsonicServiceForUser1.getMusicDirectory(artist);
	}

	@Test
	public void testSearch() throws SubsonicException {
		assertNotNull(search("test"));
	}

	@Test
	public void testSearchWithPaging() throws SubsonicException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("testSearchWithPaging:");

		SearchResult search = search("test");
		Iterator<Directory> albums = search.getAlbums();
		System.out.println("Albums:");
		while (albums.hasNext()) {
			Directory album = albums.next();
			System.out.println(album);
		}

		Iterator<Artist> artists = search.getArtists();
		System.out.println("Artists:");
		while (artists.hasNext()) {
			Artist artist = artists.next();
			System.out.println(artist);
		}

		Iterator<Song> songs = search.getSongs();
		System.out.println("Songs:");
		while (songs.hasNext()) {
			Song song = songs.next();
			System.out.println(song);
		}
	}

	@Override
	public SearchResult search(String query) throws SubsonicException {
		return subsonicServiceForUser1.search(query);
	}

	@Override
	public SearchResult2 search(String query, Integer artistCount,
			Integer artistOffset, Integer albumCound, Integer albumOffset,
			Integer songCount, Integer songOffset) throws SubsonicException {
		return subsonicServiceForUser1.search(query, artistCount, artistOffset,
				albumCound, albumOffset, songCount, songOffset);
	}

	@Test
	public void testGetPlayLists() throws SubsonicException {
		assertNotNull(getPlayLists());
	}

	@Override
	public List<Playlist> getPlayLists() throws SubsonicException {
		return subsonicServiceForUser1.getPlayLists();
	}

	@Test
	public void testGetPlaylist() throws SubsonicException {
		assertNotNull(getPlayLists().get(0).getId());
	}

	@Override
	public List<Song> getPlayList(String id) throws SubsonicException {
		return subsonicServiceForUser1.getPlayList(id);
	}

	@Test
	public void testCreateAndDeletePlaylist() throws SubsonicException {
		String newPlaylistName = createUnusedPlaylistName();

		createPlaylist(null, newPlaylistName, getRandomSongs());

		deletePlaylist(PlaylistTool.findPlaylistIdByName(newPlaylistName,
				subsonicServiceForUser1));
	}

	private String createUnusedPlaylistName() throws SubsonicException {
		Random rnd = new Random(System.currentTimeMillis());
		boolean foundInexistent = false;
		String newPlaylistName = null;
		List<Playlist> existingPlaylists = getPlayLists();

		while (!foundInexistent) {
			newPlaylistName = Long.toString(rnd.nextLong());

			foundInexistent = true;
			for (Playlist playList : existingPlaylists) {
				if (playList.getName().equals(newPlaylistName)) {
					foundInexistent = false;
				}
			}
		}
		return newPlaylistName;
	}

	private List<String> getRandomSongIds() throws SubsonicException {
		List<String> songIds = new ArrayList<String>();

		List<Song> randomSongs = getRandomSongs();
		for (Song song : randomSongs) {
			songIds.add(song.getId());
		}

		return songIds;
	}

	@Test
	public void testUpdatePlaylist() throws SubsonicException {
		String newPlaylistName = createUnusedPlaylistName();

		List<Song> randomSongs = getRandomSongs();
		createPlaylist(null, newPlaylistName, randomSongs);

		randomSongs.addAll(getRandomSongs());

		createPlaylist(PlaylistTool.findPlaylistIdByName(newPlaylistName,
				subsonicServiceForUser1), null, randomSongs);

		deletePlaylist(PlaylistTool.findPlaylistIdByName(newPlaylistName,
				subsonicServiceForUser1));
	}

	@Override
	public void createPlaylist(String playlistId, String name, List<Song> songs)
			throws SubsonicException {
		subsonicServiceForUser1.createPlaylist(playlistId, name, songs);
	}

	@Override
	public void deletePlaylist(String id) throws SubsonicException {
		subsonicServiceForUser1.deletePlaylist(id);
	}

	@Test
	public void testDownload() throws SubsonicException, InterruptedException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("testDownload:");

		// Search a song.
		List<Song> randomSongs = getRandomSongs();

		Song song = randomSongs.get(0);

		System.out.println(MessageFormat.format("Downloading: {0} [{1}]",
				song.getTitle(), song.getId()));

		final Throwable[] catchedThrowables = new Throwable[1];
		final Object lock = new Object();
		final boolean[] finished = new boolean[1];
		finished[0] = false;
		final AtomicLong bytesRead = new AtomicLong(0);

		download(song.getId(), new StreamListener() {
			@Override
			public void receivedStream(InputStream inputStream) {
				try {
					System.out.println("Bytes read");
					int read = inputStream.read();
					while (read != -1) {
						// System.out.print((char) read);
						bytesRead.incrementAndGet();
						read = inputStream.read();
					}
				} catch (IOException e) {
					catchedThrowables[0] = e;
				} finally {
					System.out.println();
					synchronized (lock) {
						finished[0] = true;
						lock.notifyAll();
					}
				}
			}
		});

		synchronized (lock) {
			while (!finished[0]) {
				lock.wait();
			}
		}

		System.out.println("downloaded bytes: " + bytesRead.longValue());
		assertNull(catchedThrowables[0]);
		assertTrue(bytesRead.longValue() > 0);
	}

	@Override
	public void download(String id, StreamListener listener)
			throws SubsonicException {
		subsonicServiceForUser1.download(id, listener);
	}

	@Test
	public void testStream() throws SubsonicException, InterruptedException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("testStream:");

		// Search a song.
		List<Song> randomSongs = getRandomSongs();

		Song song = randomSongs.get(0);

		System.out.println(MessageFormat.format("Streaming: {0} [{1}]",
				song.getTitle(), song.getId()));

		final Throwable[] catchedThrowables = new Throwable[1];
		final Object lock = new Object();
		final boolean[] finished = new boolean[1];
		finished[0] = false;
		final AtomicLong bytesRead = new AtomicLong(0);

		stream(song.getId(), BitRate.BITRATE_DEFAULT, new StreamListener() {
			@Override
			public void receivedStream(InputStream inputStream) {
				try {
					BufferedInputStream bis = new BufferedInputStream(
							inputStream);
					byte[] data = new byte[1024];

					// System.out.println("Bytes read");
					int read = bis.read(data);
					while (read != -1) {
						bytesRead.getAndAdd(read);
						System.out.println(MessageFormat.format(
								"read: {0} bytes {1}", read, bytesRead));
						read = bis.read(data);
					}
				} catch (IOException e) {
					catchedThrowables[0] = e;
				} finally {
					System.out.println();
					synchronized (lock) {
						finished[0] = true;
						lock.notifyAll();
					}
				}
			}
		});

		synchronized (lock) {
			while (!finished[0]) {
				lock.wait();
			}
		}

		System.out.println("streamed bytes: " + bytesRead.longValue());
		assertNull(catchedThrowables[0]);
		assertTrue(bytesRead.longValue() > 0);
	}

	@Override
	public void stream(String id, BitRate maxBitRate, StreamListener listener)
			throws SubsonicException {
		subsonicServiceForUser1.stream(id, maxBitRate, listener);
	}

	@Test
	public void testGetCoverArt() throws SubsonicException,
			InterruptedException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("testGetCoverArt:");

		// Search a song.
		List<Song> randomSongs = getRandomSongs();

		Song song = randomSongs.get(0);

		System.out.println(MessageFormat.format(
				"Getting coverart for: {0} [{1}]", song.getTitle(),
				song.getId()));

		final Throwable[] catchedThrowables = new Throwable[1];
		final Object lock = new Object();
		final boolean[] finished = new boolean[1];
		finished[0] = false;
		final AtomicLong bytesRead = new AtomicLong(0);

		getCoverArt(song.getId(), null, new StreamListener() {
			@Override
			public void receivedStream(InputStream inputStream) {
				try {
					BufferedInputStream bis = new BufferedInputStream(
							inputStream);
					byte[] data = new byte[1024];

					// System.out.println("Bytes read");
					int read = bis.read(data);
					while (read != -1) {
						bytesRead.getAndAdd(read);
						System.out.println(MessageFormat.format(
								"read: {0} bytes {1}", read, bytesRead));
						read = bis.read(data);
					}
				} catch (IOException e) {
					catchedThrowables[0] = e;
				} finally {
					System.out.println();
					synchronized (lock) {
						finished[0] = true;
						lock.notifyAll();
					}
				}
			}
		});

		synchronized (lock) {
			while (!finished[0]) {
				lock.wait();
			}
		}

		System.out.println("got coverart bytes: " + bytesRead.longValue());
		assertNull(catchedThrowables[0]);
		assertTrue(bytesRead.longValue() > 0);
	}

	@Override
	public void getCoverArt(String id, Integer size, StreamListener listener)
			throws SubsonicException {
		subsonicServiceForUser1.getCoverArt(id, size, listener);
	}

	@Test
	public void testChangePassword() throws SubsonicException {
		fail("Second pass change fails due to ldap error page.");
		// String pass = "\\&;@#aö/";
		// changePassword(TestConfig.USER1_NAME, pass);
		// ((TestConfig.ChangeableCredentialsProvider)
		// TestConfig.USER1_CREDENTIALS)
		// .setPassword(pass);
		// // Change back
		// changePassword(TestConfig.USER1_NAME, TestConfig.USER1_PASS);
		// ((TestConfig.ChangeableCredentialsProvider)
		// TestConfig.USER1_CREDENTIALS)
		// .setPassword(TestConfig.USER1_PASS);
	}

	@Override
	public void changePassword(String username, String password)
			throws SubsonicException {
		subsonicServiceForUser1.changePassword(username, password);
	}

	@Test
	public void testGetUser() throws SubsonicException {
		assertNotNull(getUser(TestConfig.USER1_NAME));
	}

	@Override
	public User getUser(String username) throws SubsonicException {
		return subsonicServiceForUser1.getUser(username);
	}

	@Test
	public void testCreateAndDeleteUser() throws SubsonicException {
		String user = "testUser2";
		createUser(user, "1234", null, null, null, null, null, null, null,
				null, null, null, null);

		deleteUser(user);
	}

	@Override
	public void createUser(String user, String password,
			Boolean ldapAuthenticated, Boolean adminRole, Boolean settingsRole,
			Boolean streamRole, Boolean jukeboxRole, Boolean downloadRole,
			Boolean uploadRole, Boolean playlistRole, Boolean coverArtRole,
			Boolean commentRole, Boolean podcastRole) throws SubsonicException {
		subsonicServiceForUser1.createUser(user, password, ldapAuthenticated,
				adminRole, settingsRole, streamRole, jukeboxRole, downloadRole,
				uploadRole, playlistRole, coverArtRole, commentRole,
				podcastRole);
	}

	@Override
	public void deleteUser(String username) throws SubsonicException {
		subsonicServiceForUser1.deleteUser(username);
	}

	@Test
	public void testGetChatMessages() throws SubsonicException {
		assertNotNull(getChatMessages(null));
	}

	@Test
	public void testGetChatMessagesSince() throws SubsonicException {
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(System.currentTimeMillis());

		addChatMessage("testGetChatMessagesSince");

		assertEquals(getChatMessages(time).size(), 1);
	}

	@Override
	public List<ChatMessage> getChatMessages(Calendar since)
			throws SubsonicException {
		return subsonicServiceForUser1.getChatMessages(since);
	}

	public void testAddChatMessage() throws SubsonicException {
		addChatMessage("test");
	}

	@Override
	public void addChatMessage(String message) throws SubsonicException {
		subsonicServiceForUser1.addChatMessage(message);
	}

	@Test
	public void testGetAlbumList() throws SubsonicException {
		assertTrue(getAlbumList(AlbumType.NEWEST, null, null).size() > 0);
	}

	@Override
	public List<Directory> getAlbumList(AlbumType albumType, Integer size,
			Integer offset) throws SubsonicException {
		return subsonicServiceForUser1.getAlbumList(albumType, size, offset);
	}

	@Test
	public void testGetRandomSongs() throws SubsonicException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("getRandomSongs");

		List<Song> randomSongs = getRandomSongs();

		System.out.println("number of songs: " + randomSongs.size());
		;
		for (Song child : randomSongs) {
			System.out.println("song: " + child.getId());
		}
		assertNotNull(getRandomSongs());
	}

	@Override
	public List<Song> getRandomSongs() throws SubsonicException {
		return subsonicServiceForUser1.getRandomSongs();
	}

	@Test
	public void testGetRandomSongsWithGenre() throws SubsonicException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("getRandomSongsWithGenre");

		List<Song> randomSongs = getRandomSongs();
		Song child = randomSongs.get(0);

		String genre = child.getGenre();
		System.out.println("Getting songs for genre: " + genre);

		List<Song> randomSongsWithGenre = getRandomSongs(null, genre, null,
				null, null);
		assertEquals(randomSongsWithGenre.size() > 0, true);
	}

	@Override
	public List<Song> getRandomSongs(Integer size, String genre,
			Integer fromYear, Integer toYear, MusicFolder musicFolder)
			throws SubsonicException {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void testGetLyrics() throws SubsonicException {
		System.out.println(NEW_TEST_SYSOUT);
		System.out.println("getLyrics");

		String artist = "Green Day";
		String title = "Disappearing Boy";

		String lyrics = getLyrics(artist, title);
		System.out.println(MessageFormat.format("Lyrics for {0} / {1} \n {2}",
				artist, title, lyrics));

		assertNotNull(lyrics);
	}

	@Override
	public String getLyrics(String artist, String title)
			throws SubsonicException {
		return subsonicServiceForUser1.getLyrics(artist, title);
	}

	@Override
	public void disposeService() {
		// ignored
	}

	@Override
	public boolean isDisposed() {
		return false;
	}

	@Override
	public String getStreamUrl(String id) throws SubsonicException {
		return subsonicServiceForUser1.getStreamUrl(id);
	}

	@Override
	public Directory getMusicDirectory(String folderId) {
		return subsonicServiceForUser1.getMusicDirectory(folderId);
	}
}
