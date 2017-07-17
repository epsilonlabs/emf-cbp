package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class DeleteEObjectEvent extends ChangeEvent<EObject> {

	protected EClass eClass;
	protected CBPResource resource;
	protected String id;
	protected EObject eObject;

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
	}
	
	public EClass getEClass() {
		return eClass;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public void replay() {
		resource.detached(eObject);
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}