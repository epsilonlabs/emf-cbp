package org.eclipse.epsilon.cbp.comparison.event;

import javax.lang.model.type.NullType;

import org.eclipse.emf.ecore.EPackage;

public class CBPRegisterEPackageEvent extends CBPChangeEvent<NullType> {

	protected String ePackage;
	
	public CBPRegisterEPackageEvent(String ePackage) {
		this.ePackage = ePackage;
	}

	public String getEPackage() {
		return ePackage;
	}
	
	@Override
	    public String toString() {
		return String.format("REGISTER %s", this.getEPackage());
	    }
}
