package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class UMLCompareTest {

	@Test
	public void testUMLComparison() throws IOException {

		UMLPackage.eINSTANCE.eClass();

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml", new CBPXMLResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());

		// Resource leftResouce =
		// resourceSet.createResource(URI.createFileURI("D:/TEMP/BPMN2/xmi/model.cbpxml"));
		Resource leftResouce = resourceSet
				.createResource(URI.createFileURI("D:\\TEMP\\ASE\\comparison\\epsilon.008.xmi"));
		leftResouce.load(null);
		// Resource rightResource = resourceSet.createResource(
		// URI.createFileURI("D:/TEMP/BPMN2/xmi/BPMN2-0000001-1b47f8942c6849ba1c2c61eb3e2253217361dbbc.xmi"));
		// Resource rightResource = resourceSet.createResource(
		// URI.createFileURI("D:/TEMP/BPMN2/xmi/BPMN2-0000002-ea2d0e08a81f54e12ac1f12e95fc3fbd21b5e09a.xmi"));
		Resource rightResource = resourceSet
				.createResource(URI.createFileURI("D:\\TEMP\\ASE\\comparison\\epsilon.044.xmi"));
		rightResource.load(null);

		// TreeIterator<EObject> iterator = rightResource.getAllContents();
		// while(iterator.hasNext()) {
		// EObject eObject = iterator.next();
		// ((UMLResource) rightResource).setID(eObject, null);
		// }

		IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
		BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
				Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
		postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
		Builder builder = EMFCompare.builder();
		builder.setPostProcessorRegistry(postProcessorRegistry);
		EMFCompare comparator = builder.build();

		IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
		UMLMerger umlMerger = new UMLMerger();
		umlMerger.setRanking(11);
		registry.add(umlMerger);
		IBatchMerger batchMerger = new BatchMerger(registry);

		IComparisonScope2 scope = createComparisonScope(leftResouce, rightResource);
		System.out.println("Compare ...");

		System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		Comparison comparison = comparator.compare(scope);
		EList<Diff> diffs = comparison.getDifferences();
		System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
		System.out.println("Diffs: " + diffs.size());

		while (diffs.size() > 0) {
			System.out.println("\nMerge ...");
			System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
			batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
			System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

//			leftResouce.save(null);
			System.out.println("\nRe-compare again for validation ...");
			scope = createComparisonScope(leftResouce, rightResource);
			System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
			comparison = comparator.compare(scope);
			System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
			diffs = comparison.getDifferences();
			System.out.println("Diffs: " + diffs.size());
		}
	}

	private IComparisonScope2 createComparisonScope(Resource cbpResource, Resource xmiResource) {
		IComparisonScope2 scope = new DefaultComparisonScope(cbpResource, xmiResource, null);
		Set<ResourceSet> resourceSets = ImmutableSet.of(cbpResource.getResourceSet(), xmiResource.getResourceSet());
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
