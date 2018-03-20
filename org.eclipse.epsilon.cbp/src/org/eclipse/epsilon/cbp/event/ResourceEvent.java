package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public abstract class ResourceEvent extends ChangeEvent<EObject> implements EObjectValuesEvent {
	
	protected Resource resource;
	
	public void setResource(CBPResource resource) {
		this.resource = resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
