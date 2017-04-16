package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

public class RemoveFromEAttributeEvent extends EAttributeEvent {
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<Object>) target.eGet(getEStructuralFeature())).removeAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	
}
