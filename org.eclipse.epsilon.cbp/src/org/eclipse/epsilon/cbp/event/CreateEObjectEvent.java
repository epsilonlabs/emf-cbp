package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class CreateEObjectEvent extends ChangeEvent<EObject> {
	
	protected EClass eClass;
	protected CBPResource resource;
	protected String id;
	
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
		resource.register(eClass.getEPackage().getEFactoryInstance().create(eClass)/*, id*/);
	}
	
}
