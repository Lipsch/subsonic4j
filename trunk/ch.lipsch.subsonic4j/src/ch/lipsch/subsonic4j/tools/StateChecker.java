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
package ch.lipsch.subsonic4j.tools;

import java.text.MessageFormat;

/**
 * Use this class to check the state of variables / parameters.
 * 
 * @author Erwin Betschart
 * 
 */
public final class StateChecker {

	private StateChecker() {
	}

	public static void check(Object object, String identifier) {
		if (object == null) {
			throw new IllegalStateException(MessageFormat.format(
					"{0} must not be null.", identifier)); //$NON-NLS-1$
		}
	}

	public static void checkGreaterOrEqual(int valueToCheck,
			int greaterOrEqual, String identifier) {
		if (valueToCheck < greaterOrEqual) {
			throw new IllegalStateException(MessageFormat.format(
					"{0} [{1}] must be greater or equal than {2}", identifier,
					valueToCheck, greaterOrEqual));
		}
	}

	public static void check(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}
}
