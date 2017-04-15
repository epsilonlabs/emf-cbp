package org.eclipse.epsilon.cbp.thrift;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;

public class CBPThriftResourceFactory implements Factory {

	@Override
	public Resource createResource(URI uri) {
		if (uri.toString().endsWith(".cbpthrift")) {
			return new CBPThriftResourceImpl(uri);
		} else {
			throw new RuntimeException("Unknown extension. Could not create resource for URI " + uri);
		}
	}

}
