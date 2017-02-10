package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public abstract class Event {
	
	protected List<EObject> eObjectList = new ArrayList<EObject>();

	public List<EObject> getEObjectList() {
		return eObjectList;
	}
}
