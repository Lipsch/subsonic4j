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

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Before;

import ch.lipsch.subs4j.TestConfig;
import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicFactory;
import ch.lipsch.subsonic4j.SubsonicService;

public class RLServiceTest extends TestCase {

	private SubsonicService subsonicService;

	@Override
	@Before
	public void setUp() throws MalformedURLException {
		subsonicService = SubsonicFactory.createService(new URL(
				TestConfig.SUBSONIC_URL), TestConfig.USER1_CREDENTIALS);
	}

	public void testTraverse() throws SubsonicException {
		// TODO reactivate
		throw new UnsupportedOperationException("TODO");
		// List<Index> indexes = subsonicService.getIndexes(null, null);
		//
		// for (Index index : indexes) {
		// for (Artist artist : index.getArtists()) {
		// ch.lipsch.subsonic4j.model.Directory directory = subsonicService
		// .getMusicDirectory(artist);
		// System.out.println("Dir: " + directory.getName());
		// for (Directory child : directory.getChildDirectories()) {
		// System.out.println(MessageFormat.format("Child :{0} [{1}]",
		// child.getAlbum(), child.getArtist()));
		// }
		// }
		// }
	}
}
