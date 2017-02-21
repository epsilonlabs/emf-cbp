package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class EStructuralFeatureEvent<T> extends ChangeEvent<T> {
	
	protected EStructuralFeature eStructuralFeature;
	protected EObject target = null;
	
	public EStructuralFeature getEStructuralFeature() {
		return eStructuralFeature;
	}
	
	public EObject getTarget() {
		return target;
	}
	
	public void setEStructuralFeature(EStructuralFeature eStructuralFeature) {
		this.eStructuralFeature = eStructuralFeature;
	}
	
	public void setTarget(Object object) {
		assert object instanceof EObject;
		this.target = (EObject) object;
	}
	
	public void setTarget(EObject target) {
		this.target = target;
	}
	
}
