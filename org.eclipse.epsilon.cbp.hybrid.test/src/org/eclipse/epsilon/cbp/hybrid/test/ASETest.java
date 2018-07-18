package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.neoemf.HybridNeoEMFResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.internal.resource.UMLResourceImpl;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import CBP.CBPPackage;
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

public class ASETest {

	public ASETest() {
		// 
//		UML.UMLPackage.eINSTANCE.eClass();
//		MoDiscoXMLPackage.eINSTANCE.eClass();
	}

	@Test
	public void testCreateSimpleUMLModel() throws IOException {
		UMLPackage.eINSTANCE.eClass();
		UMLFactory factory = UMLFactory.eINSTANCE;
		
		XMIResourceImpl xmiResource = (XMIResourceImpl) (new XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
		
		StringOutputStream cbpOutputStream = new StringOutputStream();
		HybridResource  hybridResource = new HybridXMIResourceImpl(xmiResource, cbpOutputStream);
		
		//Session 1
		org.eclipse.uml2.uml.Package packageX = factory.createPackage();
		packageX.setName("Package X");
		xmiResource.setID(packageX, "1");
		Class classA = factory.createClass();
		xmiResource.setID(classA, "2");
		classA.setName("Class A");
		Class classB = factory.createClass();
		xmiResource.setID(classB, "3");
		classB.setName("Class B");
		packageX.getPackagedElements().add(classA);
		packageX.getPackagedElements().add(classB);
		xmiResource.getContents().add(packageX);
		
		StringOutputStream xmiOutput = new StringOutputStream();
		hybridResource.save(xmiOutput, null);
		System.out.println(xmiOutput.toString());
		
		//Session 2
		EcoreUtil.delete(classA);
		
		xmiOutput = new StringOutputStream();
		hybridResource.save(xmiOutput, null);
		System.out.println(xmiOutput.toString());
		
		//end
		System.out.println(cbpOutputStream.toString());
		
	}
	
	@Test
	public void testCompareXMICBPEvents() throws IOException {

		try {
			CBPPackage.eINSTANCE.eClass();
			
			Iterator<String> iterator = Files.lines(Paths.get("D:\\TEMP\\ASE\\epsilon.1473.cbpxml")).skip(7702865)
					.iterator();

			while (iterator.hasNext()) {
				String line = iterator.next();
				System.out.println(line);
			}

			

			long start = 0;
			long end = 0;
			long delta = 0;
			double comparisonTime = 0;
			double mergingTime = 0;

			List<File> files = new ArrayList<>();
			files.add(new File("D:\\TEMP\\ASE\\comparison\\epsilon.000.cbpxml.xmi"));
			files.add(new File("D:\\TEMP\\ASE\\comparison\\epsilon.008.cbpxml.xmi"));
			files.add(new File("D:\\TEMP\\ASE\\comparison\\epsilon.044.cbpxml.xmi"));
			files.add(new File("D:\\TEMP\\ASE\\comparison\\epsilon.181.cbpxml.xmi"));
			files.add(new File("D:\\TEMP\\ASE\\comparison\\epsilon.388.cbpxml.xmi"));

			System.out.println("LeftFile,RightFile,ComparisonTime,MergingTime");

			for (File leftFile : files) {

				for (File rightFile : files) {

					// if (files.indexOf(leftFile) == 0) {
					// leftFile.delete();
					// leftFile.createNewFile();
					// }
					//
					// if (files.indexOf(rightFile) == 0) {
					// rightFile.delete();
					// rightFile.createNewFile();
					// }

					String leftFileName = leftFile.getName();
					String rightFileName = rightFile.getName();

					System.out.print(leftFileName + "," + rightFileName + ",");

					XMIResourceImpl leftResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
							.createResource(URI.createFileURI(leftFile.getAbsolutePath()));
					leftResource.load(null);

					XMIResourceImpl rightResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
							.createResource(URI.createFileURI(rightFile.getAbsolutePath()));
					rightResource.load(null);

					IComparisonScope scope = new DefaultComparisonScope(leftResource, rightResource, null);
					EMFCompare comparator = EMFCompare.builder().build();

					start = System.nanoTime();
					Comparison comparison = comparator.compare(scope);
					EList<Diff> diffs = comparison.getDifferences();
					end = System.nanoTime();
					delta = end - start;
					comparisonTime = delta / 1000000000.0; // seconds

					IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
					IBatchMerger batchMerger = new BatchMerger(registry);

					start = System.nanoTime();
					batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
					end = System.nanoTime();
					delta = end - start;
					mergingTime = delta / 1000000000.0; // seconds

					leftResource.unload();
					rightResource.unload();

					System.out.println(comparisonTime + "," + mergingTime);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
	}

	@Test
	public void testCompareXMI() throws IOException {

		UMLPackage.eINSTANCE.eClass();

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml", new CBPXMLResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());

		Resource leftResouce = resourceSet
				.createResource(URI.createFileURI("D:\\TEMP\\ASE\\comparison\\epsilon.018.xmi"));
		leftResouce.load(null);
		Resource rightResource = resourceSet
				.createResource(URI.createFileURI("D:\\TEMP\\ASE\\comparison\\epsilon.019.xmi"));
		rightResource.load(null);

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

		
		XMIResourceImpl diffResource = (XMIResourceImpl) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI("D:\\TEMP\\ASE\\diff.xmi"));
		diffResource.getContents().addAll(EcoreUtil.copyAll(comparison.getDifferences()));
		diffResource.save(null);
		
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

	@Test
	public void testCompareNeoEMF() throws IOException {

		try {
			UML.UMLPackage.eINSTANCE.eClass();
			// deleteFolder(databaseFile);

			File leftNeoDatabase = new File("D:\\TEMP\\ASE\\comparison\\epsilon.008.graphdb");
			File rightNeoDatabase = new File("D:\\TEMP\\ASE\\comparison\\epsilon.044.graphdb");

			Map<String, Object> neoSaveOptions = BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit().asMap();
			neoSaveOptions = Collections.emptyMap();
			Map<String, Object> neoLoadOptions = Collections.emptyMap();

			PersistenceBackendFactoryRegistry.register(BlueprintsURI.SCHEME,
					BlueprintsPersistenceBackendFactory.getInstance());
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME,
					PersistentResourceFactory.getInstance());

			PersistentResource leftResource = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(leftNeoDatabase));
			PersistentResource rightResource = (PersistentResource) resourceSet
					.createResource(BlueprintsURI.createFileURI(rightNeoDatabase));

			leftResource.load(neoLoadOptions);
			rightResource.load(neoLoadOptions);

//			int count = 0;
//			TreeIterator<EObject> iterator = leftResource.getAllContents();
//			while (iterator.hasNext()) {
//				PersistentEObject eObject = (PersistentEObject) iterator.next();
//				System.out.println(eObject.id().toString());
//				count += 1;
//
//			}
//			System.out.println("Count = " + count);
//			System.out.println("-----------------------------------");
//
//			count = 0;
//			iterator = rightResource.getAllContents();
//			while (iterator.hasNext()) {
//				PersistentEObject eObject = (PersistentEObject) iterator.next();
//				System.out.println(eObject.id().toString());
//				count += 1;
//
//			}
//			System.out.println("Count = " + count);

			IComparisonScope scope = new DefaultComparisonScope(leftResource, rightResource, null);
			EMFCompare comparator = EMFCompare.builder().build();
			Comparison comparison = comparator.compare(scope);
			EList<Diff> diffs = comparison.getDifferences();
			System.out.println("Diffs: " + diffs.size());

			IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
			IBatchMerger batchMerger = new BatchMerger(registry);
			batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());

			leftResource.close();
			rightResource.close();

			System.out.println("Finished!!!");

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
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
