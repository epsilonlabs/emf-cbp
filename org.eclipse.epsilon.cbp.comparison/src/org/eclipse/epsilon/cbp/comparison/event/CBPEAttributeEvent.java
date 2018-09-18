package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EAttribute;

public abstract class CBPEAttributeEvent extends CBPEStructuralFeatureEvent<Object> implements ICBPPrimitiveValuesEvent {
	
	public String getEAttribute() {
		return (String) eStructuralFeature;
	}
}
