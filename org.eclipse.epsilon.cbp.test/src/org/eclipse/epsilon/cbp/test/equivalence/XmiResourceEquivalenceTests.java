package org.eclipse.epsilon.cbp.test.equivalence;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.io.StringOutputStream;
import org.eclipse.epsilon.cbp.resource.CBPResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class XmiResourceEquivalenceTests {
	
	public abstract EPackage getEPackage();
	
	public void run(String eol, String extension, boolean debug) throws Exception {
		// Run the code against an XMI model
		EolModule module = new EolModule();
		module.parse(eol);
		
		ResourceSet xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource xmiResource = xmiResourceSet.createResource(URI.createURI("foo.xmi"));
		InMemoryEmfModel model = new InMemoryEmfModel("M", xmiResource, getEPackage());
		module.getContext().getModelRepository().addModel(model);
		module.execute();
		
		StringOutputStream xmiSos = new StringOutputStream();
		xmiResource.save(xmiSos, null);
		// inspect(xmiResource);
		
		// Run the code against a change-based resource
		module = new EolModule();
		module.parse(eol);
		
		ResourceSetImpl cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new CBPResourceFactory());
		Resource cbpResource = cbpResourceSet.createResource(URI.createURI("foo." + extension));
		
		model = new InMemoryEmfModel("M", cbpResource, getEPackage());
		module.getContext().getModelRepository().addModel(model);
		module.execute();
		// inspect(cbpResource);
		
		StringOutputStream cbpSos = new StringOutputStream();
		cbpResource.save(cbpSos, null);
		
		// System.out.println(cbpSos.toString());
		
		// Create a new change-based resource and load what was saved before
		cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new CBPResourceFactory());
		cbpResource = cbpResourceSet.createResource(URI.createURI("foo." + extension));
		cbpResource.load(new ByteArrayInputStream(cbpSos.toString().getBytes()), null);
		// inspect(cbpResource);
		
		xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		XMIResourceImpl copyXmiResource = (XMIResourceImpl) xmiResourceSet.createResource(URI.createURI("foo.xmi"));
		copyXmiResource.getContents().addAll(cbpResource.getContents());
		// inspect(copyXmiResource);
		
		StringOutputStream copyXmiResourceSos = new StringOutputStream();
		copyXmiResource.doSave(copyXmiResourceSos, null);
		
		if (debug) {
			System.out.println("XMIResourceImpl");
			System.out.println(xmiSos.toString());
			System.out.println();
			System.out.println("CBPResource");
			System.out.println(copyXmiResourceSos.toString());
			System.out.println("---");
			System.out.println(cbpSos.toString());
		}
		
		assertEquals(xmiSos.toString(), copyXmiResourceSos.toString());
	}
	
	protected void inspect(Resource resource) throws Exception {
		EolModule module = new EolModule();
		module.parse("var c = EClass.all.first(); c.eSuperTypes.size().println();");
		InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
		model.load();
		module.getContext().getModelRepository().addModel(model);
		module.execute();
	}
	
	protected void run(String eol) throws Exception {
		run(eol, false);
	}
	
	protected void run(String eol, boolean debug) throws Exception {
		run(eol, "cbpxml", debug);
		//run(eol, "cbptext", debug);
		//run(eol, "cbpbin", debug);
	}
	
	
	
}
