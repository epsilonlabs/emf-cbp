package org.eclipse.epsilon.cbp.event;

import javax.lang.model.type.NullType;

import org.eclipse.emf.ecore.EPackage;

public class RegisterEPackageEvent extends ChangeEvent<NullType> {

	protected EPackage ePackage;
	protected ChangeEventAdapter eventAdapter;
	
	public RegisterEPackageEvent(EPackage ePackage, ChangeEventAdapter eventAdapter) {
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

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}
}
