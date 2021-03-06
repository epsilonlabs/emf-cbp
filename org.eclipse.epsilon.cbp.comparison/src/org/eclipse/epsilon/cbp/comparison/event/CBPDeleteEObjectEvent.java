package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class CBPDeleteEObjectEvent extends CBPEObjectEvent {

    protected String eClass;
    protected String resource;
    protected String id;
    protected String ePackage;

    public void setId(String id) {
	this.id = id;
    }

    protected String eObject;

    public CBPDeleteEObjectEvent() {

    }

    public CBPDeleteEObjectEvent(String eClass, String id) {
	super();
	this.eClass = eClass;
	this.id = id;
	this.eObject = id;
	this.setValue(id);
    }

    public CBPDeleteEObjectEvent(String eClass, String resource, String id, String ePackage) {
	super();
	this.eClass = eClass;
	this.id = id;
	this.resource = resource;
	this.eObject = id;
	this.ePackage = ePackage;
	this.setValue(id);
    }

    public String getEClass() {
	return eClass;
    }

    public String getId() {
	return id;
    }

 

    public String getResource() {
	return resource;
    }

    public void setResource(String resource) {
	this.resource = resource;
    }

    public String geteObject() {
	return eObject;
    }

    public void setEObject(String eObject) {
	this.eObject = eObject;
    }

    public String getEPackage() {
        return ePackage;
    }

    public void setEPackage(String ePackage) {
        this.ePackage = ePackage;
    }
    
    @Override
    public CBPChangeEvent<?> reverse() {
	CBPCreateEObjectEvent event = new CBPCreateEObjectEvent(eClass, resource, id, ePackage);
	event.setComposite(getComposite());
	return event;
    }
    
    @Override
    public String toString() {
	return String.format("DELETE %s TYPE %s", this.getValue(), eClass);
    }
}