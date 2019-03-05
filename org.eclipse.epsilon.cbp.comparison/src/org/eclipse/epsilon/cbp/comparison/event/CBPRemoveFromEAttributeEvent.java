package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

public class CBPRemoveFromEAttributeEvent extends CBPMultiValueEAttributeEvent {

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPAddToEAttributeEvent event = new CBPAddToEAttributeEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	event.setValues(this.getValues());
	event.setOldValues(this.getOldValue());
	event.setPosition(this.getPosition());
	event.setTarget(this.getTarget());
	event.setComposite(this.getComposite());
	return event;
    }

    @Override
    public String toString() {
	String val = this.getValue().toString();
	if (this.getValue() instanceof String) {
	    val = "\"" + val + "\"";
	}
	return String.format("REMOVE %s FROM %s.%s AT %s", val, this.getTarget(), this.getEStructuralFeature(), this.getPosition());
    }
}
