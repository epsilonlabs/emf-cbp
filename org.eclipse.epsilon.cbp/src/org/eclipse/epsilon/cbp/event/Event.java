package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public abstract class Event {
	
	protected List<EObject> eObjects = new ArrayList<EObject>();

	public List<EObject> getEObjects() {
		return eObjects;
	}
}
