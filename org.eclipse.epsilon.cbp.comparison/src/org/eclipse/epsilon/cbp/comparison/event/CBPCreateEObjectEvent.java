package org.eclipse.epsilon.cbp.comparison.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class CBPCreateEObjectEvent extends CBPEObjectEvent {

	protected String  eClass;
	protected String resource;
	protected String id;
	protected EObject eObject;

	public CBPCreateEObjectEvent(EObject eObject, String id) {
		super();
		this.eObject = eObject;
		this.eClass = eObject.eClass().getName();
		this.id = id;
		this.setValue(id);
	}

	public CBPCreateEObjectEvent(String eClass, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
	}

	public CBPCreateEObjectEvent(String eClass, String resource, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
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

	@Override
	public CBPChangeEvent<?> reverse(){
		CBPDeleteEObjectEvent event = new CBPDeleteEObjectEvent(eClass, resource, id);
		return event;
	}
	
	

}
