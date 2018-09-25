package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.ReferenceChange;
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
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class CBPComparisonTest {

    public CBPComparisonTest() {
	EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
	EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
	Logger.getRootLogger().setLevel(Level.OFF);
    }

    @Test
    public void testReadingFileSpeed() throws IOException, FactoryConfigurationError, XMLStreamException {

//	File originFile = new File("D:\\TEMP\\COMPARISON2\\test\\origin.cbpxml");
//	File leftFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.cbpxml");
//	File rightFile = new File("D:\\TEMP\\COMPARISON2\\test\\right.cbpxml");

	File originFile = new File("D:\\TEMP\\COMPARISON\\temp\\origin.cbpxml");
	File leftFile = new File("D:\\TEMP\\COMPARISON\\temp\\left.cbpxml");
	File rightFile = new File("D:\\TEMP\\COMPARISON\\temp\\right.cbpxml");

	// rightFile = originFile;

	CBPComparison comparison = new CBPComparison();
	comparison.compare(leftFile, rightFile, originFile);
	assertEquals(true, true);

    }

    @Test
    public void testGetDiffsEMFCompare() throws IOException {

	MoDiscoXMLPackage.eINSTANCE.eClass();
	UMLPackage.eINSTANCE.eClass();
	NodePackage.eINSTANCE.eClass();

	// File leftXmiFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON2\\test\\right.xmi");

	File leftXmiFile = new File("D:\\TEMP\\COMPARISON\\temp\\left.xmi");
	File rightXmiFile = new File("D:\\TEMP\\COMPARISON\\temp\\right.xmi");

	// File leftXmiFile = new File("D:\\TEMP\\COMPARISON3\\test\\left.xmi");
	// File rightXmiFile = new
	// File("D:\\TEMP\\COMPARISON3\\test\\right.xmi");

	XMIResource leftXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
	XMIResource rightXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));

	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	System.out.println("Start loading models ...");
	leftXmi.load(options);
	rightXmi.load(options);

	IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
	IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
	IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
	matchEngineFactory.setRanking(100);
	IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	matchEngineRegistry.add(matchEngineFactory);

	// IPostProcessor.Descriptor.Registry<String> postProcessorRegistry =
	// new PostProcessorDescriptorRegistryImpl<String>();
	// BasicPostProcessorDescriptorImpl post = new
	// BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
	// Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
	// postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

	Builder builder = EMFCompare.builder();
	// builder.setPostProcessorRegistry(postProcessorRegistry);
	builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
	EMFCompare comparator = builder.build();

	System.out.println("Create Origin CBPEAttributeEvent");
	IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, null);
	System.out.println("Start: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	Comparison comparison = comparator.compare(scope);
	System.out.println("End: " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	EList<Diff> diffs = comparison.getDifferences();
	System.out.println("Diffs: " + diffs.size());

	for (Diff diff : diffs) {
	    String feature = null;
	    String id = null;
	    String value = null;
	    if (diff instanceof AttributeChange) {
		feature = ((AttributeChange) diff).getAttribute().getName();
		value = String.valueOf(((AttributeChange) diff).getValue());
	    } else if (diff instanceof ReferenceChange) {
		feature = ((ReferenceChange) diff).getReference().getName();
		EObject eObject = ((ReferenceChange) diff).getValue();
		value = leftXmi.getURIFragment(eObject);
		if (value == null || "/-1".equals(value)) {
		    value = rightXmi.getURIFragment(eObject);
		}
	    }
	    if (diff.getMatch().getLeft() != null) {
		id = leftXmi.getURIFragment(diff.getMatch().getLeft());
	    } else {
		id = rightXmi.getURIFragment(diff.getMatch().getRight());
	    }

	    System.out.println(id + "#" + feature + "#" + value + "; " + diff.getKind());
	}

	assertEquals(true, true);
    }

    @Test
    public void testGenerateThreeDifferentCBPs() {

    }

}
