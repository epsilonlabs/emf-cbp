package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class CBPRemoveFromEReferenceEvent extends CBPMultiValueEReferenceEvent implements ICBPEObjectValuesEvent {

 
    @Override
    public CBPChangeEvent<?> reverse() {
	CBPAddToEReferenceEvent event = new CBPAddToEReferenceEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	event.setValues(this.getValues());
	event.setOldValues(this.getOldValue());
	event.setPosition(this.getPosition());
	;
	event.setTarget(this.getTarget());
	event.setComposite(this.getComposite());
	return event;
    }
}
