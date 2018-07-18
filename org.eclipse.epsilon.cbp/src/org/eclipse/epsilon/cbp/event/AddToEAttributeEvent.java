package org.eclipse.epsilon.cbp.event;

import java.util.List;

public class AddToEAttributeEvent extends MultiValueEAttributeEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		List<Object> list = (List<Object>) target.eGet(getEStructuralFeature());
		if (position > list.size()){
			position =  list.size();
		}
		list.addAll(position, getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse(){
		RemoveFromEAttributeEvent event = new RemoveFromEAttributeEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());;
		event.setTarget(this.getTarget());
		return event;
	}
}
