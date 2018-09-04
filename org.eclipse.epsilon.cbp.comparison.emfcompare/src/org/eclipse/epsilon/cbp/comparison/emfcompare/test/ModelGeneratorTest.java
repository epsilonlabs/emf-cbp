package org.eclipse.epsilon.cbp.comparison.emfcompare.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.diff.IDiffEngine;
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
import org.eclipse.emf.compare.merge.IMerger2;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPDiffEngine;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPEObjectMatcher;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPEngine;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPMatchEngineFactory;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPRCPMatchEngineFactory;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.xml.stream.FactoryConfigurationError;

public class ModelGeneratorTest {

    String cbpOriginPath = "D:\\TEMP\\COMPARISON3\\origin.cbpxml";
    String cbpLeftPath = "D:\\TEMP\\COMPARISON3\\left.cbpxml";
    String cbpRightPath = "D:\\TEMP\\COMPARISON3\\right.cbpxml";
    String xmiOriginPath = "D:\\TEMP\\COMPARISON3\\origin.xmi";
    String xmiLeftPath = "D:\\TEMP\\COMPARISON3\\left.xmi";
    String xmiRightPath = "D:\\TEMP\\COMPARISON3\\right.xmi";

    File cbpOriginFile = new File(cbpOriginPath);
    File cbpLeftFile = new File(cbpLeftPath);
    File cbpRightFile = new File(cbpRightPath);
    File xmiOriginFile = new File(xmiOriginPath);
    File xmiLeftFile = new File(xmiLeftPath);
    File xmiRightFile = new File(xmiRightPath);

    Resource xmiOriginResource = null;
    Resource xmiLeftResource = null;
    Resource xmiRightResource = null;

    HybridResource hybridOriginResource = null;
    HybridResource hybridLeftResource = null;
    HybridResource hybridRightResource = null;

