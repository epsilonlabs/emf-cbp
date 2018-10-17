package org.eclipse.epsilon.cbp.comparison.emfcompare.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.ReferenceChange;
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
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.merge.IMerger2;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPDiffEngine;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPEObjectMatcher;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPEngine;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPMatchEngineFactory;
import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPRCPMatchEngineFactory;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

	Logger.getRootLogger().setLevel(Level.OFF);

	MoDiscoXMLPackage.eINSTANCE.eClass();
	UMLPackage.eINSTANCE.eClass();
	NodePackage.eINSTANCE.eClass();

	xmiOriginResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiOriginPath));
	xmiLeftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiLeftPath));
	xmiRightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiRightPath));

	hybridOriginResource = new HybridXMIResourceImpl(xmiOriginResource, new FileOutputStream(cbpOriginFile, true));
	hybridLeftResource = new HybridXMIResourceImpl(xmiLeftResource, new FileOutputStream(cbpLeftFile, true));
	hybridRightResource = new HybridXMIResourceImpl(xmiRightResource, new FileOutputStream(cbpRightFile, true));

    }

    @Test
    public void testEMFComparisonXMIExtension() throws IOException {

	System.out.println("Compare using XMI extension");

	// String leftPath = "D:\\TEMP\\COMPARISON2\\xmi-original\\left.xmi";
	// String rightPath = "D:\\TEMP\\COMPARISON2\\xmi-original\\right.xmi";

	// String leftPath =
	// "D:\\TEMP\\COMPARISON3\\xmi-original\\wikipedia-012.xmi";
	// String rightPath =
	// "D:\\TEMP\\COMPARISON3\\xmi-original\\wikipedia-011.xmi";

	String leftPath = "D:\\TEMP\\COMPARISON2\\test\\left.xmi";
	String rightPath = "D:\\TEMP\\COMPARISON2\\test\\right.xmi";

	Resource leftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftPath));
	Resource rightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightPath));

	long start = System.nanoTime();
	System.out.println("Loading left resource ...");
	leftResource.load(null);
	System.out.println("Loading right resource ...");
	rightResource.load(null);
	long end = System.nanoTime();
	System.out.println("Loading Time  = " + (end - start) / 1000000000.0);

	countElements(leftResource, "Left");
	countElements(rightResource, "Right");

	System.out.println("Compare ...");
	EMFCompare comparator = EMFCompare.builder().build();

	IComparisonScope2 scope = new DefaultComparisonScope(leftResource, rightResource, null);
	start = System.nanoTime();
	Comparison comparison = comparator.compare(scope);
	end = System.nanoTime();
	System.out.println("Comparison Time  = " + (end - start) / 1000000000.0);
	EList<Diff> diffs = comparison.getDifferences();

	System.out.println("Matches = " + comparison.getMatches().size());
	System.out.println("Diffs = " + diffs.size());

	// System.out.println("\nList of Diffs: XMI");
	// for (Diff diff : diffs) {
	// String leftId = null;
	// String rightId = null;
	// if (diff.getMatch().getLeft() != null) {
	// leftId = leftResource.getURIFragment(diff.getMatch().getLeft());
	// }
	// if (diff.getMatch().getRight() != null) {
	// rightId = rightResource.getURIFragment(diff.getMatch().getRight());
	// }
	//
	// String name = "";
	// if (diff instanceof ReferenceChange) {
	// name = ((ReferenceChange) diff).getReference().getName();
	// } else if (diff instanceof AttributeChange) {
	// name = ((AttributeChange) diff).getAttribute().getName();
	// }
	//
	// String output = leftId + " - " + rightId + " : " + diff.getKind() + "
	// : " + name;
	// System.out.println(output);
	// }

	assertEquals(true, true);
    }

    /**
     * @param leftResource
     * @param name
     */
    protected void countElements(Resource leftResource, String name) {
	TreeIterator<EObject> iterator = leftResource.getAllContents();
	int count = 0;
	while (iterator.hasNext()) {
	    iterator.next();
	    count += 1;
	}
	System.out.println(name + " Element Count = " + count);
    }

    @Test
    public void testEMFComparisonUMLExtension() throws IOException {

	System.out.println("Compare using UML extension");
	String umlLeftPath = "D:\\TEMP\\COMPARISON2\\test\\left-no-id.xmi";
	String umlRightPath = "D:\\TEMP\\COMPARISON2\\test\\right-no-id.xmi";

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
	matchEngineFactory.setRanking(100);
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

	System.out.println("Compare using CBPEAttributeEvent extension");

	String originPath = "D:\\TEMP\\COMPARISON2\\test\\origin.xmi";
	String leftPath = "D:\\TEMP\\COMPARISON2\\test\\left.xmi";
	String rightPath = "D:\\TEMP\\COMPARISON2\\test\\right.xmi";

	// String originPath = "D:\\TEMP\\COMPARISON3\\test\\origin.xmi";
	// String leftPath = "D:\\TEMP\\COMPARISON3\\test\\left.xmi";
	// String rightPath = "D:\\TEMP\\COMPARISON3\\test\\right.xmi";

	// String originPath = "D:\\TEMP\\COMPARISON4\\test\\origin.xmi";
	// String leftPath = "D:\\TEMP\\COMPARISON4\\test\\left.xmi";
	// String rightPath = "D:\\TEMP\\COMPARISON4\\test\\right.xmi";

	Resource cbpOriginResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(originPath));
	Resource cbpLeftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftPath));
	Resource cbpRightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightPath));

	CBPEngine.setePackage(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/uml2/5.0.0/UML"));
	// CBPEngine.setePackage(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/MoDisco/Xml/0.1.incubation/XML"));

	// System.out.println("Loading left resource ...");
	// cbpLeftResource.load(null);
	// System.out.println("Loading right resource ...");
	// cbpRightResource.load(null);

	// match engine
	IMatchEngine.Factory matchEngineFactory = new CBPMatchEngineFactory();
	matchEngineFactory.setRanking(100);
	IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	matchEngineRegistry.add(matchEngineFactory);

	// diff engine
	CBPDiffEngine diffEngine = new CBPDiffEngine();

	System.out.println("Compare ...");
	Builder builder = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry);
	builder = builder.setDiffEngine(diffEngine);
	EMFCompare cbpComparator = builder.build();

	IComparisonScope2 umlScope = new DefaultComparisonScope(cbpLeftResource, cbpRightResource, cbpOriginResource);
	long start = System.nanoTime();
	Comparison cbpComparison = cbpComparator.compare(umlScope);
	long end = System.nanoTime();
	System.out.println("Comparison Time  = " + (end - start) / 1000000000.0);
	EList<Diff> cbpDiffs = cbpComparison.getDifferences();

	System.out.println("Matches = " + cbpComparison.getMatches().size());
	System.out.println("Diffs = " + cbpDiffs.size());

	cbpLeftResource = CBPEngine.getLeftPartialResource();
	cbpRightResource = CBPEngine.getRightPartialResource();

	countElements(cbpLeftResource, "Partial Left");
	countElements(cbpRightResource, "Partial Right");

	// System.out.println("\nList of Diffs: CBPEAttributeEvent");
	// for (Diff diff : cbpDiffs) {
	// String leftId = null;
	// String rightId = null;
	// if (diff.getMatch().getLeft() != null) {
	// leftId = cbpLeftResource.getURIFragment(diff.getMatch().getLeft());
	// }
	// if (diff.getMatch().getRight() != null) {
	// rightId =
	// cbpRightResource.getURIFragment(diff.getMatch().getRight());
	// }
	// String output = leftId + " - " + rightId + " : " + diff.getKind();
	// System.out.println(output);
	// }

	assertEquals(true, true);
    }

    @Test
    public void testGenerateTwoDiffrentCBPsFromThreeXMIs() {

	try {
	    Map<Object, Object> saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
	    saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

	    // String originPath = "D:\\TEMP\\COMPARISON4\\test\\origin.xmi";
	    // String leftPath = "D:\\TEMP\\COMPARISON4\\test\\left.xmi";
	    // String rightPath = "D:\\TEMP\\COMPARISON4\\test\\right.xmi";
	    // String originCbpPath =
	    // "D:\\TEMP\\COMPARISON4\\test\\origin.cbpxml";
	    // String leftCbpPath = "D:\\TEMP\\COMPARISON4\\test\\left.cbpxml";
	    // String rightCbpPath =
	    // "D:\\TEMP\\COMPARISON4\\test\\right.cbpxml";

	    // String originPath = "D:\\TEMP\\COMPARISON\\temp\\origin.xmi";
	    // String leftPath = "D:\\TEMP\\COMPARISON\\temp\\left.xmi";
	    // String rightPath = "D:\\TEMP\\COMPARISON\\temp\\right.xmi";
	    // String originCbpPath =
	    // "D:\\TEMP\\COMPARISON\\temp\\origin.cbpxml";
	    // String leftCbpPath = "D:\\TEMP\\COMPARISON\\temp\\left.cbpxml";
	    // String rightCbpPath = "D:\\TEMP\\COMPARISON\\temp\\right.cbpxml";

	    String originPath = "D:\\TEMP\\COMPARISON3\\test\\origin.xmi";
	    String leftPath = "D:\\TEMP\\COMPARISON3\\test\\left.xmi";
	    String rightPath = "D:\\TEMP\\COMPARISON3\\test\\right.xmi";
	    String originCbpPath = "D:\\TEMP\\COMPARISON3\\test\\origin.cbpxml";
	    String leftCbpPath = "D:\\TEMP\\COMPARISON3\\test\\left.cbpxml";
	    String rightCbpPath = "D:\\TEMP\\COMPARISON3\\test\\right.cbpxml";

	    // String originPath = "D:\\TEMP\\COMPARISON2\\test\\origin.xmi";
	    // String leftPath = "D:\\TEMP\\COMPARISON2\\test\\left.xmi";
	    // String rightPath = "D:\\TEMP\\COMPARISON2\\test\\right.xmi";
	    // String originCbpPath =
	    // "D:\\TEMP\\COMPARISON2\\test\\origin.cbpxml";
	    // String leftCbpPath = "D:\\TEMP\\COMPARISON2\\test\\left.cbpxml";
	    // String rightCbpPath =
	    // "D:\\TEMP\\COMPARISON2\\test\\right.cbpxml";

	    File originFile = new File(originPath);
	    File leftFile = new File(leftPath);
	    File rightFile = new File(rightPath);
	    File originCbpFile = new File(originCbpPath);
	    if (originCbpFile.exists()) {
		originCbpFile.delete();
	    }
	    File leftCbpFile = new File(leftCbpPath);
	    if (originCbpFile.exists()) {
		leftCbpFile.delete();
	    }
	    File rightCbpFile = new File(rightCbpPath);
	    if (originCbpFile.exists()) {
		rightCbpFile.delete();
	    }

	    XMIResourceFactoryImpl factory = new XMIResourceFactoryImpl();
	    XMIResource originXmi = (XMIResource) factory.createResource(URI.createFileURI(originPath));
	    XMIResource leftXmi = (XMIResource) factory.createResource(URI.createFileURI(leftPath));
	    XMIResource rightXmi = (XMIResource) factory.createResource(URI.createFileURI(rightPath));

	    CBPXMLResourceFactory cbpFactory = new CBPXMLResourceFactory();
	    CBPResource originCbp = (CBPResource) cbpFactory.createResource(URI.createFileURI(originCbpPath));
	    CBPResource leftCbp = (CBPResource) cbpFactory.createResource(URI.createFileURI(leftCbpPath));
	    CBPResource rightCbp = (CBPResource) cbpFactory.createResource(URI.createFileURI(rightCbpPath));
	    originCbp.setIdType(IdType.NUMERIC, "O-");
	    leftCbp.setIdType(IdType.NUMERIC, "L-");
	    rightCbp.setIdType(IdType.NUMERIC, "R-");

	    originXmi.load(null);

	    TreeIterator<EObject> iterator = originXmi.getAllContents();
	    int count = 0;
	    while (iterator.hasNext()) {
		iterator.next();
		count += 1;
	    }
	    System.out.println(originXmi.getURI().lastSegment() + " Element Count = " + count);

	    originCbp.startNewSession(originCbp.getURI().lastSegment());
	    originCbp.getContents().addAll(EcoreUtil.copyAll(originXmi.getContents()));
	    originCbp.save(null);

	    Files.copy(originCbpFile, leftCbpFile);
	    Files.copy(originCbpFile, rightCbpFile);

	    IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
	    IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
	    IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
	    matchEngineFactory.setRanking(100);
	    IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	    matchEngineRegistry.add(matchEngineFactory);

	    IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
	    BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(), Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
	    postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

	    Builder builder = EMFCompare.builder();
	    builder.setPostProcessorRegistry(postProcessorRegistry);
	    builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
	    EMFCompare comparator = builder.build();

	    IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
	    UMLMerger umlMerger = new UMLMerger();
	    umlMerger.setRanking(11);
	    registry.add(umlMerger);
	    IBatchMerger batchMerger = new BatchMerger(registry);

	    System.out.println("Create Origin CBPEAttributeEvent");
	    IComparisonScope2 scope = new DefaultComparisonScope(originCbp, originXmi, null);
	    System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    Comparison comparison = comparator.compare(scope);
	    System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    EList<Diff> diffs = comparison.getDifferences();
	    System.out.println("Diffs: " + diffs.size());

	    System.out.println("\nCreate Left CBPEAttributeEvent");
	    createTargetCBP(leftXmi, leftCbp, comparator, batchMerger);
	    System.out.println("\nCreate Right CBPEAttributeEvent");
	    createTargetCBP(rightXmi, rightCbp, comparator, batchMerger);

	} catch (FactoryConfigurationError | IOException e) {
	    e.printStackTrace();
	}

	assertEquals(true, true);
    }

    /**
     * @param targetXmi
     * @param targetCbp
     * @param comparator
     * @param batchMerger
     * @throws IOException
     */
    protected void createTargetCBP(XMIResource targetXmi, CBPResource targetCbp, EMFCompare comparator, IBatchMerger batchMerger) throws IOException {
	IComparisonScope2 scope;
	Comparison comparison;
	EList<Diff> diffs;
	targetXmi.load(null);

	TreeIterator<EObject> iterator = targetXmi.getAllContents();
	int count = 0;
	while (iterator.hasNext()) {
	    iterator.next();
	    count += 1;
	}
	System.out.println(targetXmi.getURI().lastSegment() + " Element Count = " + count);

	targetCbp.load(null);
	// leftCbp.getEObjectToIdMap().clear();
	scope = new DefaultComparisonScope(targetCbp, targetXmi, null);
	System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	comparison = comparator.compare(scope);
	System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	diffs = comparison.getDifferences();
	System.out.println("Diffs: " + diffs.size());

	System.out.println("\nMerge ...");
	targetCbp.startNewSession(targetCbp.getURI().lastSegment());
	while (diffs.size() > 0) {
	    System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
	    System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

	    targetCbp.save(null);
	    System.out.println("\nRe-compare again for validation ...");
	    scope = new DefaultComparisonScope(targetCbp, targetXmi, null);
	    System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    comparison = comparator.compare(scope);
	    System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    diffs = comparison.getDifferences();
	    System.out.println("Diffs: " + diffs.size());
	}
    }

    @Test
    public void testModelGeneration() throws Exception {

	try {
	    // String originXmiPath = "D:\\TEMP\\COMPARISON4\\test\\origin.xmi";
	    // String leftXmiPath = "D:\\TEMP\\COMPARISON4\\test\\left.xmi";
	    // String rightXmiPath = "D:\\TEMP\\COMPARISON4\\test\\right.xmi";
	    // String originCbpPath =
	    // "D:\\TEMP\\COMPARISON4\\test\\origin.cbpxml";
	    // String leftCbpPath = "D:\\TEMP\\COMPARISON4\\test\\left.cbpxml";
	    // String rightCbpPath =
	    // "D:\\TEMP\\COMPARISON4\\test\\right.cbpxml";

//	     String originXmiPath = "D:\\TEMP\\COMPARISON3\\test\\origin.xmi";
//	     String leftXmiPath = "D:\\TEMP\\COMPARISON3\\test\\left.xmi";
//	     String rightXmiPath = "D:\\TEMP\\COMPARISON3\\test\\right.xmi";
//	     String originCbpPath =
//	     "D:\\TEMP\\COMPARISON3\\test\\origin.cbpxml";
//	     String leftCbpPath = "D:\\TEMP\\COMPARISON3\\test\\left.cbpxml";
//	     String rightCbpPath =
//	     "D:\\TEMP\\COMPARISON3\\test\\right.cbpxml";

	    // String originXmiNoIDPath =
	    // "D:\\TEMP\\COMPARISON\\temp\\origin-noid.xmi";
	    // String leftXmiNoIDPath =
	    // "D:\\TEMP\\COMPARISON\\temp\\left-noid.xmi";
	    // String rightXmiNoIDPath =
	    // "D:\\TEMP\\COMPARISON\\temp\\right-noid.xmi";
	    // String originXmiPath = "D:\\TEMP\\COMPARISON\\temp\\origin.xmi";
	    // String leftXmiPath = "D:\\TEMP\\COMPARISON\\temp\\left.xmi";
	    // String rightXmiPath = "D:\\TEMP\\COMPARISON\\temp\\right.xmi";
	    // String originCbpPath =
	    // "D:\\TEMP\\COMPARISON\\temp\\origin.cbpxml";
	    // String leftCbpPath = "D:\\TEMP\\COMPARISON\\temp\\left.cbpxml";
	    // String rightCbpPath = "D:\\TEMP\\COMPARISON\\temp\\right.cbpxml";

	    String originXmiPath = "D:\\TEMP\\COMPARISON2\\test\\origin.xmi";
	    String leftXmiPath = "D:\\TEMP\\COMPARISON2\\test\\left.xmi";
	    String rightXmiPath = "D:\\TEMP\\COMPARISON2\\test\\right.xmi";
	    String originCbpPath = "D:\\TEMP\\COMPARISON2\\test\\origin.cbpxml";
	    String leftCbpPath = "D:\\TEMP\\COMPARISON2\\test\\left.cbpxml";
	    String rightCbpPath = "D:\\TEMP\\COMPARISON2\\test\\right.cbpxml";

	    // Resource originXmiNoID = (new
	    // XMIResourceFactoryImpl()).createResource(URI.createFileURI(originXmiNoIDPath));
	    // Resource leftXmiNoID = (new
	    // XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiNoIDPath));
	    // Resource rightXmiNoID = (new
	    // XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiNoIDPath));

	    Resource originXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(originXmiPath));
	    Resource leftXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiPath));
	    Resource rightXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiPath));

	    HybridResource originHybrid = new HybridXMIResourceImpl(originXmi, new FileOutputStream(originCbpPath, true));
	    HybridResource leftHybrid = new HybridXMIResourceImpl(leftXmi, new FileOutputStream(leftCbpPath, true));
	    HybridResource rightHybrid = new HybridXMIResourceImpl(rightXmi, new FileOutputStream(rightCbpPath, true));

	    System.out.println("Process Origin");
	    originHybrid.loadFromCBP(new FileInputStream(originCbpPath));
	    originHybrid.save(null);
	    if (!checkAllObjectsHaveIds(originHybrid)) {
		throw new Exception("Not all objects have Ids");
	    }
	    System.out.println("Process Left");
	    leftHybrid.loadFromCBP(new FileInputStream(leftCbpPath));
	    leftHybrid.save(null);
	    if (!checkAllObjectsHaveIds(leftHybrid)) {
		throw new Exception("Not all objects have Ids");
	    }
	    System.out.println("Process Right");
	    rightHybrid.loadFromCBP(new FileInputStream(rightCbpPath));
	    rightHybrid.save(null);
	    if (!checkAllObjectsHaveIds(rightHybrid)) {
		throw new Exception("Not all objects have Ids");
	    }

	    // originXmiNoID.getContents().addAll(EcoreUtil.copyAll(originXmi.getContents()));
	    // originXmiNoID.save(null);
	    // leftXmiNoID.getContents().addAll(EcoreUtil.copyAll(leftXmi.getContents()));
	    // leftXmiNoID.save(null);
	    // rightXmiNoID.getContents().addAll(EcoreUtil.copyAll(rightXmi.getContents()));
	    // rightXmiNoID.save(null);

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (FactoryConfigurationError e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	System.out.println("Finish!");
	assertEquals(true, true);
    }

    private boolean checkAllObjectsHaveIds(HybridResource hybridResource) {
	TreeIterator<EObject> iterator = hybridResource.getStateBasedResource().getAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    // String id = ((XMIResource)
	    // hybridResource.getStateBasedResource()).getID(eObject);
	    String id = hybridResource.getEObjectId(eObject);
	    if (id == null || id.equals("/-1")) {
		return false;
	    }
	}
	return true;
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
