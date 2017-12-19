package org.eclipse.epsilon.cbp.event;

public class SetEReferenceEvent extends EReferenceEvent implements EObjectValuesEvent {
	
	@Override
	public void replay() {
//		if (eStructuralFeature.getName().equals("opposite")){
//			return;
//		}
		target.eSet(eStructuralFeature, getValue());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
}
