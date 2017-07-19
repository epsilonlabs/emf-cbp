package org.eclipse.epsilon.cbp.event;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class AddToEAttributeEvent extends EAttributeEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		List<EObject> list = (List<EObject>) target.eGet(getEStructuralFeature());
		if (position > list.size()){
			position =  list.size();
		}
		((List<Object>) target.eGet(getEStructuralFeature())).addAll(position, getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

}
