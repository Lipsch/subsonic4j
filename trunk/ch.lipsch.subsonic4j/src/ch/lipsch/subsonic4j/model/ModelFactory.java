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
package ch.lipsch.subsonic4j.model;

import java.util.List;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.impl.ArtistImpl;
import ch.lipsch.subsonic4j.model.impl.IndexImpl;
import ch.lipsch.subsonic4j.model.impl.MusicFolderImpl;
import ch.lipsch.subsonic4j.tools.StateChecker;

public final class ModelFactory {

	private ModelFactory() {
	}

	public static MusicFolder createMusicFolder(int id, String name,
			SubsonicService service) {
		StateChecker.check(service, "service");
		StateChecker.check(name, "name");
		MusicFolder musicFolder = new MusicFolderImpl(id, name, service);

		return musicFolder;
	}

	public static Artist createArtist(String name, String id,
			SubsonicService service) {
		StateChecker.check(name, "name");
		StateChecker.check(service, "service");
		StateChecker.check(id, "id");
		return new ArtistImpl(name, id, service);

	}

	public static Index createIndex(String identifier, List<Artist> artists,
			SubsonicService service) {
		StateChecker.check(service, "service");
		StateChecker.check(identifier, "identifier");
		Index index = new IndexImpl(identifier, artists, service);
		return index;
	}
}
