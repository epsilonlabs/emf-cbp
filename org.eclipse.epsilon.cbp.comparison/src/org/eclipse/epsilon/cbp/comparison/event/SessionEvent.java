package org.eclipse.epsilon.cbp.comparison.event;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SessionEvent extends ComparisonEvent {

	private String sessionId;
	private String stringTime;
	private ComparisonEvent sessionComparisonEvent;
	private List<ComparisonEvent> comparisonEvents = new ArrayList<>();

	public SessionEvent(ComparisonEvent sessionComparisonEvent)
			throws ParserConfigurationException, TransformerConfigurationException {
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

		if (sessionComparisonEvent.getChangeEvent() instanceof StartNewSessionEvent) {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			Document document = documentBuilder.newDocument();
			StartNewSessionEvent s = ((StartNewSessionEvent) sessionComparisonEvent.getChangeEvent());
			Element e = document.createElement("session");
			e.setAttribute("id", s.getSessionId());
			e.setAttribute("time", s.getTime());
		}
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