    public ModelGeneratorTest() throws FactoryConfigurationError, IOException {

	MoDiscoXMLPackage.eINSTANCE.eClass();
	UMLPackage.eINSTANCE.eClass();

	xmiOriginResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiOriginPath));
	xmiLeftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiLeftPath));
	xmiRightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiRightPath));

	hybridOriginResource = new HybridXMIResourceImpl(xmiOriginResource, new FileOutputStream(cbpOriginFile, true));
	hybridLeftResource = new HybridXMIResourceImpl(xmiLeftResource, new FileOutputStream(cbpLeftFile, true));
	hybridRightResource = new HybridXMIResourceImpl(xmiRightResource, new FileOutputStream(cbpRightFile, true));

    }

    @Test
    public void testEMFComparisonXMIExtension() throws IOException {

	Logger.getRootLogger().setLevel(Level.OFF);

	System.out.println("Compare using XMI extension");
	String leftPath = "D:\\TEMP\\COMPARISON3\\left.xmi";
	String rightPath = "D:\\TEMP\\COMPARISON3\\right.xmi";

	Resource leftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftPath));
	Resource rightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightPath));

	System.out.println("Loading left resource ...");
	leftResource.load(null);
	System.out.println("Loading right resource ...");
	rightResource.load(null);

	System.out.println("Compare ...");
	EMFCompare comparator = EMFCompare.builder().build();

	IComparisonScope2 scope = new DefaultComparisonScope(leftResource, rightResource, null);
	long start = System.nanoTime();
	Comparison comparison = comparator.compare(scope);
	long end = System.nanoTime();
	System.out.println("Comparison Time  = " + (end - start) / 1000000000.0);
	EList<Diff> diffs = comparison.getDifferences();

	System.out.println("Matches = " + comparison.getMatches().size());
	System.out.println("Diffs = " + diffs.size());

	System.out.println("\nList of Diffs: XMI");
	for (Diff diff : diffs) {
	  String leftId = null;
	  String rightId = null;
	  if (diff.getMatch().getLeft() != null ) {
	      leftId = leftResource.getURIFragment(diff.getMatch().getLeft());
	      if (leftId.equals("123")) {
		  System.out.println();
	      }
	  }
	  if (diff.getMatch().getRight() != null ) {
	      rightId = rightResource.getURIFragment(diff.getMatch().getRight());
	  }
	  String output = leftId + " - " + rightId + " : " + diff.getKind();
	  System.out.println(output);
	}
	
	assertEquals(true, true);
    }

    @Test
    public void testEMFComparisonUMLExtension() throws IOException {

	Logger.getRootLogger().setLevel(Level.OFF);

	System.out.println("Compare using UML extension");
	String umlLeftPath = "D:\\TEMP\\COMPARISON2\\left.uml";
	String umlRightPath = "D:\\TEMP\\COMPARISON2\\right.uml";

	Resource umlLeftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(umlLeftPath));
	Resource umlRightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(umlRightPath));

	System.out.println("Loading left resource ...");
	umlLeftResource.load(null);
	System.out.println("Loading right resource ...");
	umlRightResource.load(null);

	// IMerger2.Registry registry =
	// IMerger2.RegistryImpl.createStandaloneInstance();
	// UMLMerger umlMerger = new UMLMerger();
	// umlMerger.setRanking(11);
	// registry.add(umlMerger);
	// IBatchMerger batchMerger = new BatchMerger(registry);

	IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
	BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(), Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
	postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

	IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
	IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
	IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
	matchEngineFactory.setRanking(20);
	IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	matchEngineRegistry.add(matchEngineFactory);

	System.out.println("Compare ...");
	Builder builder = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry);
	builder.setPostProcessorRegistry(postProcessorRegistry);
	EMFCompare umlComparator = builder.build();

	IComparisonScope2 umlScope = new DefaultComparisonScope(umlLeftResource, umlRightResource, null);
	long start = System.nanoTime();
	Comparison umlComparison = umlComparator.compare(umlScope);
	long end = System.nanoTime();
	System.out.println("Comparison Time  = " + (end - start) / 1000000000.0);
	EList<Diff> umlDiffs = umlComparison.getDifferences();

	System.out.println("Matches = " + umlComparison.getMatches().size());
	System.out.println("Diffs = " + umlDiffs.size());

	assertEquals(true, true);
    }

    @Test
    public void testEMFComparisonCBPExtension() throws IOException {

	Logger.getRootLogger().setLevel(Level.OFF);

	System.out.println("Compare using CBP extension");
	String leftPath = "D:\\TEMP\\COMPARISON3\\left.xmi";
	String rightPath = "D:\\TEMP\\COMPARISON3\\right.xmi";

	Resource cbpLeftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftPath));
	Resource cbpRightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightPath));

	System.out.println("Loading left resource ...");
	cbpLeftResource.load(null);
	// System.out.println("Loading right resource ...");
	// cbpRightResource.load(null);

	//match engine
	IMatchEngine.Factory matchEngineFactory = new CBPMatchEngineFactory();
	matchEngineFactory.setRanking(100);
	IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	matchEngineRegistry.add(matchEngineFactory);
	
	//diff engine
	CBPDiffEngine diffEngine = new CBPDiffEngine();

	System.out.println("Compare ...");
	Builder builder = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry);
	builder = builder.setDiffEngine(diffEngine);
	EMFCompare cbpComparator = builder.build();

	IComparisonScope2 umlScope = new DefaultComparisonScope(cbpLeftResource, cbpRightResource, null);
	long start = System.nanoTime();
	Comparison cbpComparison = cbpComparator.compare(umlScope);
	long end = System.nanoTime();
	System.out.println("Comparison Time  = " + (end - start) / 1000000000.0);
	EList<Diff> cbpDiffs = cbpComparison.getDifferences();

	System.out.println("Matches = " + cbpComparison.getMatches().size());
	System.out.println("Diffs = " + cbpDiffs.size());
	
	cbpLeftResource = CBPEngine.getLeftPartialResource();
	cbpRightResource = CBPEngine.getRightPartialResource();
	
	System.out.println("\nList of Diffs: CBP");
	for (Diff diff : cbpDiffs) {
	  String leftId = null;
	  String rightId = null;
	  if (diff.getMatch().getLeft() != null ) {
	      leftId = cbpLeftResource.getURIFragment(diff.getMatch().getLeft());
	  }
	  if (diff.getMatch().getRight() != null ) {
	      rightId = cbpRightResource.getURIFragment(diff.getMatch().getRight());
	  }
	  String output = leftId + " - " + rightId + " : " + diff.getKind();
	  System.out.println(output);
	}

	assertEquals(true, true);
    }

    @Test
    public void testModelGeneration() {

	try {
	    hybridOriginResource.loadFromCBP(new FileInputStream(cbpOriginFile));
	    hybridOriginResource.save(null);
	    hybridRightResource.loadFromCBP(new FileInputStream(cbpRightFile));
	    hybridRightResource.save(null);
	    hybridLeftResource.loadFromCBP(new FileInputStream(cbpLeftFile));
	    hybridLeftResource.save(null);

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (FactoryConfigurationError e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
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
