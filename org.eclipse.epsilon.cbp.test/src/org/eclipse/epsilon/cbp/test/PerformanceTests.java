package org.eclipse.epsilon.cbp.test;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class PerformanceTests {
	
	public void run(String... sessions) throws Exception {
		runImpl("cbpxml", false, sessions);
	}
	
	public void debug(String... sessions) throws Exception {
		runImpl("cbpxml", true, sessions);
	}
	
	public void runImpl(String extension, boolean debug, String... sessions) throws Exception {
		
		final StringOutputStream cbpSos = new StringOutputStream();
		
		for (String eol : sessions) {
			final CBPResource resource = (CBPResource) new CBPXMLResourceFactory().createResource(URI.createURI("foo." + extension));
			if (!cbpSos.toString().isEmpty()) {
				benchmark(new RunnableWithIOException() {
					@Override
					public void run() throws IOException {
						resource.load(cbpSos.getInputStream(), null);
					}
				}, "Loading CBP resource");
			}
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			module.execute();
			benchmark(new RunnableWithIOException() {
				@Override
				public void run() throws IOException {
					resource.save(cbpSos, null);
				}
			}, "Saving CBP resource");
		}
		
		final StringOutputStream xmiSos = new StringOutputStream();
		
		for (String eol : sessions) {
			final XMIResourceImpl resource = new XMIResourceImpl();
			if (!xmiSos.toString().isEmpty()) {
				benchmark(new RunnableWithIOException() {
					@Override
					public void run() throws IOException {
						resource.load(xmiSos.getInputStream(), null);
					}
				}, "Loading XMI resource");
			}
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			module.execute();
			xmiSos.reset();
			benchmark(new RunnableWithIOException() {
				@Override
				public void run() throws IOException {
					resource.save(xmiSos, null);
				}
			}, "Saving XMI resource");
		}
		
		if (debug) {
			System.out.println("CBP Resource");
			System.out.println(cbpSos);
			System.out.println("XMI Resource");
			System.out.println(xmiSos);
		}
		
	}
	
	protected void benchmark(RunnableWithIOException r, String description) throws IOException {
		long start = System.currentTimeMillis();
		r.run();
		System.out.println(description + ": " + (System.currentTimeMillis() - start) + "ms");
		
	}
	
	public interface RunnableWithIOException {
		public void run() throws IOException;
	}
	
	public abstract EPackage getEPackage();
	
	
}
