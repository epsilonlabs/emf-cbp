package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class AddToEReferenceEvent extends EReferenceEvent {
	
	@Override
	public void replay() {
		((Collection<EObject>) target.eGet(getEReference())).addAll(getValues());
	}
	
}
