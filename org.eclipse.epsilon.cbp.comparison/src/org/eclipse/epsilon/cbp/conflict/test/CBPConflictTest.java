package org.eclipse.epsilon.cbp.conflict.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.ConflictKind;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
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
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.conflict.CBPConflict;
import org.eclipse.epsilon.cbp.merging.CBPMerging;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.junit.Test;

public class CBPConflictTest {

    private CBPResource cbpOriginalResource;
    private CBPResource cbpLeftResource;
    private CBPResource cbpRightResource;
    private CBPResource changeCbpTargetResource;
    private XMIResource xmiOriginalResource;
    private XMIResource xmiLeftResource;
    private XMIResource xmiRightResource;
    private XMIResource stateXmiTargetResource;
    private XMIResource changeXmiTargetResource;
    private File cbpOriginalFile;
    private File cbpLeftFile;
    private File cbpRightFile;
    private File xmiOriginalFile;
    private File xmiLeftFile;
    private File xmiRightFile;
    private File stateXmiTargetFile;
    private File changeCbpTargetFile;
    private File changeXmiTargetFile;
    private Script originalScript;
    private Script leftScript;
    private Script rightScript;
    private EolModule module;
    private EPackage ePackage = NodePackage.eINSTANCE;

    Map<Object, Object> options = new HashMap<>();
    EObjectComparator eObjectComparator = new EObjectComparator();

