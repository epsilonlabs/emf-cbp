package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class EStructuralFeatureEvent extends Event {
	
	protected EObject eObject;
	protected EStructuralFeature eStructuralFeature;
	
	public EObject geteObject() {
		return eObject;
	}
	
	public EStructuralFeature geteStructuralFeature() {
		return eStructuralFeature;
	}
	
}
