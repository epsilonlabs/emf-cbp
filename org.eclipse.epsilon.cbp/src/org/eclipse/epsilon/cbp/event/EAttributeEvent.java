package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EAttribute;

public abstract class EAttributeEvent extends EStructuralFeatureEvent<Object> implements PrimitiveValuesEvent {
	
	public EAttribute getEAttribute() {
		return (EAttribute) eStructuralFeature;
	}
}
