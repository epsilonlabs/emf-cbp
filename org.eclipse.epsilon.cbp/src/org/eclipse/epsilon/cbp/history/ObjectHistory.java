package org.eclipse.epsilon.cbp.history;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.ChangeEvent;


public class ObjectHistory {

	private EObject eObject = null;
	private boolean isMoved = false;
	private Map<EObject, AttributeHistory> attributeHistoryMap = new HashMap<EObject, AttributeHistory>();
	private Map<EObject, ReferenceHistory> referencesHistoryMap = new HashMap<EObject, ReferenceHistory>();
	private Map<String, EventHistory> getEventHistoryMap = new HashMap<String, EventHistory>();
	
	public boolean isMoved() {
		return isMoved;
	}

	public void setMoved(boolean isMoved) {
		this.isMoved = isMoved;
	}

	public ObjectHistory(EObject eObject) {
		this.eObject = eObject;
	}

	public void setEObject(EObject eObject) {
		this.eObject = eObject;
	}
	
	public EObject getEObject() {
		return eObject;
	}

	public Map<EObject, AttributeHistory> getAttributes() {
		return attributeHistoryMap;
	}

	public Map<EObject, ReferenceHistory> getReferences() {
		return referencesHistoryMap;
	}

	public Map<String, EventHistory> getEventHistoryMap() {
		return getEventHistoryMap;
	}

	public void addEventLine(ChangeEvent<?> event, int eventNumber) {
		this.addEventLine(event, eventNumber, null);
	}

	public void addEventLine(ChangeEvent<?> event, int eventNumber, Object value) {
		Line line = new Line(eventNumber, value);
		String eventName = event.getClass().getSimpleName();
		if (!this.getEventHistoryMap().containsKey(eventName)) {
			EventHistory lines = new EventHistory(Arrays.asList(line));
			this.getEventHistoryMap().put(eventName, lines);
		} else {
			this.getEventHistoryMap().get(eventName).add(line);
		}
	}

}
