package org.eclipse.epsilon.cbp.hybrid.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.gmt.modisco.java.Annotation;
import org.eclipse.gmt.modisco.java.ClassDeclaration;
import org.eclipse.gmt.modisco.java.JavaFactory;
import org.eclipse.gmt.modisco.java.JavaPackage;
import org.eclipse.gmt.modisco.java.Model;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import fr.inria.atlanmod.neoemf.core.DefaultPersistentEObject;
import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;
import graph.Graph;
import graph.GraphFactory;
import graph.GraphPackage;
import graph.Vertice;
import node.Node;
import node.NodeFactory;
import node.NodePackage;

public class UMLHybridTest {

	@Test
	public void importUMLModelTest() {

		try {
			// initialise UML package
			UMLPackage umlPackage = UMLPackage.eINSTANCE;
			UMLFactory umlFactory = UMLFactory.eINSTANCE;
			Package pkg = umlFactory.createPackage();
			System.out.println(pkg.id());
			
			GraphPackage graphPackage = GraphPackage.eINSTANCE;
			GraphFactory graphFactory = GraphFactory.eINSTANCE;
			Graph graph = graphFactory.createGraph();
			Vertice v = graphFactory.createVertice();
			graph.getVertices().add(v);

			NodePackage nodePackage = NodePackage.eINSTANCE;
			NodeFactory nodeFactory = NodeFactory.eINSTANCE;
			Node node = nodeFactory.createNode();
			node.setName("MyNode");
			System.out.println(node);

			JavaPackage javaPackage = JavaPackage.eINSTANCE;
			JavaFactory javaFactory = JavaFactory.eINSTANCE;
			Model model = javaFactory.createModel();

			// files
			File databaseFile = new File("databases/import-uml.graphdb");
			File cbpFile = new File("cbps/import-uml.cbpxml");
			File xmiFile = new File("xmis/import-uml.uml");
			File xmiFile2 = new File("xmis/import-uml2.uml");

			// load UML model
			ResourceSet xmiResourceSet = new ResourceSetImpl();
			xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml",
					new XMIResourceFactoryImpl());

			String xmiFilePath = xmiFile.getAbsolutePath();
			URI xmiFileUri = URI.createFileURI(xmiFilePath);
			Resource xmiResource = xmiResourceSet.getResource(xmiFileUri, true);

			// Resource xmiResource2 =
			// xmiResourceSet.createResource(URI.createFileURI(xmiFile2.getAbsolutePath()));

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

			PersistentResource hybridResource = persistentResource;
			// HybridResource hybridResource = new
			// HybridResource(persistentResource, outputStream);

			Map<String, Object> saveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit()
					.asMap();
			saveOptions = Collections.emptyMap();
			Map<String, Object> loadOptions = Collections.emptyMap();

			if (databaseFile.exists() == false) {
				hybridResource.save(saveOptions);
			} else {
				hybridResource.load(loadOptions);
			}

			// copy from UML xmi resource to Neo resource
			// EList<EObject> objects = xmiResource.getContents();
			// ArrayList<EObject> objects2 = (ArrayList<EObject>)
			// EcoreUtil.copyAll(objects);
			// hybridResource.getContents().addAll(objects2);
			// xmiResource2.getContents().addAll(EcoreUtil.copyAll(xmiResource.getContents()));
			hybridResource.getContents().add(pkg);

			// check if copy was successful
			TreeIterator<EObject> iterator2 = hybridResource.getAllContents();
			while (iterator2.hasNext()) {
				EObject eObject = iterator2.next();
				System.out.println(eObject);
			}

			// shutdown resources
			xmiResource.unload();
			hybridResource.unload();
			hybridResource.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
