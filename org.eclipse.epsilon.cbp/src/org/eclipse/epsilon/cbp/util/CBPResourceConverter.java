package org.eclipse.epsilon.cbp.util;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;

public class CBPResourceConverter {
	
	public static void main(String[] args) throws Exception {
		XMIResourceImpl resource = new XMIResourceImpl(URI.createFileURI("/Users/dkolovos/git/org.eclipse.epsilon/tests/org.eclipse.epsilon.etl.engine.test.acceptance/src/org/eclipse/epsilon/etl/engine/test/acceptance/oo2db/models/DB.ecore"));
		CBPResourceConverter converter = new CBPResourceConverter();
		CBPResource cbpResource = new CBPXMLResourceImpl();
		converter.convert(resource, cbpResource, null);
		cbpResource.save(System.out, null);
	}
	
	public void convert(Resource source, CBPResource target, Map<?, ?> options) throws IOException {
		source.eAdapters().add(target.eAdapters().iterator().next());
		source.load(options);
	}
	
}
