package org.eclipse.epsilon.cbp.event;

public class UnsetEAttributeEvent extends EAttributeEvent {
	
	@Override
	public void replay() {
		target.eUnset(getEStructuralFeature());
	}
	
}
