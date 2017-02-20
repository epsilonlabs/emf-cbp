package org.eclipse.epsilon.cbp.event;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class AddToEReferenceEvent extends EReferenceEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((List<EObject>) target.eGet(getEStructuralFeature())).addAll(position, getValues());
	}
	
}
