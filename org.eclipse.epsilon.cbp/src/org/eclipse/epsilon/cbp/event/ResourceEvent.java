package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

public abstract class ResourceEvent extends Event {
	
	@SuppressWarnings("unchecked")
	public ResourceEvent(Object value) {
		if (value instanceof Collection) {
			eObjects.addAll((List<EObject>) value);
		} else {
			eObjects.add((EObject) value);
		}
	}
	
	public ResourceEvent(Notification n) {
		
	}
}
