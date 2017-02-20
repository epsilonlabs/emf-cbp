package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public abstract class EReferenceEvent extends EStructuralFeatureEvent<EObject> {

	public EReference getEReference() {
		return (EReference) eStructuralFeature;
	}
}
