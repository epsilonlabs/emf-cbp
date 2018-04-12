package org.eclipse.epsilon.cbp.event;

public class RemoveFromResourceEvent extends ResourceEvent {
	
	@Override
	public void replay() {
//		Object x = resource.getContents();
//		Object y = getValues();
		resource.getContents().removeAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
}