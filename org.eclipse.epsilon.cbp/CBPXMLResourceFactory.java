package org.eclipse.epsilon.cbp.resource;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

public class CBPXMLResourceFactory extends ResourceFactoryImpl {

	protected CBPXMLResourceImpl cbpxmlResourceImpl = null;

	@Override
	public Resource createResource(URI uri) {
		if (uri.toString().endsWith(".cbpxml")) {
			cbpxmlResourceImpl = new CBPXMLResourceImpl(uri);
			return cbpxmlResourceImpl;
		} else {
			throw new RuntimeException("Unknown extension. Could not create resource for URI " + uri);
		}
	}

	// public Set<Integer> getIgnoreList() {
	// return cbpxmlResourceImpl.getIgnoreList();
	// }
}
