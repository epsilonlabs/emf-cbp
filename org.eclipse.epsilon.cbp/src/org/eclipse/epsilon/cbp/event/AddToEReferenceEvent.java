package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class AddToEReferenceEvent extends EReferenceEvent implements EObjectValuesEvent {

	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		List<EObject> list = (List<EObject>) target.eGet(getEStructuralFeature());
		if (position > list.size()) {
			position = list.size();
		}

		Collection<EObject> col = getValues();
		list.addAll(position, getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

}
