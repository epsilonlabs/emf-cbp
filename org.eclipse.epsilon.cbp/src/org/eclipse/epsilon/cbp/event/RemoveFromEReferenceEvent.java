package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class RemoveFromEReferenceEvent extends EReferenceEvent implements EObjectValuesEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<EObject>) target.eGet(getEStructuralFeature())).removeAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}
