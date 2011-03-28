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
package ch.lipsch.subsonic4j;

import java.util.Calendar;
import java.util.List;

import org.subsonic.restapi.AlbumList;
import org.subsonic.restapi.ChatMessages;
import org.subsonic.restapi.Lyrics;
import org.subsonic.restapi.NowPlaying;
import org.subsonic.restapi.Playlist;
import org.subsonic.restapi.Playlists;
import org.subsonic.restapi.RandomSongs;
import org.subsonic.restapi.SearchResult2;
import org.subsonic.restapi.User;

import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.model.License;
import ch.lipsch.subsonic4j.model.MusicFolder;

/**
 * Please note that access to the REST API requires that the server has a valid
 * license (after a 30-day trial period). To get a license key you can give a
 * donation to the Subsonic project.
 * 
 * @author Erwin Betschart
 * 
 */
public interface SubsonicService {

	/**
	 * All supported bits rates. Used for streaming music.
	 * 
	 * @author Erwin Betschart
	 * 
	 */
	public enum BitRate {
		BITRATE_DEFAULT(0), BITRATE_32(32), BITRATE_40(40), BITRATE_48(48), BITRATE_56(
				56), BITRATE_64(64), BITRATE_80(80), BITRATE_96(96), BITRATE_112(
				112), BITRATE_128(128), BITRATE_160(160), BITRATE_192(192), BITRATE_224(
				224), BITRATE_256(256), BITRATE_320(320);

		private final int bitRate;

		BitRate(int bitRate) {
			this.bitRate = bitRate;
		}

		public int intValue() {
			return bitRate;
		}

	}

	/**
	 * Types of albums to request.
	 * 
	 * @author Erwin Betschart
	 * 
	 */
	public enum AlbumType {
		RANDOM("random"), NEWEST("newest"), HIGHEST("highest"), FREQUENT(
				"frequent"), RECENT("recent");

		private final String albumType;

		AlbumType(String albumType) {
			this.albumType = albumType;
		}

		@Override
		public String toString() {
			return albumType;
		}
	}

