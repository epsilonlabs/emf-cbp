package org.eclipse.epsilon.cbp.comparison.emfstore;

import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.IChangeEventVisitor;

public class AddToResourceEvent extends ResourceEvent {

	@Override
	public void replay() {
		adapter.localProject.getModelElements().addAll(getValues());
	}

	@Override
	public ChangeEvent<?> reverse() {
		final RemoveFromResourceEvent event = new RemoveFromResourceEvent();
		event.setValues(getValues());
		event.setOldValues(getOldValue());
		event.setPosition(getPosition());
		return event;
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		// TODO Auto-generated method stub
		return null;
	}
}
