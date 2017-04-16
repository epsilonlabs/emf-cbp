package org.eclipse.epsilon.cbp.event;

public class UnsetEAttributeEvent extends EAttributeEvent {
	
	@Override
	public void replay() {
		target.eUnset(getEStructuralFeature());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
}
