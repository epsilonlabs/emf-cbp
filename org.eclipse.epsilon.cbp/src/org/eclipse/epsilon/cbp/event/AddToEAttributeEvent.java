package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class AddToEAttributeEvent extends EAttributeEvent {
	public AddToEAttributeEvent(EObject focusObject, EAttribute eAttribute, Object newValue) {
		super(focusObject, eAttribute, newValue);
	}

	public AddToEAttributeEvent(Notification n) {
		this((EObject) n.getNotifier(), (EAttribute) n.getFeature(), n.getNewValue());
	}
	
	/*
	@Override
	public void replay(CBPResource resource) {
		eObject.eSet(eAttribute, newValue);
	}*/
	
}
