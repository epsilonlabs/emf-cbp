package org.eclipse.epsilon.cbp.event;

public class SetEAttributeEvent extends SingleValueEAttributeEvent {

	@Override
	public void replay() {
		if (target != null && eStructuralFeature != null) {
			target.eSet(eStructuralFeature, this.getValue());
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
		event.setValues(this.getValues());
		event.setOldValues(this.getValues());
		event.setValues(this.getOldValues());
		event.setPosition(this.getPosition());
		event.setTarget(this.getTarget());
		return event;
	}
}
