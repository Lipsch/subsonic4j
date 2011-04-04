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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.subsonic.restapi.Response;

import ch.lipsch.subsonic4j.CredentialsProvider;
import ch.lipsch.subsonic4j.StreamListener;
import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicException.ErrorType;
import ch.lipsch.subsonic4j.SubsonicService;
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
import ch.lipsch.subsonic4j.tools.StateChecker;

/**
 * This class is thread-safe.
 * 
 * @author Erwin Betschart
 * 
 */
public class SubsonicServiceImpl implements SubsonicService {
	private static final String JAXB_CONTEXT_PATH = "org.subsonic.restapi";
	private static final String PATH_PING = "ping.view";
	private static final String PATH_LICENSE = "getLicense.view";
	private static final String PATH_GET_MUSIC_FOLDERS = "getMusicFolders.view";
	private static final String PATH_GET_NOW_PLAYING = "getNowPlaying.view";
	private static final String PATH_GET_INDEXES = "getIndexes.view";
	private static final String PATH_GET_MUSIC_DIR = "getMusicDirectory.view";
	private static final String PATH_SEARCH2 = "search2.view";
	private static final String PATH_GET_PLAYLISTS = "getPlaylists.view";
	private static final String PATH_GET_PLAYLIST = "getPlaylist.view";
	private static final String PATH_CREATE_PLAYLIST = "createPlaylist.view";
	private static final String PATH_DELETE_PLAYLIST = "deletePlaylist.view";
	private static final String PATH_CHANGE_PASSWORD = "changePassword.view";
	private static final String PATH_GET_USER = "getUser.view";
	private static final String PATH_CREATE_USER = "createUser.view";
	private static final String PATH_DELETE_USER = "deleteUser.view";
	private static final String PATH_GET_CHAT_MESSAGE = "getChatMessages.view";
	private static final String PATH_ADD_CHAT_MESSAGE = "addChatMessage.view";
	private static final String PATH_GET_ALBUM_LIST = "getAlbumList.view";
	private static final String PATH_GET_RANDOM_SONGS = "getRandomSongs.view";
	private static final String PATH_GET_LYRICS = "getLyrics.view";
	private static final String PATH_DOWNLOAD = "download.view";
	private static final String PATH_STREAM = "stream.view";
	private static final String PATH_GET_COVER_ART = "getCoverArt.view";
	private static final String HTTP_CONTENT_TYPE_TEXT_XML = "text/xml";
	private static final String HTTP_RESPONSE_HEADER_CONTENT_TYPE = "Content-Type";
	/**
	 * The root url of the subsonic server. Access must be synchronized by
	 * {@link SubsonicServiceImpl} instance.
	 */
	private URL url = null;

	/**
	 * Provides credentials for subsonic access. Access must be synchronized
	 * with {@link SubsonicServiceImpl} instance.
	 */
	private CredentialsProvider credentialsProvider = null;

	/**
	 * The jaxb context used for xml serialization. Access must be synchronized
	 * by {@link SubsonicServiceImpl} instance.
	 */
	private JAXBContext jaxbContext = null;

	/**
	 * Unmashals xml content received from subsonic server. Access must be
	 * synchronized by {@link SubsonicServiceImpl} instance.
	 */
	private Unmarshaller jaxbUnmarshaller = null;

	/**
	 * Saves the state if invalid server certificates should be tolerated.
	 * Access must be synchronized by {@link SubsonicServiceImpl} instance.
	 */
	private boolean allowInvalidCerts = true;

	/**
	 * Saves the default hostname verifier, in order to restore it if
	 * {@link #allowUntrustedCerts()} is true. Access must be synchronized by
	 * {@link SubsonicServiceImpl} instance.
	 */
	private HostnameVerifier defaultHostnameVerifier = null;

	/**
	 * If the service is disposed it must not be used anymore. Access must be
	 * synchronized through {@link SubsonicServiceImpl} instance.
	 */
	private boolean disposed = false;

	private final HttpClient httpClient = new HttpClient();

