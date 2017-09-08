package org.eclipse.epsilon.cbp.event;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.lang.model.type.NullType;

public class StartNewSessionEvent extends ChangeEvent<NullType> {

	protected String sessionId;

	public StartNewSessionEvent(String sessionId) {
		this.sessionId = sessionId;
	}

	public StartNewSessionEvent() {
		this.sessionId = ((new SimpleDateFormat("yyyyMMddHHmmssSSS"))).format(new Date());
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void replay() {

	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}
