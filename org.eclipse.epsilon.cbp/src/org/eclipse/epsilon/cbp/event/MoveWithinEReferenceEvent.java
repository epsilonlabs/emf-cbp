package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class MoveWithinEReferenceEvent extends MultiValueEReferenceEvent implements FromPositionEvent, EObjectValuesEvent {

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
		EList<EObject> list = (EList<EObject>) target.eGet(this.getEStructuralFeature());
		if (list.size() > 0) {
			if (fromPosition >= list.size()) {
				fromPosition = list.size() - 1;
			}
			if (position >= list.size()) {
				position = list.size() - 1;
			}
			EObject a = this.getValue();
			EObject b = list.get(fromPosition);
			if (b.equals(a)) {
				list.move(position, fromPosition);
			}
		}
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse(){
		MoveWithinEReferenceEvent event = new MoveWithinEReferenceEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getFromPosition());
		event.setFromPosition(this.getPosition());
		event.setTarget(this.getTarget());
		event.setComposite(this.getComposite());
		return event;
	}
}