	public SubsonicServiceImpl(URL url, boolean allowInvalidCerts,
			CredentialsProvider credentialsProvider) throws SubsonicException {
		StateChecker.check(url, "url");
		StateChecker.check(credentialsProvider, "credentialsProvider");
		this.url = url;
		this.allowInvalidCerts = allowInvalidCerts;
		this.credentialsProvider = credentialsProvider;

		try {
			initJaxb();

			if (allowInvalidCerts) {
				allowUntrustedCerts();
			}
		} catch (KeyManagementException e) {
			throw new SubsonicException(
					"Unable to allow untrusted certificates.",
					ErrorType.GENERIC, e);
		} catch (NoSuchAlgorithmException e) {
			throw new SubsonicException(
					"Unable to allow untrusted certificates.",
					ErrorType.GENERIC, e);
		} catch (JAXBException e) {
			throw new SubsonicException("Could not initialize jaxb.",
					ErrorType.GENERIC, e);
		}
	}

	private synchronized void allowUntrustedCerts()
			throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0],
				new TrustManager[] { new DefaultTrustManager() },
				new SecureRandom());
		SSLContext.setDefault(ctx);

		HostnameVerifier hv = new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		defaultHostnameVerifier = HttpsURLConnection
				.getDefaultHostnameVerifier();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	private synchronized void initJaxb() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(JAXB_CONTEXT_PATH);
		jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	}

	private synchronized Response unmarshalResponse(InputStream inputStream)
			throws JAXBException {
		Object unmarshallObj = jaxbUnmarshaller.unmarshal(inputStream);
		JAXBElement element = (JAXBElement) unmarshallObj;
		Response response = (Response) element.getValue();

		return response;
	}

	private Response fetchResponse(String connectionUrl)
			throws SubsonicException {
		HttpMethod method = new GetMethod(connectionUrl);
		Response response = null;
		try {
			httpClient.executeMethod(method);
			InputStream responseStream = method.getResponseBodyAsStream();

			response = unmarshalResponse(responseStream);

		} catch (HttpException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		} catch (IOException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		} catch (JAXBException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		} finally {
			method.releaseConnection();
		}
		SubsonicUtil.throwExceptionIfNecessary(response);
		return response;
	}

	private void fetchAsyncStream(String url, final StreamListener listener)
			throws IOException, JAXBException, SubsonicException {
		final HttpMethod method = new GetMethod(url);
		httpClient.executeMethod(method);

		final InputStream responseStream = method.getResponseBodyAsStream();
		Header contentTypeHeader = method
				.getResponseHeader(HTTP_RESPONSE_HEADER_CONTENT_TYPE);

		if (contentTypeHeader.getValue().startsWith(HTTP_CONTENT_TYPE_TEXT_XML)) {
			// There was an error
			Response response = unmarshalResponse(responseStream);
			SubsonicUtil.throwExceptionIfNecessary(response);
		} else {
			new Thread("StreamDeliverer") {
				@Override
				public void run() {
					try {
						listener.receivedStream(responseStream);
					} finally {
						method.releaseConnection();
					}
				};
			}.start();
		}
	}

	@Override
	public void ping() throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_PING);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		fetchResponse(restifiedUrl);
	}

	@Override
	public License getLicense() throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_LICENSE);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());

		Response response = fetchResponse(restifiedUrl);
		org.subsonic.restapi.License jaxbLicense = response.getLicense();
		return Jaxb2ModelFactory.createLicense(jaxbLicense, this);
	}

	@Override
	public List<MusicFolder> getMusicFolders() throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_MUSIC_FOLDERS);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createMusicFolderList(
				response.getMusicFolders(), this);
	}

	@Override
	public List<NowPlaying> getNowPlaying() throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_NOW_PLAYING);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createNowPlaying(response.getNowPlaying(),
				this);
	}

	@Override
	public List<Index> getIndexes(MusicFolder musicFolder,
			Calendar ifModifiedSince) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_INDEXES);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(
				restifiedUrl,
				"musicFolderId",
				musicFolder == null ? null : Integer.toString(musicFolder
						.getId()));
		restifiedUrl = SubsonicUtil.appendIfSet(
				restifiedUrl,
				"ifModifiedSince",
				ifModifiedSince != null ? Long.toString(ifModifiedSince
						.getTimeInMillis()) : null);

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createIndexList(response.getIndexes(), this);
	}

	@Override
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(Artist artist)
			throws SubsonicException {
		throwIfDisposed();
		StateChecker.check(artist, "artist");
		return getMusicDirectory(artist.getId());
	}

	@Override
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(
			MusicFolder musicFolder) throws SubsonicException {
		throwIfDisposed();
		StateChecker.check(musicFolder, "musicFolder");
		return getMusicDirectory(Integer.toString(musicFolder.getId()));
	}

	@Override
	public ch.lipsch.subsonic4j.model.Directory getMusicDirectory(
			String folderId) {
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_MUSIC_DIR);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", folderId);

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createDirectory(response.getDirectory(), this);
	}

	@Override
	public SearchResult search(String query) throws SubsonicException {
		return search(query, 20, 0, 20, 0, 20, 0);
	}

	@Override
	public SearchResult search(String query, Integer artistCount,
			Integer artistOffset, Integer albumCount, Integer albumOffset,
			Integer songCount, Integer songOffset) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_SEARCH2);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "query", query);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "artistCount",
				Integer.toString(artistCount));
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "artistOffset",
				Integer.toString(artistOffset));
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "albumCount",
				Integer.toString(albumCount));
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "albumOffset",
				Integer.toString(albumOffset));
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "songCount",
				Integer.toString(songCount));
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "songOffset",
				Integer.toString(songOffset));

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createSearchResult(
				response.getSearchResult2(), this);
	}

	@Override
	public List<Playlist> getPlayLists() throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_PLAYLISTS);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createPlaylists(response.getPlaylists(), this);
	}

	@Override
	public List<Song> getPlayList(String id) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_PLAYLIST);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", id);

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createSongs(response.getPlaylist().getEntry(),
				this);
	}

	@Override
	public void createPlaylist(String playlistId, String name,
			List<String> songIds) throws SubsonicException {
		throw new UnsupportedOperationException(
				"Update playlist seems not to work.");
		// TODO -> create a call to update the playlist.
		// throwIfDisposed();
		// String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
		// PATH_CREATE_PLAYLIST);
		// restifiedUrl =
		// SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
		// getCredentialsProvider());
		// restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "playlistId",
		// playlistId);
		// restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "name", name);
		// for (String songId : songIds) {
		// restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "songId",
		// songId);
		// }
		//
		// fetchResponse(restifiedUrl);
	}

	@Override
	public void deletePlaylist(String id) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_DELETE_PLAYLIST);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", id);

		fetchResponse(restifiedUrl);
	}

	@Override
	public void download(String id, StreamListener listener)
			throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_DOWNLOAD);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", id);

		try {
			fetchAsyncStream(restifiedUrl, listener);
		} catch (IOException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		} catch (JAXBException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		}
	}

	@Override
	public void stream(String id, BitRate maxBitRate, StreamListener listener)
			throws SubsonicException {
		StateChecker.check(id, "id");
		StateChecker.check(listener, "listener");

		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_STREAM);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", id);
		restifiedUrl = SubsonicUtil.appendIfSet(
				restifiedUrl,
				"maxBitRate",
				maxBitRate == null ? null : Integer.toString(maxBitRate
						.intValue()));

		try {
			fetchAsyncStream(restifiedUrl, listener);
		} catch (IOException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		} catch (JAXBException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		}
	}

	@Override
	public void getCoverArt(String id, Integer size, StreamListener listener)
			throws SubsonicException {
		throwIfDisposed();
		StateChecker.check(id, "id");
		StateChecker.check(listener, "listener");

		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_COVER_ART);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", id);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "size",
				size == null ? null : Integer.toString(size));

		try {
			fetchAsyncStream(restifiedUrl, listener);
		} catch (IOException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		} catch (JAXBException e) {
			throw new SubsonicException(SubsonicException.ErrorType.GENERIC, e);
		}
	}

	@Override
	public void changePassword(String username, String password)
			throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_CHANGE_PASSWORD);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "username",
				username);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "password",
				password);

		fetchResponse(restifiedUrl);
	}

	@Override
	public User getUser(String username) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_USER);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "username",
				username);

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createUser(response.getUser(), this);
	}

	@Override
	public void createUser(String username, String password,
			Boolean ldapAuthenticated, Boolean adminRole, Boolean settingsRole,
			Boolean streamRole, Boolean jukeboxRole, Boolean downloadRole,
			Boolean uploadRole, Boolean playlistRole, Boolean coverArtRole,
			Boolean commentRole, Boolean podcastRole) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_CREATE_USER);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "username",
				username);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "password",
				password);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl,
				"ldapAuthenticated", ldapAuthenticated == null ? null
						: ldapAuthenticated.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "adminRole",
				adminRole == null ? null : adminRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "settingsRole",
				settingsRole == null ? null : settingsRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "streamRole",
				streamRole == null ? null : streamRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "jukeboxRole",
				jukeboxRole == null ? null : jukeboxRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "downloadRole",
				downloadRole == null ? null : downloadRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "uploadRole",
				uploadRole == null ? null : uploadRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "playlistRole",
				playlistRole == null ? null : playlistRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "coverArtRole",
				coverArtRole == null ? null : coverArtRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "commentRole",
				commentRole == null ? null : commentRole.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "podcastRole",
				podcastRole == null ? null : podcastRole.toString());

		fetchResponse(restifiedUrl);
	}

	@Override
	public void deleteUser(String username) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_DELETE_USER);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "username",
				username);

		fetchResponse(restifiedUrl);
	}

	@Override
	public List<ChatMessage> getChatMessages(Calendar since)
			throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_CHAT_MESSAGE);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "since",
				since == null ? null : Long.toString(since.getTimeInMillis()));

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createChatMessages(response.getChatMessages(),
				this);
	}

	private synchronized URL getUrl() {
		return url;
	}

	@Override
	public void addChatMessage(String message) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_ADD_CHAT_MESSAGE);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "message",
				message);

		fetchResponse(restifiedUrl);
	}

	@Override
	public List<Directory> getAlbumList(AlbumType albumType, Integer size,
			Integer offset) throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_ALBUM_LIST);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "type",
				albumType.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "size",
				size == null ? null : size.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "offset",
				offset == null ? null : offset.toString());

		Response response;
		response = fetchResponse(restifiedUrl);

		return Jaxb2ModelFactory.createDirectories(response.getAlbumList(),
				this);
	}

	@Override
	public List<Song> getRandomSongs() throws SubsonicException {
		return getRandomSongs(null, null, null, null, null);
	}

	@Override
	public List<Song> getRandomSongs(Integer size, String genre,
			Integer fromYear, Integer toYear, MusicFolder musicFolder)
			throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_RANDOM_SONGS);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "size",
				size != null ? size.toString() : null);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "genre",
				genre != null ? genre.toString() : null);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "fromYear",
				fromYear != null ? fromYear.toString() : null);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "toYear",
				toYear != null ? toYear.toString() : null);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "musicFolderId",
				musicFolder != null ? Integer.toString(musicFolder.getId())
						: null);

		Response response;
		response = fetchResponse(restifiedUrl);
		return Jaxb2ModelFactory.createSongs(response.getRandomSongs()
				.getSong(), this);
	}

	private synchronized CredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	@Override
	public String getLyrics(String artist, String title)
			throws SubsonicException {
		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_GET_LYRICS);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "artist",
				artist.toString());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "title",
				title.toString());

		Response response;
		response = fetchResponse(restifiedUrl);
		return response.getLyrics().getContent();
	}

	/**
	 * This class is used for allowing untrusted certificates.
	 * 
	 * @author Erwin Betschart
	 * 
	 */
	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

	@Override
	public synchronized void disposeService() {
		disposed = true;

		// Restores the original hostname verifier.
		if (allowInvalidCerts && defaultHostnameVerifier != null) {
			HttpsURLConnection
					.setDefaultHostnameVerifier(defaultHostnameVerifier);
		}
	}

	@Override
	public synchronized boolean isDisposed() {
		return disposed;
	}

	private void throwIfDisposed() throws SubsonicException {
		if (isDisposed()) {
			throw new SubsonicException("Service is disposed",
					ErrorType.IS_DISPOSED);
		}
	}

	@Override
	public String getStreamUrl(String id) throws SubsonicException {
		StateChecker.check(id, "id");

		throwIfDisposed();
		String restifiedUrl = SubsonicUtil.restifySubsonicUrl(getUrl(),
				PATH_STREAM);
		restifiedUrl = SubsonicUtil.appendCredentialsAsFirstParam(restifiedUrl,
				getCredentialsProvider());
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "id", id);
		restifiedUrl = SubsonicUtil.appendIfSet(restifiedUrl, "maxBitRate",
				Integer.toString(BitRate.BITRATE_DEFAULT.intValue()));

		return restifiedUrl;
	}
}
