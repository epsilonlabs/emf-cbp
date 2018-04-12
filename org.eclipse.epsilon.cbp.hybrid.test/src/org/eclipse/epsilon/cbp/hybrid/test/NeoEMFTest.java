package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.Node;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;

public class NeoEMFTest {

	public final int ITERATION = 25;
	public final int START_FROM = 3;
	public final int SLEEP_TIME = 1000;

//	 File neoDatabase = new File("D:\\TEMP\\ASE\\_output.bpmn2.graphdb");
//	 File targetCbpFile = new File("D:\\TEMP\\ASE\\_output.bpmn2.cbxpml");
//	 File sourceXmiFile = new File("D:\\TEMP\\ASE\\bpmn2.192.xmi");

//	File neoDatabase = new File("D:\\TEMP\\ASE\\_output.epsilon.graphdb");
//	File targetCbpFile = new File("D:\\TEMP\\ASE\\_output.epsilon.cbxpml");
	// File sourceXmiFile = new File("D:\\TEMP\\ASE\\epsilon.1009.xmi");
//	File sourceXmiFile = new File("D:\\TEMP\\ASE\\epsilon.940.xmi");
	// File sourceXmiFile = new File("D:\\TEMP\\ASE\\epsilon.1473.xmi");

	 File neoDatabase = new File("D:\\TEMP\\ASE\\_output.wikipedia.graphdb");
	 File targetCbpFile = new File("D:\\TEMP\\ASE\\_output.wikipedia.cbxpml");
	 File sourceXmiFile = new File("D:\\TEMP\\ASE\\wikipedia.10187.xmi");

	public NeoEMFTest() {
		UMLPackage.eINSTANCE.eClass();
		MoDiscoXMLPackage.eINSTANCE.eClass();
	}

	@Test
	public void testNeoEMFLoad() {
		try {
			System.out.println("Start ....");

			System.out.println("Processing " + sourceXmiFile.getName() + "....");
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();
			NeoEMFTest.deleteFolder(neoDatabase);

			// init neoemf
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());

			PersistentResource neoResource = (PersistentResource) PersistentResourceFactory.getInstance()
					.createResource(BlueprintsURI.createFileURI(neoDatabase));

			Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit().asMap();
			Map<String, Object> neoLoadOptions = Collections.emptyMap();

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);

			// init xmi and load it
			Map<Object, Object> xmiSaveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiSaveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
					XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
			// Map<Object, Object> xmiLoadOptions = (new
			// XMIResourceImpl()).getDefaultLoadOptions();
			// xmiLoadOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
			// XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(sourceXmiFile.getAbsolutePath()));
			xmiResource.load(null);

			// StringOutputStream output = new StringOutputStream();
			// xmiResource.save(output, xmiSaveOptions);
			// System.out.println(output.toString());

			// copy xmi to neoemf
			neoResource.getContents().addAll(xmiResource.getContents());
			neoResource.save(neoSaveOptions);
			targetCbpBufferedStream.close();
			targetCbpFileStream.close();
			neoResource.unload();
			neoResource.close();
			xmiResource.unload();

			// // validate
			// neoResource.load(neoLoadOptions);
			// TreeIterator<EObject> iterator = neoResource.getAllContents();
			// while (iterator.hasNext()) {
			// EObject eObject = iterator.next();
			// String id = ((PersistentEObject) eObject).id().toString();
			// System.out.println(id);
			// }

			// reinitialise and reload and measure time and memory
			System.out.println("Reload the XMI Several Times");
			long[] loadTime = new long[ITERATION];
			long[] loadMemory = new long[ITERATION];
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + i);

				neoResource = (PersistentResource) PersistentResourceFactory.getInstance()
						.createResource(BlueprintsURI.createFileURI(neoDatabase));

				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				neoResource.load(neoLoadOptions);

				long endTime = System.nanoTime();
				System.gc();
				Thread.sleep(SLEEP_TIME);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				loadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					loadMemory[i] += 0;
				} else {
					loadMemory[i] += Math.abs(endMemory - startMemory);
				}
				System.out.println((loadTime[i] / 1000000000.0) + ", " + (loadMemory[i] / 1000000.0));

				neoResource.unload();
				neoResource.close();
			}

			// // validate
			// hybridResource.load(neoLoadOptions);
			// TreeIterator<EObject> iterator = hybridResource.getAllContents();
			// while (iterator.hasNext()) {
			// EObject eObject = iterator.next();
			// String id = ((PersistentEObject) eObject).id().toString();
			// System.out.println(id);
			// }

			// neoResource.load(neoLoadOptions);
			// Resource resource = (new
			// XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
			// resource.getContents().addAll(neoResource.getContents());
			// StringOutputStream sos = new StringOutputStream();
			// resource.save(sos, null);
			// System.out.println(sos);

			// closing
			neoResource.unload();

			// print results
			System.out.println();
			System.out.println("NEO Load Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadTime[i] / 1000000000.0 + ",NEO");
				}
			}

			System.out.println();
			System.out.println("NEO Load Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadMemory[i] / 1000000.0 + ",NEO");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	@Test
	public void testNeoEMFSave() {
		try {
			System.out.println("Please run the load test first before running this one.");
			System.out.println("Start ....");

			System.out.println("Processing " + sourceXmiFile.getName() + "....");
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			// init neoemf
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			PersistentResource neoResource = (PersistentResource) PersistentResourceFactory.getInstance()
					.createResource(BlueprintsURI.createFileURI(neoDatabase));

			Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit().asMap();
			Map<String, Object> neoLoadOptions = Collections.emptyMap();

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile, true);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);
			neoResource.load(neoLoadOptions);

			List<EObject> list = new ArrayList<>();
			TreeIterator<EObject> iterator = neoResource.getAllContents();
			while (iterator.hasNext()) {
				list.add(iterator.next());
			}

			int measureCount = 0;
			long[] saveTime = new long[ITERATION];
			long[] saveMemory = new long[ITERATION];

			for (; measureCount < ITERATION; measureCount++) {

				// select an object randomly and modify its name to raise an
				// event
				EObject eObject = list.get((new Random()).nextInt(list.size()));
				if (eObject instanceof Node) {
					((Node) eObject).setName("measurement-" + measureCount);
				}
				if (eObject instanceof NamedElement) {
					((NamedElement) eObject).setName("measurement-" + measureCount);
				}

				// do measure
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				neoResource.save(neoSaveOptions);

				long endTime = System.nanoTime();
				System.gc();
				Thread.sleep(SLEEP_TIME);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				saveTime[measureCount] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					saveMemory[measureCount] += 0;
				} else {
					saveMemory[measureCount] += Math.abs(endMemory - startMemory);
				}

				System.out.println(
						(saveTime[measureCount] / 1000000000.0) + ", " + (saveMemory[measureCount] / 1000000.0));
				targetCbpFileStream.flush();
				targetCbpBufferedStream.flush();
			}

			targetCbpFileStream.close();
			targetCbpBufferedStream.close();
			neoResource.close();

			// print results
			System.out.println();
			System.out.println("NEO Save Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveTime[i] / 1000000000.0 + ",NEO");
				}
			}

			System.out.println();
			System.out.println("NEO Save Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveMemory[i] / 1000000.0 + ",NEO");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					if (file.delete() == false) {
						System.out.println();
					}
				}
			}
		}
		if (folder.delete() == false) {
			System.out.println();
		}
	}
}
