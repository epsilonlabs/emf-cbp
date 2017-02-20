package org.eclipse.epsilon.cbp.event;

public class RemoveFromResourceEvent extends ResourceEvent {
	
	@Override
	public void replay() {
		resource.getContents().removeAll(getValues());
	}
	
}