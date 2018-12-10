package org.eclipse.epsilon.cbp.merging.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.merging.CBPMerging;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class CBPMergingTest {

    Map<Object, Object> options = new HashMap<>();

    File treeFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\tree.txt");
    File cbpDiffFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\left.txt");
    File emfcDiffFile = new File("D:\\\\TEMP\\\\FASE\\\\Debug\\right.txt");

    public CBPMergingTest() {
	EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
	Logger.getRootLogger().setLevel(Level.OFF);
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
    }

    @Test
    public void testCBPMerging() throws FactoryConfigurationError, Exception {

	// String dir = "Example";
	String dir = "Debug";

	File originFile = new File("D:\\TEMP\\FASE\\" + dir + "\\origin.cbpxml");
	File leftFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + "\\left.cbpxml");
	File rightFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + "\\right.cbpxml");
	File leftXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + "\\left.xmi");
	File rightXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + "\\right.xmi");
	File targetXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + "\\target.xmi");

	// do comparison
	ICBPComparison comparison = new CBPComparisonImpl();
	comparison.setDiffEMFCompareFile(new File(originFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
	comparison.setObjectTreeFile(new File(originFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
	comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
	List<CBPDiff> diffs = comparison.compare(leftFile, rightFile, originFile);

	// try to merge
	CBPMerging merging = new CBPMerging();
	merging.mergeAllLeftToRight(targetXmiFile, leftXmiFile, rightXmiFile, diffs);

	// evaluate merging result
	Resource targetXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));
	targetXmi.load(options);
	Resource leftXmi = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
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

	assertEquals(0, evalDiffs.size());
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
//   	Collections.sort(list);

   	// System.out.println("\nEXPORT FOR COMPARISON WITH CBP:");
   	for (String item : list) {
   	    System.out.println(item);
   	}
   	System.out.println("Merged CBP vs Left Side Diffs Size: " + list.size());
   	return list;
       }

}
