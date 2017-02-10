package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public abstract class EAttributeEvent extends Event {
	// the EObject under question
	private EObject focusObject;

	// the attribute related to this event
	private EAttribute eAttribute;

	// values list
	private List<Object> eAttributeValuesList = new ArrayList<Object>();

	@SuppressWarnings("unchecked")
	public EAttributeEvent(EObject focusObject, EAttribute eAttribute, Object value) {

		this.focusObject = focusObject;
		this.eAttribute = eAttribute;

		// if value is collection
		if (value instanceof Collection) {
			eAttributeValuesList.addAll((List<Object>) value);
		} else {
			eAttributeValuesList.add(value);
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
		focusObject = (EObject) n.getNotifier();

		// set the feature
		eAttribute = (EAttribute) n.getFeature();

		if (value instanceof Collection) {
			eAttributeValuesList.addAll((List<Object>) value);
		} else {
			eAttributeValuesList.add(value);
		}
	}

	public EObject getFocusObject() {
		return focusObject;
	}

	public EAttribute getEAttribute() {
		return eAttribute;
	}

	public List<Object> getEAttributeValuesList() {
		return eAttributeValuesList;
	}
}
