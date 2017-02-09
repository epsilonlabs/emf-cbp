package org.eclipse.epsilon.cbp.test.equivalence;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class XmiResourceEquivalenceTests {
	
	public abstract EPackage getEPackage();
	
	public void run(String eol, String extension) throws Exception {
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
		
		// Run the code against a change-based resource
		module = new EolModule();
		module.parse(eol);
		
		ResourceSetImpl cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new CBPResourceFactory());
		Resource cbpResource = cbpResourceSet.createResource(URI.createURI("foo." + extension));
		
		model = new InMemoryEmfModel("M", cbpResource, getEPackage());
		module.getContext().getModelRepository().addModel(model);
		module.execute();
		
		StringOutputStream cbpSos = new StringOutputStream();
		cbpResource.save(cbpSos, null);
		
		// Create a new change-based resource and load what was saved before
		cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new CBPResourceFactory());
		cbpResource = cbpResourceSet.createResource(URI.createURI("foo." + extension));
		cbpResource.load(new ByteArrayInputStream(cbpSos.toString().getBytes()), null);
		
		XMIResourceImpl copyXmiResource = new XMIResourceImpl();
		copyXmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
		StringOutputStream copyXmiResourceSos = new StringOutputStream();
		copyXmiResource.doSave(copyXmiResourceSos, null);
		
		assertEquals(xmiSos.toString(), copyXmiResourceSos.toString());
	}
	
	public void run(String eol) throws Exception {
		run(eol, "cbptext");
		run(eol, "cbpbin");
	}
	
	class StringOutputStream extends OutputStream {

		StringBuffer buffer = new StringBuffer();

		@Override
		public void write(int chr) throws IOException {
			buffer.append((char) chr);
		}

		@Override
		public String toString() {
			return buffer.toString();
		}
	}
	
}
