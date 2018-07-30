package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class RemoveFromResourceEvent extends ResourceEvent {
	
	@Override
	public void replay() {
		resource.getContents().removeAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public ChangeEvent<?> reverse(){
		AddToResourceEvent event = new AddToResourceEvent();
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());;
		return event;
	}
}