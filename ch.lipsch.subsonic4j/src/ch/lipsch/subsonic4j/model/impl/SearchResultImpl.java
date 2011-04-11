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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.subsonic.restapi.SearchResult2;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.internal.Jaxb2ModelFactory;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.SearchResult;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class SearchResultImpl extends AbstractSubsonicModelObject implements
		SearchResult {

	private enum SearchType {
		SONGS, ALBUMS, ARTISTS
	};

	private final List<Song> songs;
	private final List<Directory> albums;
	private final List<Artist> artists;
	private final SearchParams initialSearchParams;

	public SearchResultImpl(List<Song> songs, List<Directory> albums,
			List<Artist> artists, SearchParams searchParams,
			SubsonicService service) {
		super(service);
		StateChecker.check(songs, "songs");
		StateChecker.check(albums, "albums");
		StateChecker.check(artists, "artists");
		StateChecker.check(searchParams, "searchParams");
		this.songs = songs;
		this.albums = albums;
		this.artists = artists;
		this.initialSearchParams = searchParams;
	}

	@Override
	public Iterator<Song> getSongs() {
		ResultDeliverer<Song> results = new ResultDeliverer<Song>(songs,
				SearchType.SONGS);
		return results;
	}

	@Override
	public Iterator<Directory> getAlbums() {
		ResultDeliverer<Directory> results = new ResultDeliverer<Directory>(
				albums, SearchType.ALBUMS);
		return results;
	}

	@Override
	public Iterator<Artist> getArtists() {
		ResultDeliverer<Artist> results = new ResultDeliverer<Artist>(artists,
				SearchType.ARTISTS);
		return results;
	}

	private final class ResultDeliverer<T> implements Iterator<T> {
		private List<T> currentResult = null;
		private volatile int currentIndex = 0;
		private final SearchType typeOfSearch;
		private volatile int currentSearchOffset = 0;

		private ResultDeliverer(List<T> initialResult, SearchType typeOfSearch) {
			currentResult = initialResult;
			this.typeOfSearch = typeOfSearch;
			currentSearchOffset = initialResult.size() + 1;
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = false;
			if (currentIndex == currentResult.size()) {
				// We are over the last index fetch a new result to check if
				// there
				// are more results to deliver.
				SearchResult2 result = getService()
						.search(initialSearchParams.getQuery(),
								initialSearchParams.getArtistCount(),
								currentSearchOffset,
								initialSearchParams.getAlbumCount(),
								currentSearchOffset,
								initialSearchParams.getSongCount(),
								currentSearchOffset);
				currentSearchOffset += Math.max(Math.max(result.getAlbum()
						.size(), result.getArtist().size()), result.getSong()
						.size());

				switch (typeOfSearch) {
				case ALBUMS:
					if (result.getAlbum().size() > 0) {
						currentIndex = 0;
						hasNext = true;
						currentResult = (List<T>) Jaxb2ModelFactory
								.createDirectories(result.getAlbum(),
										getService());
					}
					break;

				case ARTISTS:
					if (result.getArtist().size() > 0) {
						currentIndex = 0;
						hasNext = true;
						currentResult = (List<T>) Jaxb2ModelFactory
								.createArtistList(result.getArtist(),
										getService());
					}
					break;

				case SONGS:
					if (result.getSong().size() > 0) {
						currentIndex = 0;
						hasNext = true;
						currentResult = (List<T>) Jaxb2ModelFactory
								.createSongs(result.getSong(), getService());
					}
					break;
				default:
					throw new IllegalStateException(
							"Encountered invalid enum: " + typeOfSearch);
				}
			} else {
				hasNext = currentIndex <= (currentResult.size() - 1);
			}
			return hasNext;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			} else {
				currentIndex++;
				return currentResult.get(currentIndex - 1);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}
	}

	public final static class SearchParams {
		private String query = null;
		private Integer artistCount = null;
		private Integer artistOffset = null;
		private Integer albumCount = null;
		private Integer albumOffset = null;
		private Integer songCount = null;
		private Integer songOffset = null;

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public Integer getArtistCount() {
			return artistCount;
		}

		public void setArtistCount(Integer artistCount) {
			this.artistCount = artistCount;
		}

		public Integer getArtistOffset() {
			return artistOffset;
		}

		public void setArtistOffset(Integer artistOffset) {
			this.artistOffset = artistOffset;
		}

		public Integer getAlbumCount() {
			return albumCount;
		}

		public void setAlbumCount(Integer albumCount) {
			this.albumCount = albumCount;
		}

		public Integer getAlbumOffset() {
			return albumOffset;
		}

		public void setAlbumOffset(Integer albumOffset) {
			this.albumOffset = albumOffset;
		}

		public Integer getSongCount() {
			return songCount;
		}

		public void setSongCount(Integer songCount) {
			this.songCount = songCount;
		}

		public Integer getSongOffset() {
			return songOffset;
		}

		public void setSongOffset(Integer songOffset) {
			this.songOffset = songOffset;
		}
	}
}
