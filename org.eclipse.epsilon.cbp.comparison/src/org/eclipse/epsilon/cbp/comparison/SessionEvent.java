package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.List;

public class SessionEvent extends ComparisonEvent {

	private String sessionId;
	private String stringTime;
	private ComparisonEvent sessionComparisonEvent;
	private List<ComparisonEvent> comparisonEvents = new ArrayList<>();

	public SessionEvent(ComparisonEvent sessionComparisonEvent) {
		this.sessionComparisonEvent = sessionComparisonEvent;
		this.setEventType(sessionComparisonEvent.getEventType());
		this.setTarget(sessionComparisonEvent.getTarget());
		this.setChangeEvent(sessionComparisonEvent.getChangeEvent());
		this.setEventString(sessionComparisonEvent.getEventString());
		this.setFeature(sessionComparisonEvent.getFeature());
		this.setFeatureName(sessionComparisonEvent.getFeatureName());
		this.setFrom(sessionComparisonEvent.getFrom());
		this.setPosition(sessionComparisonEvent.getPosition());
		this.setTargetId(sessionComparisonEvent.getTargetId());
		this.setTo(sessionComparisonEvent.getTo());
		this.setValue(sessionComparisonEvent.getValue());
		this.setValueId(sessionComparisonEvent.getValueId());
	}

	public ComparisonEvent getSessionComparisonEvent() {
		return sessionComparisonEvent;
	}

	public void setSessionComparisonEvent(ComparisonEvent sessionComparisonEvent) {
		this.sessionComparisonEvent = sessionComparisonEvent;
	}

	public List<ComparisonEvent> getComparisonEvents() {
		return comparisonEvents;
	}

	public void setComparisonEvents(List<ComparisonEvent> comparisonEvents) {
		this.comparisonEvents = comparisonEvents;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getStringTime() {
		return stringTime;
	}

	public void setStringTime(String stringTime) {
		this.stringTime = stringTime;
	}

}
