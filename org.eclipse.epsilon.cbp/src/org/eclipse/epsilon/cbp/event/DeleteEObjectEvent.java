package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class DeleteEObjectEvent extends ChangeEvent<EObject> {

	protected EClass eClass;
	protected CBPResource resource;
	protected String id;
	protected EObject eObject;
	protected Object parentEObject;
	protected Object eReference;
	
	public DeleteEObjectEvent(EObject eObject, String id) {
		this(eObject, id, null, null);
	}
	public DeleteEObjectEvent(EObject eObject, String id, Object parentEObject, Object eReference) {
		super();
		this.eObject = eObject;
		this.eClass = eObject.eClass();
		this.id = id;
		this.setValue(eObject);
		this.parentEObject = parentEObject; 
		this.eReference = eReference;
	}

	public DeleteEObjectEvent(EClass eClass, CBPResource resource, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
		this.eObject = resource.getEObject(id);
	}
	
	public Object getEReference(){
		return this.eReference;
	}
	public Object getParentEObject(){
		return this.parentEObject;
	}

	public EClass getEClass() {
		return eClass;
	}

	public String getId() {
		return id;
	}

	@Override
	public void replay() {
		this.eObject = resource.getEObject(this.id);
		this.setValue(eObject);
		//EcoreUtil.remove(eObject);
		EcoreUtil.delete(eObject,true);
		//resource.unregister(eObject);
//		resource.detached(eObject);
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}