package org.eclipse.epsilon.hybrid.event.neoemf;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.IChangeEventVisitor;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;

public class RemoveFromEReferenceEvent extends org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		((Collection<EObject>) target.eGet(getEStructuralFeature())).removeAll(getValues());
//		for (EObject value : getValues()) {
//			((PersistentEObject) value).setMapped(false);
//		}
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}
