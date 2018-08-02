package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.util.EList;

public class MoveWithinEAttributeEvent extends MultiValueEAttributeEvent implements FromPositionEvent {

	private int fromPosition;

	@Override
	public int getFromPosition() {
		return fromPosition;
	}

	@Override
	public void setFromPosition(int fromPosition) {
		this.fromPosition = fromPosition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		EList<Object> list = (EList<Object>) target.eGet(getEStructuralFeature());
		if (fromPosition >= list.size()) {
			fromPosition = list.size() - 1;
		}
		if (position >= list.size()) {
			position = list.size() - 1;
		}
		list.move(position, fromPosition);
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse() {
		MoveWithinEAttributeEvent event = new MoveWithinEAttributeEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		int temp = this.getPosition();
		event.setPosition(this.getFromPosition());
		event.setFromPosition(temp);
		event.setTarget(this.getTarget());
		event.setComposite(this.getComposite());
		return event;
	}
}
