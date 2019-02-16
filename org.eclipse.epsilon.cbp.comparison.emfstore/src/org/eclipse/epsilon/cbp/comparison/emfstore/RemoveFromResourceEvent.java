package org.eclipse.epsilon.cbp.comparison.emfstore;

import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.IChangeEventVisitor;

public class RemoveFromResourceEvent extends ResourceEvent {

	@Override
	public void replay() {
		adapter.localProject.getModelElements().removeAll(getValues());
	}

	@Override
	public ChangeEvent<?> reverse() {
		final AddToResourceEvent event = new AddToResourceEvent();
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