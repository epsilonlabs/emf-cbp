package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;

public class AddEObjectsToResourceEvent extends ResourceEvent {
	public AddEObjectsToResourceEvent(Notification n) {
		super(n);
	}

	public AddEObjectsToResourceEvent(Object addedEObjects) {
		super(addedEObjects);
	}
}
