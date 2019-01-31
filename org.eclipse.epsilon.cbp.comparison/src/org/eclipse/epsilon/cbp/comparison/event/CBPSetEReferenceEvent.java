package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class CBPSetEReferenceEvent extends CBPEReferenceEvent implements ICBPEObjectValuesEvent {

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPSetEReferenceEvent event = new CBPSetEReferenceEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	Object temp = this.getValues();
	event.setValues(this.getOldValues());
	event.setOldValues(temp);
	event.setPosition(this.getPosition());
	event.setTarget(this.getTarget());
	return event;
    }

    @Override
    public String toString() {
	if (this.getOldValue() != null) {
	    return String.format("set %s.%s to %s", this.getTarget(), this.getEStructuralFeature(), this.getValue());
	} else {
	    return String.format("set %s.%s from %s to %s", this.getTarget(), this.getEStructuralFeature(), this.getOldValue(), this.getValue());
	}
    }
}
