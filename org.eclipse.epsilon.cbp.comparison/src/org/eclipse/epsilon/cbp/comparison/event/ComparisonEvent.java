package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.epsilon.cbp.event.ChangeEvent;

public class ComparisonEvent {
	ChangeEvent<?> changeEvent = null;
	EObject target = null;
	String targetId = null;
	Object value = null;
	String valueId = null;
	EStructuralFeature feature = null;
	String featureName = null;
	int position = -1;
	int from = -1;
	int to = -1;
	Class<?> eventType = null;
	String eventString = null;
	boolean isConflicted = false;
	ConflictedEventPair conflictedEventPair = null;
	String composite = null;


	public ComparisonEvent() {
	}
	
	public ComparisonEvent(Class<?> eventType, ChangeEvent<?> changeEvent, String eventString) {
		this.eventType = eventType;
		this.changeEvent = changeEvent;
		this.eventString = eventString;
	}
	
	public ComparisonEvent(Class<?> eventType, ChangeEvent<?> changeEvent, EObject target, Object value,
			EStructuralFeature feature, int position, String eventString, String targetId, String valueId, String featureName) {
		this.eventType = eventType;
		this.changeEvent = changeEvent;
		this.target = target;
		this.value = value;
		this.feature = feature;
		this.featureName = featureName;
		this.position = position;
		this.eventString = eventString;
		this.targetId = targetId;
		this.valueId = valueId;
	}

	public Class<?> getEventType() {
		return eventType;
	}

	public ChangeEvent<?> getChangeEvent() {
		return changeEvent;
	}

	public EObject getTarget() {
		return target;
	}

	public Object getValue() {
		return value;
	}

	public EStructuralFeature getFeature() {
		return feature;
	}

	public int getPosition() {
		return position;
	}

	public String getTargetId() {
		return targetId;
	}

	public String getValueId() {
		return valueId;
	}

	public String getEventString() {
		return eventString;
	}

	public void setChangeEvent(ChangeEvent<?> changeEvent) {
		this.changeEvent = changeEvent;
	}

	public void setTarget(EObject target) {
		this.target = target;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public void setFeature(EStructuralFeature feature) {
		this.feature = feature;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setEventType(Class<?> eventType) {
		this.eventType = eventType;
	}

	public void setEventString(String eventString) {
		this.eventString = eventString;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public boolean isConflicted() {
		return isConflicted;
	}

	public void setIsConflicted(boolean isConflicted) {
		this.isConflicted = isConflicted;
	}

	public ConflictedEventPair getConflictedEventPair() {
		return conflictedEventPair;
	}

	public void setConflictedEventPair(ConflictedEventPair conflictedEventPair) {
		this.conflictedEventPair = conflictedEventPair;
	}

	public String getComposite() {
		return this.composite;
	}

	public void setComposite(String composite) {
		this.composite = composite;
	}

}