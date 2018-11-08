package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

public interface ICBPEObjectValuesEvent {
	
	public Collection<Object> getValues();
	
	public Collection<Object> getOldValues();
	
}
