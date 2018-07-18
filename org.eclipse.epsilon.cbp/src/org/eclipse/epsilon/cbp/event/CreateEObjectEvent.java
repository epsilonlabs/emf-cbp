package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class CreateEObjectEvent extends EObjectEvent {

	protected EClass eClass;
	protected CBPResource resource;
	protected String id;
	protected EObject eObject;

	public CreateEObjectEvent(EObject eObject, String id) {
		super();
		this.eObject = eObject;
		this.eClass = eObject.eClass();
		this.id = id;
		this.setValue(eObject);
	}

	public CreateEObjectEvent(EClass eClass, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
	}

	public CreateEObjectEvent(EClass eClass, CBPResource resource, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
	}

	public CreateEObjectEvent(EClass eClass) {
		super();
		this.eClass = eClass;
	}

	public EClass getEClass() {
		return eClass;
	}

	public String getId() {
		return id;
	}

	@Override
	public void replay() {
		this.eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);
		this.setValue(this.eObject);
		if (this.id == null) {
			resource.register(this.eObject);
		} else {
			resource.register(this.eObject, this.id);
		}
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse(){
		DeleteEObjectEvent event = new DeleteEObjectEvent(eClass, resource, id);
		return event;
	}
	
	

}
