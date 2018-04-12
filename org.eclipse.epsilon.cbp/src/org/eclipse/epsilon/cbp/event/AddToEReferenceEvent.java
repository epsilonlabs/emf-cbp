package org.eclipse.epsilon.cbp.event;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList.UnmodifiableEList;
import org.eclipse.emf.ecore.EObject;

public class AddToEReferenceEvent extends EReferenceEvent implements EObjectValuesEvent {

	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		List<EObject> list = (List<EObject>) target.eGet(getEStructuralFeature());
		if (list instanceof UnmodifiableEList){
			return;
		}
		if (position > list.size()) {
			position = list.size();
		}
		list.addAll(position, this.getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

}
