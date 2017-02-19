package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class RemoveFromEReferenceEvent extends EReferenceEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<EObject>) target.eGet(getEStructuralFeature())).removeAll(getValues());
	}
}
