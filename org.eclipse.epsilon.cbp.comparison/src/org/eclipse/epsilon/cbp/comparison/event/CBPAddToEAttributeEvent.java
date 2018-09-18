package org.eclipse.epsilon.cbp.comparison.event;

import java.util.List;

public class CBPAddToEAttributeEvent extends CBPMultiValueEAttributeEvent {
	

	@Override
	public CBPChangeEvent<?> reverse(){
		CBPRemoveFromEAttributeEvent event = new CBPRemoveFromEAttributeEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());;
		event.setTarget(this.getTarget());
		return event;
	}
}
