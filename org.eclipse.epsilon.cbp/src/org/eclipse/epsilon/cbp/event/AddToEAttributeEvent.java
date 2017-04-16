package org.eclipse.epsilon.cbp.event;

import java.util.List;

public class AddToEAttributeEvent extends EAttributeEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((List<Object>) target.eGet(getEStructuralFeature())).addAll(position, getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

}
