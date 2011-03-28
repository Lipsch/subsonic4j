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
import java.util.Collections;
import java.util.List;

import org.subsonic.restapi.Child;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.internal.Jaxb2ModelFactory;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class DirectoryImpl extends AbstractSubsonicModelObject implements
		Directory {

	private final org.subsonic.restapi.Directory directory;

	/**
	 * Directly contained songs in this directory. Built up lazily. Access must
	 * be synchronized with {@link DirectoryImpl} instance.
	 */
	private List<Song> songs = null;

	/**
	 * Directly contained child directories in this directory. Built up lazily.
	 * Access must be synchronized with {@link DirectoryImpl} instance.
	 */
	private List<Directory> subDirectories = null;

	public DirectoryImpl(org.subsonic.restapi.Directory directory,
			SubsonicService service) {
		super(service);
		StateChecker.check(directory, "directory");
		this.directory = directory;

	}

	private synchronized void buildUpChildsOnce() {
		if (subDirectories == null || songs == null) {
			subDirectories = new ArrayList<Directory>();
			songs = new ArrayList<Song>();
			for (Child child : directory.getChild()) {
				if (child.isIsDir()) {
					subDirectories.add(getService().getMusicDirectory(
							child.getId()));
				} else {
					songs.add(Jaxb2ModelFactory.createSong(child, getService()));
				}
			}
		}
	}

	@Override
	public String getName() {
		return directory.getName();
	}

	@Override
	public synchronized List<Directory> getChildDirectories() {
		buildUpChildsOnce();
		return Collections.unmodifiableList(subDirectories);
	}

	@Override
	public List<Song> getSongs() {
		buildUpChildsOnce();
		return Collections.unmodifiableList(songs);
	}

	@Override
	public String getId() {
		return directory.getId();
	}

	@Override
	public String toString() {
		return getName();
	}

}
