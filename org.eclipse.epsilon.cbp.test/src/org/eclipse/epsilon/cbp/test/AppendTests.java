package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import static org.junit.Assert.assertEquals;

public abstract class AppendTests {
	
	public void run(String... sessions) throws Exception {
		runImpl("cbpxml", sessions);
	}
	
	public void runImpl(String extension, String... sessions) throws Exception {
		
		StringOutputStream multiSessionSos = new StringOutputStream();
		
		for (String eol : sessions) {
			CBPResource resource = (CBPResource) new CBPResourceFactory().createResource(URI.createURI("foo." + extension));
			resource.load(multiSessionSos.getInputStream(), null);
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			module.execute();
			resource.save(multiSessionSos, null);
		}
		
		StringOutputStream singleSessionSos = new StringOutputStream();
		CBPResource resource = (CBPResource) new CBPResourceFactory().createResource(URI.createURI("foo." + extension));
		for (String eol : sessions) {
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			module.execute();
		}
		resource.save(singleSessionSos, null);
		
		assertEquals(singleSessionSos.toString(), multiSessionSos.toString());
		
	}
	
	public abstract EPackage getEPackage();
	
}