	/**
	 * Used to test connectivity with the server.
	 * 
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public void ping() throws SubsonicException;

	/**
	 * Get details about the software license. Takes no extra parameters.
	 * 
	 * @return The license.
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public License getLicense() throws SubsonicException;

	/**
	 * Returns all configured music folders.
	 * 
	 * @return The music folders.
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public List<MusicFolder> getMusicFolders() throws SubsonicException;

	/**
	 * Returns what is currently being played by all users.
	 * 
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public NowPlaying getNowPlaying() throws SubsonicException;

	/**
	 * Returns an indexed structure of all artists. If one of the parameters are
	 * provided both have to be provided.
	 * 
	 * @param musicFolder
	 *            If specified, only return artists in the given music folder.
	 *            See getMusicFolders. May be <code>null</code>
	 * @param ifModifiedSince
	 *            If specified, only return a result if the artist collection
	 *            has changed since the given time. May be <code>null</code>
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public List<Index> getIndexes(MusicFolder musicFolder,
			Calendar ifModifiedSince) throws SubsonicException;

	/**
	 * Returns a listing of all files in a music directory. Typically used to
	 * get list of albums for an artist, or list of songs for an album.
	 * 
	 * @param musicFolder
	 *            The music folder. Obtained by calls to getIndexes or
	 *            getMusicDirectory
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(
			MusicFolder musicFoler) throws SubsonicException;

	/**
	 * Returns a listing of all files in a music directory. Typically used to
	 * get list of albums for an artist, or list of songs for an album.
	 * 
	 * @param artist
	 *            The music folder. Obtained by calls to getIndexes or
	 *            getMusicDirectory
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(Artist artist)
			throws SubsonicException;

	/**
	 * Returns a listing of all files in a music directory. Typically used to
	 * get list of albums for an artist, or list of songs for an album.
	 * 
	 * @param folderId
	 *            The music folder. Obtained by calls to getIndexes or
	 *            getMusicDirectory
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(String folderId);

	/**
	 * Returns albums, artists and songs matching the given search criteria.
	 * 
	 * @param query
	 *            Search query.
	 * @return May return only a subset of all found item. If all items should
	 *         be received call
	 *         {@link #search(String, Integer, Integer, Integer, Integer, Integer, Integer)}
	 *         which has paging support.
	 * @since 1.4.0
	 * @throws SubsonicException
	 */
	public SearchResult2 search(String query) throws SubsonicException;

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
			Integer artistOffset, Integer albumCound, Integer albumOffset,
			Integer songCount, Integer songOffset) throws SubsonicException;

	/**
	 * Returns the ID and name of all saved playlists.
	 * 
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public Playlists getPlayLists() throws SubsonicException;

	/**
	 * Returns a listing of files in a saved playlist.
	 * 
	 * @param id
	 *            ID of the playlist to return, as obtained by getPlaylists.
	 * @return
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public Playlist getPlayList(String id) throws SubsonicException;

	/**
	 * Creates or updates a saved playlist. Note: The user must be authorized to
	 * create playlists (see Settings > Users > User is allowed to create and
	 * delete playlists).
	 * 
	 * @param playlistId
	 *            The playlist ID. (required if updating)
	 * @param name
	 *            The human-readable name of the playlist. (required if
	 *            creating)
	 * @param songIds
	 *            ID of a song in the playlist. Use one songId parameter for
	 *            each song in the playlist.
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public void createPlaylist(String playlistId, String name,
			List<String> songIds) throws SubsonicException;

	/**
	 * Deletes a saved playlist.
	 * 
	 * @param id
	 *            ID of the playlist to delete, as obtained by getPlaylists.
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public void deletePlaylist(String id) throws SubsonicException;

	/**
	 * Downloads a given music file.
	 * 
	 * @param id
	 *            A string which uniquely identifies the file to download.
	 *            Obtained by calls to getMusicDirectory.
	 * @param listener
	 *            Listener to which the stream is delivered.
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public void download(String id, StreamListener listener)
			throws SubsonicException;

	/**
	 * Streams a given music file.
	 * 
	 * @param A
	 *            string which uniquely identifies the file to stream. Obtained
	 *            by calls to getMusicDirectory.
	 * @param maxBitRate
	 *            If specified, the server will attempt to limit the bitrate to
	 *            this value, in kilobits per second. If set to zero, no limit
	 *            is imposed.
	 * @param listener
	 *            Listener to which the music stream is delivered.
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public void stream(String id, BitRate maxBitRate, StreamListener listener)
			throws SubsonicException;

	/**
	 * Returns a cover art image.
	 * 
	 * @param id
	 *            A string which uniquely identifies the cover art file to
	 *            download. Obtained by calls to getMusicDirectory.
	 * @param size
	 *            If specified, scale image to this size.
	 * @param listener
	 *            Listener to which the stream is delivered.
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public void getCoverArt(String id, Integer size, StreamListener listener)
			throws SubsonicException;

	/**
	 * Changes the password of an existing Subsonic user, using the following
	 * parameters. You can only change your own password unless you have admin
	 * privileges.
	 * 
	 * @param username
	 *            The name of the user which should change its password.
	 * @param password
	 *            The new password of the new user, either in clear text of
	 *            hex-encoded (see above).
	 * @since 1.0.0
	 * @throws SubsonicException
	 */
	public void changePassword(String username, String password)
			throws SubsonicException;

	/**
	 * Get details about a given user, including which authorization roles it
	 * has. Can be used to enable/disable certain features in the client, such
	 * as jukebox control.
	 * 
	 * @param username
	 *            The name of the user to retrieve. You can only retrieve your
	 *            own user unless you have admin privileges.
	 * @return
	 * @since 1.3.0
	 * @throws SubsonicException
	 */
	public User getUser(String username) throws SubsonicException;

	/**
	 * Creates a new Subsonic user, using the following parameters:
	 * 
	 * @param user
	 *            The name of the new user. (required)
	 * @param password
	 *            The password of the new user, either in clear text of
	 *            hex-encoded (see above). (required)
	 * @param ldapAuthenticated
	 *            Whether the user is authenicated in LDAP. Default: false
	 * @param adminRole
	 *            Whether the user is administrator. Default: false
	 * @param settingsRole
	 *            Whether the user is allowed to change settings and password.
	 *            Default: true
	 * @param streamRole
	 *            Whether the user is allowed to play files. Default: true
	 * @param jukeboxRole
	 *            Whether the user is allowed to play files in jukebox mode.
	 *            Default: false
	 * @param downloadRole
	 *            Whether the user is allowed to download files. Default: false
	 * @param uploadRole
	 *            Whether the user is allowed to upload files. Default: false
	 * @param playlistRole
	 *            Whether the user is allowed to create and delete playlists.
	 *            Default: false
	 * @param coverArtRole
	 *            Whether the user is allowed to change cover art and tags.
	 *            Default: false
	 * @param commentRole
	 *            Whether the user is allowed to create and edit comments and
	 *            ratings. Default: false
	 * @param podcastRole
	 *            Whether the user is allowed to administrate Podcasts. Default:
	 *            false
	 * @since 1.1.0
	 * @throws SubsonicException
	 */
	public void createUser(String user, String password,
			Boolean ldapAuthenticated, Boolean adminRole, Boolean settingsRole,
			Boolean streamRole, Boolean jukeboxRole, Boolean downloadRole,
			Boolean uploadRole, Boolean playlistRole, Boolean coverArtRole,
			Boolean commentRole, Boolean podcastRole) throws SubsonicException;

	/**
	 * Deletes an existing Subsonic user, using the following parameters:
	 * 
	 * @param username
	 *            The name of the user to delete.
	 * @since 1.3.0
	 * @throws SubsonicException
	 */
	public void deleteUser(String username) throws SubsonicException;

	/**
	 * Returns the current visible (non-expired) chat messages.
	 * 
	 * @param since
	 *            Only return messages newer than this time (in millis since Jan
	 *            1 1970). May be <code>null</code>.
	 * @return
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public ChatMessages getChatMessages(Long since) throws SubsonicException;

	/**
	 * Adds a message to the chat log.
	 * 
	 * @param message
	 *            The chat message.
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public void addChatMessage(String message) throws SubsonicException;

	/**
	 * Returns a list of random, newest, highest rated etc. albums. Similar to
	 * the album lists on the home page of the Subsonic web interface.
	 * 
	 * @param albumType
	 *            The list type. Must be one of the following: random, newest,
	 *            highest, frequent, recent.
	 * @param size
	 *            The number of albums to return. Max 500. May be
	 *            <code>null</code>.
	 * @param offset
	 *            The list offset. Useful if you for example want to page
	 *            through the list of newest albums. Max 5000. May be
	 *            <code>null</code>.
	 * @return
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public AlbumList getAlbumList(AlbumType albumType, Integer size,
			Integer offset) throws SubsonicException;

	/**
	 * Returns ten random songs.
	 * 
	 * @return
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public RandomSongs getRandomSongs() throws SubsonicException;

	/**
	 * Returns random songs matching the given criteria.
	 * 
	 * @param size
	 *            The maximum number of songs to return. Max 500. If
	 *            <code>null</code> default 10 is taken.
	 * @param genre
	 *            Only returns songs belonging to this genre. May be
	 *            <code>null</code>.
	 * @param fromYear
	 *            Only return songs published after or in this year. May be
	 *            <code>null</code>.
	 * @param toYear
	 *            Only return songs published before or in this year. May be
	 *            <code>null</code>.
	 * @param musicFolder
	 *            Only return songs in the given music folder. See
	 *            getMusicFolders. May be <code>null</code>.
	 * @return
	 * @since 1.2.0
	 * @throws SubsonicException
	 */
	public RandomSongs getRandomSongs(Integer size, String genre,
			Integer fromYear, Integer toYear, MusicFolder musicFolder)
			throws SubsonicException;

	/**
	 * Searches for and returns lyrics for a given song.
	 * 
	 * @param artist
	 *            The artist name. May be <code>null</code>.
	 * @param title
	 *            The song title. May be <code>null</code>.
	 * @since 1.2.0
	 * @return
	 * @throws SubsonicException
	 */
	public Lyrics getLyrics(String artist, String title)
			throws SubsonicException;

	// TODO Jukebox conrol

	/**
	 * Disposes the subsonic service.
	 */
	public void disposeService();

	/**
	 * Checks if this service is disposed.
	 * 
	 * @return True if the service is disposed.
	 */
	public boolean isDisposed();

	public String getStreamUrl(String id) throws SubsonicException;
}