package org.eclipse.epsilon.cbp.event;

public class UnsetEAttributeEvent extends SingleValueEAttributeEvent {

	@Override
	public void replay() {
		if (target != null && getEStructuralFeature()!= null) {
		target.eUnset(getEStructuralFeature());
		}
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse() {
		SetEAttributeEvent event = new SetEAttributeEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		Object temp = this.getValues();
		event.setValues(this.getOldValues());
		event.setOldValues(temp);
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
