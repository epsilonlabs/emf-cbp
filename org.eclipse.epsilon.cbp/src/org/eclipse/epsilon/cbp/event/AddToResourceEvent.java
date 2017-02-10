package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

public class AddToResourceEvent extends ResourceEvent {
	
	public AddToResourceEvent(Notification n) {
		super(n);
		if (n.getNewValue() instanceof Collection) {
			eObjects.addAll((List<EObject>) n.getNewValue());
		} else {
			eObjects.add((EObject) n.getNewValue());
		}
	}

	public AddToResourceEvent(Object addedEObjects) {
		super(addedEObjects);
	}
}
