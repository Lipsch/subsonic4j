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
package ch.lipsch.subs4j;

import ch.lipsch.subsonic4j.CredentialsProvider;
import ch.lipsch.subsonic4j.tools.StateChecker;

public final class TestConfig {

	public static final String SUBSONIC_URL = "http://localhost:4040";
	public static final String USER1_NAME = "test1";
	public static final String USER1_PASS = "test1";

	public static final CredentialsProvider USER1_CREDENTIALS = new ChangeableCredentialsProvider();

	private TestConfig() {
	}

	public static class ChangeableCredentialsProvider implements
			CredentialsProvider {

		private String password = TestConfig.USER1_PASS;

		public void setPassword(String password) {
			StateChecker.check(password, "password");
			this.password = password;
		}

		@Override
		public String getUserName() {
			return TestConfig.USER1_NAME;
		}

		@Override
		public String getPassword() {
			return password;
		}

	}
}
