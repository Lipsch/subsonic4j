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

import ch.lipsch.subsonic4j.tools.StateChecker;

public final class SubsonicException extends RuntimeException {

	private static final long serialVersionUID = -2928790174149706943L;

	/**
	 * Type of error see also http://www.subsonic.org/pages/api.jsp.
	 */
	public enum ErrorType {
		/* 0 A generic error. My also be a local client problem. */
		GENERIC,
		/* 10 Required parameter is missing. */
		MISSING_REQUIRED_PARAMETER,
		/* 20 Incompatible Subsonic REST protocol version. Client must upgrade. */
		CLIENT_MUST_UPGRADE,
		/* 30 Incompatible Subsonic REST protocol version. Server must upgrade. */
		SERVER_MUST_UPGRADE,
		/* 40 Wrong username or password. */
		WRONG_USER_OR_PASS,
		/* 50 User is not authorized for the given operation. */
		UNAUTHERIZED_FOR_OPERATION,
		/*
		 * 60 The trial period for the Subsonic server is over. Please donate to
		 * get a license key. Visit subsonic.org for details.
		 */
		TRIAL_PERIOD_EXCEEDED,
		/* 70 The requested data was not found. */
		DATA_NOT_FOUND,
		/*
		 * -1 Is thrown if the subsonic service is disposed. Not an official
		 * subsonic error code.
		 */
		IS_DISPOSED
	}

	private final ErrorType errorType;

	public SubsonicException(String message, int errorCode, Throwable throwable) {
		super(message, throwable);
		switch (errorCode) {
		case 0:
			errorType = ErrorType.GENERIC;
			break;

		case 10:
			errorType = ErrorType.MISSING_REQUIRED_PARAMETER;
			break;

		case 20:
			errorType = ErrorType.CLIENT_MUST_UPGRADE;
			break;

		case 30:
			errorType = ErrorType.SERVER_MUST_UPGRADE;
			break;

		case 40:
			errorType = ErrorType.WRONG_USER_OR_PASS;
			break;

		case 50:
			errorType = ErrorType.UNAUTHERIZED_FOR_OPERATION;
			break;

		case 60:
			errorType = ErrorType.TRIAL_PERIOD_EXCEEDED;
			break;

		case 70:
			errorType = ErrorType.DATA_NOT_FOUND;
			break;

		case -1:
			errorType = ErrorType.IS_DISPOSED;
			break;
		default:
			errorType = ErrorType.GENERIC;
			break;
		}
	}

	public SubsonicException(ErrorType errorType, Throwable throwable) {
		super(throwable);
		StateChecker.check(errorType, "errorType");
		this.errorType = errorType;
	}

	public SubsonicException(String message, ErrorType errorType,
			Throwable throwable) {
		super(message, throwable);
		StateChecker.check(errorType, "errorType");
		this.errorType = errorType;
	}

	public SubsonicException(String message, int errorCode) {
		this(message, errorCode, null);
	}

	public SubsonicException(String message, ErrorType errorType) {
		super(message);
		StateChecker.check(errorType, "errorType");
		this.errorType = errorType;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
}
