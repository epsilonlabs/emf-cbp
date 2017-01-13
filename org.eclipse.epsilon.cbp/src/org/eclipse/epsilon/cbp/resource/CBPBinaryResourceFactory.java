package org.eclipse.epsilon.cbp.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class CBPBinaryResourceFactory extends XMIResourceFactoryImpl{

	@Override
	public Resource createResource(URI uri) {
		return new CBPBinaryResourceImpl(uri);
	}
}
