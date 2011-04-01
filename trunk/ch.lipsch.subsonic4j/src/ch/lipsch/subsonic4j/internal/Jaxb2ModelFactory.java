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
package ch.lipsch.subsonic4j.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.subsonic.restapi.AlbumList;
import org.subsonic.restapi.ChatMessages;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.Indexes;
import org.subsonic.restapi.MusicFolders;
import org.subsonic.restapi.NowPlayingEntry;
import org.subsonic.restapi.PlaylistIdAndName;
import org.subsonic.restapi.Playlists;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.ChatMessage;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.model.License;
import ch.lipsch.subsonic4j.model.ModelFactory;
import ch.lipsch.subsonic4j.model.MusicFolder;
import ch.lipsch.subsonic4j.model.NowPlaying;
import ch.lipsch.subsonic4j.model.Playlist;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.model.User;
import ch.lipsch.subsonic4j.model.User.Role;
import ch.lipsch.subsonic4j.model.impl.ChatMessageImpl;
import ch.lipsch.subsonic4j.model.impl.DirectoryImpl;
import ch.lipsch.subsonic4j.model.impl.LicenseImpl;
import ch.lipsch.subsonic4j.model.impl.NowPlayingImpl;
import ch.lipsch.subsonic4j.model.impl.PlayListImpl;
import ch.lipsch.subsonic4j.model.impl.SongImpl;
import ch.lipsch.subsonic4j.model.impl.UserImpl;
import ch.lipsch.subsonic4j.tools.StateChecker;

public final class Jaxb2ModelFactory {

	private Jaxb2ModelFactory() {
	}

	public static List<MusicFolder> createMusicFolderList(
			MusicFolders jaxbMusicFolders, SubsonicService service) {
		StateChecker.check(jaxbMusicFolders, "jaxbMusicFolders");
		StateChecker.check(service, "service");
		List<MusicFolder> musicFolders = new ArrayList<MusicFolder>();

		for (org.subsonic.restapi.MusicFolder jaxbMusicFolder : jaxbMusicFolders
				.getMusicFolder()) {
			MusicFolder musicFolder = ModelFactory
					.createMusicFolder(jaxbMusicFolder.getId(),
							jaxbMusicFolder.getName(), service);
			musicFolders.add(musicFolder);
		}

		return musicFolders;
	}

	public static List<Index> createIndexList(Indexes jaxbIndexes,
			SubsonicService service) {
		StateChecker.check(service, "service");
		List<Index> indexes = new ArrayList<Index>();
		for (org.subsonic.restapi.Index jaxbIndex : jaxbIndexes.getIndex()) {
			indexes.add(ModelFactory.createIndex(jaxbIndex.getName(),
					createArtistList(jaxbIndex, service), service));
		}
		return indexes;
	}

	private static List<Artist> createArtistList(
			org.subsonic.restapi.Index jaxbIndex, SubsonicService service) {
		List<Artist> artists = new ArrayList<Artist>();

		List<org.subsonic.restapi.Artist> jaxbArtists = jaxbIndex.getArtist();
		for (org.subsonic.restapi.Artist jaxbArtist : jaxbArtists) {
			artists.add(ModelFactory.createArtist(jaxbArtist.getName(),
					jaxbArtist.getId(), service));
		}

		return artists;
	}

	public static License createLicense(
			org.subsonic.restapi.License jaxbLicense, SubsonicService service) {
		StateChecker.check(jaxbLicense, "jaxbLicense");
		StateChecker.check(service, "service");
		return new LicenseImpl(jaxbLicense.getDate().toGregorianCalendar(),
				jaxbLicense.getKey(), jaxbLicense.getEmail(), service);
	}

	public static Directory createDirectory(
			org.subsonic.restapi.Directory jaxbDirectory,
			SubsonicService service) {
		return new DirectoryImpl(jaxbDirectory, service);
	}

	public static List<Directory> createDirectories(AlbumList jaxbAlbums,
			SubsonicService service) {
		StateChecker.check(jaxbAlbums, "jaxbAlbums");
		StateChecker.check(service, "service");

		List<Directory> directories = new ArrayList<Directory>();
		for (Child jaxbAlbum : jaxbAlbums.getAlbum()) {
			directories.add(new DirectoryImpl(jaxbAlbum, service));
		}

		return directories;
	}

