package org.eclipse.epsilon.cbp.state2change.test;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.IPreChangeEventHandler;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.uml2.uml.internal.impl.PropertyImpl;

public class UMLPreChangeEventHandlerImpl implements IPreChangeEventHandler {

	@Override
	public boolean isCancelled(ChangeEvent<?> e) {
		boolean result = false;
		if (e instanceof SetEReferenceEvent) {
			SetEReferenceEvent event = (SetEReferenceEvent) e;
			EObject obj = event.getTarget();
			EStructuralFeature sf = event.getEStructuralFeature();
			if (obj instanceof PropertyImpl && sf.isMany() == false && sf.getName().equals("opposite")) {
				result = true;
			}
		}
		return result;
	}

}
