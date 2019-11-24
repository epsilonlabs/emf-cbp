package org.eclipse.epsilon.cbp.conflict.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.emfstore.common.ESSystemOutProgressMonitor;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.epsilon.cbp.bigmodel.ModifiedEMFCompare;
import org.eclipse.epsilon.cbp.bigmodel.ModifiedEMFCompare.ModifiedBuilder;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodeFactory;
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
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.ConnectorKind;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;

public class CBPConflictTest {

    private List<String> ecbpConflicts = new ArrayList<String>();
    private List<String> emfcConflicts = new ArrayList<String>();

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
    public void testThesisConflicts() {
	try {

	    EStructuralFeature feature = NodeFactory.eINSTANCE.createNode().eClass().getEStructuralFeature("valNodes");
	    boolean isOrdered = feature.isOrdered();

	    // EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(),
	    // NodePackage.eINSTANCE);
	    // ePackage = (EPackage)
	    // EPackage.Registry.INSTANCE.get(NodePackage.eINSTANCE.getNsURI());
	    // EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(),
	    // NodePackage.eINSTANCE);
	    // ePackage = (EPackage)
	    // EPackage.Registry.INSTANCE.get(JavaPackage.eINSTANCE.getNsURI());

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

	    originalScript.add("var root = new Node;");
	    originalScript.add("root.name = \"ROOT\";");
	    originalScript.add("var character = new Node;");
	    originalScript.add("character.name = \"Character\";");
	    originalScript.add("var attack = new Node;");
	    originalScript.add("attack.name = \"attack\";");
	    originalScript.add("var gem = new Node;");
	    originalScript.add("gem.name = \"gem\";");
	    originalScript.add("var target = new Node;");
	    originalScript.add("target.name = \"target\";");
	    originalScript.add("var weapon = new Node;");
	    originalScript.add("weapon.name = \"weapon\";");
	    originalScript.add("character.valNodes.add(attack);");
	    originalScript.add("attack.valNodes.add(gem);");
	    originalScript.add("attack.valNodes.add(target);");
	    originalScript.add("attack.valNodes.add(weapon);");

	    originalScript.add("var troll = new Node;");
	    originalScript.add("troll.name = \"Troll\";");

	    originalScript.add("var giant = new Node;");
	    originalScript.add("giant.name = \"Giant\";");
	    originalScript.add("var cast = new Node;");
	    originalScript.add("cast.name = \"cast\";");
	    originalScript.add("giant.valNodes.add(cast);");

	    originalScript.add("var knight = new Node;");
	    originalScript.add("knight.name = \"Knight\";");
	    originalScript.add("var smash = new Node;");
	    originalScript.add("smash.name = \"smash\";");
	    originalScript.add("knight.valNodes.add(smash);");

	    originalScript.add("var mage = new Node;");
	    originalScript.add("mage.name = \"Mage\";");

	    originalScript.add("root.valNodes.add(character);");
	    originalScript.add("root.valNodes.add(troll);");
	    originalScript.add("root.valNodes.add(giant);");
	    originalScript.add("root.valNodes.add(knight);");
	    originalScript.add("root.valNodes.add(mage);");

	    // left script
	    leftScript.add("var root = Node.allInstances.selectOne(node | node.name == \"ROOT\");");
	    leftScript.add("var character = Node.allInstances.selectOne(node | node.name == \"Character\");");
	    leftScript.add("var troll = Node.allInstances.selectOne(node | node.name == \"Troll\");");
	    leftScript.add("var knight = Node.allInstances.selectOne(node | node.name == \"Knight\");");
	    leftScript.add("var attack = Node.allInstances.selectOne(node | node.name == \"attack\");");
	    leftScript.add("var giant = Node.allInstances.selectOne(node | node.name == \"Giant\");");
	    leftScript.add("var leftGeneral = new Node;");
	    leftScript.add("leftGeneral.name = \"Left Generalisation\";");
	    leftScript.add("troll.valNode = leftGeneral;");
	    leftScript.add("leftGeneral.refNode = character;");
	    leftScript.add("character.name = \"Hero\";");
	    leftScript.add("knight.valNode = leftGeneral;");
	    leftScript.add("attack.valNodes.move(2,1);");
	    leftScript.add("delete giant;");
	    leftScript.add("troll.name = \"Ogre\";");

	    // right script
	    rightScript.add("var root = Node.allInstances.selectOne(node | node.name == \"ROOT\");");
	    rightScript.add("var character = Node.allInstances.selectOne(node | node.name == \"Character\");");
	    rightScript.add("var troll = Node.allInstances.selectOne(node | node.name == \"Troll\");");
	    rightScript.add("var mage = Node.allInstances.selectOne(node | node.name == \"Mage\");");
	    rightScript.add("var attack = Node.allInstances.selectOne(node | node.name == \"attack\");");
	    rightScript.add("var smash = Node.allInstances.selectOne(node | node.name == \"smash\");");
	    rightScript.add("var giant = Node.allInstances.selectOne(node | node.name == \"Giant\");");
	    rightScript.add("var knight = Node.allInstances.selectOne(node | node.name == \"Knight\");");
	    rightScript.add("var cast = Node.allInstances.selectOne(node | node.name == \"cast\");");
	    rightScript.add("attack.valNodes.move(0,1);");
	    rightScript.add("giant.valNodes.add(smash);");
	    rightScript.add("mage.valNodes.add(cast);");
	    rightScript.add("var rightGeneral = new Node;");
	    rightScript.add("rightGeneral.name = \"Right Generalisation\";");
	    rightScript.add("troll.valNode = rightGeneral;");
	    rightScript.add("rightGeneral.refNode = character;");
	    rightScript.add("character.name = \"Hero\";");
	    rightScript.add("mage.valNode = rightGeneral;");
	    rightScript.add("troll.name = \"Orc\";");

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
	    // sortResourceElements(changeXmiTargetResource);
	    changeXmiTargetResource.save(options);

	    System.out.println("Sort target model of state-based comparison");
	    stateXmiTargetResource.load(options);
	    // sortResourceElements(stateXmiTargetResource);
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
    public void testSoSymConflicts() {
	try {
	    // EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(),
	    // NodePackage.eINSTANCE);
	    // ePackage = (EPackage)
	    // EPackage.Registry.INSTANCE.get(NodePackage.eINSTANCE.getNsURI());
	    EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	    // EPackage.Registry.INSTANCE.put(JavaPackage.eINSTANCE.getNsURI(),
	    // JavaPackage.eINSTANCE);
	    EPackage.Registry.INSTANCE.put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);
	    ePackage = (EPackage) EPackage.Registry.INSTANCE.get(UMLPackage.eINSTANCE.getNsURI());
	    UMLFactory eFactory = UMLFactory.eINSTANCE;

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

	    // Class x = eFactory.createClass();
	    // x.set

	    originalScript.add("var character = new Class;");
	    originalScript.add("character.name = \"Character\";");
	    originalScript.add("var attack = new Operation;");
	    originalScript.add("attack.name = \"attack\";");
	    originalScript.add("var gem = new Parameter;");
	    originalScript.add("gem.name = \"gem\";");
	    originalScript.add("var target = new Parameter;");
	    originalScript.add("target.name = \"target\";");
	    originalScript.add("var weapon = new Parameter;");
	    originalScript.add("weapon.name = \"weapon\";");
	    originalScript.add("character.ownedOperations.add(attack);");
	    originalScript.add("attack.ownedParameters.add(gem);");
	    originalScript.add("attack.ownedParameters.add(target);");
	    originalScript.add("attack.ownedParameters.add(weapon);");

	    originalScript.add("var troll = new Class;");
	    originalScript.add("troll.name = \"Troll\";");

	    originalScript.add("var giant = new Class;");
	    originalScript.add("giant.name = \"Giant\";");
	    originalScript.add("var cast = new Operation;");
	    originalScript.add("cast.name = \"cast\";");
	    originalScript.add("giant.ownedOperations.add(cast);");

	    originalScript.add("var knight = new Class;");
	    originalScript.add("knight.name = \"Knight\";");
	    originalScript.add("var smash = new Operation;");
	    originalScript.add("smash.name = \"smash\";");
	    originalScript.add("knight.ownedOperations.add(smash);");

	    originalScript.add("var mage = new Class;");
	    originalScript.add("mage.name = \"Mage\";");

	    // left script
	    leftScript.add("var character = Class.allInstances.selectOne(node | node.name == \"Character\");");
	    leftScript.add("var troll = Class.allInstances.selectOne(node | node.name == \"Troll\");");
	    leftScript.add("var knight = Class.allInstances.selectOne(node | node.name == \"Knight\");");
	    leftScript.add("var attack = Operation.allInstances.selectOne(node | node.name == \"attack\");");
	    leftScript.add("var giant = Class.allInstances.selectOne(node | node.name == \"Giant\");");
	    leftScript.add("var leftGeneral = new Generalization;");
	    leftScript.add("troll.generalizations.add(leftGeneral);");
	    leftScript.add("leftGeneral.general = character;");
	    leftScript.add("character.name = \"Hero\";");
	    leftScript.add("knight.generalizations.add(leftGeneral);");
	    leftScript.add("attack.ownedParameters.move(2,1);");
	    leftScript.add("delete giant;");
	    leftScript.add("troll.name = \"Ogre\";");

	    // right script
	    rightScript.add("var character = Class.allInstances.selectOne(node | node.name == \"Character\");");
	    rightScript.add("var troll = Class.allInstances.selectOne(node | node.name == \"Troll\");");
	    rightScript.add("var mage = Class.allInstances.selectOne(node | node.name == \"Mage\");");
	    rightScript.add("var attack = Operation.allInstances.selectOne(node | node.name == \"attack\");");
	    rightScript.add("var smash = Operation.allInstances.selectOne(node | node.name == \"smash\");");
	    rightScript.add("var giant = Class.allInstances.selectOne(node | node.name == \"Giant\");");
	    rightScript.add("var knight = Class.allInstances.selectOne(node | node.name == \"Knight\");");
	    rightScript.add("var cast = Operation.allInstances.selectOne(node | node.name == \"cast\");");
	    rightScript.add("attack.ownedParameters.move(0,1);");
	    rightScript.add("giant.ownedOperations.add(smash);");
	    rightScript.add("mage.ownedOperations.add(cast);");
	    rightScript.add("var rightGeneral = new Generalization;");
	    rightScript.add("troll.generalizations.add(rightGeneral);");
	    rightScript.add("rightGeneral.general = character;");
	    rightScript.add("character.name = \"Hero\";");
	    rightScript.add("mage.generalizations.add(rightGeneral);");
	    rightScript.add("troll.name = \"Orc\";");

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
	    // sortResourceElements(changeXmiTargetResource);
	    changeXmiTargetResource.save(options);

	    System.out.println("Sort target model of state-based comparison");
	    stateXmiTargetResource.load(options);
	    // sortResourceElements(stateXmiTargetResource);
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

	    originalScript.add("var root = new Node;");
	    originalScript.add("root.name = \"ROOT\";");
	    originalScript.add("var nodeA = new Node;");
	    originalScript.add("nodeA.name = \"A\";");
	    originalScript.add("var nodeB = new Node;");
	    originalScript.add("nodeB.name = \"B\";");
	    originalScript.add("var nodeC = new Node;");
	    originalScript.add("nodeC.name = \"C\";");
	    originalScript.add("var nodeD = new Node;");
	    originalScript.add("nodeD.name = \"D\";");
	    originalScript.add("var nodeE = new Node;");
	    originalScript.add("nodeE.name = \"E\";");
	    originalScript.add("var nodeF = new Node;");
	    originalScript.add("nodeF.name = \"F\";");
	    originalScript.add("root.valNodes.add(nodeA);");
	    originalScript.add("root.valNodes.add(nodeB);");
	    originalScript.add("root.valNodes.add(nodeC);");
	    originalScript.add("root.valNodes.add(nodeD);");
	    originalScript.add("nodeB.valNode = nodeE;");
	    // originalScript.add("root.valNodes.add(nodeE);");

	    // left script
	    leftScript.add("var nodeA = Node.allInstances.selectOne(node | node.name == \"A\");");
	    leftScript.add("var nodeB = Node.allInstances.selectOne(node | node.name == \"B\");");
	    leftScript.add("var nodeC = Node.allInstances.selectOne(node | node.name == \"C\");");
	    leftScript.add("var nodeD = Node.allInstances.selectOne(node | node.name == \"D\");");
	    leftScript.add("var nodeE = Node.allInstances.selectOne(node | node.name == \"E\");");
	    leftScript.add("var nodeF = Node.allInstances.selectOne(node | node.name == \"F\");");
	    leftScript.add("delete nodeE;");
	    // leftScript.add("nodeA.valNode = new Node;");

	    // right script
	    rightScript.add("var nodeA = Node.allInstances.selectOne(node | node.name == \"A\");");
	    rightScript.add("var nodeB = Node.allInstances.selectOne(node | node.name == \"B\");");
	    rightScript.add("var nodeC = Node.allInstances.selectOne(node | node.name == \"C\");");
	    rightScript.add("var nodeD = Node.allInstances.selectOne(node | node.name == \"D\");");
	    rightScript.add("var nodeE = Node.allInstances.selectOne(node | node.name == \"E\");");
	    rightScript.add("var nodeF = Node.allInstances.selectOne(node | node.name == \"F\");");
	    rightScript.add("nodeD.valNode = nodeE;");
	    rightScript.add("nodeC.valNode = nodeA;");
	    rightScript.add("nodeB.valNode = nodeA;");

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
	    doThreeWayComparison(xmiLeftResource, xmiRightResource, null);

	    System.out.println("\n\nCBP:");
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
    public void testManualConflicts() {
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

	    NodeFactory factory = NodeFactory.eINSTANCE;
	    Node rootNode1 = factory.createNode();
	    rootNode1.setName("ROOT1");
	    Node rootNode2 = factory.createNode();
	    rootNode2.setName("ROOT2");
	    cbpOriginalResource.getContents().add(rootNode1);
	    cbpOriginalResource.getContents().add(rootNode2);

	    Node nodeA = factory.createNode();
	    nodeA.setName("A");
	    Node nodeB = factory.createNode();
	    nodeB.setName("B");
	    Node nodeC = factory.createNode();
	    nodeC.setName("C");
	    Node nodeD = factory.createNode();
	    nodeD.setName("D");
	    rootNode1.getValNodes().add(nodeA);
	    rootNode2.getValNodes().add(nodeB);
	    rootNode2.getValNodes().add(nodeC);
	    nodeD.setParent(nodeA);
	    nodeB.setRefNode(nodeA);

	    String aId = cbpOriginalResource.getEObjectId(nodeA);
	    String bId = cbpOriginalResource.getEObjectId(nodeB);
	    String cId = cbpOriginalResource.getEObjectId(nodeC);
	    String dId = cbpOriginalResource.getEObjectId(nodeD);
	    String root2Id = cbpOriginalResource.getEObjectId(rootNode2);

	    cbpOriginalResource.save(options);
	    exportToXmi(cbpOriginalResource, xmiOriginalResource);
	    Files.copy(cbpOriginalFile.toPath(), cbpLeftFile.toPath());
	    Files.copy(cbpOriginalFile.toPath(), cbpRightFile.toPath());
	    cbpLeftResource.load(null);
	    cbpRightResource.load(null);

	    // process right file first
	    System.out.println("LOADING RIGHT MODEL");
	    cbpRightResource.startNewSession("RIGHT");
	    nodeA = (Node) cbpRightResource.getEObject(aId);
	    nodeD = (Node) cbpRightResource.getEObject(dId);
	    cbpRightResource.startCompositeEvent();
	    EcoreUtil.delete(nodeD);
	    // nodeD.setParent(null);
	    cbpRightResource.endCompositeEvent();
	    cbpRightResource.startCompositeEvent();
	    EcoreUtil.delete(nodeA);
	    cbpLeftResource.endCompositeEvent();

	    // Node nodeX = factory.createNode();
	    // nodeX.setName("X");
	    // nodeA.setValNode(nodeX);

	    cbpRightResource.save(options);
	    exportToXmi(cbpRightResource, xmiRightResource);

	    // process left
	    System.out.println("LOADING LEFT MODEL");
	    cbpLeftResource.startNewSession("LEFT");
	    nodeB = (Node) cbpLeftResource.getEObject(bId);
	    nodeD = (Node) cbpLeftResource.getEObject(dId);
	    cbpLeftResource.startCompositeEvent();
	    EcoreUtil.delete(nodeD);
	    // nodeD.setParent(null);
	    cbpLeftResource.endCompositeEvent();
	    cbpLeftResource.startCompositeEvent();
	    EcoreUtil.delete(nodeB);
	    cbpLeftResource.endCompositeEvent();

	    // Node nodeY = factory.createNode();
	    // nodeY.setName("Y");
	    // nodeA.setValNode(nodeY);

	    cbpLeftResource.save(options);
	    exportToXmi(cbpLeftResource, xmiLeftResource);

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
	    doThreeWayComparison(xmiLeftResource, xmiRightResource, null);

	    System.out.println("\nCBP:");
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

		Files.copy(cbpLeftFile.toPath(), changeCbpTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(xmiLeftFile.toPath(), changeXmiTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(xmiRightFile.toPath(), stateXmiTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		// do cbp comparison
		ICBPComparison comparison = doCbpComparison(cbpLeftFile, cbpRightFile, cbpOriginalFile);

		// // do cbp merging
		// doCbpMerging(comparison);

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
		ModifiedEMFCompare emfCompare = doThreeWayComparison(leftXmi, targetStateXmi, originXmi);
		targetStateXmi.save(options);

		// System.out.println();
		// System.out.println("Sort target model of change-based
		// comparison");
		// targetChangeXmi.load(options);
		// CBPComparisonUtil.sort(targetChangeXmi);
		// targetChangeXmi.save(options);
		//
		// System.out.println("Sort target model of state-based
		// comparison");
		// targetStateXmi.load(options);
		// CBPComparisonUtil.sort(targetStateXmi);
		// targetStateXmi.save(options);

		// EList<Diff> evalDiffs =
		// compareCBPvsXMITargets(targetChangeXmi, targetStateXmi);

		ecbpConflicts = new ArrayList<String>(comparison.getConflictStrings());

		System.out.println();
		System.out.println("CBP SINGLE CONFLICTS");
		List<String> listEcbpConflicts = new LinkedList<String>(ecbpConflicts);
		Collections.sort(listEcbpConflicts);
		for (String tempStr : listEcbpConflicts) {
		    System.out.println(tempStr);
		}
		System.out.println("Size = " + listEcbpConflicts.size());
		System.out.println();
		// ---------------

		targetStateXmi.unload();
		leftXmi.unload();
		rightXmi.unload();
		targetChangeXmi.unload();

		System.out.println("");
		System.out.println("UNDETECTED BY EMF COMPARE");
		emfcConflicts = new ArrayList<String>(emfCompare.getEmfcConflicts());
		ecbpConflicts.removeAll(emfcConflicts);
		Collections.sort(ecbpConflicts);
		int j = 1;
		for (String conflict : ecbpConflicts) {
		    System.out.println(j + ": " + conflict);
		    j++;
		}

		System.out.println("");
		System.out.println("UNDETECTED BY CBP");
		emfcConflicts.removeAll(comparison.getConflictStrings());
		Collections.sort(emfcConflicts);
		j = 1;
		for (String conflict : emfcConflicts) {
		    System.out.println(j + ": " + conflict);
		    j++;
		}
		System.console();
	    }

	    System.out.println();
	    analyseUndetectedByEcbpConflicts(emfcConflicts);
	    System.out.println();
	    analyseUndetectedByEmfcConflicts(ecbpConflicts);
	    System.console();

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    private void analyseUndetectedByEcbpConflicts(List<String> undetectedByEcbp) {
	// conflicts caused by derived moves
	List<String> derivedMoves = new ArrayList<>();

	// this one should be detected by ECBP, however since conflicts are not
	// in one group (one conflict), seperated into different conflicts, it
	// cannot be straightforwardly detected
	List<String> conflictByDeletion = new ArrayList<>();

	// others unhandled conflicts
	List<String> otherUnhandledConflicts = new ArrayList<>();

	for (String line : undetectedByEcbp) {
	    String[] segments = line.split(" <-> ");
	    String left = segments[0];
	    String right = segments[1];

	    if ((left.contains("MOVE") && left.contains("IN")) || (right.contains("MOVE") && right.contains("IN"))) {
		derivedMoves.add(line);
	    } else
	    //
	    if (left.contains("DELETE") || right.contains("DELETE")) {
		conflictByDeletion.add(line);
	    } else
	    //
	    if (left.contains("SET") && right.contains("SET")) {
		conflictByDeletion.add(line);
	    } else
	    //
	    {
		otherUnhandledConflicts.add(line);
		System.out.println("unhandled by ecbp: " + line);
		System.console();
	    }
	}

	// if (derivedMoves.size() + conflictByDeletion.size() ==
	// undetectedByEcbp.size()) {
	System.out.println();
	System.out.println("DETAILS OF UNDETECTED CONFLICTS BY CBP");
	System.out.println("Total dirty conflicts = " + undetectedByEcbp.size());
	System.out.println("Derived Moves = " + derivedMoves.size());
	System.out.println("Conflicts by deletion = " + conflictByDeletion.size());
	System.out.println("Other conflicts = " + otherUnhandledConflicts.size());
	System.out.println("Conflicts by non-deletion = " + (derivedMoves.size() + otherUnhandledConflicts.size()));
	System.out.println("Real undentected conflicts = " + (undetectedByEcbp.size() - conflictByDeletion.size()));
	// }

    }

    private void analyseUndetectedByEmfcConflicts(List<String> undetectedByEcbp) {
	// conflicts caused by real moves
	List<String> realMoves = new ArrayList<>();

	// conflicts caused by real moves
	List<String> resetToOriginalConflicts = new ArrayList<>();

	// assigned a value to container left by item moved to another container
	List<String> modifySingleValueContainmentConflicts = new ArrayList<>();

	// this one should be detected by ECBP, however since conflicts are not
	// in one group (one conflict), seperated into different conflicts, it
	// cannot be straightforwardly detected
	List<String> conflictByDeletion = new ArrayList<>();

	// others unhandled conflicts
	List<String> otherUnhandledConflicts = new ArrayList<>();

	for (String line : undetectedByEcbp) {
	    String[] segments = line.split(" <-> ");
	    String left = segments[0];
	    String right = segments[1];

	    if ((left.contains("MOVE") && left.contains("IN")) || (right.contains("MOVE") && right.contains("IN"))) {
		realMoves.add(line);
	    } else
	    //
	    if ((left.contains("SET")) && (right.contains("SET"))) {
		resetToOriginalConflicts.add(line);
	    } else
	    //
	    if ((left.contains("SET")) || (right.contains("SET"))) {
		String[] leftSegments = left.split(" TO ");
		String[] rightSegments = right.split(" TO ");

		if (leftSegments[0].contains("true") && leftSegments[1].contains("true")) {
		    resetToOriginalConflicts.add(line);
		} else if (leftSegments[0].contains("false") && leftSegments[1].contains("false")) {
		    resetToOriginalConflicts.add(line);
		} else if (rightSegments[0].contains("true") && rightSegments[1].contains("true")) {
		    resetToOriginalConflicts.add(line);
		} else if (rightSegments[0].contains("false") && rightSegments[1].contains("false")) {
		    resetToOriginalConflicts.add(line);
		} else if (left.contains("DELETE") || right.contains("DELETE")) {
		    conflictByDeletion.add(line);
		} else if ((left.contains("ACROSS") || left.contains("SET")) && (right.contains("ACROSS") || right.contains("SET"))) {
		    modifySingleValueContainmentConflicts.add(line);
		} else {
		    otherUnhandledConflicts.add(line);
		    System.out.println("unhandled by emfc: " + line);
		    System.console();
		}
	    } else
	    //
	    if (left.contains("DELETE") || right.contains("DELETE")) {
		conflictByDeletion.add(line);
	    } else
	    //
	    if ((left.contains("ACROSS") || left.contains("SET")) && (right.contains("ACROSS") || right.contains("SET"))) {
		modifySingleValueContainmentConflicts.add(line);
	    } else
	    //
	    {
		otherUnhandledConflicts.add(line);
		System.out.println("unhandled by emfc: " + line);
		System.console();
	    }
	    System.console();
	}

	// if (derivedMoves.size() + conflictByDeletion.size() ==
	// undetectedByEcbp.size()) {
	System.out.println();
	System.out.println("DETAILS OF UNDETECTED CONFLICTS BY EMF COMPARE");
	System.out.println("Total dirty conflicts = " + undetectedByEcbp.size());
	System.out.println("Derived Moves = " + realMoves.size());
	System.out.println("Reset-to-original conflicts = " + resetToOriginalConflicts.size());
	System.out.println("Conflicts by deletion = " + conflictByDeletion.size());
	System.out.println("Modify single-valued containment conflicts = " + modifySingleValueContainmentConflicts.size());
	System.out.println("Other conflicts = " + otherUnhandledConflicts.size());
	System.out.println("Conflicts by non-deletion = " + (realMoves.size() + resetToOriginalConflicts.size() + otherUnhandledConflicts.size() + modifySingleValueContainmentConflicts.size()));
	System.out.println("Real undentected conflicts = " + (undetectedByEcbp.size() - conflictByDeletion.size()));
	// }

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
    private ModifiedEMFCompare doThreeWayComparison(XMIResource leftXmi, XMIResource rightXmi, XMIResource originXmi) throws FileNotFoundException, IOException, Exception {
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

	ModifiedBuilder builder = ModifiedEMFCompare.modifiedBuilder();
	builder.setPostProcessorRegistry(postProcessorRegistry);
	builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
	ModifiedEMFCompare comparator = (ModifiedEMFCompare) builder.build();

	IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, originXmi);
	Comparison emfComparison = comparator.compare(scope);
	EList<Diff> diffs = emfComparison.getDifferences();
	comparator.printConflicts(leftXmi, rightXmi, originXmi, emfComparison.getConflicts());

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

	// batchMerger.copyAllLeftToRight(leftDiffs, new BasicMonitor());
	// // batchMerger.copyAllLeftToRight(rightDiffs, new BasicMonitor());
	//
	// TreeIterator<EObject> targetIterator = rightXmi.getAllContents();
	// while (targetIterator.hasNext()) {
	// EObject eObject = targetIterator.next();
	// String id = eObject2IdMap.get(eObject);
	// if (id != null) {
	// rightXmi.setID(eObject, id);
	// }
	// }

	return comparator;
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

	ModifiedBuilder builder = ModifiedEMFCompare.modifiedBuilder();
	builder.setPostProcessorRegistry(postProcessorRegistry);
	builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
	ModifiedEMFCompare comparator = (ModifiedEMFCompare) builder.build();

	IComparisonScope2 scope = new DefaultComparisonScope(leftXmi, rightXmi, null);
	Comparison emfComparison = comparator.compare(scope);
	EList<Diff> evalDiffs = emfComparison.getDifferences();

	comparator.printEMFCompareDiffs(leftXmi, rightXmi, evalDiffs);

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

    public void exportToXmi(CBPResource cbpResource, XMIResource xmiResource) {
	xmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
	TreeIterator<EObject> cbpIterator = cbpResource.getAllContents();
	TreeIterator<EObject> xmiIterator = xmiResource.getAllContents();
	while (cbpIterator.hasNext() && xmiIterator.hasNext()) {
	    String id = cbpResource.getURIFragment(cbpIterator.next());
	    xmiResource.setID(xmiIterator.next(), id);
	}
	try {
	    xmiResource.save(options);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
