package org.eclipse.epsilon.cbp.event;

public class UnsetEAttributeEvent extends EAttributeEvent {
	
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
		SetEAttributeEvent event = new SetEAttributeEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getValues());
		event.setValues(this.getOldValues());
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
