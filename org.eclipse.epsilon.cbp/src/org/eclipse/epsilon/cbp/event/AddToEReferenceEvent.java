package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class AddToEReferenceEvent extends EReferenceEvent {
	public AddToEReferenceEvent(EObject focusObject, EReference eReference, Object newValue) {
		super(focusObject, eReference, newValue);
	}

	public AddToEReferenceEvent(Notification n) {
		super(n);
	}
}
