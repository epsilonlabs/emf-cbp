package org.eclipse.epsilon.cbp.event;

import javax.lang.model.type.NullType;

import org.eclipse.emf.ecore.EPackage;

public class RegisterEPackageEvent extends Event<NullType> {

	protected EPackage ePackage;

	public RegisterEPackageEvent(EPackage ePackage) {
		this.ePackage = ePackage;
	}

	public EPackage getEPackage() {
		return ePackage;
	}
	
	@Override
	public void replay() {
		
	}
}
