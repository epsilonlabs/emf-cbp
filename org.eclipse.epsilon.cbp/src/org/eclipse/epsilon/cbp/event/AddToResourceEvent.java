package org.eclipse.epsilon.cbp.event;

public class AddToResourceEvent extends ResourceEvent {
	
	@Override
	public void replay() {
		resource.getContents().addAll(getValues());
	}
	
}
