package org.eclipse.epsilon.hybrid.event.xmi;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;

public class DeleteEObjectEvent extends org.eclipse.epsilon.cbp.event.DeleteEObjectEvent {

    protected HybridResource resource;

    public DeleteEObjectEvent(EObject eObject, String id) {
	super(eObject, id);
	this.eObject = eObject;
	this.eClass = eObject.eClass();
	this.id = id;
	this.setValue(eObject);
    }

    public DeleteEObjectEvent(EClass eClass, HybridResource resource, String id) {
	super();
	this.eClass = eClass;
	this.id = id;
	this.resource = resource;
	this.eObject = resource.getEObject(id);
    }

    @Override
    public void replay() {
	this.eObject = resource.getEObject(this.id);
	if (this.eObject != null) {
	    this.setValue(eObject);
	    EcoreUtil.delete(eObject);
//	    resource.unregister(eObject);
	}
    }

}