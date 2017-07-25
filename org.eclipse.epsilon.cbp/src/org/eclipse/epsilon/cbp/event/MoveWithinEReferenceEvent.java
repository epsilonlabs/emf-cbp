package org.eclipse.epsilon.cbp.event;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class MoveWithinEReferenceEvent extends EReferenceEvent implements FromPositionEvent, EObjectValuesEvent {

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
		EList<EObject> list = (EList<EObject>) target.eGet(getEStructuralFeature());
		if (fromPosition >= list.size()) {
			fromPosition = list.size() - 1;
		}
		if (position >= list.size()) {
			position = list.size() - 1;
		}
		
//		int steps = position - fromPosition; 
//		Object value = this.getValue();
//		fromPosition = list.indexOf(value);
//		if ((fromPosition + steps) <= 0){
//			position = 0;
//		}else if((fromPosition + steps) >= list.size()-1){
//			position = list.size()-1;
//		}
		list.move(position, fromPosition);
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

}
