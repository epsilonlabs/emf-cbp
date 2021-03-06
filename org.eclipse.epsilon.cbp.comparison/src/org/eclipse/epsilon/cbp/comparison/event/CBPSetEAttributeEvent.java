package org.eclipse.epsilon.cbp.comparison.event;

public class CBPSetEAttributeEvent extends CBPSingleValueEAttributeEvent {

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPSetEAttributeEvent event = new CBPSetEAttributeEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	Object temp = this.getValues();
	event.setValues(this.getOldValues());
	event.setOldValues(temp);
	event.setPosition(this.getPosition());
	event.setTarget(this.getTarget());
	event.setComposite(getComposite());
	event.setEClass(this.getEClass());
	return event;
    }

    @Override
    public String toString() {
	Object val = this.getValue();
//	if ( this.getValue() instanceof String) {
//	    val = "\"" + val + "\"";
//	}

//	if (this.getOldValue() != null) {
	    Object oldVal = this.getOldValue();
//	    if (this.getOldValue() instanceof String) {
//		oldVal = "\"" + oldVal + "\"";
//	    }
	    return String.format("SET %s.%s FROM %s TO %s", this.getTarget(), this.getEStructuralFeature(), oldVal, val);
//	} else {
//	    return String.format("SET %s.%s TO %s", this.getTarget(), this.getEStructuralFeature(), val);
//	}
    }
}
