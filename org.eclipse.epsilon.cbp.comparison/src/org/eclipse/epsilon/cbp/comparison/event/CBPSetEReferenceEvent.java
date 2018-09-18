package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class CBPSetEReferenceEvent extends CBPEReferenceEvent implements ICBPEObjectValuesEvent {
	
	
	@Override
	public CBPChangeEvent<?> reverse(){
		CBPSetEReferenceEvent event = new CBPSetEReferenceEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		Object temp = this.getValues();
		event.setValues(this.getOldValues());
		event.setOldValues(temp);
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
