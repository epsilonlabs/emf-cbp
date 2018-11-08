package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
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
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResource.IdType;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class FASE2019Test {

    private EPackage ePackage = (EPackage) UMLPackage.eINSTANCE;
    // private EPackage ePackage = (EPackage) NodePackage.eINSTANCE;

    private File originXmiFile = new File("D:\\TEMP\\FASE\\Example\\origin.xmi");
    private File originCbpFile = new File("D:\\TEMP\\FASE\\Example\\origin.cbpxml");
    private File leftXmiFile = new File("D:\\TEMP\\FASE\\Example\\left.xmi");
    private File leftCbpFile = new File("D:\\TEMP\\FASE\\Example\\left.cbpxml");
    private File rightXmiFile = new File("D:\\TEMP\\FASE\\Example\\right.xmi");
    private File rightCbpFile = new File("D:\\TEMP\\FASE\\Example\\right.cbpxml");
    private File cbpDiffFile = new File("D:\\TEMP\\FASE\\Example\\left.txt");
    private File emfCompareDiffFile = new File("D:\\TEMP\\FASE\\Example\\right.txt");
    private File treeFile = new File("D:\\TEMP\\FASE\\Example\\tree.txt");

    @Test
    public void testCBPComparison() throws IOException, FactoryConfigurationError, XMLStreamException {
	ICBPComparison comparison = new CBPComparisonImpl();
	comparison.setDiffEMFCompareFile(cbpDiffFile);
	comparison.setObjectTreeFile(treeFile);
	comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
	comparison.compare(leftCbpFile, rightCbpFile, originCbpFile);
	assertEquals(true, true);
    }

    @Test
    public void testEMFCompareComparison() throws IOException {

	XMIResource leftXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
	XMIResource rightXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));

	System.out.println("Start loading models ...");

	long start = System.nanoTime();
	leftXmi.load(null);
	rightXmi.load(null);
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
	exportEMFCompareDiffs(emfCompareDiffFile, leftXmi, rightXmi, diffs);

	assertEquals(true, true);
    }

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
  	    } 
  	    else if (diff instanceof MultiplicityElementChange) {
//  		MultiplicityElementChange change = (MultiplicityElementChange) diff;
//  		if (change.getEReference() != null) {
//  		    EObject eObject = change.getDiscriminant();
//  		    value = left.getURIFragment(eObject);
//  		    if (value == null || "/-1".equals(value)) {
//  			value = right.getURIFragment(eObject);
//  		    }
//  		    feature = change.getEReference().getName();
//  		} else {
//  		    continue;
//  		}
  		continue;
  	    } 
  	    else if (diff instanceof ResourceAttachmentChange) {
  		feature = "resource";
  		value = new String(id);
  		id = new String(feature);

  	    } else if (diff instanceof AssociationChange) {
//  		AssociationChange change = (AssociationChange) diff;
//  		if (change.getEReference() != null) {
//  		    EObject eObject = change.getDiscriminant();
//  		    value = left.getURIFragment(eObject);
//  		    if (value == null || "/-1".equals(value)) {
//  			value = right.getURIFragment(eObject);
//  		    }
//  		    feature = change.getEReference().getName();
//  		} else {
//  		    continue;
//  		}
  		continue;

  	    } 
  	    else if (diff instanceof DirectedRelationshipChange) {
  		
//  		DirectedRelationshipChange change = (DirectedRelationshipChange) diff;
//  		if (change.getEReference() != null) {
//  		    EObject eObject = change.getDiscriminant();
//  		    value = left.getURIFragment(eObject);
//  		    if (value == null || "/-1".equals(value)) {
//  			value = right.getURIFragment(eObject);
//  		    }
//  		    feature = change.getEReference().getName();
//  		} else {
//  		    continue;
//  		}
  		continue;
  	    } 
  	    else {
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
  	System.out.println("Diffs: " + list.size());
  	return list;
      }

    @Test
    public void testExample() throws Exception {

	originXmiFile.delete();
	originCbpFile.delete();
	leftXmiFile.delete();
	leftCbpFile.delete();
	rightXmiFile.delete();
	rightCbpFile.delete();

	// ORIGIN
	XMIResource originXmiResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(originXmiFile.getAbsolutePath()));
	HybridResource originResource = new HybridXMIResourceImpl(originXmiResource, new FileOutputStream(originCbpFile, true));
	originResource.setIdType(IdType.NUMERIC, "O-");
	String originSessionName = "ORIGIN";

	StringBuilder originScript = new StringBuilder();
	originScript.append("var c1 = new Class;");
	originScript.append("c1.name = \"Class 1\";");
	originScript.append("var o1 = new Operation;");
	originScript.append("o1.name = \"Operation A\";");
	originScript.append("var o2 = new Operation;");
	originScript.append("o2.name = \"Operation B\";");
	originScript.append("var o3 = new Operation;");
	originScript.append("o3.name = \"Operation C\";");
	originScript.append("var o4 = new Operation;");
	originScript.append("o4.name = \"Operation D\";");
	originScript.append("var o5 = new Operation;");
	originScript.append("o5.name = \"Operation E\";");
	originScript.append("c1.ownedOperations.add(o1);");
	originScript.append("c1.ownedOperations.add(o2);");
	originScript.append("c1.ownedOperations.add(o3);");
	originScript.append("c1.ownedOperations.add(o4);");
	originScript.append("c1.ownedOperations.add(o5);");

	saveModel(originResource, originSessionName, originScript);

	FileUtils.copyFile(originXmiFile, leftXmiFile);
	FileUtils.copyFile(originXmiFile, rightXmiFile);
	FileUtils.copyFile(originCbpFile, leftCbpFile);
	FileUtils.copyFile(originCbpFile, rightCbpFile);

	// LEFT
	StringBuilder leftScript = new StringBuilder();
	leftScript.append("var o4 = Operation.allInstances.selectOne(node | node.name == \"Operation D\");");
	leftScript.append("o4.name = \"Op D\";");
	leftScript.append("var c1 = Class.allInstances.selectOne(node | node.name == \"Class 1\");");
	leftScript.append("c1.name = \"Class 01\";");
	leftScript.append("c1.ownedOperations.move(4, 2);");
	leftScript.append("delete o4;");
	leftScript.append("var o5 = new Operation;");
	leftScript.append("o5.name = \"Operation F\";");
	leftScript.append("c1.ownedOperations.add(3, o5);");

	// RIGHT
	StringBuilder rightScript = new StringBuilder();
	rightScript.append("var c1 = Class.allInstances.selectOne(node | node.name == \"Class 1\");");
	rightScript.append("var o2 = Operation.allInstances.selectOne(node | node.name == \"Operation B\");");
	rightScript.append("o2.name = \"Operation BB\";");
	rightScript.append("c1.ownedOperations.move(4, 2);");
	rightScript.append("c1.ownedOperations.move(2, 3);");
	rightScript.append("var o6 = new Operation;");
	rightScript.append("o6.name = \"Operation G\";");
	rightScript.append("c1.ownedOperations.add(3, o6);");
	rightScript.append("c1.ownedOperations.move(0, 1);");
	rightScript.append("o2.name = \"Operation B\";");
	rightScript.append("c1.name = \"Class01\";");

	// LEFT
	XMIResource leftXmiResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
	HybridResource leftResource = new HybridXMIResourceImpl(leftXmiResource, new FileOutputStream(leftCbpFile, true));
	leftResource.setIdType(IdType.NUMERIC, "L-");
	leftResource.load(null);
	String leftSessionName = "LEFT";

	// RIGHT
	XMIResource rightXmiResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
	HybridResource rightResource = new HybridXMIResourceImpl(rightXmiResource, new FileOutputStream(rightCbpFile, true));
	rightResource.setIdType(IdType.NUMERIC, "R-");
	rightResource.load(null);
	String rightSessionName = "RIGHT";

//	// LEFT SWITCH
//	saveModel(leftResource, leftSessionName, rightScript);
//	// RIGHT SWITCH
//	saveModel(rightResource, rightSessionName, leftScript);

	// //LEFT
	 saveModel(leftResource, leftSessionName, leftScript);
	// //RIGHT
	 saveModel(rightResource, rightSessionName, rightScript);

    }

    /**
     * @param hybridResource
     * @param sessionName
     * @param script
     * @throws Exception
     * @throws EolRuntimeException
     * @throws IOException
     */
    private void saveModel(HybridResource hybridResource, String sessionName, StringBuilder script) throws Exception, EolRuntimeException, IOException {
	EolModule module = new EolModule();
	InMemoryEmfModel model = new InMemoryEmfModel("M", hybridResource, ePackage);
	module.getContext().getModelRepository().addModel(model);

	hybridResource.startNewSession(sessionName);
	module.parse(script.toString());
	module.execute();
	hybridResource.save(null);
	hybridResource.unload();
    }

}
