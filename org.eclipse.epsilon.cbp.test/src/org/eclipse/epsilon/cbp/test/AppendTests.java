package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class AppendTests {

	private final String extension;
	private final Factory factory;
	
	public AppendTests(String extension, Resource.Factory factory) {
		this.extension = extension;
		this.factory = factory;
	}

	public void run(String... sessions) throws Exception {
		runImpl(extension, false, sessions);
	}
	
	public void debug(String... sessions) throws Exception {
		runImpl(extension, true, sessions);
	}
	
	public void runImpl(String extension, boolean debug, String... sessions) throws Exception {
		
		StringOutputStream multiSessionSosWithoutReload = new StringOutputStream();
		
		CBPResource resource = (CBPResource) factory.createResource(URI.createURI("foo." + extension));
		resource.load(multiSessionSosWithoutReload.getInputStream(), null);
		for (String eol : sessions) {
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			module.execute();
			resource.save(multiSessionSosWithoutReload, null);
		}
		
		StringOutputStream multiSessionSosWithReload = new StringOutputStream();
		
		for (String eol : sessions) {
			resource = (CBPResource) factory.createResource(URI.createURI("foo." + extension));
			resource.load(multiSessionSosWithReload.getInputStream(), null);
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			resource.startNewSession();
			module.execute();
			resource.save(multiSessionSosWithReload, null);
		}
		
		StringOutputStream singleSessionSos = new StringOutputStream();
		resource = (CBPResource) factory.createResource(URI.createURI("foo." + extension));
		for (String eol : sessions) {
			EolModule module = new EolModule();
			module.parse(eol);
			InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
			module.getContext().getModelRepository().addModel(model);
			module.execute();
		}
		resource.save(singleSessionSos, null);
		
		if (debug) {
			System.out.println("Multi-session with reload");
			System.out.println(multiSessionSosWithReload.toString());
			System.out.println("Multi-session without reload");
			System.out.println(multiSessionSosWithoutReload.toString());
			System.out.println("Single-session");
			System.out.println(singleSessionSos.toString());
		}
		
		assertEquals(singleSessionSos.toString(), multiSessionSosWithReload.toString());
		assertEquals(multiSessionSosWithReload.toString(), multiSessionSosWithoutReload.toString());
		
	}
	
	public abstract EPackage getEPackage();
		
}
