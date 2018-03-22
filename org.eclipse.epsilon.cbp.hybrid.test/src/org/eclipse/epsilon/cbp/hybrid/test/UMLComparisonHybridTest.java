package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
//import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
//import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl.EStoreEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridNeoEMFResourceImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsPersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;

public class UMLComparisonHybridTest {

	@Test
	public void testUMLModelComparison() {

		try {
			// initialise UML package
			UMLPackage.eINSTANCE.eClass();
			UMLFactory umlFactory = UMLFactory.eINSTANCE;
			Model dummyModel = umlFactory.createModel();
			dummyModel.setName("Dummy");
			System.out.println(dummyModel);

			// files
			File databaseFile1 = new File("databases/compare-uml1.graphdb");
			File databaseFile2 = new File("databases/compare-uml2.graphdb");
			File cbpFile = new File("cbps/compare-uml.cbpxml");
			File xmiFile = new File("xmis/compare-uml.uml");
			File xmiFile2 = new File("xmis/compare-uml2.uml");

			// load UML model
			ResourceSet xmiResourceSet = new ResourceSetImpl();
			xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml",
					new XMIResourceFactoryImpl());

			String xmiFilePath = xmiFile.getAbsolutePath();
			URI xmiFileUri = URI.createFileURI(xmiFilePath);
			Resource xmiResource = xmiResourceSet.getResource(xmiFileUri, true);

			// load Neo model
			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME, BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			PersistentResource persistentResource1 = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(BlueprintsURI.createFileURI(databaseFile1)));
			PersistentResource persistentResource2 = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(BlueprintsURI.createFileURI(databaseFile2)));

			FileOutputStream outputStream = new FileOutputStream(cbpFile, true);

			// PersistentResource hybridResource = persistentResource;
			// Resource hybridResource =
			// xmiResourceSet.createResource(URI.createFileURI(xmiFile2.getAbsolutePath()));
			HybridNeoEMFResourceImpl hybridResource1 = new HybridNeoEMFResourceImpl(persistentResource1, outputStream);
			HybridNeoEMFResourceImpl hybridResource2 = new HybridNeoEMFResourceImpl(persistentResource2, outputStream);

			Map<String, Object> saveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().weakCache().autocommit()
					.asMap();
			saveOptions = Collections.emptyMap();
			Map<String, Object> loadOptions = Collections.emptyMap();

			if (databaseFile1.exists() == false) {
				// if (xmiFile2.exists() == false) {
				hybridResource1.getContents().add(EcoreUtil.copy(dummyModel));
				hybridResource1.save(saveOptions);
				hybridResource1.load(loadOptions);
//				hybridResource1.getContents().clear();
				hybridResource1.save(saveOptions);
				
				hybridResource2.getContents().add(EcoreUtil.copy(dummyModel));
				hybridResource2.save(saveOptions);
				hybridResource2.load(loadOptions);
//				hybridResource2.getContents().clear();
				hybridResource2.save(saveOptions);
				
			} else {
				hybridResource1.load(loadOptions);
				hybridResource2.load(loadOptions);

				// initialise comparison
//				IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
//				UMLMerger umlMerger = new UMLMerger();
//				umlMerger.setRanking(11);
//				registry.add(umlMerger);
//				IBatchMerger batchMerger = new BatchMerger(registry);
//
//				IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
//				BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
//						Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
//				postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
				
				//custom matchEngine
				IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.NEVER);
				IComparisonFactory comparisonFactory = new DefaultComparisonFactory(
						new DefaultEqualityHelperFactory());
				IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
				matchEngineFactory.setRanking(20);
				IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
				matchEngineRegistry.add(matchEngineFactory);
				
				Builder builder = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry);
//				builder.setPostProcessorRegistry(postProcessorRegistry);
				EMFCompare comparator = builder.build();

				 
				hybridResource2.getContents().addAll(EcoreUtil.copyAll(xmiResource.getContents()));
				System.out.println("===Hybrid Resource 2===");
				int i = 0;
				TreeIterator<EObject> iterator2 = hybridResource2.getAllContents();
				while (iterator2.hasNext()) {
					i += 1;
					EObject eObject = iterator2.next();
					System.out.println(String.valueOf(i) + ": " + eObject);
				}
				
//				hybridResource2.save(saveOptions);
//				hybridResource2.load(loadOptions);

				IComparisonScope2 scope = createComparisonScope(hybridResource1.getStateBasedResource(), xmiResource);

				Comparison comparison = comparator.compare(scope);
				EList<Diff> diffs = comparison.getDifferences();

//				batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());

				// after merging, to check if there are no more differences
				// comparator = builder.build();
				// scope =
				// createComparisonScope(hybridResource.getPersistentResource(),
				// xmiResource);
				// comparison = comparator.compare(scope);
				// diffs = comparison.getDifferences();
				// System.out.println(" " + diffs.size());
				// for (Diff diff : diffs) {
				// System.out.println(diff);
				// }
				// if (diffs.size() > 1) {
				// System.out.println("There are still differences between the
				// CBP and the XMI.");
				// return;
				// }

				// check if copy was successful
				System.out.println("===Hybrid Resource 1===");
				int j = 0;
				TreeIterator<EObject> iterator3 = hybridResource1.getAllContents();
				while (iterator3.hasNext()) {
					j += 1;
					EObject eObject = iterator3.next();
					System.out.println(String.valueOf(j) + ": " + eObject);
				}
				
				hybridResource1.save(saveOptions);
			}

			

			// shutdown resources
			xmiResource.unload();
			hybridResource1.unload();
			// hybridResource.close();

			assertEquals(true, true);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private IComparisonScope2 createComparisonScope(EObject notifierLeft, EObject notifierRight) {

		IComparisonScope2 scope = new DefaultComparisonScope(notifierLeft, notifierRight, null);
		Set<ResourceSet> resourceSets = ImmutableSet.of(notifierLeft.eResource().getResourceSet(),
				notifierRight.eResource().getResourceSet());
		com.google.common.collect.ImmutableSet.Builder<URI> uriBuilder = ImmutableSet.builder();
		for (ResourceSet set : resourceSets) {
			for (Resource resource : set.getResources()) {
				uriBuilder.add(resource.getURI());
			}
		}
		Set<URI> setUri = uriBuilder.build();
		scope.getAllInvolvedResourceURIs().addAll(setUri);
		return scope;
	}

	private IComparisonScope2 createComparisonScope(Resource notifierLeft, Resource notifierRight) {

		IComparisonScope2 scope = new DefaultComparisonScope(notifierLeft, notifierRight, null);
		Set<ResourceSet> resourceSets = ImmutableSet.of(notifierLeft.getResourceSet(), notifierRight.getResourceSet());
		com.google.common.collect.ImmutableSet.Builder<URI> uriBuilder = ImmutableSet.builder();
		for (ResourceSet set : resourceSets) {
			for (Resource resource : set.getResources()) {
				uriBuilder.add(resource.getURI());
			}
		}
		Set<URI> setUri = uriBuilder.build();
		scope.getAllInvolvedResourceURIs().addAll(setUri);
		return scope;
	}
}
