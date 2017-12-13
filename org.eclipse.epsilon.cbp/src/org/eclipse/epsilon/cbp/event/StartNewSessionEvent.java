package org.eclipse.epsilon.cbp.event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.lang.model.type.NullType;

public class StartNewSessionEvent extends ChangeEvent<NullType> {

	protected String sessionId;
	protected String time;

	public StartNewSessionEvent(String sessionId, String time) {
		this.sessionId = sessionId;
		this.time = time;
	}
	
	public StartNewSessionEvent(String sessionId) {
		this.sessionId = sessionId;
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.time = dateFormatGmt.format(new Date());
	}

	public StartNewSessionEvent() {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.sessionId = dateFormatGmt.format(new Date());
		this.time = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public String getTime(){
		return this.time;
	}

	@Override
	public void replay() {

	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}
