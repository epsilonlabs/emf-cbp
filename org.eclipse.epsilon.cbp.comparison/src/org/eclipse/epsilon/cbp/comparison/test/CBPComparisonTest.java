package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
import org.eclipse.emf.compare.ResourceAttachmentChange;
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
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.merging.CBPMerging;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.google.common.io.Files;

public class CBPComparisonTest {

    Map<Object, Object> options = new HashMap<>();

    File treeFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\tree.txt");
    File cbpDiffFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\left.txt");
    File emfcDiffFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\right.txt");

    public CBPComparisonTest() {
	EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
	Logger.getRootLogger().setLevel(Level.OFF);
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	options.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
    }

    @Test
    public void testGetDiffsCBP() throws IOException, FactoryConfigurationError, XMLStreamException {

	File originFile = new File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
	File leftFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\left.cbpxml");
	File rightFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\right.cbpxml");

	// File originFile = new
	// File("D:\\TEMP\\COMPARISON2\\test\\origin.cbpxml");
	// File leftFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.cbpxml");
	// File rightFile = new
	// File("D:\\TEMP\\COMPARISON2\\test\\right.cbpxml");

	// File originFile = new
	// File("D:\\TEMP\\COMPARISON3\\test\\origin.cbpxml");
	// File leftFile = new File("D:\\TEMP\\COMPARISON3\\test\\left.cbpxml");
	// File rightFile = new
	// File("D:\\TEMP\\COMPARISON3\\test\\right.cbpxml");

	// File originFile = new
	// File("D:\\TEMP\\COMPARISON\\temp\\origin.cbpxml");
	// File leftFile = new File("D:\\TEMP\\COMPARISON\\temp\\left.cbpxml");
	// File rightFile = new
	// File("D:\\TEMP\\COMPARISON\\temp\\right.cbpxml");

	// rightFile = originFile;

	ICBPComparison comparison = new CBPComparisonImpl();
	comparison.setDiffEMFCompareFile(new File(originFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
	comparison.setObjectTreeFile(new File(originFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
	comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
	comparison.compare(leftFile, rightFile, originFile);
	assertEquals(true, true);

    }

    @Test
    public void testGetDiffsEMFCompare() throws IOException {

	MoDiscoXMLPackage.eINSTANCE.eClass();
	UMLPackage.eINSTANCE.eClass();
	NodePackage.eINSTANCE.eClass();

	File leftXmiFile = new File("D:\\TEMP\\FASE\\Debug\\left.xmi");
	File rightXmiFile = new File("D:\\TEMP\\FASE\\Debug\\right.xmi");

	// File leftXmiFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON2\\test\\right.xmi");

	// File leftXmiFile = new File("D:\\TEMP\\COMPARISON\\temp\\left.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON\\temp\\right.xmi");

	// File leftXmiFile = new
	// File("D:\\TEMP\\COMPARISON\\temp\\left-noid.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON\\temp\\right-noid.xmi");

	// File leftXmiFile = new
	// File("D:\\TEMP\\COMPARISON3\\test\\left-noid.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON3\\test\\right-noid.xmi");

	// File leftXmiFile = new File("D:\\TEMP\\COMPARISON3\\test\\left.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON3\\test\\right.xmi");

	File outputRightFile = new File(leftXmiFile.getAbsolutePath().replaceAll("left.xmi", "right.txt"));

	XMIResource leftXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
	XMIResource rightXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));

	System.out.println("Start loading models ...");

	long start = System.nanoTime();
	leftXmi.load(options);
	rightXmi.load(options);
	long end = System.nanoTime();
	System.out.println("Loading time = " + ((end - start) / 1000000000.0));

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

	System.out.println("Compare");
	IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, null);
	start = System.nanoTime();
	Comparison comparison = comparator.compare(scope);
	end = System.nanoTime();
	System.out.println("Compute differences time = " + ((end - start) / 1000000000.0));
	EList<Diff> diffs = comparison.getDifferences();

	System.out.println("\nDIFFERENCES:");
	exportEMFCompareDiffs(outputRightFile, leftXmi, rightXmi, diffs);

	// System.out.println("Try to Merge ...");
	// IMerger2.Registry registry =
	// IMerger2.RegistryImpl.createStandaloneInstance();
	//// UMLMerger umlMerger = new UMLMerger();
	//// umlMerger.setRanking(11);
	//// registry.add(umlMerger);
	// IBatchMerger batchMerger = new BatchMerger(registry);
	// batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
	//
	//
	// System.out.println("Compare");
	// scope = new DefaultComparisonScope(leftXmi, rightXmi, null);
	// start = System.nanoTime();
	// comparison = comparator.compare(scope);
	// end = System.nanoTime();
	// System.out.println("Compute differences time = " + ((end - start) /
	// 1000000000.0));
	// diffs = comparison.getDifferences();
	// System.out.println("After Merge Diffs: " + diffs.size());

	assertEquals(true, true);
    }

    /**
     * @param outputFile
     * @param left
     * @param right
     * @param diffs
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<String> exportEMFCompareDiffs(File outputFile, Resource left, Resource right, EList<Diff> diffs) throws FileNotFoundException, IOException {
	Set<String> set = new HashSet<>();
	for (Diff diff : diffs) {
	    String feature = null;
	    String id = null;
	    String value = null;

	    if (diff.getMatch().getLeft() != null) {
		id = left.getURIFragment(diff.getMatch().getLeft());
	    } else {
		id = right.getURIFragment(diff.getMatch().getRight());
	    }

	    if (diff instanceof AttributeChange) {
		feature = ((AttributeChange) diff).getAttribute().getName();
		value = String.valueOf(((AttributeChange) diff).getValue());
	    } else if (diff instanceof ReferenceChange) {
		feature = ((ReferenceChange) diff).getReference().getName();
		EObject eObject = ((ReferenceChange) diff).getValue();
		value = left.getURIFragment(eObject);
		if (value == null || "/-1".equals(value)) {
		    value = right.getURIFragment(eObject);
		}
	    } else if (diff instanceof MultiplicityElementChange) {
		// MultiplicityElementChange change =
		// (MultiplicityElementChange) diff;
		// if (change.getEReference() != null) {
		// EObject eObject = change.getDiscriminant();
		// value = left.getURIFragment(eObject);
		// if (value == null || "/-1".equals(value)) {
		// value = right.getURIFragment(eObject);
		// }
		// feature = change.getEReference().getName();
		// } else {
		// continue;
		// }
		continue;
	    } else if (diff instanceof ResourceAttachmentChange) {
		feature = "resource";
		value = new String(id);
		id = new String(feature);

	    } else if (diff instanceof AssociationChange) {
		// AssociationChange change = (AssociationChange) diff;
		// if (change.getEReference() != null) {
		// EObject eObject = change.getDiscriminant();
		// value = left.getURIFragment(eObject);
		// if (value == null || "/-1".equals(value)) {
		// value = right.getURIFragment(eObject);
		// }
		// feature = change.getEReference().getName();
		// } else {
		// continue;
		// }
		continue;

	    } else if (diff instanceof DirectedRelationshipChange) {

		// DirectedRelationshipChange change =
		// (DirectedRelationshipChange) diff;
		// if (change.getEReference() != null) {
		// EObject eObject = change.getDiscriminant();
		// value = left.getURIFragment(eObject);
		// if (value == null || "/-1".equals(value)) {
		// value = right.getURIFragment(eObject);
		// }
		// feature = change.getEReference().getName();
		// } else {
		// continue;
		// }
		continue;
	    } else {
		System.out.println("UNHANDLED DIFF: " + diff.getClass().getName());
	    }

	    String x = id + "." + feature + "." + value + "." + diff.getKind();
	    set.add(x.trim());
	}
	// System.out.println("Before Merge Diffs: " + diffs.size());

	List<String> list = new ArrayList<>(set);
	Collections.sort(list);

	// System.out.println("\nEXPORT FOR COMPARISON WITH CBP:");
	FileOutputStream output = new FileOutputStream(outputFile);
	for (String item : list) {
	    output.write(item.getBytes());
	    output.write(System.lineSeparator().getBytes());
	}
	System.out.println("State-based Diffs Size: " + list.size());
	return list;
    }

    @Test
    public void testGetContentsOfFeatures() throws IOException {

	File leftFile = new File("D:\\\\TEMP\\\\FASE\\Debug\\left.xmi");
	File rightFile = new File("D:\\\\TEMP\\\\FASE\\Debug\\right.xmi");
	File targetFile = new File("D:\\\\TEMP\\\\FASE\\Debug\\target.xmi");

	ResourceSet resourceSet = new ResourceSetImpl();
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	XMIResource leftResource = (XMIResource) resourceSet.createResource(URI.createFileURI(leftFile.getAbsolutePath()));
	XMIResource rightResource = (XMIResource) resourceSet.createResource(URI.createFileURI(rightFile.getAbsolutePath()));
	XMIResource targetResource = (XMIResource) resourceSet.createResource(URI.createFileURI(targetFile.getAbsolutePath()));

	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

	leftResource.load(options);
	rightResource.load(options);
	targetResource.load(options);

	String id = "O-43655";
	String featureName = "packagedElement";

	EObject leftObject = leftResource.getEObject(id);
	EReference leftReference = (EReference) leftObject.eClass().getEStructuralFeature(featureName);
	EList<EObject> leftValues = (EList<EObject>) leftObject.eGet(leftReference);

	EObject rightObject = rightResource.getEObject(id);
	EReference rightReference = (EReference) rightObject.eClass().getEStructuralFeature(featureName);
	EList<EObject> rightValues = (EList<EObject>) rightObject.eGet(rightReference);

	EObject targetObject = targetResource.getEObject(id);
	EReference targetReference = (EReference) targetObject.eClass().getEStructuralFeature(featureName);
	EList<EObject> targetValues = (EList<EObject>) targetObject.eGet(targetReference);

	int size = (leftValues.size() > rightValues.size()) ? leftValues.size() : rightValues.size();
	// int size = (leftValues.size() > targetValues.size()) ?
	// leftValues.size() : targetValues.size();

	System.out.println("\nLEFT:");
	for (int i = 0; i < leftValues.size(); i++) {
	    String leftId = "";
	    EObject leftValue = leftValues.get(i);
	    leftId = leftResource.getURIFragment(leftValue);
	    System.out.println(leftId);

	}

	System.out.println("\nRIGHT:");
	for (int i = 0; i < rightValues.size(); i++) {
	    String rightId = "";
	    EObject rightValue = rightValues.get(i);
	    rightId = rightResource.getURIFragment(rightValue);
	    System.out.println(rightId);
	}

	System.out.println("\nTARGET:");
	for (int i = 0; i < targetValues.size(); i++) {
	    String targetId = "";
	    EObject targetValue = targetValues.get(i);
	    targetId = targetResource.getURIFragment(targetValue);
	    System.out.println(targetId);
	}

	System.out.println("\nPAIR LEFT-RIGHT:");
	for (int i = 0; i < size; i++) {
	    String leftId = "";
	    String rightId = "";

	    if (i < leftValues.size()) {
		EObject leftValue = leftValues.get(i);
		leftId = leftResource.getURIFragment(leftValue);
	    }

	    if (i < rightValues.size()) {
		EObject rightValue = rightValues.get(i);
		rightId = rightResource.getURIFragment(rightValue);
	    }

	    System.out.println(i + ": " + leftId + " - " + rightId);

	}

	System.out.println("\nPAIR LEFT-TARGET:");
	for (int i = 0; i < size; i++) {
	    String leftId = "";
	    String rightId = "";

	    if (i < leftValues.size()) {
		EObject leftValue = leftValues.get(i);
		leftId = leftResource.getURIFragment(leftValue);
	    }

	    if (i < targetValues.size()) {
		EObject rightValue = targetValues.get(i);
		rightId = targetResource.getURIFragment(rightValue);
	    }

	    System.out.println(i + ": " + leftId + " - " + rightId);

	}
    }

    @Test
    public void testModelComparisonPerformance() throws FactoryConfigurationError, Exception {

	File debugDir = new File("D:\\TEMP\\FASE\\Debug\\");

	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

	final int REPEAT_UNTIL_MODEL = 68;
	String caseName = "Epsilon";
	// String caseName = "Wikipedia";

	File originDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\origin");
	File sourceDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\source");
	File leftDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\left");
	File rightDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\right");
	File leftIdDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\left-id");
	File rightIdDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\right-id");
	File originIdDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\origin-id");
	File cbpDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\cbp");

	File outputFile = new File("D:\\TEMP\\FASE\\" + caseName + "\\output.csv");
	FileOutputStream output = new FileOutputStream(outputFile);
	appendHeader(output);

	File[] files = sourceDir.listFiles();

	if (files.length < 3) {
	    System.out.println("There should be at least 3 files !");
	    return;
	}

	FileUtils.cleanDirectory(cbpDir);
	FileUtils.cleanDirectory(originDir);
	// FileUtils.cleanDirectory(leftDir);
	// FileUtils.cleanDirectory(rightDir);
	FileUtils.cleanDirectory(originIdDir);
	FileUtils.cleanDirectory(leftIdDir);
	FileUtils.cleanDirectory(rightIdDir);

	int originPos = 0;
	File originFile = files[originPos];
	int leftPos = 2;
	int rightPos = 1;

	FileUtils.copyFileToDirectory(originFile, originDir);
	originFile = originDir.listFiles()[0];

	// for (; leftPos < files.length; leftPos++) {
	// rightPos = leftPos - 1;
	// File leftFile = files[leftPos];
	// File rightFile = files[rightPos];
	//
	// if (originFile.length() == leftFile.length() || originFile.length()
	// == rightFile.length() || leftFile.length() == rightFile.length()) {
	// continue;
	// }
	//
	// FileUtils.copyFileToDirectory(leftFile, leftDir);
	// FileUtils.copyFileToDirectory(rightFile, rightDir);
	// }
	//
	File[] leftFiles = leftDir.listFiles();
	File[] rightFiles = rightDir.listFiles();

	XMIResourceFactoryImpl factory = new XMIResourceFactoryImpl();
	XMIResource originXmi = (XMIResource) factory.createResource(URI.createFileURI(originFile.getAbsolutePath()));

	CBPXMLResourceFactory cbpFactory = new CBPXMLResourceFactory();
	File originCbpFile = new File(cbpDir.getAbsolutePath() + File.separator + "origin.cbpxml");
	CBPXMLResourceImpl originCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(originCbpFile.getAbsolutePath()));

	originCbp.setIdType(IdType.NUMERIC, "O-");

	System.out.print("Loading " + originFile.getName() + "...");
	originXmi.load(options);

	int count = countElements(originXmi);
	System.out.println(" Element Count = " + count);

	originCbp.startNewSession(originFile.getName());
	originCbp.getContents().addAll(EcoreUtil.copyAll(originXmi.getContents()));
	originCbp.save(null);

	File originWithIdFile = new File(originFile.getAbsolutePath().replace("origin", "origin-id"));
	saveXmiWithID(originCbp, originWithIdFile);

	doEMFComparisonAndMerging(originCbp, originXmi);

	File leftCbpFile = new File(leftDir.getAbsolutePath() + File.separator + ".." + File.separator + "cbp" + File.separator + "left.cbpxml");
	Files.copy(originCbpFile, leftCbpFile);
	File leftCbpZero = new File(leftCbpFile.getAbsolutePath().replaceAll(".cbpxml", ".000.cbpxml"));
	Files.copy(originCbpFile, leftCbpZero);

	File rightCbpFile = new File(rightDir.getAbsolutePath() + File.separator + ".." + File.separator + "cbp" + File.separator + "right.cbpxml");
	Files.copy(originCbpFile, rightCbpFile);
	File rightCbpZero = new File(rightCbpFile.getAbsolutePath().replaceAll(".cbpxml", ".000.cbpxml"));
	Files.copy(originCbpFile, rightCbpZero);

	CBPXMLResourceImpl leftCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
	CBPXMLResourceImpl rightCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
	leftCbp.setIdType(IdType.NUMERIC, "L-");
	rightCbp.setIdType(IdType.NUMERIC, "R-");
	System.out.println("Loading " + leftCbpFile.getName() + "...");
	leftCbp.load(null);
	System.out.println("Loading " + rightCbpFile.getName() + "...");
	rightCbp.load(null);

	Result rootResult = new Result();
	rootResult.setLeftFileName(leftCbpZero.getName().replaceAll(".cbpxml", ""));
	rootResult.setRightFileName(rightCbpZero.getName().replaceAll(".cbpxml", ""));
	rootResult.setLeftElementCount(countElements(originCbp));
	rootResult.setRightElementCount(countElements(originXmi));
	rootResult.setLeftEventCount(countLines(leftCbpZero));
	rootResult.setRightEventCount(countLines(rightCbpZero));
	doEMFComparison(originWithIdFile, originWithIdFile, rootResult);
	doCBPComparison(leftCbpZero, rightCbpZero, originCbpFile, rootResult);
	appendResult(output, rootResult);

	int limit = leftFiles.length;
	for (int i = 0; i < limit; i++) {

	    if (i == REPEAT_UNTIL_MODEL) {
		return;
	    }

	    File leftXmiFile = leftFiles[i];
	    File rightXmiFile = rightFiles[i];

	    System.out.println("\n" + (i + 1) + ". Processing " + leftXmiFile.getName() + " and " + rightXmiFile.getName());

	    XMIResource leftXmi = (XMIResource) factory.createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
	    XMIResource rightXmi = (XMIResource) factory.createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));

	    System.out.print("Loading " + leftXmiFile.getName() + "...");
	    leftXmi.load(options);
	    count = countElements(originXmi);
	    System.out.println(" = " + count);

	    System.out.print("loading " + rightXmiFile.getName() + "...");
	    rightXmi.load(options);
	    count = countElements(rightXmi);
	    System.out.println(" = " + count);

	    System.out.println("\nCompare and Merge Left CBP " + leftXmiFile.getName());
	    leftCbp.startNewSession(leftXmiFile.getName());
	    doEMFComparisonAndMerging(leftCbp, leftXmi);
	    leftCbp.save(null);

	    System.out.println("\nCompare and Merge Right CBP and " + rightXmiFile.getName());
	    rightCbp.startNewSession(rightXmiFile.getName());
	    doEMFComparisonAndMerging(rightCbp, rightXmi);
	    rightCbp.save(null);

	    String index = String.valueOf((1000 + i + 1)).substring(1, 4);
	    File leftCbpWithIndexFile = new File(leftCbpFile.getAbsolutePath().replaceAll(".cbpxml", "." + index + ".cbpxml"));
	    Files.copy(leftCbpFile, leftCbpWithIndexFile);
	    File rightCbpWithIndexFile = new File(rightCbpFile.getAbsolutePath().replaceAll(".cbpxml", "." + index + ".cbpxml"));
	    Files.copy(rightCbpFile, rightCbpWithIndexFile);

	    System.out.println("Saving xmi with id");
	    File leftXmiWithIdFile = new File(leftXmiFile.getAbsolutePath().replace("left", "left-id"));
	    saveXmiWithID(leftCbp, leftXmiWithIdFile);
	    File rightXmiWithIdFile = new File(rightXmiFile.getAbsolutePath().replace("right", "right-id"));
	    saveXmiWithID(rightCbp, rightXmiWithIdFile);

	    Result result = new Result();
	    result.setLeftFileName(leftCbpWithIndexFile.getName().replaceAll(".cbpxml", ""));
	    result.setRightFileName(rightCbpWithIndexFile.getName().replaceAll(".cbpxml", ""));
	    result.setLeftElementCount(countElements(leftXmi));
	    result.setRightElementCount(countElements(rightXmi));
	    result.setLeftEventCount(countLines(leftCbpWithIndexFile));
	    result.setRightEventCount(countLines(rightCbpWithIndexFile));
	    try {
		
		boolean isDeleted = false;
		while (!isDeleted) {
		    try {
			FileUtils.cleanDirectory(debugDir);
			isDeleted = true;
			System.out.println("Deletion OK");
		    } catch (Exception e) {
			System.out.println("Deletion FAIL");
		    }
		}

		File debugOriginCbpFile = new File(debugDir.getAbsolutePath() + File.separator + "origin.cbpxml");
		File debugLeftCbpFile = new File(debugDir.getAbsolutePath() + File.separator + "left.cbpxml");
		File debugRightFile = new File(debugDir.getAbsolutePath() + File.separator + "right.cbpxml");
		File debugOriginXmiFile = new File(debugDir.getAbsolutePath() + File.separator + "origin.xmi");
		File debugLeftXmiFile = new File(debugDir.getAbsolutePath() + File.separator + "left.xmi");
		File debugRightXmiFile = new File(debugDir.getAbsolutePath() + File.separator + "right.xmi");
		File debugTargetXmiFile = new File(debugDir.getAbsolutePath() + File.separator + "target.xmi");

		FileUtils.copyFile(originWithIdFile, debugOriginXmiFile);
		FileUtils.copyFile(leftXmiWithIdFile, debugLeftXmiFile);
		FileUtils.copyFile(rightXmiWithIdFile, debugRightXmiFile);
		FileUtils.copyFile(originCbpFile, debugOriginCbpFile);
		FileUtils.copyFile(leftCbpWithIndexFile, debugLeftCbpFile);
		FileUtils.copyFile(rightCbpWithIndexFile, debugRightFile);

		File destDir = new File("D:\\TEMP\\FASE\\" + caseName + "\\data\\" + (i + 1));
		FileUtils.copyDirectory(debugDir, destDir);

		doEMFComparison(leftXmiWithIdFile, rightXmiWithIdFile, result);
		doCBPComparison(leftCbpWithIndexFile, rightCbpWithIndexFile, originCbpFile, result);

		int deltaCount = Math.abs(result.getChangeBasedDiffCount() - result.getStateBasedDiffCount());
		// int threshold = (int) (0.10 * result.getStateBasedDiffCount()
		// / 1.0);
		if (deltaCount > 0) {
		    System.out.println("ERROR!: " + result.getChangeBasedDiffCount() + " vs. " + result.getStateBasedDiffCount());

		} else {
		    System.out.println("Final Diffs = " + deltaCount);
		}

		// // do comparison
		// ICBPComparison comparison = new CBPComparisonImpl();
		// comparison.setDiffEMFCompareFile(new
		// File(originFile.getAbsolutePath().replaceAll("origin.cbpxml",
		// "left.txt")));
		// comparison.setObjectTreeFile(new
		// File(originFile.getAbsolutePath().replaceAll("origin.cbpxml",
		// "tree.txt")));
		// comparison.addObjectTreePostProcessor(new
		// UMLObjectTreePostProcessor());
		// List<CBPDiff> diffs = comparison.compare(debugLeftCbpFile,
		// debugRightFile, debugOriginCbpFile);
		//
		// // try to merge
		// CBPMerging merging = new CBPMerging();
		// merging.mergeAllLeftToRight(debugTargetXmiFile,
		// debugLeftXmiFile, debugRightXmiFile, diffs);
		// int returnVal = evaluateCBPMergingResult(debugTargetXmiFile,
		// debugLeftXmiFile);
		// FileUtils.copyFile(debugTargetXmiFile, new
		// File(destDir.getAbsolutePath() + File.separator +
		// "target.xmi"));
		// if (returnVal > 0) {
		// throw new Exception("Case " + (i + 1) + " - CBP Merging still
		// has differences: diff size = " + returnVal);
		// }

		appendResult(output, result);
	    } catch (Exception ex) {
		ex.printStackTrace();
		throw new Exception(ex.getMessage());
	    }

	    leftXmi.unload();
	    rightXmi.unload();
	}

	output.close();
    }

    public void appendHeader(FileOutputStream output) throws IOException {
	output.write("Left Name".getBytes());
	output.write(",".getBytes());
	output.write("Right Name".getBytes());
	output.write(",".getBytes());
	output.write("Left Element Count".getBytes());
	output.write(",".getBytes());
	output.write("Right Element Count".getBytes());
	output.write(",".getBytes());
	output.write("Left Event Count".getBytes());
	output.write(",".getBytes());
	output.write("Right Event Count".getBytes());
	output.write(",".getBytes());
	output.write("XMI Diff Count".getBytes());
	output.write(",".getBytes());
	output.write("CBP Diff Count".getBytes());
	output.write(",".getBytes());
	output.write("XMI Load Time".getBytes());
	output.write(",".getBytes());
	output.write("CBP Load Time".getBytes());
	output.write(",".getBytes());
	output.write("XMI Comparison Time".getBytes());
	output.write(",".getBytes());
	output.write("CBP Comparison Time".getBytes());
	output.write(System.lineSeparator().getBytes());
	output.flush();
    }

    public void appendResult(FileOutputStream output, Result result) throws IOException {
	output.write(result.getLeftFileName().getBytes());
	output.write(",".getBytes());
	output.write(result.getRightFileName().getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getLeftElementCount()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getRightElementCount()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getLeftEventCount()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getRightEventCount()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getStateBasedDiffCount()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getChangeBasedDiffCount()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getStateBasedLoadTime()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getChangeBasedLoadTime()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getStateBasedComparisonTime()).getBytes());
	output.write(",".getBytes());
	output.write(String.valueOf(result.getChangeBasedComparisonTime()).getBytes());
	output.write(System.lineSeparator().getBytes());
	output.flush();
    }

    /**
     * @param leftCbpFile
     * @throws FileNotFoundException
     * @throws IOException
     */
    private int countLines(File leftCbpFile) throws FileNotFoundException, IOException {
	BufferedReader reader = new BufferedReader(new FileReader(leftCbpFile));
	int lines = 0;
	while (reader.readLine() != null)
	    lines++;
	reader.close();
	return lines;
    }

    /**
     * @param cbp
     * @param xmiFile
     * @throws IOException
     */
    private void saveXmiWithID(CBPXMLResourceImpl cbp, File xmiFile) throws IOException {
	XMIResource xmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
	xmi.getContents().addAll(EcoreUtil.copyAll(cbp.getContents()));
	TreeIterator<EObject> cbpIterator = cbp.getAllContents();
	TreeIterator<EObject> xmiIterator = xmi.getAllContents();

	while (cbpIterator.hasNext() && xmiIterator.hasNext()) {
	    EObject cbpEObject = cbpIterator.next();
	    String id = cbp.getURIFragment(cbpEObject);
	    EObject xmiEObject = xmiIterator.next();
	    xmi.setID(xmiEObject, id);
	}
	xmi.save(null);
	xmi.unload();
    }

    private void doCBPComparison(File leftFile, File rightFile, File originFile, Result result) throws IOException, FactoryConfigurationError, XMLStreamException {
	ICBPComparison comparison = new CBPComparisonImpl();
	comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
	comparison.setObjectTreeFile(treeFile);
	comparison.setDiffEMFCompareFile(cbpDiffFile);
	comparison.compare(leftFile, rightFile, originFile);
	result.setChangeBasedComparisonTime(comparison.getComparisonTime());
	result.setChangeBasedDiffCount(comparison.getDiffCount());
	result.setChangeBasedLoadTime(comparison.getLoadTime());
    }

    private void doEMFComparison(File leftFile, File rightFile, Result result) throws IOException {
	Resource leftResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftFile.getAbsolutePath()));
	Resource rightResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightFile.getAbsolutePath()));
	long start = System.nanoTime();
	leftResource.load(options);
	rightResource.load(options);
	long end = System.nanoTime();
	result.setStateBasedLoadTime(end - start);
	doEMFComparison(leftResource, rightResource, result, emfcDiffFile);
	leftResource.unload();
	rightResource.unload();

    }

    /**
     * @param right
     * @param left
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void doEMFComparison(Resource left, Resource right, Result result, File outputFile) throws FileNotFoundException, IOException {
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

	// System.out.println("Compare " + cbp.getURI().lastSegment() + " and "
	// + xmi.getURI().lastSegment());
	IComparisonScope2 scope = new DefaultComparisonScope(left, right, null);
	long start = System.nanoTime();
	// System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd
	// HH:mm:ss")).format(new Date()));
	Comparison comparison = comparator.compare(scope);
	long end = System.nanoTime();
	// System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd
	// HH:mm:ss")).format(new Date()));
	EList<Diff> diffs = comparison.getDifferences();

	System.out.println("Comparison Time = " + (end - start) / 1000000.0 + " ms");
	System.out.println("State-based Diffs Size = " + diffs.size());

	int exportedDiffCount = exportEMFCompareDiffs(outputFile, left, right, diffs).size();

	result.setStateBasedDiffCount(exportedDiffCount);
	result.setStateBasedComparisonTime(end - start);
    }

    private void doEMFComparisonAndMerging(CBPResource cbp, XMIResource xmi) {
	doEMFComparisonAndMerging(cbp, xmi, null);
    }

    /**
     * @param xmi
     * @param cbp
     */
    private void doEMFComparisonAndMerging(CBPResource cbp, XMIResource xmi, Result result) {
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

	System.out.println("Compare " + cbp.getURI().lastSegment() + " and " + xmi.getURI().lastSegment());
	IComparisonScope2 scope = new DefaultComparisonScope(cbp, xmi, null);
	System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	long start = System.nanoTime();
	Comparison comparison = comparator.compare(scope);
	long end = System.nanoTime();
	System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	EList<Diff> diffs = comparison.getDifferences();
	System.out.println("Diffs: " + diffs.size());

	if (result != null) {
	    result.setStateBasedDiffCount(diffs.size());
	    result.setStateBasedComparisonTime(end - start);
	}

	while (diffs.size() > 0) {

	    System.out.println("Merging ...");
	    System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
	    System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

	    System.out.println("RE-Compare " + cbp.getURI().lastSegment() + " and " + xmi.getURI().lastSegment());
	    scope = new DefaultComparisonScope(cbp, xmi, null);
	    System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    start = System.nanoTime();
	    comparison = comparator.compare(scope);
	    end = System.nanoTime();
	    System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	    diffs = comparison.getDifferences();
	    System.out.println("Diffs: " + diffs.size());

	    if (result != null) {
		result.setStateBasedDiffCount(result.getStateBasedDiffCount() + diffs.size());
		result.setStateBasedComparisonTime(result.getStateBasedComparisonTime() + end - start);
	    }
	}
    }

    /**
     * @param resource
     * @return
     */
    private int countElements(Resource resource) {
	TreeIterator<EObject> iterator = resource.getAllContents();
	int count = 0;
	while (iterator.hasNext()) {
	    iterator.next();
	    count += 1;
	}
	return count;
    }

    @Test
    public void simpleTest() throws IOException {
	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

	File xmiFile = new File("D:\\TEMP\\FASE\\Epsilon\\origin\\" + "BPMN2-0000025-1cda815932a8b0a1c75b36c817903731305ae694.xmi");
	XMIResource xmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
	xmi.load(options);

	File cbpFile = new File(xmiFile.getAbsolutePath().replaceAll(".xmi", ".cbpxml"));
	CBPXMLResourceImpl cbp = (CBPXMLResourceImpl) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
	cbp.getContents().addAll(EcoreUtil.copyAll(xmi.getContents()));
	cbp.save(null);

	doEMFComparisonAndMerging(cbp, xmi);
    }

    @Test
    public void checkElementsPositions() throws IOException {

	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

	File originResourceFile = new File("D:\\TEMP\\FASE\\Debug\\origin.xmi");
	File leftResourceFile = new File("D:\\TEMP\\FASE\\Debug\\left.xmi");
	File rightResourceFile = new File("D:\\TEMP\\FASE\\Debug\\right.xmi");
	File targetResourceFile = new File("D:\\TEMP\\FASE\\Debug\\target.xmi");

	ResourceSet resourceSet = new ResourceSetImpl();
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	Resource originResource = resourceSet.createResource(URI.createFileURI(originResourceFile.getAbsolutePath()));
	Resource leftResource = resourceSet.createResource(URI.createFileURI(leftResourceFile.getAbsolutePath()));
	Resource rightResource = resourceSet.createResource(URI.createFileURI(rightResourceFile.getAbsolutePath()));
	Resource targetResource = resourceSet.createResource(URI.createFileURI(targetResourceFile.getAbsolutePath()));

	originResource.load(options);
	leftResource.load(options);
	rightResource.load(options);
	targetResource.load(options);

	{
	    String a = "O-52341";
	    EObject eObject = originResource.getEObject(a);
	    if (eObject != null) {
		EObject eContainer = eObject.eContainer();
		String containerId = originResource.getURIFragment(eContainer);
		EStructuralFeature eFeature = eObject.eContainingFeature();
		boolean isContainment = ((EReference) eFeature).isContainment();
		boolean isOrdered = eFeature.isOrdered();
		int pos = 0;
		if (eObject.eContainer().eGet(eFeature) instanceof EList<?>) {
		    EList<EObject> eList = (EList<EObject>) eObject.eContainer().eGet(eFeature);
		    pos = eList.indexOf(eObject);
		}
		System.out.println(a + " Origin Pos = " + containerId + "." + eFeature.getName() + "." + pos);
	    }
	}

	{
	    String a = "O-52341";
	    EObject eObject = rightResource.getEObject(a);
	    if (eObject != null) {
		EObject eContainer = eObject.eContainer();
		String containerId = rightResource.getURIFragment(eContainer);
		EStructuralFeature eFeature = eObject.eContainingFeature();
		boolean isContainment = ((EReference) eFeature).isContainment();
		boolean isOrdered = eFeature.isOrdered();
		int pos = 0;
		if (eObject.eContainer().eGet(eFeature) instanceof EList<?>) {
		    EList<EObject> eList = (EList<EObject>) eObject.eContainer().eGet(eFeature);
		    pos = eList.indexOf(eObject);
		}
		System.out.println(a + " Right Pos = " + containerId + "." + eFeature.getName() + "." + pos);
	    }
	}

	{
	    String a = "O-52341";
	    EObject eObject = leftResource.getEObject(a);
	    if (eObject != null) {
		EObject eContainer = eObject.eContainer();
		String containerId = leftResource.getURIFragment(eContainer);
		EStructuralFeature eFeature = eObject.eContainingFeature();
		boolean isContainment = ((EReference) eFeature).isContainment();
		boolean isOrdered = eFeature.isOrdered();
		int pos = 0;
		if (eObject.eContainer().eGet(eFeature) instanceof EList<?>) {
		    EList<EObject> eList = (EList<EObject>) eObject.eContainer().eGet(eFeature);
		    pos = eList.indexOf(eObject);
		}
		System.out.println(a + " Left Pos = " + containerId + "." + eFeature.getName() + "." + pos);
	    }
	}

	{
	    String a = "O-52341";
	    EObject eObject = targetResource.getEObject(a);
	    if (eObject != null) {
		EObject eContainer = eObject.eContainer();
		String containerId = targetResource.getURIFragment(eContainer);
		EStructuralFeature eFeature = eObject.eContainingFeature();
		boolean isContainment = ((EReference) eFeature).isContainment();
		boolean isOrdered = eFeature.isOrdered();
		int pos = 0;
		if (eObject.eContainer().eGet(eFeature) instanceof EList<?>) {
		    EList<EObject> eList = (EList<EObject>) eObject.eContainer().eGet(eFeature);
		    pos = eList.indexOf(eObject);
		}
		System.out.println(a + " Target Pos = " + containerId + "." + eFeature.getName() + "." + pos);
	    }
	    System.out.println();
	}
    }

    @Test
    public void checEquality() throws IOException {

	ResourceSet resourceSet = new ResourceSetImpl();
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml", new CBPXMLResourceFactory());

	System.out.println("Start loading resources ...");
	String cbpPath = "D:\\TEMP\\FASE\\Debug\\left.cbpxml";
	String xmiPath = "D:\\TEMP\\FASE\\Debug\\left.xmi";

	CBPResource cbp = (CBPResource) resourceSet.createResource(URI.createFileURI(cbpPath));
	XMIResource xmi = (XMIResource) resourceSet.createResource(URI.createFileURI(xmiPath));

	cbp.load(null);
	xmi.load(options);
	TreeIterator<EObject> iterator = xmi.getAllContents();
	while (iterator.hasNext()) {
	    xmi.setID(iterator.next(), null);
	}

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

	System.out.println("Compare " + cbp.getURI().lastSegment() + " and " + xmi.getURI().lastSegment());
	IComparisonScope2 scope = new DefaultComparisonScope(cbp, xmi, null);
	System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	long start = System.nanoTime();
	Comparison comparison = comparator.compare(scope);
	long end = System.nanoTime();
	System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	EList<Diff> diffs = comparison.getDifferences();
	System.out.println("Diffs: " + diffs.size());
    }

    private int evaluateCBPMergingResult(File debugTargetXmiFile, File debugLeftXmiFile) throws FileNotFoundException, IOException {

	Resource targetXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(debugTargetXmiFile.getAbsolutePath()));
	targetXmi.load(options);
	Resource leftXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(debugLeftXmiFile.getAbsolutePath()));
	leftXmi.load(options);

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

	System.out.println("Compare");
	IComparisonScope2 scope = new DefaultComparisonScope(targetXmi, leftXmi, null);
	long start = System.nanoTime();
	Comparison emfComparison = comparator.compare(scope);
	long end = System.nanoTime();
	System.out.println("Compute differences time = " + ((end - start) / 1000000000.0));
	EList<Diff> evalDiffs = emfComparison.getDifferences();
	System.out.println("Eval Diffs = " + evalDiffs.size());
	printEMFCompareDiffs(targetXmi, leftXmi, evalDiffs);

	return evalDiffs.size();
    }

    private List<String> printEMFCompareDiffs(Resource left, Resource right, EList<Diff> diffs) throws FileNotFoundException, IOException {
	Set<String> set = new HashSet<>();
	for (Diff diff : diffs) {
	    String feature = null;
	    String id = null;
	    String value = null;

	    if (diff.getMatch().getLeft() != null) {
		id = left.getURIFragment(diff.getMatch().getLeft());
	    } else {
		id = right.getURIFragment(diff.getMatch().getRight());
	    }

	    if (diff instanceof AttributeChange) {
		feature = ((AttributeChange) diff).getAttribute().getName();
		value = String.valueOf(((AttributeChange) diff).getValue());
	    } else if (diff instanceof ReferenceChange) {
		feature = ((ReferenceChange) diff).getReference().getName();
		EObject eObject = ((ReferenceChange) diff).getValue();
		value = left.getURIFragment(eObject);
		if (value == null || "/-1".equals(value)) {
		    value = right.getURIFragment(eObject);
		}
	    } else if (diff instanceof MultiplicityElementChange) {
		continue;
	    } else if (diff instanceof ResourceAttachmentChange) {
		feature = "resource";
		value = new String(id);
		id = new String(feature);
	    } else if (diff instanceof AssociationChange) {
		continue;
	    } else if (diff instanceof DirectedRelationshipChange) {
		continue;
	    } else {
		System.out.println("UNHANDLED DIFF: " + diff.getClass().getName());
	    }

	    String x = id + "." + feature + "." + value + "." + diff.getKind();
	    set.add(x.trim());
	}
	// System.out.println("Before Merge Diffs: " + diffs.size());

	List<String> list = new ArrayList<>(set);
	// Collections.sort(list);

	// System.out.println("\nEXPORT FOR COMPARISON WITH CBP:");
	for (String item : list) {
	    System.out.println(item);
	}
	System.out.println("All Right to Left Merged CBP Diffs Size: " + list.size());
	return list;
    }
}
