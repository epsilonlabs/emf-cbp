package org.eclipse.epsilon.cbp.event;

public class UnsetEReferenceEvent extends SingleValueEReferenceEvent {
	
	@Override
	public void replay() {
		target.eUnset(getEStructuralFeature());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public ChangeEvent<?> reverse(){
		SetEReferenceEvent event = new SetEReferenceEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		Object temp = this.getValues();
		event.setValues(this.getOldValues());
		event.setOldValues(temp);
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
