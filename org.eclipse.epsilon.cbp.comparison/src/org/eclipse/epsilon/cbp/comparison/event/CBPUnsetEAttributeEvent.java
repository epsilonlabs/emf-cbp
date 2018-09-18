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
}
