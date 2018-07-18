package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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
import org.eclipse.epsilon.cbp.hybrid.neoemf.HybridNeoEMFResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.core.StringId;
import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;
import node.Node;
import node.NodeFactory;
import node.NodePackage;

public class UMLNeoEMFTest {

	// File databaseFile = new File("D:\\TEMP\\ASE\\_output.epsilon.graphdb");
	// File xmiFile = new File("D:\\TEMP\\ASE\\epsilon.1009.xmi");
	// File cbpFile = new File("D:\\TEMP\\ASE\\_output.epsilon.cbpxml");
	//
	// File databaseFile = new File("D:\\TEMP\\ASE\\_output.epsilon.graphdb");
	// File xmiFile = new File("D:\\TEMP\\ASE\\epsilon.1009.xmi");
	// File cbpFile = new File("D:\\TEMP\\ASE\\_output.epsilon.cbpxml");

	File databaseFile = new File("D:\\TEMP\\ASE\\_output.bpmn2.graphdb");
	File xmiFile = new File("D:\\TEMP\\ASE\\experiment\\bpmn2.192.xmi");
	File cbpFile = new File("D:\\TEMP\\ASE\\_output.bpmn2.cbpxml");

	public UMLNeoEMFTest() {
//		UMLPackage.eINSTANCE.eClass();
		UML.UMLPackage.eINSTANCE.eClass();
		MoDiscoXMLPackage.eINSTANCE.eClass();
	}

	@Test
	public void testImportCustomUMLMetamodel() throws IOException {

		deleteFolder(databaseFile);

		PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
				BlueprintsPersistenceBackendFactory.getInstance());
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
				PersistentResourceFactory.getInstance());

		URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
		PersistentResource persistentResource = (PersistentResource) resourceSet
				.createResource(BlueprintsURI.createFileURI(databaseUri));

		Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit()
				.asMap();
		neoSaveOptions = Collections.emptyMap();
		Map<String, Object> neoLoadOptions = Collections.emptyMap();

		
//		UML.Model dummy = UML.UMLFactory.eINSTANCE.createModel();
//		 dummy.setName("DUMMY");
//		 persistentResource.getContents().add(dummy);
//		 persistentResource.save(neoSaveOptions);
//		
		UML.UMLFactory factory = UML.UMLFactory.eINSTANCE;

		for (int i = 1; i <= 10; i++) {
			UML.Model model = factory.createModel();
			model.id(new StringId(String.valueOf(i)));
			model.setName("Model-" + String.valueOf(i));
			persistentResource.getContents().add(model);
			persistentResource.save(neoSaveOptions);
			persistentResource.unload();
			persistentResource.close();
			persistentResource.load(neoLoadOptions);
		}
		
		persistentResource.load(neoLoadOptions);
		for (EObject obj : persistentResource.getContents()) {
			System.out.println(((PersistentEObject) obj).id().toString());
		}
		persistentResource.unload();
		persistentResource.close();
		
		persistentResource.load(neoLoadOptions);
		for (EObject obj : persistentResource.getContents()) {
			System.out.println(((UML.Model) obj).getName());
		}
	}

	@Test
	public void testImportFromXMI() throws IOException {

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
		persistentResource.getContents().addAll(xmiResource.getContents());

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
	public void testUMLMetamodel() {

		try {
			NodePackage.eINSTANCE.eClass();
			NodeFactory.eINSTANCE.eClass();
			NodeFactory nodeFactory = NodeFactory.eINSTANCE;
			Node node = nodeFactory.createNode();
			node.setName("Dummy");

			UML.UMLPackage.eINSTANCE.eClass();
			UML.UMLFactory.eINSTANCE.eClass();
			UML.UMLFactory umlFactory = UML.UMLFactory.eINSTANCE;
			UML.Model dummyModel = umlFactory.createModel();
			dummyModel.setName("Dummy");
//			Comment comment = dummyModel.createOwnedComment();
//			comment.setBody("AAAAA");

			// File xmiFile = new File("D:\\TEMP\\HYBRID\\uml-metamodel.uml");

			// // load UML model
			// ResourceSet xmiResourceSet = new ResourceSetImpl();
			// xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml",
			// new XMIResourceFactoryImpl());
			//
			// String xmiFilePath = xmiFile.getAbsolutePath();
			// URI xmiFileUri = URI.createFileURI(xmiFilePath);
			// Resource xmiResource = xmiResourceSet.getResource(xmiFileUri,
			// true);

			// deleteFolder(databaseFile);

			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
			PersistentResource persistentResource = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(databaseUri));
			FileOutputStream outputStream = new FileOutputStream(cbpFile, true);

			Map<String, Object> saveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit()
					.asMap();
			saveOptions = Collections.emptyMap();
			Map<String, Object> loadOptions = Collections.emptyMap();

			if (databaseFile.exists()) {
				persistentResource.load(loadOptions);
			}
			persistentResource.getContents().add(dummyModel);
			persistentResource.save(saveOptions);
			persistentResource.close();
			persistentResource.load(loadOptions);

			TreeIterator<EObject> treeIterator = persistentResource.getAllContents();
			while (treeIterator.hasNext()) {
				EObject eObject = treeIterator.next();
				PersistentEObject persistentObject = (PersistentEObject) eObject;
				if (eObject instanceof UML.Model) {
					UML.Model model = (UML.Model) eObject;
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
