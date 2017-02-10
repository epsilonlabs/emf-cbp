package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public abstract class EReferenceEvent extends Event {
	// object under question
	private EObject eObject;

	// EReference related to this event
	private EReference eReference;

	@SuppressWarnings("unchecked")
	public EReferenceEvent(EObject eObject, EReference eReference, Object value) {
		this.eObject = eObject;

		this.eReference = eReference;

		// if value is a collection
		if (value instanceof Collection) {
			eObjects.addAll((Collection<? extends EObject>) value);
		} else {
			eObjects.add((EObject) value);
		}
	}

	@SuppressWarnings("unchecked")
	public EReferenceEvent(Notification n) {
		
		// get notifier
		this.eObject = (EObject) n.getNotifier();

		// get feature
		this.eReference = (EReference) n.getFeature();

		// if event is add
		if (this instanceof AddToEReferenceEvent || this instanceof SetEReferenceEvent) {
			// if new value is collection
			if (n.getNewValue() instanceof Collection) {
				eObjects.addAll((List<EObject>) n.getNewValue());
			} else {
				eObjects.add((EObject) n.getNewValue());
			}
		}
		// if event is not add
		else {
			// if old value is collection
			if (n.getOldValue() instanceof Collection) {
				eObjects.addAll((List<EObject>) n.getOldValue());
			} else {
				eObjects.add((EObject) n.getOldValue());
			}
		}
	}

	public EObject getEObject() {
		return eObject;
	}

	public EReference getEReference() {
		return eReference;
	}
}
