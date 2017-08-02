package org.eclipse.epsilon.cbp.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.ChangeEvent;


public class EObjectHistory {

	private EObject eObject = null;
	private boolean isMoved = false;
	private Map<EObject, EObjectHistory> attributes = new HashMap<EObject, EObjectHistory>();
	private Map<EObject, EObjectHistory> references = new HashMap<EObject, EObjectHistory>();
	private Map<String, List<Line>> eventRecords = new HashMap<String, List<Line>>();
	
	public boolean isMoved() {
		return isMoved;
	}

	public void setMoved(boolean isMoved) {
		this.isMoved = isMoved;
	}

	public EObjectHistory(EObject eObject) {
		this.eObject = eObject;
	}

	public EObject getEObject() {
		return eObject;
	}

	public Map<EObject, EObjectHistory> getAttributes() {
		return attributes;
	}

	public Map<EObject, EObjectHistory> getReferences() {
		return references;
	}

	public Map<String, List<Line>> getEventRecords() {
		return eventRecords;
	}

	public void addEventRecord(ChangeEvent<?> event, int lineNumber) {
		this.addEventRecord(event, lineNumber, null);
	}

	public void addEventRecord(ChangeEvent<?> event, int lineNumber, Object value) {
		Line line = new Line(lineNumber, value);
		String eventName = event.getClass().getSimpleName();
		if (!this.getEventRecords().containsKey(eventName)) {
			List<Line> lines = new ArrayList<Line>(Arrays.asList(line));
			this.getEventRecords().put(eventName, lines);
		} else {
			this.getEventRecords().get(eventName).add(line);
		}
	}

}
