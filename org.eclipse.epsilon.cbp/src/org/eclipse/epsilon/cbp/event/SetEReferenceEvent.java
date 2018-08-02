package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

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
	
	@Override
	public ChangeEvent<?> reverse(){
		SetEReferenceEvent event = new SetEReferenceEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		Object temp = this.getValues();
		event.setValues(this.getOldValues());
		event.setOldValues(temp);
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
