package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.ModelMBeanInfoSupport;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridNeoEMFResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridXmiResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.resource.DefaultPersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;
import fr.inria.atlanmod.neoemf.util.logging.NeoLogger;

public class HybridNeoEMFTest {

	public final int ITERATION = 3;
	public final int START_FROM = 0;
	public final int SLEEP_TIME = 1000;

	public HybridNeoEMFTest() {

	}

	@Test
	public void testHybridNeoEMFLoad() {
		try {
			System.out.println("Start ....");

			// init common
			File targetNeoFile = new File("D:\\TEMP\\HYBRID\\output_load.graphdb");
			File targetCbpFile = new File("D:\\TEMP\\HYBRID\\output_load.cbxpml");
			// File sourceXmiFile = new
			// File("D:\\TEMP\\HYBRID\\smalluml-final-state.xmi");
//			File sourceXmiFile = new File("D:\\TEMP\\HYBRID\\bpmn2-final-state.xmi");
			File sourceXmiFile = new File("D:\\TEMP\\HYBRID\\epsilon-final-state.xmi");

			System.out.println("Processing " + sourceXmiFile.getName() + "....");
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();
			HybridNeoEMFTest.deleteFolder(targetNeoFile);

			// init neoemf
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());

			PersistentResource neoResource = (PersistentResource) PersistentResourceFactory.getInstance()
					.createResource(BlueprintsURI.createFileURI(targetNeoFile));

			Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit().asMap();
			Map<String, Object> neoLoadOptions = Collections.emptyMap();

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);
			HybridResource hybridResource = new HybridNeoEMFResourceImpl(neoResource, targetCbpBufferedStream);

			// init xmi and load it
			Map<Object, Object> xmiSaveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiSaveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
					XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
			Map<Object, Object> xmiLoadOptions = (new XMIResourceImpl()).getDefaultLoadOptions();
			xmiLoadOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
					XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(sourceXmiFile.getAbsolutePath()));
			xmiResource.load(xmiLoadOptions);

			// copy xmi to neoemf
			hybridResource.getContents().addAll(EcoreUtil.copyAll(xmiResource.getContents()));
			hybridResource.save(neoSaveOptions);
			targetCbpBufferedStream.close();
			targetCbpFileStream.close();
			hybridResource.unload();
			((HybridNeoEMFResourceImpl) hybridResource).close();
			xmiResource.unload();

			// reinitialise and reload and measure time and memory
			System.out.println("Reload the XMI Several Times");
			long[] loadTime = new long[ITERATION];
			long[] loadMemory = new long[ITERATION];
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + i);

				neoResource = (PersistentResource) PersistentResourceFactory.getInstance()
						.createResource(BlueprintsURI.createFileURI(targetNeoFile));

				hybridResource = new HybridNeoEMFResourceImpl(neoResource, targetCbpBufferedStream);

				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				hybridResource.load(neoLoadOptions);

				long endTime = System.nanoTime();
				endTime = System.nanoTime();
				System.gc();
				// Thread.sleep(SLEEP_TIME);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				loadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					loadMemory[i] += 0;
				} else {
					loadMemory[i] += Math.abs(endMemory - startMemory);
				}
				System.out.println((loadTime[i] / 1000000000.0) + ", " + (loadMemory[i] / 1000000.0));

				hybridResource.unload();
				((HybridNeoEMFResourceImpl) hybridResource).close();
			}

			// // validate
			// hybridResource.load(neoLoadOptions);
			// TreeIterator<EObject> iterator = hybridResource.getAllContents();
			// while (iterator.hasNext()) {
			// EObject eObject = iterator.next();
			// String id = ((PersistentEObject) eObject).id().toString();
			// System.out.println(id);
			// }

			// hybridResource.load(neoLoadOptions);
			// Resource resource = (new
			// XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
			// resource.getContents().addAll(hybridResource.getContents());
			// StringOutputStream sos = new StringOutputStream();
			// resource.save(sos, null);
			//// System.out.println(sos);
			// hybridResource.unload();

			// closing
			hybridResource.unload();

			// print results
			System.out.println();
			System.out.println("Hybrid XMI Load Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadTime[i] / 1000000000.0 + ",H-XMI");
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Load Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadMemory[i] / 1000000.0 + ",H-XMI");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	@Test
	public void testHybridNeoEMFSave() {
		try {
			System.out.println("Please run the load test first before running this one.");
			System.out.println("Start ....");

			// init common
			File sourceNeoFile = new File("D:\\TEMP\\HYBRID\\output_load.graphdb");
			File targetCbpFile = new File("D:\\TEMP\\HYBRID\\output_save.cbxpml");
			// File sourceXmiFile = new
			// File("D:\\TEMP\\HYBRID\\smalluml-final-state.xmi");
			File sourceXmiFile = new File("D:\\TEMP\\HYBRID\\bpmn2-final-state.xmi");

			System.out.println("Processing " + sourceXmiFile.getName() + "....");
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			// init neoemf
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			PersistentResource neoResource = (PersistentResource) PersistentResourceFactory.getInstance()
					.createResource(BlueprintsURI.createFileURI(sourceNeoFile));

			Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit().asMap();
			Map<String, Object> neoLoadOptions = Collections.emptyMap();

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile, true);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);
			HybridResource hybridResource = new HybridNeoEMFResourceImpl(neoResource, targetCbpBufferedStream);
			hybridResource.load(neoLoadOptions);

			UMLFactory factory = UMLFactory.eINSTANCE;
			Package p = factory.createPackage();

			int measureCount = 0;
			long[] saveTime = new long[ITERATION];
			long[] saveMemory = new long[ITERATION];

			for (; measureCount < ITERATION; measureCount++) {

				if (hybridResource.getContents().get(0) instanceof Model) {
					Model model = (Model) hybridResource.getContents().get(0);
//					model.getPackagedElements().add(EcoreUtil.copy(p));
					model.setName("dummy-" + measureCount);
				}

				// do measure
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				hybridResource.save(neoSaveOptions);

				long endTime = System.nanoTime();
				System.gc();
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
			((HybridNeoEMFResourceImpl) hybridResource).close();

			// print results
			System.out.println();
			System.out.println("Hybrid XMI Save Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveTime[i] / 1000000000.0 + ",H-XMI");
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Save Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveMemory[i] / 1000000.0 + ",H-XMI");
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
