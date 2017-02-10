package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class EAttributeEvent extends EStructuralFeatureEvent {

	// values list
	protected List<Object> values = new ArrayList<Object>();

	@SuppressWarnings("unchecked")
	public EAttributeEvent(EObject eObject, EAttribute eAttribute, Object value) {

		this.eObject = eObject;
		this.eStructuralFeature = eAttribute;

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
		eStructuralFeature = (EStructuralFeature) n.getFeature();

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
		return (EAttribute) eStructuralFeature;
	}

	public List<Object> getValues() {
		return values;
	}
	
}
