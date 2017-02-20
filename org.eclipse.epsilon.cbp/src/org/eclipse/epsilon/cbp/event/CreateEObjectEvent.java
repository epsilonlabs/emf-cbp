package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class CreateEObjectEvent extends Event<EObject> implements EObjectValuesEvent {
	
	protected EClass eClass;
	protected CBPResource resource;
	
	public CreateEObjectEvent(EClass eClass, CBPResource resource) {
		super();
		this.eClass = eClass;
		this.resource = resource;
	}
	
	public CreateEObjectEvent(EClass eClass) {
		super();
		this.eClass = eClass;
	}
	
	public EClass getEClass() {
		return eClass;
	}
	
	public void setResource(CBPResource resource) {
		this.resource = resource;
	}
	
	@Override
	public void replay() {
		resource.getEObjects().add(eClass.getEPackage().getEFactoryInstance().create(eClass));
	}
	
}
