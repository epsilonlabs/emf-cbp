package org.eclipse.epsilon.cbp.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

public class CBPResourceFactory extends ResourceFactoryImpl {
	
	@Override
	public Resource createResource(URI uri) {
		if (uri.toString().endsWith(".cbpbin")) {
			return new CBPBinaryResourceImpl(uri);
		}
		else if (uri.toString().endsWith(".cbptext")) {
			return new CBPTextResourceImpl(uri);
		}
		else throw new RuntimeException("Unknown extension. Could not create resource for URI " + uri);
	}
	
}
