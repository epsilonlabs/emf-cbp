package org.eclipse.epsilon.cbp.comparison.event;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList.UnmodifiableEList;
import org.eclipse.emf.ecore.EObject;

public class CBPAddToEReferenceEvent extends CBPMultiValueEReferenceEvent implements ICBPEObjectValuesEvent {

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPRemoveFromEReferenceEvent event = new CBPRemoveFromEReferenceEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	event.setValues(this.getValues());
	event.setOldValues(this.getOldValue());
	event.setPosition(this.getPosition());
	event.setTarget(this.getTarget());
	event.setComposite(this.getComposite());
	return event;
    }
}