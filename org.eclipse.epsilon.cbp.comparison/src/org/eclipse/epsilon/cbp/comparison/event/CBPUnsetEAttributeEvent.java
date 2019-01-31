package org.eclipse.epsilon.cbp.comparison.event;

public class CBPUnsetEAttributeEvent extends CBPSingleValueEAttributeEvent {

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPSetEAttributeEvent event = new CBPSetEAttributeEvent();
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
	    return String.format("unset %s.%s to %s", this.getTarget(), this.getEStructuralFeature(), this.getValue());
	} else {
	    return String.format("unset %s.%s from %s to %s", this.getTarget(), this.getEStructuralFeature(), this.getOldValue(), this.getValue());
	}
    }
}
