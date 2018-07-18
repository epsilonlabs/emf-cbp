package org.eclipse.epsilon.cbp.event;

public class MultiValueEReferenceEvent extends EReferenceEvent {

	@Override
	public void replay() {
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return null;
	}

}
