package org.eclipse.epsilon.cbp.hybrid;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;

public class HybridResourceFactory extends PersistentResourceFactory {

	@Override
	public Resource createResource(URI uri) {
			PersistentResource resource = (PersistentResource) super.getInstance().createResource(uri);
			return new HybridResource(uri, resource);
	}
}
