package org.eclipse.epsilon.hybrid.event.xmi;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;

public class CreateEObjectEvent extends org.eclipse.epsilon.cbp.event.CreateEObjectEvent {

	protected HybridResource resource;
	
	public CreateEObjectEvent(EClass eClass, HybridResource resource, String id) {
		super(null, null, null);
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
	}

	public CreateEObjectEvent(EObject eObject, String id) {
		super(null, null);
		this.eObject = eObject;
		this.eClass = eObject.eClass();
		this.id = id;
		this.setValue(eObject);
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
	
	
}
