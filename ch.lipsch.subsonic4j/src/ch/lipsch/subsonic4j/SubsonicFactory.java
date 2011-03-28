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

import java.net.URL;

import ch.lipsch.subsonic4j.internal.SubsonicServiceImpl;

/**
 * Factory for {@link SubsonicService} creation.
 * 
 * @author Erwin Betschart
 * 
 */
public final class SubsonicFactory {

	public static SubsonicService createService(URL url,
			CredentialsProvider credentialsProvider) throws SubsonicException {
		return createService(url, true, credentialsProvider);
	}

	public static SubsonicService createService(URL url,
			boolean allowInvalidCerts, CredentialsProvider credentialsProvider)
			throws SubsonicException {
		return new SubsonicServiceImpl(url, allowInvalidCerts,
				credentialsProvider);
	}
}
