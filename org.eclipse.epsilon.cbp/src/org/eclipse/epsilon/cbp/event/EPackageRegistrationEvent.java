package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EPackage;

public class EPackageRegistrationEvent extends Event {

	protected EPackage ePackage;

	public EPackageRegistrationEvent(EPackage ePackage) {
		this.ePackage = ePackage;
	}

	public EPackage getePackage() {
		return ePackage;
	}

}
