package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.common.util.EList;

public class CBPMoveWithinEAttributeEvent extends CBPMultiValueEAttributeEvent implements ICBPFromPositionEvent {

    private int fromPosition;

    @Override
    public int getFromPosition() {
	return fromPosition;
    }

    @Override
    public void setFromPosition(int fromPosition) {
	this.fromPosition = fromPosition;
    }

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPMoveWithinEAttributeEvent event = new CBPMoveWithinEAttributeEvent();
	event.setEStructuralFeature(this.getEStructuralFeature());
	event.setValues(this.getValues());
	event.setOldValues(this.getOldValue());
	int temp = this.getPosition();
	event.setPosition(this.getFromPosition());
	event.setFromPosition(temp);
	event.setTarget(this.getTarget());
	event.setComposite(this.getComposite());
	return event;
    }
    
    @Override
    public String toString() {
	return String.format("move %s in %s.%s from %s to %s", this.getValue(), this.getTarget(), this.getEStructuralFeature(), this.getFromPosition(), this.getPosition());
    }
}
