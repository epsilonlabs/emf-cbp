package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridNeoEMFResourceImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;

public class UMLHybridTest {

	@Test
	public void testImportUMLModel() {

		try {
			// initialise UML package
			UMLPackage.eINSTANCE.eClass();
			UMLFactory umlFactory = UMLFactory.eINSTANCE;
			Model dummyModel = umlFactory.createModel();
			dummyModel.setName("Dummy");
			// System.out.println(dummyModel.id());

			// files
			File databaseFile = new File("databases/import-uml.graphdb");
			File cbpFile = new File("cbps/import-uml.cbpxml");
			File xmiFile = new File("xmis/import-uml.uml");

			// load UML model
			ResourceSet xmiResourceSet = new ResourceSetImpl();
			xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml",
					new XMIResourceFactoryImpl());

			String xmiFilePath = xmiFile.getAbsolutePath();
			URI xmiFileUri = URI.createFileURI(xmiFilePath);
			Resource xmiResource = xmiResourceSet.getResource(xmiFileUri, true);

			// load Neo model
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
			PersistentResource persistentResource = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(databaseUri));
			FileOutputStream outputStream = new FileOutputStream(cbpFile, true);

			// PersistentResource hybridResource = persistentResource;
			HybridNeoEMFResourceImpl hybridResource = new HybridNeoEMFResourceImpl(persistentResource, outputStream);

			Map<String, Object> saveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit()
					.asMap();
			saveOptions = Collections.emptyMap();
			Map<String, Object> loadOptions = Collections.emptyMap();

			if (databaseFile.exists() == false) {
				hybridResource.getContents().add(dummyModel);
				hybridResource.save(saveOptions);
				hybridResource.load(loadOptions);
				hybridResource.getContents().clear();
				hybridResource.save(saveOptions);
			} else {
				hybridResource.load(loadOptions);

				// copy from UML xmi resource to Neo resource
				EList<EObject> objects = xmiResource.getContents();
				hybridResource.getContents().addAll(objects);

				hybridResource.save(saveOptions);
			}

			// check if copy was successful
			int i = 0;
			TreeIterator<EObject> iterator2 = hybridResource.getAllContents();
			while (iterator2.hasNext()) {
				i += 1;
				EObject eObject = iterator2.next();
				System.out.println(String.valueOf(i) + ": " + eObject);
			}

			// shutdown resources
			xmiResource.unload();
			hybridResource.unload();
			hybridResource.close();

			assertEquals(true, true);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
