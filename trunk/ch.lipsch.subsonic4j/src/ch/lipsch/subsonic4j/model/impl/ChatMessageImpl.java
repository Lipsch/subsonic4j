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
import ch.lipsch.subsonic4j.model.ChatMessage;
import ch.lipsch.subsonic4j.tools.StateChecker;

public class ChatMessageImpl extends AbstractSubsonicModelObject implements
		ChatMessage {

	private final String message;
	private final Calendar time;
	private final String author;

	public ChatMessageImpl(String message, Calendar time, String author,
			SubsonicService service) {
		super(service);
		StateChecker.check(message, "message");
		StateChecker.check(time, "time");
		StateChecker.check(author, "author");
		this.message = message;
		this.time = time;
		this.author = author;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Calendar getTime() {
		return time;
	}

	@Override
	public String getAuthor() {
		return author;
	}
}
