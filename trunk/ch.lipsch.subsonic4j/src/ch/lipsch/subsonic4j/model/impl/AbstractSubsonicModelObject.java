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

import ch.lipsch.subsonic4j.SubsonicException;
import ch.lipsch.subsonic4j.SubsonicException.ErrorType;
import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.internal.InternalSubsonicService;
import ch.lipsch.subsonic4j.tools.StateChecker;

public abstract class AbstractSubsonicModelObject {

	private final InternalSubsonicService service;

	public AbstractSubsonicModelObject(SubsonicService service) {
		StateChecker.check(service, "service");
		if (!(service instanceof InternalSubsonicService)) {
			throw new SubsonicException(
					"A subsonic service implementation must implement InternalSubsonicService",
					ErrorType.GENERIC);
		}
		this.service = (InternalSubsonicService) service;
	}

	public InternalSubsonicService getService() {
		return service;
	}
}
