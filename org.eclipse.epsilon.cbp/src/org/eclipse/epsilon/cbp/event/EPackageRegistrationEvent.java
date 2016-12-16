package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.ecore.EPackage;

public class EPackageRegistrationEvent extends Event{

	protected String nsuri;
	protected EPackage ePackage;
	
	public EPackageRegistrationEvent(int eventType) {
		super(Event.REGISTER_EPACKAGE);
	}
	
	public  EPackageRegistrationEvent(int eventType, EPackage ePackage)
	{
		super(Event.REGISTER_EPACKAGE);
		this.ePackage = ePackage;
	}

	public EPackage getePackage() {
		return ePackage;
	}

}
