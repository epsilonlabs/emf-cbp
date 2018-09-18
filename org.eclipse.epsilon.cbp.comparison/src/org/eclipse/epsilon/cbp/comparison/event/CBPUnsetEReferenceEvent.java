package org.eclipse.epsilon.cbp.comparison.event;

public class CBPUnsetEReferenceEvent extends CBPSingleValueEReferenceEvent {

	
	@Override
	public CBPChangeEvent<?> reverse(){
		CBPSetEReferenceEvent event = new CBPSetEReferenceEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		Object temp = this.getValues();
		event.setValues(this.getOldValues());
		event.setOldValues(temp);
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
