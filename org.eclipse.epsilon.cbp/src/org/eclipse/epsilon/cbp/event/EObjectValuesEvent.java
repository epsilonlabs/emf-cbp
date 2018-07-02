package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public interface EObjectValuesEvent {
	
	public Collection<EObject> getValues();
	
	public Collection<EObject> getOldValues();
	
}
