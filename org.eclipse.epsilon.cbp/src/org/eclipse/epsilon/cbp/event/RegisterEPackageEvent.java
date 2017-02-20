package org.eclipse.epsilon.cbp.event;

import javax.lang.model.type.NullType;

import org.eclipse.emf.ecore.EPackage;

public class RegisterEPackageEvent extends Event<NullType> {

	protected EPackage ePackage;
	protected EventAdapter eventAdapter;
	
	public RegisterEPackageEvent(EPackage ePackage, EventAdapter eventAdapter) {
		this.ePackage = ePackage;
		this.eventAdapter = eventAdapter;
	}

	public EPackage getEPackage() {
		return ePackage;
	}
	
	@Override
	public void replay() {
		eventAdapter.ePackages.add(ePackage);
	}
}
