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

	private final RootHolder root;

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

	public DirectoryImpl(Child root, SubsonicService service) {
		super(service);
		this.root = new RootHolder(root);
	}

	public DirectoryImpl(org.subsonic.restapi.Directory directory,
			SubsonicService service) {
		super(service);
		StateChecker.check(directory, "directory");
		root = new RootHolder(directory);
	}

	private synchronized void buildUpChildsOnce() {
		if (subDirectories == null || songs == null) {
			subDirectories = new ArrayList<Directory>();
			songs = new ArrayList<Song>();
			root.buildUpChilds();
		}
	}

	@Override
	public String getName() {
		return root.getName();
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
		return root.getId();
	}

	@Override
	public String toString() {
		return getName();
	}

	private class RootHolder {
		private final org.subsonic.restapi.Directory directory;
		private final Child rootChild;

		private RootHolder(org.subsonic.restapi.Directory directory) {
			StateChecker.check(directory, "directory");
			this.directory = directory;
			this.rootChild = null;
		}

		public String getName() {
			if (directory != null) {
				return directory.getName();
			} else {
				return rootChild.getTitle();
			}
		}

		private RootHolder(Child root) {
			StateChecker.check(root, "root");
			StateChecker.check(root.isIsDir(), "root must be a directory");
			this.rootChild = root;
			this.directory = null;
		}

		public void buildUpChilds() {
			if (directory != null) {
				for (Child child : directory.getChild()) {
					if (child.isIsDir()) {
						subDirectories.add(getService().getMusicDirectory(
								child.getId()));
					} else {
						songs.add(Jaxb2ModelFactory.createSong(child,
								getService()));
					}
				}
			} else {
				Directory musicDir = getService().getMusicDirectory(
						rootChild.getId());
				subDirectories.addAll(musicDir.getChildDirectories());
				songs.addAll(musicDir.getSongs());
			}
		}

		private String getId() {
			if (directory != null) {
				return directory.getId();
			} else {
				return rootChild.getId();
			}
		}
	}

}
