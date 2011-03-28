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

import java.util.Calendar;

import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.License;
import ch.lipsch.subsonic4j.tools.StateChecker;

public final class LicenseImpl extends AbstractSubsonicModelObject implements
		License {

	private final Calendar issueDate;
	private final String licenseKey;
	private final String licenseeEmail;

	public LicenseImpl(Calendar issueDate, String licenseKey,
			String licenseeEmail, SubsonicService service) {
		super(service);
		StateChecker.check(issueDate, "issueDate");
		StateChecker.check(licenseKey, "licenseKey");
		StateChecker.check(licenseeEmail, "licenseeEmail");

		this.issueDate = issueDate;
		this.licenseKey = licenseKey;
		this.licenseeEmail = licenseeEmail;
	}

	@Override
	public Calendar getIssueDate() {
		return issueDate;
	}

	@Override
	public String getLicenseKey() {
		return licenseKey;
	}

	@Override
	public String getLicenseeEmail() {
		return licenseeEmail;
	}

}
