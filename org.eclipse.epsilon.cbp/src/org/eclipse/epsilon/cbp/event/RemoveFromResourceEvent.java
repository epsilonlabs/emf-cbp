package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;

public class RemoveFromResourceEvent extends ResourceEvent {
	public RemoveFromResourceEvent(Notification n) {
		super(n);
	}

	public RemoveFromResourceEvent(Object removedEObjects) {
		super(removedEObjects);
	}
}
