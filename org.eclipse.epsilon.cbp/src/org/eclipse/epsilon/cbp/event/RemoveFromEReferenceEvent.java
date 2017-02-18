package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class RemoveFromEReferenceEvent extends EReferenceEvent {
	/* EObject added to another EObject via some EReference */
	public RemoveFromEReferenceEvent(EObject focusObject, EReference eReference, Object oldValue) {
		super(focusObject, eReference, oldValue);
	}

	public RemoveFromEReferenceEvent(Notification n) {
		super(n);
	}
}
