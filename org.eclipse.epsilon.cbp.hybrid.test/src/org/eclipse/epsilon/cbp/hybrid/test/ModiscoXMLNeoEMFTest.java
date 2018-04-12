package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridNeoEMFResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.Element;
import org.eclipse.gmt.modisco.xml.Root;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLFactory;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
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
import node.Node;
import node.NodeFactory;
import node.NodePackage;

public class ModiscoXMLNeoEMFTest {

	@Test
	public void testImportFromXMI() throws IOException {
		UMLPackage.eINSTANCE.eClass();

		File databaseFile = new File("D:\\TEMP\\HYBRID\\uml-metamodel.graphdb");
		File xmiFile = new File("D:\\TEMP\\HYBRID\\smalluml-final-state.xmi");
		// File xmiFile = new File("D:\\TEMP\\HYBRID\\bpmn2-final-state.xmi");
		// File xmiFile = new File("D:\\TEMP\\HYBRID\\epsilon-final-state.xmi");

		//// init xmi
		Map<Object, Object> xmiSaveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		xmiSaveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
		Map<Object, Object> xmiLoadOptions = (new XMIResourceImpl()).getDefaultLoadOptions();
		xmiLoadOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

		XMIResourceImpl xmiResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
		xmiResource.load(xmiLoadOptions);

		//// init neoemf
		deleteFolder(databaseFile);
		PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
				BlueprintsPersistenceBackendFactory.getInstance());
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
				PersistentResourceFactory.getInstance());

		URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
		PersistentResource persistentResource = (PersistentResource) resourceSet
				.createResource(BlueprintsURI.createFileURI(databaseUri));

		Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit()
				.asMap();
		neoSaveOptions = Collections.emptyMap();
		Map<String, Object> neoLoadOptions = Collections.emptyMap();

		// copy
		persistentResource.getContents().addAll(EcoreUtil.copyAll(xmiResource.getContents()));
		persistentResource.save(neoSaveOptions);

		String id = ((PersistentEObject) persistentResource.getContents().get(0)).id().toString();

		persistentResource.unload();
		persistentResource.close();

		// check
		persistentResource.load(neoLoadOptions);
		xmiResource.unload();
		xmiResource = (XMIResourceImpl) (new XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
		xmiResource.getContents().addAll(persistentResource.getContents());
		StringOutputStream outputStream = new StringOutputStream();
		xmiResource.save(outputStream, xmiSaveOptions);
		System.out.println(outputStream);

		// closing
		outputStream.close();
		xmiResource.unload();
		persistentResource.unload();
		persistentResource.close();

		// check get object only
		persistentResource.load(neoLoadOptions);
		PersistentEObject eObject = (PersistentEObject) persistentResource.getEObject(id);
		System.out.println(eObject);
	}

	@Test
	public void testModMetamodel() {

		try {
			MoDiscoXMLPackage.eINSTANCE.eClass();
			MoDiscoXMLFactory factory = MoDiscoXMLFactory.eINSTANCE;
			Element dummyModel = factory.createElement();
			dummyModel.setName("0");
			org.eclipse.gmt.modisco.xml.Comment comment = factory.createComment();
			comment.setName("QQQQ");
			dummyModel.getChildren().add(comment);

			// files
			File databaseFile = new File("D:\\TEMP\\HYBRID\\modiscoxml-metamodel.graphdb");

			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
			PersistentResource persistentResource = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(databaseUri));

			Map<String, Object> saveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit()
					.asMap();
			saveOptions = Collections.emptyMap();
			Map<String, Object> loadOptions = Collections.emptyMap();

			if (databaseFile.exists()) {
				persistentResource.load(loadOptions);
			}
			persistentResource.getContents().add(dummyModel);
			persistentResource.save(saveOptions);
			persistentResource.load(loadOptions);

			TreeIterator<EObject> treeIterator = persistentResource.getAllContents();
			while (treeIterator.hasNext()) {
				EObject eObject = treeIterator.next();
				PersistentEObject persistentObject = (PersistentEObject) eObject;
				System.out.println(persistentObject.id());
				if (eObject instanceof Element) {
					Element model = (Element) eObject;
					System.out.println(model.getName());
				}
			}

			persistentResource.close();

			assertEquals(true, true);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

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
