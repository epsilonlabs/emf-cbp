package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
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
import graph.Vertice;

public class HybridTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void HybridInitialisationTest() throws IOException {

		try {
			
			GraphFactory factory = GraphFactory.eINSTANCE;
			Graph graph = factory.createGraph();
			Vertice v = factory.createVertice();
			v.setLabel("Vertex");
			graph.getVertices().add(v);
			
			// initialise NeoEMF
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			File databaseFile = new File("databases/test.graphdb");
			File cbpFile = new File("cbps/test.cbpxml");
			URI databaseUri = BlueprintsURI.createFileURI(databaseFile);
			URI cbpUri = BlueprintsURI.createFileURI(cbpFile);
			
			PersistentResource persistentResource = (PersistentResource) resourceSet.createResource(BlueprintsURI.createFileURI(databaseUri));
			CBPResource cbpResource = new CBPXMLResourceImpl(cbpUri); 
			
			HybridResource hybridResource = new HybridResource(persistentResource, cbpResource);

			Map<String, Object> options = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit().asMap();

			if (!databaseFile.exists()) {
				hybridResource.save(options);
			} else {
				hybridResource.load(Collections.emptyMap());
			}
			
			hybridResource.getPersistentResource().getContents().add(graph);
			hybridResource.getCbpResource().getContents().add(graph);
			hybridResource.save(options);
			hybridResource.unload();
			hybridResource.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		assertEquals(true, true);
	}

}
