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

import org.subsonic.restapi.SearchResult2;

import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicService;

public interface InternalSubsonicService extends SubsonicService {

	/**
	 * Returns a listing of all files in a music directory. Typically used to
	 * get list of albums for an artist, or list of songs for an album.
	 * 
	 * @param folderId
	 *            The music folder. Obtained by calls to getIndexes or
	 *            getMusicDirectory
	 * @return A directory containing songs & sub directories.
	 * @throws SubsonicException
	 *             In case of problems.
	 */
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(
			String folderId);

	/**
	 * Returns albums, artists and songs matching the given search criteria.
	 * Supports paging through the result.
	 * 
	 * @param query
	 *            Search query.
	 * @param artistCount
	 *            Maximum number of artists to return. Default: 20
	 * @param artistOffset
	 *            Search result offset for artists. Used for paging. Default: 0
	 * @param albumCound
	 *            Maximum number of albums to return. Default: 20
	 * @param albumOffset
	 *            Search result offset for albums. Used for paging. Default:0
	 * @param songCount
	 *            Maximum number of songs to return. Default: 20
	 * @param songOffset
	 *            Search result offset for songs. Used for paging. Default: 0
	 * @return
	 * @since 1.4.0
	 * @throws SubsonicException
	 */
	public SearchResult2 search(String query, Integer artistCount,
			Integer artistOffset, Integer albumCount, Integer albumOffset,
			Integer songCount, Integer songOffset) throws SubsonicException;
}
