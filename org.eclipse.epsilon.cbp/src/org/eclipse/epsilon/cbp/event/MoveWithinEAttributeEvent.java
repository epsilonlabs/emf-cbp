package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.util.EList;

public class MoveWithinEAttributeEvent extends EAttributeEvent implements FromPositionEvent {

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
		((EList<Object>) target.eGet(getEStructuralFeature())).move(position, fromPosition);
	}

}
