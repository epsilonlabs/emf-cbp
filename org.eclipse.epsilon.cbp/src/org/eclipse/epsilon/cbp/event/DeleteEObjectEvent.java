package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class DeleteEObjectEvent extends EObjectEvent {

	protected EClass eClass;
	protected CBPResource resource;
	protected String id;
	public void setId(String id) {
		this.id = id;
	}

	protected EObject eObject;
	
	public DeleteEObjectEvent() {
		
	}

	public DeleteEObjectEvent(EClass eClass, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
	}
	
	public DeleteEObjectEvent(EObject eObject, String id) {
		super();
		this.eObject = eObject;
		this.eClass = eObject.eClass();
		this.id = id;
		this.setValue(eObject);
	}

	public DeleteEObjectEvent(EClass eClass, CBPResource resource, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
		this.eObject = resource.getEObject(id);
	}

	public EClass getEClass() {
		return eClass;
	}

	public String getId() {
		return id;
	}

	public EClass geteClass() {
		return eClass;
	}

	public void seteClass(EClass eClass) {
		this.eClass = eClass;
	}

	public CBPResource getResource() {
		return resource;
	}

	public void setResource(CBPResource resource) {
		this.resource = resource;
	}

	public EObject geteObject() {
		return eObject;
	}

	public void seteObject(EObject eObject) {
		this.eObject = eObject;
	}

	@Override
	public void replay() {
		this.eObject = resource.getEObject(this.id);
		this.setValue(eObject);
		EcoreUtil.delete(eObject);
		resource.unregister(eObject);
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public ChangeEvent<?> reverse(){
		CreateEObjectEvent event = new CreateEObjectEvent(eClass, resource, id);
		return event;
	}
}