package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public abstract class EAttributeEvent extends Event {
	// the EObject under question
	private EObject eObject;

	// the attribute related to this event
	private EAttribute eAttribute;

	// values list
	private List<Object> values = new ArrayList<Object>();

	@SuppressWarnings("unchecked")
	public EAttributeEvent(EObject eObject, EAttribute eAttribute, Object value) {

		this.eObject = eObject;
		this.eAttribute = eAttribute;

		// if value is collection
		if (value instanceof Collection) {
			values.addAll((List<Object>) value);
		} else {
			values.add(value);
		}
	}

	@SuppressWarnings("unchecked")
	public EAttributeEvent(Notification n) {

		Object value = null;

		// if event is add, get new value; if not, get old value
		if (this instanceof AddToEAttributeEvent || this instanceof SetEAttributeEvent) {
			value = n.getNewValue();
		} else {
			value = n.getOldValue();
		}

		// set notifier as the EObject under question
		eObject = (EObject) n.getNotifier();

		// set the feature
		eAttribute = (EAttribute) n.getFeature();

		if (value instanceof Collection) {
			values.addAll((List<Object>) value);
		} else {
			values.add(value);
		}
	}

	public EObject getEObject() {
		return eObject;
	}

	public EAttribute getEAttribute() {
		return eAttribute;
	}

	public List<Object> getValues() {
		return values;
	}
}
