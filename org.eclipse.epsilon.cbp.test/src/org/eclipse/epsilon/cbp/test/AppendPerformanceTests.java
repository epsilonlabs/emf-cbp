package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class AppendPerformanceTests {

	private final String extension;
	private final Factory factory;

	long beforeXMISave = 0;
	long afterXMISave = 0;
	long beforeOCBPAppend = 0;
	long afterOCBPAppend = 0;
	long beforeUOCBPAppend = 0;
	long afterUOCBPAppend = 0;
	long numberOfNodes = 0;
	long limit = 0;
	long limitIncrement = 200;

	public abstract Class<?> getNodeClass();

	public AppendPerformanceTests(String extension, Resource.Factory factory) {
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

		System.out.print("NumOps\tNumNodes\tOCBPAppen\tUCBPAppen\tXMISave\n");

		StringOutputStream cbpOutput = new StringOutputStream();
		ByteArrayOutputStream ignoreListOutput = new ByteArrayOutputStream();
		CBPResource cbpResource = (CBPResource) factory.createResource(URI.createURI("foo." + extension));
		
		StringOutputStream unoptimisedCbpOutput = new StringOutputStream();
		CBPResource unoptimisedCbpResource = (CBPResource) factory.createResource(URI.createURI("foo." + extension));

		StringOutputStream xmiOutput = new StringOutputStream();
		Resource xmiResource = new XMIResourceFactoryImpl().createResource(URI.createURI("foo.xmi"));

		EolModule optimisedCbpModule = new EolModule();
		EolModule unoptimisedCbpModule = new EolModule();
		EolModule xmiModule = new EolModule();
		
		InMemoryEmfModel cbpModel = new InMemoryEmfModel("M", cbpResource, getEPackage());
		InMemoryEmfModel unoptimisedCbpModel = new InMemoryEmfModel("M", unoptimisedCbpResource, getEPackage());
		InMemoryEmfModel xmiModel = new InMemoryEmfModel("M", xmiResource, getEPackage());

		optimisedCbpModule.getContext().getModelRepository().addModel(cbpModel);
		unoptimisedCbpModule.getContext().getModelRepository().addModel(unoptimisedCbpModel);
		xmiModule.getContext().getModelRepository().addModel(xmiModel);
		
		Map<String, Boolean> options = new HashMap<>();
		options.put("optimise", false);
		
		int count = 1;
		for (String eol : sessions) {

			// Optimised CBP ------------------
			optimisedCbpModule.parse(eol);
			optimisedCbpModule.execute();

			beforeOCBPAppend = System.nanoTime();
			cbpResource.save(cbpOutput, null);
			cbpResource.saveIgnoreSet(ignoreListOutput);
			afterOCBPAppend = System.nanoTime();

			// Unoptimised CBP ------------------
			unoptimisedCbpModule.parse(eol);
			unoptimisedCbpModule.execute();
			
			beforeUOCBPAppend = System.nanoTime();
			unoptimisedCbpResource.save(unoptimisedCbpOutput, options);
			afterUOCBPAppend = System.nanoTime();

			// XMI -----------------
			xmiModule.parse(eol);
			xmiModule.execute();

			xmiOutput.reset();
			beforeXMISave = System.nanoTime();
			xmiResource.save(xmiOutput, null);
			afterXMISave = System.nanoTime();

			TreeIterator<EObject> iterator = xmiResource.getAllContents();
			numberOfNodes = 0;
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();

				if (getEPackage().equals(NodePackage.eINSTANCE)) {
					if (getNodeClass().isInstance(eObject)) {
						numberOfNodes += 1;
					}
				} else if (getEPackage().equals(ConferencePackage.eINSTANCE)) {
					EClassifier eClassifier = ConferencePackage.eINSTANCE
							.getEClassifier(getNodeClass().getSimpleName());
					if (eObject.eClass().getEAllSuperTypes().contains(eClassifier)) {
						numberOfNodes += 1;
					}
				}

			}

			// print time
			if (numberOfNodes >= limit) {
				System.out.print(count + "\t");
				System.out.print(String.format("%1$s\t", numberOfNodes));
				System.out.print(String.format("%1$.6f\t", (double) (afterOCBPAppend - beforeOCBPAppend) / 1000000));
				System.out.print(String.format("%1$.6f\t", (double) (afterUOCBPAppend - beforeUOCBPAppend) / 1000000));
				System.out.print(String.format("%1$.6f\n", (double) (afterXMISave - beforeXMISave) / 1000000));
				limit += limitIncrement;
			}
			count += 1;
		}

		// convert CBP Resource into XMI for assert equals
		Resource cbpXmiResource = new XMIResourceFactoryImpl().createResource(URI.createURI("foo.xmi"));
		cbpXmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));

		StringOutputStream cbpXmiOutput = new StringOutputStream();
		cbpXmiResource.save(cbpXmiOutput, null);

		if (debug) {
			System.out.println();
			System.out.println("XMI Save----------");
			System.out.println(xmiOutput.toString());
			System.out.println("CBP Append--------");
			System.out.println(cbpOutput.toString());
			System.out.println("CBP to XMI --------");
			System.out.println(cbpXmiOutput.toString());
		}

		assertEquals(xmiOutput.toString(), cbpXmiOutput.toString());

	}

	public abstract EPackage getEPackage();

}
