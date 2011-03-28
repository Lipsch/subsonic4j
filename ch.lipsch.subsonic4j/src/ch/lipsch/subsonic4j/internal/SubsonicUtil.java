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

import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.subsonic.restapi.Response;
import org.subsonic.restapi.ResponseStatus;

import ch.lipsch.subsonic4j.CredentialsProvider;
import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicException.ErrorType;
import ch.lipsch.subsonic4j.tools.StateChecker;

public final class SubsonicUtil {

	private static final String REST_PATH = "rest";
	private static final String SUBSONIC_VERSION = "1.4.0";
	private static final String CLIENT_APP = "subs4j";

	private SubsonicUtil() {
	}

	private static String addSlashIfNecessary(String url) {
		if (!url.endsWith("/")) {
			return url + "/";
		}
		return url;
	}

	/**
	 * Appends a key value pair to an url if the value is set. Must only be used
	 * there is already a parameter set on the url. * @param url
	 * 
	 * @param key
	 *            The parameter name. May be <code>null</code>.
	 * @param value
	 *            The value. May be <code>null</code>.
	 * @return The key & value added to the url:
	 *         http://12.34.56.78?first=bla&key=value
	 * @throws SubsonicException
	 *             In case the value can not be encode to fit into the url.
	 */
	public static String appendIfSet(String url, String key, String value)
			throws SubsonicException {
		if (key != null && value != null) {
			try {
				return url + "&" + key + "=" + URIUtil.encodeWithinQuery(value);
			} catch (URIException e) {
				throw new SubsonicException(ErrorType.GENERIC, e);
			}
		} else {
			return url;
		}
	}

	public static String appendCredentialsAsFirstParam(String url,
			CredentialsProvider credentials) {
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("?u=");
		sb.append(credentials.getUserName());
		sb.append("&p=");
		sb.append(SubsonicUtil.hexEncodePassword(credentials.getPassword()));
		sb.append("&v=");
		sb.append(SUBSONIC_VERSION);
		sb.append("&c=");
		sb.append(CLIENT_APP);
		return sb.toString();
	}

	public static String restifySubsonicUrl(URL url, String view) {
		StateChecker.check(url, "url");
		StateChecker.check(view, "view");
		String restifiedUrl = url.toString();
		restifiedUrl = addSlashIfNecessary(restifiedUrl);
		restifiedUrl += REST_PATH;
		restifiedUrl = addSlashIfNecessary(restifiedUrl);
		restifiedUrl += view;
		return restifiedUrl;
	}

	public static void throwExceptionIfNecessary(Response response)
			throws SubsonicException {
		if (response.getStatus().equals(ResponseStatus.FAILED)) {
			throw new SubsonicException(response.getError().getMessage(),
					response.getError().getCode());
		}
	}

	/**
	 * Copied from subsonic source (Util.java).
	 * 
	 * Converts an array of bytes into an array of characters representing the
	 * hexadecimal values of each byte in order. The returned array will be
	 * double the length of the passed array, as it takes two characters to
	 * represent any given byte.
	 * 
	 * @param data
	 *            Bytes to convert to hexadecimal characters.
	 * @return A string containing hexadecimal characters.
	 */
	public static String hexEncodePassword(String password) {
		final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		byte[] passwordInBytes;
		try {
			passwordInBytes = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		int length = passwordInBytes.length;
		char[] out = new char[length << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < length; i++) {
			out[j++] = HEX_DIGITS[(0xF0 & passwordInBytes[i]) >>> 4];
			out[j++] = HEX_DIGITS[0x0F & passwordInBytes[i]];
		}

		String encodedPass = new String(out);
		encodedPass = "enc:" + encodedPass;
		return encodedPass;
	}
}
