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

import java.io.InputStream;

/**
 * Implement this interface to be able to receive streams from subsonic.
 * 
 * @author Erwin Betschart
 * 
 */
public interface StreamListener {

	/**
	 * Delivers the requested stream.
	 * 
	 * @param inputStream
	 *            The inputstream. The caller will must close the stream.
	 */
	public void receivedStream(InputStream inputStream);
}
