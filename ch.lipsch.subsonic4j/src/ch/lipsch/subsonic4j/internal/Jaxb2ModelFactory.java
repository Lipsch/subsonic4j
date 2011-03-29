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
import java.util.List;

import org.subsonic.restapi.ChatMessages;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.Indexes;
import org.subsonic.restapi.MusicFolders;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.ChatMessage;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.model.License;
import ch.lipsch.subsonic4j.model.ModelFactory;
import ch.lipsch.subsonic4j.model.MusicFolder;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.model.impl.ChatMessageImpl;
import ch.lipsch.subsonic4j.model.impl.DirectoryImpl;
import ch.lipsch.subsonic4j.model.impl.LicenseImpl;
import ch.lipsch.subsonic4j.model.impl.SongImpl;
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

	public static Song createSong(Child child, SubsonicService service) {
		StateChecker.check(child, "child");
		StateChecker.check(service, "service");
		return new SongImpl(child.getTitle(), child.getId(), service);
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
}
