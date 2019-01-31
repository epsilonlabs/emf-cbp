package org.eclipse.epsilon.cbp.comparison.event;

public class CBPAddToEAttributeEvent extends CBPMultiValueEAttributeEvent {

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPRemoveFromEAttributeEvent event = new CBPRemoveFromEAttributeEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	event.setValues(this.getValues());
	event.setOldValues(this.getOldValue());
	event.setPosition(this.getPosition());
	event.setTarget(this.getTarget());
	return event;
    }

    @Override
    public String toString() {
	return String.format("add %s to %s.%s at %s", this.getValue(), this.getTarget(), this.getEStructuralFeature(), this.getPosition());
    }
}
