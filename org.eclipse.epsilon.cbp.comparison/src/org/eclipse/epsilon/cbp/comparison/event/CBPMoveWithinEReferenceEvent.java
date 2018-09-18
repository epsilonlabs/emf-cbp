package org.eclipse.epsilon.cbp.comparison.event;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class CBPMoveWithinEReferenceEvent extends CBPMultiValueEReferenceEvent implements ICBPFromPositionEvent, ICBPEObjectValuesEvent {

	private int fromPosition;

	@Override
	public int getFromPosition() {
		return fromPosition;
	}

	@Override
	public void setFromPosition(int fromPosition) {
		this.fromPosition = fromPosition;
	}

	@Override
	public CBPChangeEvent<?> reverse(){
		CBPMoveWithinEReferenceEvent event = new CBPMoveWithinEReferenceEvent();
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
