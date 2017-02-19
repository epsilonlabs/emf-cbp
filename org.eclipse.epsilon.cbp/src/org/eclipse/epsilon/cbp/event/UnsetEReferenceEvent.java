package org.eclipse.epsilon.cbp.event;

public class UnsetEReferenceEvent extends EReferenceEvent {
	
	@Override
	public void replay() {
		target.eUnset(getEStructuralFeature());
	}
	
}
