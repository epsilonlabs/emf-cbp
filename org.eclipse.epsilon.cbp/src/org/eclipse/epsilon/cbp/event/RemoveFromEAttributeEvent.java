package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

public class RemoveFromEAttributeEvent extends EAttributeEvent {
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<Object>) target.eGet(getEStructuralFeature())).removeAll(getValues());
	}
}