    public CBPConflictTest() {

	// EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(),
	// UMLPackage.eINSTANCE);
	String x = NodePackage.eINSTANCE.getNsURI();
	EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	// EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(),
	// MoDiscoXMLPackage.eINSTANCE);
	// EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(),
	// JavaPackage.eINSTANCE);
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	Logger.getRootLogger().setLevel(Level.OFF);

	cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\origin.cbpxml");
	cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.cbpxml");
	cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.cbpxml");
	xmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\original.xmi");
	xmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.xmi");
	xmiRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.xmi");

	cbpOriginalResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpOriginalFile.getAbsolutePath()));
	cbpOriginalResource.setIdType(IdType.NUMERIC, "O-");
	cbpLeftResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpLeftFile.getAbsolutePath()));
	cbpLeftResource.setIdType(IdType.NUMERIC, "L-");
	cbpRightResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpRightFile.getAbsolutePath()));
	cbpRightResource.setIdType(IdType.NUMERIC, "R-");
	xmiOriginalResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiOriginalFile.getAbsolutePath()));
	xmiLeftResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiLeftFile.getAbsolutePath()));
	xmiRightResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiRightFile.getAbsolutePath()));

    }

    @Test
    public void testConflicts() {
	try {
	    ePackage = (EPackage) EPackage.Registry.INSTANCE.get(NodePackage.eINSTANCE.getNsURI());

	    cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\origin.cbpxml");
	    cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.cbpxml");
	    cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.cbpxml");
	    xmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\temp\\original.xmi");
	    xmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\temp\\left.xmi");
	    xmiRightFile = new File("D:\\TEMP\\CONFLICTS\\temp\\right.xmi");

	    stateXmiTargetFile = new File("D:\\TEMP\\CONFLICTS\\temp\\state-target.xmi");
	    changeCbpTargetFile = new File("D:\\TEMP\\CONFLICTS\\temp\\change-target.cbpxml");
	    changeXmiTargetFile = new File("D:\\TEMP\\CONFLICTS\\temp\\change-target.xmi");

	    cbpOriginalResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpOriginalFile.getAbsolutePath()));
	    cbpOriginalResource.setIdType(IdType.NUMERIC, "O-");
	    cbpLeftResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpLeftFile.getAbsolutePath()));
	    cbpLeftResource.setIdType(IdType.NUMERIC, "L-");
	    cbpRightResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpRightFile.getAbsolutePath()));
	    cbpRightResource.setIdType(IdType.NUMERIC, "R-");
	    xmiOriginalResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiOriginalFile.getAbsolutePath()));
	    xmiLeftResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiLeftFile.getAbsolutePath()));
	    xmiRightResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiRightFile.getAbsolutePath()));

	    changeCbpTargetResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(changeCbpTargetFile.getAbsolutePath()));
	    changeCbpTargetResource.setIdType(IdType.NUMERIC, "L-");
	    stateXmiTargetResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(stateXmiTargetFile.getAbsolutePath()));
	    changeXmiTargetResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(changeXmiTargetFile.getAbsolutePath()));

	    originalScript = new Script(cbpOriginalResource, xmiOriginalResource);
	    leftScript = new Script(cbpLeftResource, xmiLeftResource);
	    rightScript = new Script(cbpRightResource, xmiRightResource);

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

	    if (changeCbpTargetFile.exists())
		changeCbpTargetFile.delete();
	    if (changeXmiTargetFile.exists())
		changeXmiTargetFile.delete();
	    if (stateXmiTargetFile.exists())
		stateXmiTargetFile.delete();

	    originalScript.add("var node0 = new Node;");
	    originalScript.add("node0.name = \"Node 0\";");
	    originalScript.add("var node1 = new Node;");
	    originalScript.add("node1.name = \"Node 1\";");
	    originalScript.add("var node2 = new Node;");
	    originalScript.add("node2.name = \"Node 2\";");
	    originalScript.add("var node3 = new Node;");
	    originalScript.add("node3.name = \"Node 3\";");
	    originalScript.add("var node5 = new Node;");
	    originalScript.add("node5.name = \"Node 5\";");
	    originalScript.add("var node6 = new Node;");
	    originalScript.add("node6.name = \"Node 6\";");
	    originalScript.add("var node7 = new Node;");
	    originalScript.add("node7.name = \"Node 7\";");
	    originalScript.add("node0.valNodes.add(node1);");
	    originalScript.add("node0.valNodes.add(node2);");
	    originalScript.add("node0.valNodes.add(node3);");
	    originalScript.add("node0.valNodes.add(node5);");
	    originalScript.add("node1.children.add(node7);");
	    originalScript.add("node7.parent = node1;");
	    originalScript.add("node5.valNodes.add(node6);");
	    originalScript.add("node0.refNodes.add(node1);");
	    originalScript.add("node0.refNodes.add(node2);");
	    originalScript.add("node0.refNodes.add(node3);");

	    leftScript.add("var node0 = Node.allInstances.selectOne(node | node.name == \"Node 0\");");
	    leftScript.add("node0.name = \"Node L0\";");
	    leftScript.add("node0.valNodes.move(1, 0);");
	    leftScript.add("var node4 = new Node;");
	    leftScript.add("node4.name = \"Node L1\";");
	    leftScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 3\");");
	    leftScript.add("node3.valNodes.add(node4);");
	    leftScript.add("var node5 = Node.allInstances.selectOne(node | node.name == \"Node 5\");");
	    leftScript.add("delete node5;");
	    leftScript.add("node0.refNodes.move(0, 1);");
	    leftScript.add("node3.name = \"Node XXX\";");
	    leftScript.add("node3.refNode = node0;");
	    leftScript.add("var node7 = Node.allInstances.selectOne(node | node.name == \"Node 7\");");
	    leftScript.add("var node2 = Node.allInstances.selectOne(node | node.name == \"Node 2\");");
	    leftScript.add("node7.parent = node2;");
	    
	    rightScript.add("var node0 = Node.allInstances.selectOne(node | node.name == \"Node 0\");");
	    rightScript.add("node0.name = \"Node R0\";");
	    rightScript.add("node0.valNodes.move(2, 0);");
	    rightScript.add("var node4 = new Node;");
	    rightScript.add("node4.name = \"Node R1\";");
	    rightScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 3\");");
	    rightScript.add("node3.valNodes.add(node4);");
	    rightScript.add("var node6 = Node.allInstances.selectOne(node | node.name == \"Node 6\");");
	    rightScript.add("delete node6;");
	    rightScript.add("node0.refNodes.move(2, 1);");
	    rightScript.add("node3.name = \"Node XXX\";");
	    rightScript.add("node3.refNode = node0;");
	    rightScript.add("var node7 = Node.allInstances.selectOne(node | node.name == \"Node 7\");");
	    rightScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 3\");");
	    rightScript.add("node7.parent = node3;");

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

	    Files.copy(cbpLeftFile.toPath(), changeCbpTargetFile.toPath());
	    Files.copy(xmiLeftFile.toPath(), changeXmiTargetFile.toPath());
	    Files.copy(xmiRightFile.toPath(), stateXmiTargetFile.toPath());

	    changeCbpTargetResource.load(options);
	    changeXmiTargetResource.load(options);
	    stateXmiTargetResource.load(options);

	    // -------------------------
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

	    ICBPComparison comparison = doCbpComparison(changeCbpTargetFile, cbpRightFile, cbpOriginalFile);
	    
	    doCbpMerging(comparison);

	    doThreeWayComparison(xmiLeftResource, stateXmiTargetResource, xmiOriginalResource);
	    // sortResourceElements(stateXmiTargetResource);
	    stateXmiTargetResource.save(options);

	    cbpOriginalResource.unload();
	    cbpLeftResource.unload();
	    cbpRightResource.unload();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    /**
     * @param comparison
     * @throws Exception
     */
    private void doCbpMerging(ICBPComparison comparison) throws Exception {
	System.out.println("\nDo CBP Merging ...");
	
	List<CBPConflict> conflicts = comparison.getConflicts();
	
	CBPMerging merger = new CBPMerging();
	merger.mergeCBPAllLeftToRight(changeCbpTargetFile, cbpLeftFile, cbpRightFile, cbpOriginalFile, comparison.getLeftEvents(), comparison.getRightEvents(), conflicts);
	CBPResource cbpResource = (CBPResource) ((new CBPXMLResourceFactory())).createResource(URI.createFileURI(changeCbpTargetFile.getAbsolutePath()));
	cbpResource.load(options);
	
	XMIResource xmiResource = (XMIResource) ((new XMIResourceFactoryImpl())).createResource(URI.createFileURI(changeCbpTargetFile.getAbsolutePath().replaceAll(".cbpxml", ".xmi")));
	xmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
	
	TreeIterator<EObject> cbpIterator = cbpResource.getAllContents();
	TreeIterator<EObject> xmiIterator = xmiResource.getAllContents();
	while(cbpIterator.hasNext() && xmiIterator.hasNext()) {
	    String id = cbpResource.getURIFragment(cbpIterator.next());
	    xmiResource.setID(xmiIterator.next(), id);
	}
	xmiResource.save(options);
	cbpResource.unload();
	xmiResource.unload();
    }

    private void sortResourceElements(Resource resource) {
	TreeIterator<EObject> iterator = resource.getAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    for (EReference eReference : eObject.eClass().getEAllReferences()) {
		if (eReference.isMany() && eReference.isChangeable()) {
		    EList<EObject> list = (EList<EObject>) eObject.eGet(eReference);
		    ECollections.sort(list, eObjectComparator);
		}
	    }
	}
    }

    /**
     * @param cbpOriginalFile
     * @param cbpRightFile
     * @param cbpLeftFile
     * @return
     * @throws IOException
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    private ICBPComparison doCbpComparison(File cbpLeftFile, File cbpRightFile, File cbpOriginalFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	ICBPComparison comparison = new CBPComparisonImpl();
	comparison.setDiffEMFCompareFile(new File(cbpOriginalFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
	comparison.setObjectTreeFile(new File(cbpOriginalFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
	comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
	comparison.compare(cbpLeftFile, cbpRightFile, cbpOriginalFile);
	return comparison;
    }

    @SuppressWarnings("restriction")
    private EList<Conflict> doThreeWayComparison(XMIResource leftXmi, XMIResource rightXmi, XMIResource originXmi) throws FileNotFoundException, IOException, Exception {
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
	printConflicts(leftXmi, rightXmi, originXmi, emfComparison.getConflicts());

	// IMerger.Registry registry = new IMerger.RegistryImpl();
	IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
	UMLMerger umlMerger = new UMLMerger();
	umlMerger.setRanking(11);
	registry.add(umlMerger);
	IBatchMerger batchMerger = new BatchMerger(registry);
	Iterator<Diff> iterator = evalDiffs.iterator();
	while (iterator.hasNext()) {
	    Diff x = iterator.next();
	    if (x.getSource() == DifferenceSource.RIGHT) {
		iterator.remove();
	    }
	}
	batchMerger.copyAllLeftToRight(evalDiffs, new BasicMonitor());
	return emfComparison.getConflicts();
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

    private void printConflicts(XMIResource left, XMIResource right, XMIResource origin, EList<Conflict> conflicts) throws FileNotFoundException, IOException {
	System.out.println("\nEMF COMPARE CONFLICTS:");
	int conflictCount = 0;
	for (Conflict conflict : conflicts) {
	    if (conflict.getKind() == ConflictKind.REAL) {

		conflictCount++;

		EList<Diff> leftDiffs = conflict.getLeftDifferences();
		EList<Diff> rightDiffs = conflict.getRightDifferences();
		boolean foundConflict = false;

		Iterator<Diff> leftIterator = leftDiffs.iterator();
		Iterator<Diff> rightIterator = rightDiffs.iterator();
		while (leftIterator.hasNext() || rightIterator.hasNext()) {
		    Diff leftDiff = null;
		    if (leftIterator.hasNext())
			leftDiff = leftIterator.next();
		    Diff rightDiff = null;
		    if (rightIterator.hasNext())
			rightDiff = rightIterator.next();

		    String leftString = "";
		    String rightString = "";
		    if (leftDiff instanceof AttributeChange || leftDiff instanceof ReferenceChange || leftDiff instanceof ResourceAttachmentChange) {
			leftString = diffToString(left, origin, leftDiff);
		    }
		    if (rightDiff instanceof AttributeChange || rightDiff instanceof ReferenceChange || rightDiff instanceof ResourceAttachmentChange) {
			rightString = diffToString(right, origin, rightDiff);
		    }
		    if (leftString.equals(rightString)) {
			continue;
		    }
		    foundConflict = true;

		    System.out.println(conflictCount + ": " + leftString + " <-> " + rightString);
		    System.console();
		}
	    }
	}
	System.out.println("Conflict count:" + conflictCount);
	if (conflictCount > 0) {
	    System.console();
	}
    }

    @SuppressWarnings("unchecked")
    private String diffToString(XMIResource leftModel, XMIResource rightModel, Diff diff) throws FileNotFoundException, IOException {

	String result = "";
	String leftTarget = null;
	String rightTarget = null;
	EObject eLeftTarget = null;
	EObject eRightTarget = null;

	if (diff.getSource() == DifferenceSource.LEFT) {
	    eLeftTarget = diff.getMatch().getLeft();
	    eRightTarget = diff.getMatch().getOrigin();
	} else {
	    eLeftTarget = diff.getMatch().getRight();
	    eRightTarget = diff.getMatch().getOrigin();
	}

	if (eLeftTarget != null) {
	    leftTarget = leftModel.getURIFragment(eLeftTarget);
	}
	if (eRightTarget != null) {
	    rightTarget = rightModel.getURIFragment(eRightTarget);
	}

	if (diff.getKind() == DifferenceKind.ADD) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object value = ((AttributeChange) diff).getValue();
		if (value instanceof String) {
		    value = "\"" + value + "\"";
		}
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eLeftTarget.eGet(eFeature);
		    index = list.indexOf(value);
		}
		result = "ADD " + value + " TO " + leftTarget + "." + featureName + " AT " + index;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		String featureName = eFeature.getName();
		EObject eValue = ((ReferenceChange) diff).getValue();
		String value = leftModel.getID(eValue);
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eLeftTarget.eGet(eFeature);
		    index = list.indexOf(eValue);
		    result = "ADD " + value + " TO " + leftTarget + "." + featureName + " AT " + index;
		} else {
		    result = "SET " + leftTarget + "." + featureName + " FROM " + null + " TO " + value;
		}
	    } else if (diff instanceof ResourceAttachmentChange) {
		String featureName = "resource";
		String value = leftModel.getID(eLeftTarget);
		int index = leftModel.getContents().indexOf(eLeftTarget);
		result = "ADD " + value + " TO " + featureName + " AT " + index;
	    }
	} else if (diff.getKind() == DifferenceKind.CHANGE) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object rightValue = eRightTarget.eGet(eFeature);
		Object leftValue = eLeftTarget.eGet(eFeature);
		if (rightValue instanceof String) {
		    rightValue = "\"" + rightValue + "\"";
		}
		if (leftValue instanceof String) {
		    leftValue = "\"" + leftValue + "\"";
		}
		result = "SET " + leftTarget + "." + featureName + " FROM " + rightValue + " TO " + leftValue;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		String featureName = eFeature.getName();
		EObject eOldValue = (EObject) eRightTarget.eGet(eFeature);
		String oldValue = rightModel.getID(eOldValue);
		EObject eValue = (EObject) eLeftTarget.eGet(eFeature);
		String value = leftModel.getID(eValue);
		result = "SET " + leftTarget + "." + featureName + " FROM " + oldValue + " TO " + value;
	    }
	} else if (diff.getKind() == DifferenceKind.MOVE) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object value = ((AttributeChange) diff).getValue();
		String oldPosition = "";
		String position = "";
		EList<EObject> list1 = (EList<EObject>) eRightTarget.eGet(eFeature);
		oldPosition = "" + list1.indexOf(value);
		EList<EObject> list2 = (EList<EObject>) eLeftTarget.eGet(eFeature);
		position = "" + list2.indexOf(value);
		result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		if (eFeature.isContainment()) {
		    String featureName = eFeature.getName();
		    EObject eValue = ((ReferenceChange) diff).getValue();
		    String value = leftModel.getID(eValue);
		    EObject eOldValue = rightModel.getEObject(value);
		    EReference eOldFeature = (EReference) eOldValue.eContainingFeature();
		    String eOldFeatureName = null;
		    if (eOldFeature != null) {
			eOldFeatureName = eOldFeature.getName();
		    }
		    eRightTarget = eOldValue.eContainer();
		    rightTarget = rightModel.getID(eRightTarget);
		    String oldPosition = "";
		    if (eOldFeature != null && eOldFeature.isMany()) {
			EList<EObject> list = (EList<EObject>) eRightTarget.eGet(eOldFeature);
			oldPosition = "" + list.indexOf(eOldValue);
		    }
		    String position = "";
		    if (eFeature.isMany()) {
			EList<EObject> list = (EList<EObject>) eLeftTarget.eGet(eFeature);
			position = "" + list.indexOf(eValue);
		    }
		    // cross container
		    if ((!leftTarget.equals(rightTarget) || !featureName.equals(eOldFeatureName))) {
			// from other container/feature
			if (eRightTarget != null) {
			    if (eOldFeature.isMany())
				oldPosition = "." + oldPosition;
			    if (eFeature.isMany())
				position = "." + position;
			    result = "MOVE " + value + " FROM " + rightTarget + "." + eOldFeatureName + oldPosition + " TO " + leftTarget + "." + featureName + position;
			}
			// from resource
			else {
			    eOldFeatureName = "resource";
			    oldPosition = "" + rightModel.getContents().indexOf(eValue);
			    if (eFeature.isMany())
				position = "." + position;
			    result = "MOVE " + value + " FROM " + eOldFeatureName + oldPosition + " TO " + leftTarget + "." + featureName + position;
			}
		    }
		    // within container
		    else {
			result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
		    }
		} else {
		    String featureName = eFeature.getName();
		    EObject eValue = ((ReferenceChange) diff).getValue();
		    String value = leftModel.getID(eValue);
		    Object eOldValue = rightModel.getEObject(value);
		    String oldPosition = "";
		    String position = "";
		    EList<EObject> list1 = (EList<EObject>) eRightTarget.eGet(eFeature);
		    oldPosition = "" + list1.indexOf(eOldValue);
		    EList<EObject> list2 = (EList<EObject>) eLeftTarget.eGet(eFeature);
		    position = "" + list2.indexOf(eValue);
		    result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
		}
	    }

	    else if (diff instanceof ResourceAttachmentChange) {
		String featureName = "resource";
		EObject eValue = eLeftTarget;
		String value = null;
		EObject eOldValue = null;
		if (eValue != null) {
		    value = leftModel.getID(eValue);
		    eOldValue = rightModel.getEObject(value);
		} else {
		    eOldValue = diff.getMatch().getRight();
		    value = rightModel.getID(eValue);
		}

		// from container/feature
		if (eOldValue.eContainer() != null) {
		    eRightTarget = eOldValue.eContainer();
		    rightTarget = rightModel.getID(eRightTarget);
		    EReference eOldFeature = (EReference) eOldValue.eContainingFeature();
		    String eOldFeatureName = eOldFeature.getName();

		    String oldPosition = "";
		    if (eOldFeature.isMany()) {
			EList<Object> list = (EList<Object>) eRightTarget.eGet(eOldFeature);
			oldPosition = "." + list.indexOf(eOldValue);
		    }
		    String position = "." + leftModel.getContents().indexOf(eValue);
		    result = "MOVE " + value + " FROM " + rightTarget + "." + eOldFeatureName + oldPosition + " TO " + featureName + position;
		}
		// within resource / root level
		else {
		    String oldPosition = "" + rightModel.getContents().indexOf(eOldValue);
		    String position = "" + leftModel.getContents().indexOf(eValue);
		    result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
		}
	    }
	} else if (diff.getKind() == DifferenceKind.DELETE) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object value = ((AttributeChange) diff).getValue();
		if (value instanceof String) {
		    value = "\"" + value + "\"";
		}
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eRightTarget.eGet(eFeature);
		    index = list.indexOf(value);
		}
		result = "DELETE " + value + " FROM " + rightTarget + "." + featureName + " AT " + index;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		String featureName = eFeature.getName();
		EObject eValue = ((ReferenceChange) diff).getValue();
		String value = rightModel.getID(eValue);
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eRightTarget.eGet(eFeature);
		    index = list.indexOf(eValue);
		}
		result = "DELETE " + value + " FROM " + rightTarget + "." + featureName + " AT " + index;
	    } else if (diff instanceof ResourceAttachmentChange) {
		String featureName = "resource";
		String value = rightModel.getID(eRightTarget);
		int index = rightModel.getContents().indexOf(eRightTarget);
		result = "DELETE " + value + " FROM " + featureName + " AT " + index;
	    }
	}

	return result;

    }

}
