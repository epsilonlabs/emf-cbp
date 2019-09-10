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
	event.setComposite(getComposite());
	event.setEClass(this.getEClass());
	return event;
    }

    @Override
    public String toString() {
	// if (this.getOldValue() != null) {
	Object oldVal = this.getOldValue();
	// if (this.getOldValue() instanceof String) {
	// oldVal = "\"" + oldVal + "\"";
	// }
	return String.format("UNSET %s.%s FROM %s TO %s", this.getTarget(), this.getEStructuralFeature(), oldVal, this.getValue());
	// } else {
	// return String.format("UNSET %s.%s TO %s", this.getTarget(),
	// this.getEStructuralFeature(), this.getValue());
	// }
    }
}
