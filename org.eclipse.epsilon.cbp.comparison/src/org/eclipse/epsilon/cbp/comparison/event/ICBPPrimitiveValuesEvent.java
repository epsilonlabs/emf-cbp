package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

public interface ICBPPrimitiveValuesEvent {
	
	public Collection<Object> getValues();
	public Collection<Object> getOldValues();
	
}
