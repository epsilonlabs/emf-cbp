package org.eclipse.epsilon.cbp.comparison.emfstore;

import org.eclipse.emf.ecore.EClass;

public class CreateEObjectEvent extends org.eclipse.epsilon.cbp.event.CreateEObjectEvent {

	private final CBP2EMFStoreAdapter resource;

	public CreateEObjectEvent(EClass eClass, CBP2EMFStoreAdapter resource, String id) {
		super(eClass, id);
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
	}

	@Override
	public void replay() {
		eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);
		setValue(eObject);
		resource.register(eObject, id);
	}
}
