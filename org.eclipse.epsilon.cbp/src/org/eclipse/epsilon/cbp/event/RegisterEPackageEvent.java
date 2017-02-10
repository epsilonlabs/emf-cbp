package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EPackage;

public class RegisterEPackageEvent extends Event {

	protected EPackage ePackage;

	public RegisterEPackageEvent(EPackage ePackage) {
		this.ePackage = ePackage;
	}

	public EPackage getePackage() {
		return ePackage;
	}

}
