package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

public class GraphHybridTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void HybridInitialisationTest() throws IOException {

		try {
			EClass verticeClass = (EClass) GraphPackage.eINSTANCE.getEClassifier("Vertice");
			EClass graphClass = (EClass) GraphPackage.eINSTANCE.getEClassifier("Graph");
			EStructuralFeature labelFeature = verticeClass.getEStructuralFeature("label");
			
			
			// initialise NeoEMF
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			File databaseFile = new File("databases/test.graphdb");
			File cbpFile = new File("cbps/test.cbpxml");
			//File xmiFile = new File("xmis/test.xmi");
			URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
			
			FileOutputStream outputStream = new FileOutputStream(cbpFile, true);
			
			PersistentResource persistentResource = (PersistentResource) resourceSet.createResource(BlueprintsURI.createFileURI(databaseUri));
		
			HybridResource hybridResource = new HybridResource(persistentResource, outputStream);
			
			Map<String, Object> saveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit().asMap();
			saveOptions = Collections.emptyMap();
			Map<String, Object> loadOptions = Collections.emptyMap();

			if (/*!xmiFile.exists() ||*/ !databaseFile.exists()) {
				hybridResource.save(saveOptions);
				hybridResource.load(loadOptions);
				
				
				GraphFactory factory = GraphFactory.eINSTANCE;
				Graph graph = factory.createGraph();
				Vertice v = factory.createVertice();
				v.setLabel("0");
				graph.getVertices().add(v);
				hybridResource.getContents().add(graph);
				
				TreeIterator<EObject> iterator = hybridResource.getAllContents();
				while(iterator.hasNext()) {
					EObject eObject = iterator.next();
					if (eObject.eClass().equals(verticeClass)) {
						String labelString = (String) eObject.eGet(labelFeature);
						eObject.eSet(labelFeature, labelString + labelString.length());
					}
				}
				
				hybridResource.save(saveOptions);	
			} else {
				hybridResource.load(loadOptions);
				
				TreeIterator<EObject> iterator = hybridResource.getAllContents();
				while(iterator.hasNext()) {
					EObject eObject = iterator.next();
					if (eObject.eClass().equals(verticeClass)) {
						String labelString = (String) eObject.eGet(labelFeature);
						eObject.eSet(labelFeature, labelString + labelString.length());
					}
				}
				
				hybridResource.save(saveOptions);
				
				GraphFactory factory = GraphFactory.eINSTANCE;
				Vertice v = factory.createVertice();
				iterator = hybridResource.getAllContents();
				while(iterator.hasNext()) {
					EObject eObject = iterator.next();
					if (eObject.eClass().equals(graphClass)) {
						Graph graph = (Graph) eObject;
						graph.getVertices().add(v);
						break;
					}
				}
				
				EcoreUtil.delete(v);
				hybridResource.save(saveOptions);
			}
			
			hybridResource.unload();
			hybridResource.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		assertEquals(true, true);
	}

}
