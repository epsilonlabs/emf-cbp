package org.eclipse.epsilon.cbp.merging.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.ConflictKind;
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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.junit.After;
import org.junit.Test;

public class CBPConflictTest {

    private CBPResource cbpOriginalResource;
    private CBPResource cbpLeftResource;
    private CBPResource cbpRightResource;
    private XMIResource xmiOriginalResource;
    private XMIResource xmiLeftResource;
    private XMIResource xmiRightResource;
    private File cbpOriginalFile;
    private File cbpLeftFile;
    private File cbpRightFile;
    private File xmiOriginalFile;
    private File xmiLeftFile;
    private File xmiRightFile;
    private Script originalScript;
    private Script leftScript;
    private Script rightScript;
    private EolModule module;
    private EPackage ePackage = NodePackage.eINSTANCE;

    public CBPConflictTest() {

	Logger.getRootLogger().setLevel(Level.OFF);

	cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\original.cbpxml");
	cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.cbpxml");
	cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.cbpxml");
	xmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\original.xmi");
	xmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.xmi");
	xmiRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.xmi");

	if (cbpOriginalFile.exists())
	    cbpOriginalFile.delete();
	if (cbpLeftFile.exists())
	    cbpLeftFile.delete();
	if (cbpRightFile.exists())
	    cbpRightFile.delete();
	if (xmiOriginalFile.exists())
	    xmiOriginalFile.delete();
	if (xmiLeftFile.exists())
	    xmiLeftFile.delete();
	if (xmiRightFile.exists())
	    xmiRightFile.delete();

	cbpOriginalResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpOriginalFile.getAbsolutePath()));
	cbpOriginalResource.setIdType(IdType.NUMERIC, "O-");
	cbpLeftResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpLeftFile.getAbsolutePath()));
	cbpLeftResource.setIdType(IdType.NUMERIC, "L-");
	cbpRightResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpRightFile.getAbsolutePath()));
	cbpRightResource.setIdType(IdType.NUMERIC, "R-");
	xmiOriginalResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiOriginalFile.getAbsolutePath()));
	xmiLeftResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiLeftFile.getAbsolutePath()));
	xmiRightResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiRightFile.getAbsolutePath()));

	originalScript = new Script(cbpOriginalResource, xmiOriginalResource);
	leftScript = new Script(cbpLeftResource, xmiLeftResource);
	rightScript = new Script(cbpRightResource, xmiRightResource);
    }

    @Test
    public void testConflicts() {
	try {
	    originalScript.add("var node0 = new Node;");
	    originalScript.add("node0.name = \"Node 00\";");
	    originalScript.add("var node1 = new Node;");
	    originalScript.add("node1.name = \"Node 01\";");
	    originalScript.add("var node2 = new Node;");
	    originalScript.add("node2.name = \"Node 02\";");
	    originalScript.add("var node3 = new Node;");
	    originalScript.add("node3.name = \"Node 03\";");
	    originalScript.add("var node4 = new Node;");
	    originalScript.add("node4.name = \"Node 04\";");
	    originalScript.add("node3.valNodes.add(node4);");
	    originalScript.add("node0.valNodes.add(node1);");
	    originalScript.add("node0.valNodes.add(node2);");
	    originalScript.add("node0.valNodes.add(node3);");

	    leftScript.add("var node0 = Node.allInstances.selectOne(node | node.name == \"Node 00\");");
	    leftScript.add("node0.name = \"Node A\";");
	    leftScript.add("node0.valNodes.move(2, 1);");
	    leftScript.add("var node4 = Node.allInstances.selectOne(node | node.name == \"Node 04\");");
	    leftScript.add("node4.name = \"Node 44\";");
	    leftScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
	    leftScript.add("var node5 = new Node;");
	    leftScript.add("node5.name = \"Node 05\";");
	    leftScript.add("node1.valNodes.add(0, node5);");
	    // leftScript.add("var node3 = Node.allInstances.selectOne(node |
	    // node.name == \"Node 03\");");
	    // leftScript.add("delete node3;");
	    // leftScript.add("var node2 = Node.allInstances.selectOne(node |
	    // node.name == \"Node 02\");");
	    // leftScript.add("delete node2;");
	    // leftScript.add("node0.valNodes.remove(1);");

	    rightScript.add("var node0 = Node.allInstances.selectOne(node | node.name == \"Node 00\");");
	    rightScript.add("node0.name = \"Node Z\";");
	    rightScript.add("node0.valNodes.move(0, 1);");
	    rightScript.add("var node4 = Node.allInstances.selectOne(node | node.name == \"Node 04\");");
	    rightScript.add("node4.name = \"Node 44\";");
	    rightScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
	    rightScript.add("delete node1;");
	    // rightScript.add("var node3 = Node.allInstances.selectOne(node |
	    // node.name == \"Node 03\");");
	    // rightScript.add("node3.name = \"Node XYZ\";");

	    originalScript.run("ORIGIN");
	    originalScript.save(null);

	    Files.copy(cbpOriginalFile.toPath(), cbpLeftFile.toPath());
	    Files.copy(cbpOriginalFile.toPath(), cbpRightFile.toPath());
	    cbpLeftResource.load(null);
	    cbpRightResource.load(null);

	    leftScript.run("LEFT");
	    leftScript.save(null);
	    rightScript.run("RIGHT");
	    rightScript.save(null);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    @After
    public void postTest() throws Exception {
	System.out.println("ORIGIN:");
	for (String line : Files.readAllLines(cbpOriginalFile.toPath())) {
	    System.out.println(line);
	}
	System.out.println();
	System.out.println("RIGHT:");
	for (String line : Files.readAllLines(cbpRightFile.toPath())) {
	    System.out.println(line);
	}
	System.out.println();
	System.out.println("LEFT:");
	for (String line : Files.readAllLines(cbpLeftFile.toPath())) {
	    System.out.println(line);
	}
	System.out.println();
	System.out.println("DIFFS:");
	// doThreeWayComparison(xmiLeftResource, xmiRightResource, null);
	doThreeWayComparison(xmiLeftResource, xmiRightResource, xmiOriginalResource);

	cbpOriginalResource.unload();
	cbpLeftResource.unload();
	cbpRightResource.unload();
    }

    private void doThreeWayComparison(XMIResource leftXmi, XMIResource rightXmi, XMIResource originXmi) throws FileNotFoundException, IOException, Exception {
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

	IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, originXmi);
	Comparison emfComparison = comparator.compare(scope);
	EList<Diff> evalDiffs = emfComparison.getDifferences();
	Iterator<Diff> iterator = evalDiffs.iterator();
	while (iterator.hasNext()) {
	    Diff x = iterator.next();
	    if (x instanceof MultiplicityElementChange || x instanceof AssociationChange || x instanceof DirectedRelationshipChange) {
		iterator.remove();
	    }
	}
	printEMFCompareDiffs(leftXmi, rightXmi, evalDiffs, emfComparison);
	printConflicts(leftXmi, rightXmi, originXmi, emfComparison.getConflicts());

    }

    public class Script {
	private String text = new String();
	private CBPResource cbpResource;
	private XMIResource xmiResource;
	private InMemoryEmfModel model;

	public Script(CBPResource cbpResource, XMIResource xmiResource) {
	    this.cbpResource = cbpResource;
	    this.xmiResource = xmiResource;
	}

	public CBPResource getCbpResource() {
	    return cbpResource;
	}

	public XMIResource getXmiResource() {
	    return xmiResource;
	}

	public void add(String line) {
	    text = text.concat(line).concat(System.lineSeparator());
	}

	public void clear() {
	    text = "";
	}

	public String toString() {
	    return text;
	}

	public void run(String sessionName) throws Exception {
	    cbpResource.startNewSession(sessionName);
	    module = new EolModule();
	    module.parse(text);
	    model = new InMemoryEmfModel("M", cbpResource, ePackage);
	    module.getContext().getModelRepository().addModel(model);
	    module.execute();
	}

	public void save(Map<?, ?> options) throws IOException {
	    xmiResource.getContents().clear();
	    xmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
	    TreeIterator<EObject> cbpIterator = cbpResource.getAllContents();
	    TreeIterator<EObject> xmiIterator = xmiResource.getAllContents();
	    while (cbpIterator.hasNext() && xmiIterator.hasNext()) {
		String id = cbpResource.getURIFragment(cbpIterator.next());
		EObject eObject = xmiIterator.next();
		xmiResource.setID(eObject, id);
	    }
	    cbpResource.save(options);
	    xmiResource.save(options);
	}
    }

    private void printConflicts(XMIResource left, XMIResource right, XMIResource origin, EList<Conflict> conflicts) {
	Set<String> set = new HashSet<>();
	System.out.println("\nCONFLICTS:");
	System.out.println("Conflict count:" + conflicts.size());
	for (Conflict conflict : conflicts) {
	    if (conflict.getKind() == ConflictKind.REAL) {
		EList<Diff> leftDiffs = conflict.getLeftDifferences();
		EList<Diff> rightDiffs = conflict.getRightDifferences();

		System.console();
	    }
	}
    }

    private void printEMFCompareDiffs(XMIResource left, XMIResource right, EList<Diff> diffs, Comparison emfComparison) throws FileNotFoundException, IOException {
	Set<String> set = new HashSet<>();
	for (Diff diff : diffs) {
	    String feature = null;
	    String id = null;
	    String value = null;
	    int position = 0;

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
		EObject eValue1 = ((ReferenceChange) diff).getValue();
		value = left.getURIFragment(eValue1);
		if (value == null || "/-1".equals(value)) {
		    EObject eValue2 = emfComparison.getMatch(eValue1).getRight();
		    if (eValue2 != null) {
			value = right.getURIFragment(eValue2);
		    } else {
			EObject eValue3 = emfComparison.getMatch(eValue1).getLeft();
			value = left.getURIFragment(eValue3);
		    }
		}
		EObject obj = left.getEObject(value);
		if (obj != null && obj.eContainingFeature().isMany()) {
		    EList<EObject> list = (EList<EObject>) obj.eContainer().eGet(obj.eContainingFeature());
		    position = list.indexOf(obj);
		} else {
		    obj = right.getEObject(value);
		    EList<EObject> list = (EList<EObject>) obj.eContainer().eGet(obj.eContainingFeature());
		    position = list.indexOf(obj);
		}

	    } else if (diff instanceof MultiplicityElementChange) {
		continue;
	    } else if (diff instanceof ResourceAttachmentChange) {
		feature = "resource";
		value = new String(id);
		id = new String(feature);
		EObject eObject = left.getEObject(id);
		position = left.getContents().indexOf(eObject);
	    } else if (diff instanceof AssociationChange) {
		continue;
	    } else if (diff instanceof DirectedRelationshipChange) {
		continue;
	    } else {
		System.out.println("UNHANDLED DIFF: " + diff.getClass().getName());
	    }

	    String x = id + "." + feature + "." + position + "." + value + "." + diff.getKind();
	    set.add(x.trim());
	}

	List<String> list = new ArrayList<>(set);
	for (String item : list) {
	    System.out.println(item);
	}
    }

}
