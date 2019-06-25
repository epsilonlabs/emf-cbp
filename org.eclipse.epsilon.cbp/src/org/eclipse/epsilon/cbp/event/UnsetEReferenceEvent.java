package org.eclipse.epsilon.cbp.event;

import javax.swing.SingleSelectionModel;

public class UnsetEReferenceEvent extends EReferenceEvent implements EObjectValuesEvent {

    @Override
    public void replay() {
	if (target != null && getEStructuralFeature() != null) {
	    target.eUnset(getEStructuralFeature());
	}
    }

    @Override
    public <U> U accept(IChangeEventVisitor<U> visitor) {
	return visitor.visit(this);
    }

    @Override
    public ChangeEvent<?> reverse() {
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
