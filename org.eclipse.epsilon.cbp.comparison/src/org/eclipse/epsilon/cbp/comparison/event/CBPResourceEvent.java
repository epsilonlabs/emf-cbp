package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public abstract class CBPResourceEvent extends CBPChangeEvent<EObject> implements ICBPEObjectValuesEvent {

    protected String resource;

    public void setResource(String resource) {
	this.resource = resource;
    }
}