	public static Song createSong(Child child, SubsonicService service) {
		StateChecker.check(child, "child");
		StateChecker.check(service, "service");
		return new SongImpl(child.getTitle(), child.getId(), child.getGenre(),
				service);
	}

	public static List<ChatMessage> createChatMessages(
			ChatMessages jaxbChatMessages, SubsonicService service) {
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
		for (org.subsonic.restapi.ChatMessage jaxbChatMessage : jaxbChatMessages
				.getChatMessage()) {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(jaxbChatMessage.getTime());
			ChatMessage chatMessage = new ChatMessageImpl(
					jaxbChatMessage.getMessage(), time,
					jaxbChatMessage.getUsername(), service);
			chatMessages.add(chatMessage);
		}
		return chatMessages;
	}

	public static List<NowPlaying> createNowPlaying(
			org.subsonic.restapi.NowPlaying jaxbNowPlaying,
			SubsonicService service) {
		StateChecker.check(jaxbNowPlaying, "jaxbNowPlaying");
		StateChecker.check(service, "service");

		List<NowPlaying> nowPlayings = new ArrayList<NowPlaying>();

		for (NowPlayingEntry jaxbNowPlayingEntry : jaxbNowPlaying.getEntry()) {
			Song currentlyPlayedSong = createSong(jaxbNowPlayingEntry, service);
			NowPlaying nowPlaying = new NowPlayingImpl(
					jaxbNowPlayingEntry.getUsername(),
					jaxbNowPlayingEntry.getPlayerName(),
					jaxbNowPlayingEntry.getMinutesAgo(), currentlyPlayedSong,
					service);
		}

		return nowPlayings;
	}

	public static List<Song> createSongs(List<Child> jaxbChildren,
			SubsonicService service) {
		StateChecker.check(jaxbChildren, "jaxbChildren");
		StateChecker.check(service, "service");

		List<Song> songs = new ArrayList<Song>();
		for (Child child : jaxbChildren) {
			songs.add(createSong(child, service));
		}
		return songs;
	}

	public static User createUser(org.subsonic.restapi.User jaxbUser,
			SubsonicService service) {
		StateChecker.check(jaxbUser, "jaxbUser");
		StateChecker.check(service, "service");

		Set<Role> roles = new HashSet<Role>();

		if (jaxbUser.isAdminRole()) {
			roles.add(Role.ADMIN);
		}
		if (jaxbUser.isCommentRole()) {
			roles.add(Role.COMMENTS);
		}
		if (jaxbUser.isCoverArtRole()) {
			roles.add(Role.COVER_ART);
		}
		if (jaxbUser.isDownloadRole()) {
			roles.add(Role.DOWNLOAD);
		}
		if (jaxbUser.isJukeboxRole()) {
			roles.add(Role.JUKEBOX);
		}
		if (jaxbUser.isPlaylistRole()) {
			roles.add(Role.PLAYLIST);
		}
		if (jaxbUser.isPodcastRole()) {
			roles.add(Role.PODCASTS);
		}
		if (jaxbUser.isSettingsRole()) {
			roles.add(Role.SETTINGS);
		}
		if (jaxbUser.isStreamRole()) {
			roles.add(Role.STREAMING);
		}
		if (jaxbUser.isUploadRole()) {
			roles.add(Role.UPLOAD);
		}
		return new UserImpl(jaxbUser.getUsername(), roles, service);
	}

	public static List<Playlist> createPlaylists(Playlists jaxbPlaylists,
			SubsonicService service) {
		StateChecker.check(jaxbPlaylists, "jaxbPlaylists");
		StateChecker.check(service, "service");

		List<Playlist> playlist = new ArrayList<Playlist>();
		for (PlaylistIdAndName jaxbPlaylist : jaxbPlaylists.getPlaylist()) {
			playlist.add(new PlayListImpl(jaxbPlaylist, service));
		}
		return playlist;
	}
}
