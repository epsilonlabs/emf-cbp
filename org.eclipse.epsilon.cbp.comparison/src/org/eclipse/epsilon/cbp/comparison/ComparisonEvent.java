package org.eclipse.epsilon.cbp.comparison;

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
	int position = -1;
	Class<?> eventType = null;
	String eventString = null;

	
	public ComparisonEvent(Class<?> eventType, ChangeEvent<?> changeEvent, EObject target, Object value,
			EStructuralFeature feature, int position, String eventString, String targetId, String valueId) {
		this.eventType = eventType;
		this.changeEvent = changeEvent;
		this.target = target;
		this.value = value;
		this.feature = feature;
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

	
}