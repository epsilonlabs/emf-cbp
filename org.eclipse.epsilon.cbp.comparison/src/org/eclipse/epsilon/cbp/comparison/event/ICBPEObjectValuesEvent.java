package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public interface ICBPEObjectValuesEvent {
	
	public Collection<String> getValues();
	
	public Collection<String> getOldValues();
	
}
