package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

public interface ICBPPrimitiveValuesEvent {
	
	public Collection<String> getValues();
	public Collection<String> getOldValues();
	
}
