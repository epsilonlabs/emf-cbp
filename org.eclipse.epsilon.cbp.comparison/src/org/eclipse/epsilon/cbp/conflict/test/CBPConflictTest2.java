package org.eclipse.epsilon.cbp.conflict.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.conflict.CBPConflict;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.merging.CBPMerging;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Test;

public class CBPConflictTest2 {

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

    public CBPConflictTest2() {

	// EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(),
	// UMLPackage.eINSTANCE);
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
    public void testUMLConflicts() {
	try {
	    ePackage = UMLPackage.eINSTANCE;
	    UMLFactory factory = UMLFactory.eINSTANCE;

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

	    Package p = factory.createPackage();
	    p.setName("PackageA");
	    InterfaceRealization iA = factory.createInterfaceRealization();
	    iA.setName("InterfaceB");
	    Interface i2 = factory.createInterface();
	    iA.setName("InterfaceC");
	    Class c1 = factory.createClass();
	    c1.setName("ClassD");
	    p.getPackagedElements().add(iA);
	    p.getPackagedElements().add(i2);
	    p.getPackagedElements().add(c1);
	    c1.getInterfaceRealizations().add(iA);
	    Operation o = factory.createOperation();
	    // c1.getOP
	    // c1.getUsedInterfaces().add(iA);

	    originalScript.add("var p = new Package;");
	    originalScript.add("p.name = \"RootPackage\";");
	    originalScript.add("var i1 = new InterfaceRealization;");
	    originalScript.add("i1.name = \"InterfaceA\";");
	    originalScript.add("var i2 = new InterfaceRealization;");
	    originalScript.add("i2.name = \"InterfaceB\";");
	    originalScript.add("var c3 = new Class;");
	    originalScript.add("c3.name = \"ClassD\";");
	    originalScript.add("var o1 = new Operation;");
	    originalScript.add("o1.name = \"OperationE\";");
	    originalScript.add("var param1 = new Parameter;");
	    originalScript.add("param1.name = \"ParamF\";");
	    originalScript.add("var param2 = new Parameter;");
	    originalScript.add("param2.name = \"ParamG\";");
	    originalScript.add("var c4 = new Class;");
	    originalScript.add("c4.name = \"ClassH\";");
	    originalScript.add("var param3 = new Parameter;");
	    originalScript.add("param3.name = \"ParamI\";");
	    originalScript.add("var param4 = new Parameter;");
	    originalScript.add("param4.name = \"ParamK\";");
	    originalScript.add("var o2 = new Operation;");
	    originalScript.add("o2.name = \"OperationJ\";");
	    originalScript.add("var c5 = new Class;");
	    originalScript.add("c5.name = \"ClassL\";");
	    originalScript.add("var c6 = new Class;");
	    originalScript.add("c6.name = \"ClassN\";");
	    originalScript.add("var o3 = new Operation;");
	    originalScript.add("o3.name = \"OperationM\";");
	    originalScript.add("p.packagedElements.add(i1);");
	    originalScript.add("p.packagedElements.add(i2);");
	    originalScript.add("p.packagedElements.add(c4);");
	    originalScript.add("p.packagedElements.add(c3);");
	    originalScript.add("p.packagedElements.add(c5);");
	    originalScript.add("c3.ownedOperations.add(o1);");
	    originalScript.add("c3.ownedOperations.add(o2);");
	    originalScript.add("c6.ownedOperations.add(o3);");
	    originalScript.add("o2.ownedParameters.add(param3);");
	    originalScript.add("\"Original Script has been successfully executed\".println();");

	    leftScript.add("var i = InterfaceRealization.allInstances.selectOne(x | x.name == \"InterfaceA\");");
	    leftScript.add("var c = Class.allInstances.selectOne(x | x.name == \"ClassD\");");
	    leftScript.add("var c2 = Class.allInstances.selectOne(x | x.name == \"ClassH\");");
	    leftScript.add("var o = Operation.allInstances.selectOne(x | x.name == \"OperationE\");");
	    leftScript.add("var p = Parameter.allInstances.selectOne(x | x.name == \"ParamF\");");
	    leftScript.add("var p2 = Package.allInstances.selectOne(x | x.name == \"RootPackage\");");
	    leftScript.add("var o2 = Operation.allInstances.selectOne(x | x.name == \"OperationJ\");");
	    leftScript.add("var p3 = Parameter.allInstances.selectOne(x | x.name == \"ParamK\");");
	    leftScript.add("var c3 = Class.allInstances.selectOne(x | x.name == \"ClassL\");");
	    leftScript.add("var o3 = Operation.allInstances.selectOne(x | x.name == \"OperationM\");");
	    leftScript.add("c.interfaceRealizations.add(i);");
	    leftScript.add("o.ownedParameters.add(p);");
	    leftScript.add("p2.name = \"MainPackage\";");
	    leftScript.add("o2.ownedParameters.add(p3);");
	    leftScript.add("delete c2;");
	    leftScript.add("c3.ownedOperations.add(o3);");
	    leftScript.add("\"Left Script has been successfully executed\".println();");
	    //
	    rightScript.add("var i = InterfaceRealization.allInstances.selectOne(x | x.name == \"InterfaceB\");");
	    rightScript.add("var c = Class.allInstances.selectOne(x | x.name == \"ClassD\");");
	    rightScript.add("var c2 = Class.allInstances.selectOne(x | x.name == \"ClassH\");");
	    rightScript.add("var o = Operation.allInstances.selectOne(x | x.name == \"OperationE\");");
	    rightScript.add("var p = Parameter.allInstances.selectOne(x | x.name == \"ParamG\");");
	    rightScript.add("var p2 = Package.allInstances.selectOne(x | x.name == \"RootPackage\");");
	    rightScript.add("var o2 = Operation.allInstances.selectOne(x | x.name == \"OperationJ\");");
	    rightScript.add("var p3 = Parameter.allInstances.selectOne(x | x.name == \"ParamI\");");
	    rightScript.add("var o3 = Operation.allInstances.selectOne(x | x.name == \"OperationM\");");
	    rightScript.add("c.interfaceRealizations.add(i);");
	    rightScript.add("o.ownedParameters.add(p);");
	    rightScript.add("c2.name = \"ExClassH\";");
	    rightScript.add("p2.name = \"Package\";");
	    rightScript.add("delete p3;");
	    rightScript.add("c.ownedOperations.add(o3);");
	    rightScript.add("\"Right Script has been successfully executed\".println();");

	    originalScript.run("ORIGIN");
	    originalScript.save(null);

	    Files.copy(cbpOriginalFile.toPath(), cbpLeftFile.toPath());
	    Files.copy(cbpOriginalFile.toPath(), cbpRightFile.toPath());
	    cbpLeftResource.load(null);
	    cbpRightResource.load(null);

	    leftScript.run("LEFT");
	    leftScript.save(null);

	    EObject x = cbpLeftResource.getContents().get(0);

	    rightScript.run("RIGHT");
	    rightScript.save(null);

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

	    doCbpComparison(cbpLeftFile, cbpRightFile, cbpOriginalFile);

	    doThreeWayComparison(xmiLeftResource, xmiRightResource, xmiOriginalResource);

	    cbpOriginalResource.unload();
	    cbpLeftResource.unload();
	    cbpRightResource.unload();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    @Test
    public void testConflicts() {
	try {
	    ePackage = NodePackage.eINSTANCE;

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
	    originalScript.add("node0.valNodes.add(node1);");
	    originalScript.add("node0.valNodes.add(node2);");
	    originalScript.add("node0.valNodes.add(node3);");
	    // originalScript.add("node0.valNodes.add(node5);");

	    leftScript.add("var node0 = Node.allInstances.selectOne(org.eclipse.epsilon.cbp.comparison.model.node | org.eclipse.epsilon.cbp.comparison.model.node.name == \"Node 0\");");
	    leftScript.add("node0.name = \"Node L0\";");
	    leftScript.add("var node4 = new Node;");
	    leftScript.add("node4.name = \"Node L1\";");
	    leftScript.add("node0.valNodes.add(node4);");

	    rightScript.add("var node0 = Node.allInstances.selectOne(org.eclipse.epsilon.cbp.comparison.model.node | org.eclipse.epsilon.cbp.comparison.model.node.name == \"Node 0\");");
	    rightScript.add("node0.name = \"Node R0\";");
	    rightScript.add("var node4 = new Node;");
	    rightScript.add("node4.name = \"Node R1\";");
	    rightScript.add("node0.valNodes.add(node4);");

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

	    Files.copy(cbpRightFile.toPath(), changeCbpTargetFile.toPath());
	    Files.copy(xmiRightFile.toPath(), changeXmiTargetFile.toPath());
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

	    List<CBPConflict> conflicts = doCbpComparison(cbpLeftFile, changeCbpTargetFile, cbpOriginalFile);
	    Set<CBPChangeEvent<?>> eventSet = new LinkedHashSet<>();
	    for (CBPConflict conflict : conflicts) {
		eventSet.addAll(conflict.getRightEvents());
	    }
	    List<CBPChangeEvent<?>> sortedEvents = new ArrayList<>(eventSet);
	    Collections.sort(sortedEvents, new CBPChangeEventSortComparator());

	    doThreeWayComparison(xmiLeftResource, stateXmiTargetResource, xmiOriginalResource);
	    stateXmiTargetResource.save(options);

	    cbpOriginalResource.unload();
	    cbpLeftResource.unload();
	    cbpRightResource.unload();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    @Test
    public void testCoflictCoverage() {
	try {

	    List<String> operations = new ArrayList<>(Arrays.asList("DELETE", "ADD", "REMOVE", "SET", "UNSET", "MOVE"));
	    List<String> targets = new ArrayList<>(Arrays.asList("X", "Y"));
	    List<String> features = new ArrayList<>(Arrays.asList("refNodes", "valNodes", "refNode", "valNode", "deep", "values"));
	    List<String> valObjects = new ArrayList<>(Arrays.asList("A", "B", "Y", "X"));
	    List<String> indexes = new ArrayList<>(Arrays.asList("" + 0, "" + 2));
	    List<String> valLiterals = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));

	    for (String leftOperation : operations) {
		for (String rightOperation : operations) {
		    for (String leftTarget : targets) {
			for (String rightTarget : targets) {
			    for (String leftFeature : features) {
				for (String rightFeature : features) {
				    for (String leftValObject : valObjects) {
					for (String rightValObject : valObjects) {

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

					    originalScript = new Script(cbpOriginalResource, xmiOriginalResource);
					    originalScript.add("var x = new Node;");
					    originalScript.add("x.name = \"X\";");
					    originalScript.add("var y = new Node;");
					    originalScript.add("y.name = \"Y\";");
					    originalScript.add("var z = new Node;");
					    originalScript.add("z.name = \"Z\";");
					    originalScript.add("var a = new Node;");
					    originalScript.add("a.name = \"A\";");
					    originalScript.add("var b = new Node;");
					    originalScript.add("b.name = \"B\";");
					    originalScript.add("var c = new Node;");
					    originalScript.add("c.name = \"C\";");
					    originalScript.add("var d = new Node;");
					    originalScript.add("d.name = \"D\";");
					    originalScript.add("var e = new Node;");
					    originalScript.add("e.name = \"E\";");
					    originalScript.add("var f = new Node;");
					    originalScript.add("f.name = \"F\";");
					    originalScript.add("var g = new Node;");
					    originalScript.add("g.name = \"G\";");
					    originalScript.add("var h = new Node;");
					    originalScript.add("h.name = \"H\";");
					    originalScript.add("z.valNodes.add(a);");
					    originalScript.add("z.valNodes.add(b);");
					    originalScript.add("x.refNodes.add(a);");
					    originalScript.add("x.refNodes.add(b);");
					    originalScript.add("y.refNodes.add(a);");
					    originalScript.add("y.refNodes.add(b);");
					    originalScript.add("x.valNodes.add(c);");
					    originalScript.add("x.valNodes.add(d);");
					    originalScript.add("x.valNodes.add(e);");
					    originalScript.add("y.valNodes.add(f);");
					    originalScript.add("y.valNodes.add(g);");
					    originalScript.add("y.valNodes.add(h);");

					    leftScript = new Script(cbpLeftResource, xmiLeftResource);
					    rightScript = new Script(cbpRightResource, xmiRightResource);

					    leftScript.add("var node0 = Node.allInstances.selectOne(org.eclipse.epsilon.cbp.comparison.model.node | org.eclipse.epsilon.cbp.comparison.model.node.name == \"" + leftTarget + "\");");
					    if (leftOperation.equals("DELETE")) {
						leftScript.add("delete node0;");
					    } else if (leftOperation.equals("ADD")) {
						if (leftFeature.equals("valNodes") || leftFeature.equals("refNodes")) {
						    leftScript.add("var node1 = Node.allInstances.selectOne(org.eclipse.epsilon.cbp.comparison.model.node | org.eclipse.epsilon.cbp.comparison.model.node.name == \"" + leftValObject + "\");");
						    leftScript.add("node0." + leftFeature + ".add(node1);");
						}
					    }

					    rightScript.add("var node0 = Node.allInstances.selectOne(org.eclipse.epsilon.cbp.comparison.model.node | org.eclipse.epsilon.cbp.comparison.model.node.name == \"" + rightTarget + "\");");
					    if (rightOperation.equals("DELETE")) {
						rightScript.add("delete node0;");
					    } else if (rightOperation.equals("ADD")) {
						if (rightFeature.equals("valNodes") || rightFeature.equals("refNodes")) {
						    rightScript.add("var node1 = Node.allInstances.selectOne(org.eclipse.epsilon.cbp.comparison.model.node | org.eclipse.epsilon.cbp.comparison.model.node.name == \"" + rightValObject + "\");");
						    rightScript.add("node0." + rightFeature + ".add(node1);");
						}
					    }

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

					    // // -------------------------
					    // System.out.println("ORIGIN:");
					    // for (String line :
					    // Files.readAllLines(cbpOriginalFile.toPath()))
					    // {
					    // System.out.println(line);
					    // }
					    // System.out.println();
					    // System.out.println("RIGHT:");
					    // for (String line :
					    // Files.readAllLines(cbpRightFile.toPath()))
					    // {
					    // System.out.println(line);
					    // }
					    // System.out.println();
					    // System.out.println("LEFT:");
					    // for (String line :
					    // Files.readAllLines(cbpLeftFile.toPath()))
					    // {
					    // System.out.println(line);
					    // }

					    System.out.println("\nLEFT:");
					    System.out.println(leftScript.toString());

					    System.out.println("\nRIGHT:");
					    System.out.println(rightScript.toString());

					    System.out.println();
					    System.out.println("DIFFS:");
					    EList<Conflict> conflicts = doThreeWayComparison(xmiLeftResource, xmiRightResource, xmiOriginalResource);
					    System.out.println();

					    cbpOriginalResource.unload();
					    cbpLeftResource.unload();
					    cbpRightResource.unload();

					}
				    }
				}
			    }
			}
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    @Test
    public void testBatchConflicts() {

	int result = 0;

	try {
	    String dir = "Epsilon";
	    int startFrom = 10; // min 1
	    // problems:
	    // null pointer while merging: 57, 58
	    int caseNum = 10; // max 68

	    for (int i = startFrom; i <= caseNum; i++) {

		System.out.println("\nMODEL " + i + "---------------------------");

		File originFile = new File("D:\\TEMP\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\origin.cbpxml");
		File leftFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\left.cbpxml");
		File rightFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\right.cbpxml");
		File originXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\origin.xmi");
		File leftXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\left.xmi");
		File rightXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\right.xmi");
		File targetXmiFile = new File("D:\\\\TEMP\\\\FASE\\" + dir + File.separator + "data" + File.separator + i + "\\target.xmi");

		// do cbp comparison
		doCbpComparison(leftFile, rightFile, originFile);

		XMIResource originXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(originXmiFile.getAbsolutePath()));
		originXmi.load(options);
		XMIResource targetXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));
		targetXmi.load(options);

		XMIResource rightXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
		rightXmi.load(options);
		XMIResource leftXmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
		leftXmi.load(options);

		// do emf comparison
		doThreeWayComparison(leftXmi, rightXmi, originXmi);

		targetXmi.unload();
		leftXmi.unload();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
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
    private List<CBPConflict> doCbpComparison(File cbpLeftFile, File cbpRightFile, File cbpOriginalFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	ICBPComparison comparison = new CBPComparisonImpl();
	comparison.setDiffEMFCompareFile(new File(cbpOriginalFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
	comparison.setObjectTreeFile(new File(cbpOriginalFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
	comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
	comparison.compare(cbpLeftFile, cbpRightFile, cbpOriginalFile);
	return comparison.getConflicts();
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
	Iterator<Diff> iterator = evalDiffs.iterator();
	while (iterator.hasNext()) {
	    Diff x = iterator.next();
	    if (x instanceof MultiplicityElementChange || x instanceof AssociationChange || x instanceof DirectedRelationshipChange) {
		iterator.remove();
	    }
	}
	// printEMFCompareDiffs(leftXmi, rightXmi, evalDiffs, emfComparison);
	printConflicts(leftXmi, rightXmi, originXmi, emfComparison.getConflicts());

	IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
	UMLMerger umlMerger = new UMLMerger();
	umlMerger.setRanking(11);
	registry.add(umlMerger);
	IBatchMerger batchMerger = new BatchMerger(registry);
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
