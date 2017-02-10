package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

public class RemoveFromResourceEvent extends ResourceEvent {
	public RemoveFromResourceEvent(Notification n) {
		super(n);
		if (n.getOldValue() instanceof Collection) {
			eObjects.addAll((List<EObject>) n.getOldValue());
		} else {
			eObjects.add((EObject) n.getOldValue());
		}
	}

	public RemoveFromResourceEvent(Object removedEObjects) {
		super(removedEObjects);
	}
}
