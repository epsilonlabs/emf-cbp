package org.eclipse.epsilon.cbp.comparison.emfstore;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public abstract class ResourceEvent extends org.eclipse.epsilon.cbp.event.ResourceEvent {

	protected Resource resource;
	protected CBP2EMFStoreAdapter adapter;

	@Override
	public void setResource(CBPResource resource) {
		this.resource = resource;
	}

	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setResource(CBP2EMFStoreAdapter resource) {
		adapter = resource;
	}
}
