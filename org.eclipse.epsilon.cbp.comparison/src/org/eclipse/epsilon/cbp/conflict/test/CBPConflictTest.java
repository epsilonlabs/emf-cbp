package org.eclipse.epsilon.cbp.conflict.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.impl.DirectedRelationshipChangeImpl;
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
import org.eclipse.epsilon.cbp.comparison.util.CBPComparisonUtil;
import org.eclipse.epsilon.cbp.conflict.CBPConflict;
import org.eclipse.epsilon.cbp.merging.CBPMerging;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.ConnectorKind;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;

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

    public CBPConflictTest() {

	// EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(),
	// MoDiscoXMLPackage.eINSTANCE);
	// EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(),
	// JavaPackage.eINSTANCE);
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	options.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
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
	    // EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(),
	    // NodePackage.eINSTANCE);
	    // ePackage = (EPackage)
	    // EPackage.Registry.INSTANCE.get(NodePackage.eINSTANCE.getNsURI());
	    EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	    ePackage = (EPackage) EPackage.Registry.INSTANCE.get(JavaPackage.eINSTANCE.getNsURI());

	    cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml");
	    cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml");
	    cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml");
	    xmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.xmi");
	    xmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.xmi");
	    xmiRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.xmi");
	    stateXmiTargetFile = new File("D:\\TEMP\\CONFLICTS\\debug\\state-target.xmi");
	    changeCbpTargetFile = new File("D:\\TEMP\\CONFLICTS\\debug\\change-target.cbpxml");
	    changeXmiTargetFile = new File("D:\\TEMP\\CONFLICTS\\debug\\change-target.xmi");
	    // cbpOriginalFile = new
	    // File("D:\\TEMP\\FASE\\Debug\\origin.cbpxml");
	    // cbpLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.cbpxml");
	    // cbpRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.cbpxml");
	    // xmiOriginalFile = new
	    // File("D:\\TEMP\\FASE\\Debug\\original.xmi");
	    // xmiLeftFile = new File("D:\\TEMP\\FASE\\Debug\\left.xmi");
	    // xmiRightFile = new File("D:\\TEMP\\FASE\\Debug\\right.xmi");
	    // stateXmiTargetFile = new
	    // File("D:\\TEMP\\FASE\\Debug\\state-target.xmi");
	    // changeCbpTargetFile = new
	    // File("D:\\TEMP\\FASE\\Debug\\change-target.cbpxml");
	    // changeXmiTargetFile = new
	    // File("D:\\TEMP\\FASE\\Debug\\change-target.xmi");

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
	    originalScript.add("node0.valNodes.add(node1);");
	    originalScript.add("node0.valNodes.add(node2);");
	    originalScript.add("node0.valNodes.add(node3);");

	    
	    leftScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 3\");");
	    leftScript.add("delete node3;");

	    rightScript.add("var node2 = Node.allInstances.selectOne(node | node.name == \"Node 2\");");
	    rightScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 3\");");
	    rightScript.add("node3.valNodes.add(node2);");

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

	    stateXmiTargetResource.load(options);
	    doThreeWayComparison(xmiLeftResource, stateXmiTargetResource, xmiOriginalResource);
	    // sortResourceElements(stateXmiTargetResource);
	    stateXmiTargetResource.save(options);

	    System.out.println();
	    System.out.println("Sort target model of change-based comparison");
	    changeXmiTargetResource.load(options);
	    sortResourceElements(changeXmiTargetResource);
	    changeXmiTargetResource.save(options);

	    System.out.println("Sort target model of state-based comparison");
	    stateXmiTargetResource.load(options);
	    sortResourceElements(stateXmiTargetResource);
	    stateXmiTargetResource.save(options);

	    compareCBPvsXMITargets(changeXmiTargetResource, stateXmiTargetResource);

	    cbpOriginalResource.unload();
	    cbpLeftResource.unload();
	    cbpRightResource.unload();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    @Test
    public void testBatchConflicts() {

	// EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(),
	// UMLPackage.eINSTANCE);
	// ePackage = (EPackage)
	// EPackage.Registry.INSTANCE.get(UMLPackage.eINSTANCE.getNsURI());
	EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(), JavaPackage.eINSTANCE);
	ePackage = (EPackage) EPackage.Registry.INSTANCE.get(JavaPackage.eINSTANCE.getNsURI());
	EPackage.Registry.INSTANCE.put(MoDiscoXMLPackage.eINSTANCE.getNsURI(), MoDiscoXMLPackage.eINSTANCE);
	ePackage = (EPackage) EPackage.Registry.INSTANCE.get(MoDiscoXMLPackage.eINSTANCE.getNsURI());

	int result = 0;

	// create validation by comparing to EMF compare, should be sorted first
	//
	try {
	    String dir = "Epsilon";
	    int startFrom = 2; // min 1
	    // problems:
	    // null pointer while merging: 57, 58
	    int caseNum = 2; // max 68

	    for (int i = startFrom; i <= caseNum; i++) {

		System.out.println("\nMODEL " + i + "---------------------------");

		cbpOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml");
		cbpLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml");
		cbpRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml");
		xmiOriginalFile = new File("D:\\TEMP\\CONFLICTS\\debug\\origin.xmi");
		xmiLeftFile = new File("D:\\TEMP\\CONFLICTS\\debug\\left.xmi");
		xmiRightFile = new File("D:\\TEMP\\CONFLICTS\\debug\\right.xmi");

		stateXmiTargetFile = new File("D:\\TEMP\\CONFLICTS\\debug\\state-target.xmi");
		changeCbpTargetFile = new File("D:\\TEMP\\CONFLICTS\\debug\\change-target.cbpxml");
		changeXmiTargetFile = new File("D:\\TEMP\\CONFLICTS\\debug\\change-target.xmi");

		// cbpOriginalFile = new File("D:\\TEMP\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\origin.cbpxml");
		// cbpLeftFile = new File("D:\\\\TEMP\\\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\left.cbpxml");
		// cbpRightFile = new File("D:\\\\TEMP\\\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\right.cbpxml");
		// xmiOriginalFile = new File("D:\\\\TEMP\\\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\origin.xmi");
		// xmiLeftFile = new File("D:\\\\TEMP\\\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i + "\\left.xmi");
		// xmiRightFile = new File("D:\\\\TEMP\\\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\right.xmi");
		//
		// stateXmiTargetFile = new File("D:\\TEMP\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\state-target.xmi");
		// changeCbpTargetFile = new File("D:\\TEMP\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\change-target.cbpxml");
		// changeXmiTargetFile = new File("D:\\TEMP\\FASE\\" + dir +
		// File.separator + "data" + File.separator + i +
		// "\\change-target.xmi");

		Files.copy(cbpLeftFile.toPath(), changeCbpTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(xmiLeftFile.toPath(), changeXmiTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(xmiRightFile.toPath(), stateXmiTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		// do cbp comparison
		ICBPComparison comparison = doCbpComparison(cbpLeftFile, cbpRightFile, cbpOriginalFile);

		// do cbp merging
		doCbpMerging(comparison);

		XMIResource originXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiOriginalFile.getAbsolutePath()));
		originXmi.load(options);
		XMIResource rightXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiRightFile.getAbsolutePath()));
		rightXmi.load(options);
		XMIResource leftXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiLeftFile.getAbsolutePath()));
		leftXmi.load(options);

		XMIResource targetStateXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(stateXmiTargetFile.getAbsolutePath()));
		targetStateXmi.load(options);
		XMIResource targetChangeXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(changeXmiTargetFile.getAbsolutePath()));

		// do emf compare
		doThreeWayComparison(leftXmi, targetStateXmi, originXmi);
		targetStateXmi.save(options);

		System.out.println();
		System.out.println("Sort target model of change-based comparison");
		targetChangeXmi.load(options);
		CBPComparisonUtil.sort(targetChangeXmi);
		// sortResourceElements(targetChangeXmi);
		targetChangeXmi.save(options);

		System.out.println("Sort target model of state-based comparison");
		targetStateXmi.load(options);
		CBPComparisonUtil.sort(targetStateXmi);
		// sortResourceElements(targetStateXmi);
		targetStateXmi.save(options);

		EList<Diff> evalDiffs = compareCBPvsXMITargets(targetChangeXmi, targetStateXmi);
		if (evalDiffs.size() > 0) {
		    throw new Exception("MODEL " + i + ": there are difference(s) = " + evalDiffs.size());
		}

		targetStateXmi.unload();
		leftXmi.unload();
		rightXmi.unload();
		targetChangeXmi.unload();

	    }
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
	long x = changeCbpTargetFile.length();
	merger.mergeCBPAllLeftToRight(changeCbpTargetFile, cbpLeftFile, cbpRightFile, cbpOriginalFile, comparison.getLeftEvents(), comparison.getRightEvents(), conflicts);
	long y = changeCbpTargetFile.length();
	CBPResource cbpResource = (CBPResource) ((new CBPXMLResourceFactory())).createResource(URI.createFileURI(changeCbpTargetFile.getAbsolutePath()));
	cbpResource.load(options);

	XMIResource xmiResource = (XMIResource) ((new XMIResourceFactoryImpl())).createResource(URI.createFileURI(changeXmiTargetFile.getAbsolutePath()));
	xmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));

	TreeIterator<EObject> cbpIterator = cbpResource.getAllContents();
	TreeIterator<EObject> xmiIterator = xmiResource.getAllContents();
	while (cbpIterator.hasNext() && xmiIterator.hasNext()) {
	    String id = cbpResource.getURIFragment(cbpIterator.next());
	    xmiResource.setID(xmiIterator.next(), id);
	}
	xmiResource.save(options);
	// EObject z = xmiResource.getEObject("R-0");
	cbpResource.unload();
	xmiResource.unload();
    }

    private void sortResourceElements(Resource resource) {

	TreeIterator<EObject> iterator = resource.getAllContents();
	Map<EObject, String> eObject2IdMap = new HashMap<>();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    String id = resource.getURIFragment(eObject);
	    eObject2IdMap.put(eObject, id);
	}

	iterator = resource.getAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    String id = resource.getURIFragment(eObject);
	    // System.out.println("EObject: " + id);
	    // System.console();
	    for (EReference eReference : eObject.eClass().getEAllReferences()) {
		if (eReference.isMany() && eReference.isChangeable()) {
		    EList<EObject> list = (EList<EObject>) eObject.eGet(eReference);
		    if (list != null && list.size() > 0) {
			// ECollections.sort(list);
			ECollections.sort(list, new EObjectComparator());
		    }
		}
	    }
	}

	iterator = resource.getAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    String id = eObject2IdMap.get(eObject);
	    ((XMIResource) resource).setID(eObject, id);
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
	System.out.println("Do Three-Way, State-based Comparison ...");
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
	EList<Diff> diffs = emfComparison.getDifferences();
	printConflicts(leftXmi, rightXmi, originXmi, emfComparison.getConflicts());

	// IMerger.Registry registry = new IMerger.RegistryImpl();
	IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
	UMLMerger umlMerger = new UMLMerger();
	umlMerger.setRanking(11);
	registry.add(umlMerger);
	IBatchMerger batchMerger = new BatchMerger(registry);
	List<Diff> leftDiffs = diffs.stream().filter(d -> d.getSource() == DifferenceSource.LEFT).collect(Collectors.toList());
	// List<Diff> rightDiffs = diffs.stream().filter(d -> d.getSource() ==
	// DifferenceSource.RIGHT ).collect(Collectors.toList());

	Map<EObject, String> eObject2IdMap = new HashMap<>();
	TreeIterator<EObject> leftIterator = leftXmi.getAllContents();
	while (leftIterator.hasNext()) {
	    EObject eObject = leftIterator.next();
	    String id = leftXmi.getURIFragment(eObject);
	    eObject2IdMap.put(eObject, id);
	}
	TreeIterator<EObject> rightIterator = rightXmi.getAllContents();
	while (rightIterator.hasNext()) {
	    EObject eObject = rightIterator.next();
	    String id = rightXmi.getURIFragment(eObject);
	    eObject2IdMap.put(eObject, id);
	}

	batchMerger.copyAllLeftToRight(leftDiffs, new BasicMonitor());
	// batchMerger.copyAllLeftToRight(rightDiffs, new BasicMonitor());

	TreeIterator<EObject> targetIterator = rightXmi.getAllContents();
	while (targetIterator.hasNext()) {
	    EObject eObject = targetIterator.next();
	    String id = eObject2IdMap.get(eObject);
	    if (id != null) {
		rightXmi.setID(eObject, id);
	    }
	}

	return emfComparison.getConflicts();
    }

    @SuppressWarnings("restriction")
    private EList<Diff> compareCBPvsXMITargets(XMIResource leftXmi, XMIResource rightXmi) throws FileNotFoundException, IOException, Exception {
	System.out.println("\nComparing CBP vs XMI Targets");

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

	IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, null);
	Comparison emfComparison = comparator.compare(scope);
	EList<Diff> evalDiffs = emfComparison.getDifferences();

	printEMFCompareDiffs(leftXmi, rightXmi, evalDiffs);

	return evalDiffs;
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
	    if (conflict
		    .getKind() == ConflictKind.REAL /*
						     * || conflict.getKind() ==
						     * ConflictKind.PSEUDO
						     */) {

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
		    // if (leftString.equals(rightString)) {
		    // continue;
		    // }
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
		EObject eValue = null;
		String value = null;
		if (eLeftTarget != null) {
		    eValue = (EObject) eLeftTarget.eGet(eFeature);
		    value = leftModel.getID(eValue);
		}
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
	System.out.println("Diffs Size: " + list.size());
	return list;
    }

}
