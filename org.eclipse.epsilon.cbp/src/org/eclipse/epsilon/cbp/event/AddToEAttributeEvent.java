package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

public class AddToEAttributeEvent extends EAttributeEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<Object>) target.eGet(getEAttribute())).addAll(getValues());
	}
}
