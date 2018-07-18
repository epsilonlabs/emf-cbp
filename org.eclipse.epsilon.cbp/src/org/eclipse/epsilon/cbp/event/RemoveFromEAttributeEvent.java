package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

public class RemoveFromEAttributeEvent extends MultiValueEAttributeEvent {
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<Object>) target.eGet(getEStructuralFeature())).removeAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse(){
		AddToEAttributeEvent event = new AddToEAttributeEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());;
		event.setTarget(this.getTarget());
		event.setComposite(this.getComposite());
		return event;
	}
}
