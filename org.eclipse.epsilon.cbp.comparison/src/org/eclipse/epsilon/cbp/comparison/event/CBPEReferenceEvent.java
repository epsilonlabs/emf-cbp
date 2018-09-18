package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public abstract class CBPEReferenceEvent extends CBPEStructuralFeatureEvent<EObject> {

	public String getEReference() {
		return (String) eStructuralFeature;
	}
}
