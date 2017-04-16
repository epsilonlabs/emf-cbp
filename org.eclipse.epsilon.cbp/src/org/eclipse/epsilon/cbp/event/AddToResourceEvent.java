package org.eclipse.epsilon.cbp.event;

public class AddToResourceEvent extends ResourceEvent {
	
	@Override
	public void replay() {
		resource.getContents().addAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
}
