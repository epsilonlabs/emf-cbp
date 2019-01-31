package org.eclipse.epsilon.cbp.comparison.event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.lang.model.type.NullType;

public class CBPStartNewSessionEvent extends CBPChangeEvent<NullType> {

    protected String sessionId;
    protected String time;

    public CBPStartNewSessionEvent(String sessionId, String time) {
	this.sessionId = sessionId;
	this.time = time;
    }

    public CBPStartNewSessionEvent(String sessionId) {
	this.sessionId = sessionId;
	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");
	dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	this.time = dateFormatGmt.format(new Date());
    }

    public CBPStartNewSessionEvent() {
	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");
	dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	this.sessionId = dateFormatGmt.format(new Date());
	this.time = sessionId;
    }

    public String getSessionId() {
	return sessionId;
    }

    public String getTime() {
	return this.time;
    }

    @Override
    public String toString() {
	return String.format("session %s", this.getSessionId());

    }
}
