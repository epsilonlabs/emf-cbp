package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class CBPCreateEObjectEvent extends CBPEObjectEvent {

    protected String eClass;
    protected String resource;
    protected String id;
    protected String eObject;
    protected String ePackage;

    public CBPCreateEObjectEvent(String eClass, String id) {
	super();
	this.eClass = eClass;
	this.id = id;
	this.eObject = id;
	this.setValue(id);
    }

    public CBPCreateEObjectEvent(String eClass, String resource, String id, String ePackage) {
	super();
	this.eClass = eClass;
	this.id = id;
	this.resource = resource;
	this.eObject = id;
	this.ePackage = ePackage;
	this.setValue(id);
    }

    public CBPCreateEObjectEvent(String eClass) {
	super();
	this.eClass = eClass;
    }

    public String getEClass() {
	return eClass;
    }

    public String getId() {
	return id;
    }

    public String getEPackage() {
        return ePackage;
    }

    public void setEPackage(String ePackage) {
        this.ePackage = ePackage;
    }

    @Override
    public CBPChangeEvent<?> reverse() {
	CBPDeleteEObjectEvent event = new CBPDeleteEObjectEvent(eClass, resource, id, ePackage);
	return event;
    }

    @Override
    public String toString() {
	return String.format("CREATE %s TYPE %s", this.getValue(), this.getEClass());
    }

}
