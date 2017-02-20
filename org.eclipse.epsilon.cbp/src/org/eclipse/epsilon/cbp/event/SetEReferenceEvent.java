package org.eclipse.epsilon.cbp.event;

public class SetEReferenceEvent extends EReferenceEvent implements EObjectValuesEvent {
	
	@Override
	public void replay() {
		target.eSet(eStructuralFeature, getValue());
	}
	
}
