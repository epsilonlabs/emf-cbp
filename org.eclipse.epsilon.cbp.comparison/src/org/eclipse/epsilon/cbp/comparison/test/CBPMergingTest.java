package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonOld;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.junit.After;
import org.junit.Test;

public class CBPMergingTest {

	public enum MergeMode {
		UpdateLeftWithAllLeftSolutions, UpdateLeftWithAllRightSolutions, UpdateRightWithAllLeftSolutions, UpdateRightWithAllRightSolutions
	}

	private File originCbpFile;
	private File leftCbpFile;
	private File rightCbpFile;
	private File originXmiFile;
	private File leftXmiFile;
	private File rightXmiFile;
	private File leftXmiFileNoId;
	private File rightXmiFileNoId;
	private HybridResource originResource;
	private HybridResource leftResource;
	private HybridResource rightResource;
	private XMIResource originXmiResource;
	private XMIResource leftXmiResource;
	private XMIResource rightXmiResource;
	private CBPComparisonOld cbpComparison;
	private File targetFile;
	private EPackage ePackage = NodePackage.eINSTANCE;
	private EolModule module;
	private InMemoryEmfModel model;
	private Script rightScript;
	private Script leftScript;
	private Script originalScript;
	private File targetCbpFile;
	private String actualValue;
	private String expectedValue;

	
	@Test
	public void testReadingFiles() throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
		File originFile = new File("D:\\TEMP\\COMPARISON\\origin.cbpxml");
		File leftFile = new File("D:\\TEMP\\COMPARISON\\left.cbpxml");
		File rightFile = new File("D:\\TEMP\\COMPARISON\\right.cbpxml");
		
		CBPComparisonOld comparison = new CBPComparisonOld(leftFile, rightFile);
		comparison.compare();
		
		assertEquals(true, true);
		
	}
	
	
	public void initialise(String testName) throws FileNotFoundException {
		System.out
				.println("\n###### " + testName + " ##############################################################\n");

		originCbpFile = new File("D:\\TEMP\\COMPARISON\\temp\\" + testName + "-root.cbpxml");
		if (originCbpFile.exists())
			originCbpFile.delete();
		leftCbpFile = new File("D:\\TEMP\\COMPARISON\\temp\\" + testName + "-left.cbpxml");
		if (leftCbpFile.exists())
			leftCbpFile.delete();
		rightCbpFile = new File("D:\\TEMP\\COMPARISON\\temp\\" + testName + "-right.cbpxml");
		if (rightCbpFile.exists())
			rightCbpFile.delete();

		originXmiFile = new File("D:\\TEMP\\COMPARISON\\origin.xmi");
		if (originXmiFile.exists())
			originCbpFile.delete();
		leftXmiFile = new File("D:\\TEMP\\COMPARISON\\left.xmi");
		if (leftXmiFile.exists())
			leftXmiFile.delete();
		rightXmiFile = new File("D:\\TEMP\\COMPARISON\\right.xmi");
		if (rightXmiFile.exists())
			rightXmiFile.delete();

		leftXmiFileNoId = new File("D:\\TEMP\\COMPARISON\\left-no-id.xmi");
		if (leftXmiFileNoId.exists())
			leftXmiFileNoId.delete();
		rightXmiFileNoId = new File("D:\\TEMP\\COMPARISON\\right-no-id.xmi");
		if (rightXmiFileNoId.exists())
			rightXmiFileNoId.delete();

		NodePackage.eINSTANCE.eClass();

		originXmiResource = (XMIResource) (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI(originXmiFile.getAbsolutePath()));
		originResource = new HybridXMIResourceImpl(originXmiResource, new FileOutputStream(originCbpFile, false));
		originalScript = new Script(originResource);

		leftXmiResource = (XMIResource) (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
		leftResource = new HybridXMIResourceImpl(leftXmiResource, new FileOutputStream(leftCbpFile, false));
		leftScript = new Script(leftResource);

		rightXmiResource = (XMIResource) (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
		rightResource = new HybridXMIResourceImpl(rightXmiResource, new FileOutputStream(rightCbpFile, false));
		rightScript = new Script(rightResource);
	}

	@After
	public void tearDown() throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
		// print results
		List<String> leftLines = Files.readAllLines(leftCbpFile.toPath());
		List<String> rightLines = Files.readAllLines(rightCbpFile.toPath());

		System.out.println("LEFT:");
		for (String line : leftLines) {
			System.out.println(line);
		}

		System.out.println();
		System.out.println("RIGHT:");
		for (String line : rightLines) {
			System.out.println(line);
		}

		System.out.println();
		System.out.println("CONFLICTS:");
		List<ConflictedEventPair> conflictedEventPairs = cbpComparison.getConflictedEventPairs();
		for (ConflictedEventPair pair : conflictedEventPairs) {
			System.out.println(pair.getLeftEvent().getEventString() + " != " + pair.getRightEvent().getEventString());
		}

		System.out.println();
		System.out.println("MERGED EVENTS:");
		for (ComparisonEvent event : cbpComparison.getMergedEvents()) {
			System.out.println(event.getEventString());
		}

		System.out.println();
		System.out.println("MERGED XMI:");
		System.out.println(actualValue);

		originResource.unload();
		leftResource.unload();
		rightResource.unload();

	}

	/**
	 * @throws IOException
	 */
	protected String getXMIString(File targetFile) throws IOException {
		// test reload
		Resource xmiMergedResource = (new XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
		HybridResource hybridResource = new HybridXMIResourceImpl(xmiMergedResource,
				new FileOutputStream(targetFile, true));
		hybridResource.loadFromCBP(new FileInputStream(targetFile));
		StringOutputStream output = new StringOutputStream();
		xmiMergedResource.save(output, null);
		String outputString = output.toString();
		// System.out.println(outputString);
		return outputString.trim();
	}

	public void findConflicts() {
		List<String> eventTypes = new ArrayList<>();
		eventTypes.add("var %s = new Node;");
		eventTypes.add("%s.%s = %s;");

		// eventTypes.add(CBPDeleteEObjectEvent.class);

		List<String> featureList = new ArrayList<>();
		featureList.add("defName");
		featureList.add("values");
		featureList.add("valNode");
		featureList.add("refNode");
		featureList.add("valNodes");
		featureList.add("refNodes");

		List<String> targetList = new ArrayList<>();
		targetList.add("node1");
		targetList.add("node6");

		List<String> objValueList1 = new ArrayList<>();
		objValueList1.add("node2");
		objValueList1.add("node3");
		objValueList1.add("node4");
		objValueList1.add("node5");

		List<String> objValueList2 = new ArrayList<>();
		objValueList2.add("node7");
		objValueList2.add("node8");
		objValueList2.add("node9");
		objValueList2.add("node10");

	}

	@Test
	public void testMoveBetweenTwoContainers() throws IOException {
		initialise("testGenerateTwoDifferentModels");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n"
				+ "  <Node name=\"Node A\"/>\r\n" + "  <Node name=\"Node 02\">\r\n"
				+ "    <valNodes name=\"Node 03\"/>\r\n" + "    <valNodes name=\"Node 04\"/>\r\n" + "  </Node>\r\n"
				+ "  <Node name=\"Node 05\"/>\r\n" + "</xmi:XMI>";
		try {
			// origin
			originalScript.add("var node1 = new Node;");
			originalScript.add("node1.name = \"Node 01\";");
			originalScript.add("var node2 = new Node;");
			originalScript.add("node2.name = \"Node 02\";");
			originalScript.add("var node3 = new Node;");
			originalScript.add("node3.name = \"Node 03\";");
			originalScript.add("node1.valNodes.add(node3);");
			originalScript.add("node1.valNodes.remove(node3);");
			originalScript.add("node2.valNodes.add(node3);");
	
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(actualValue, actualValue);
	}
	
	@Test
	public void testGenerateTwoDifferentModels() throws IOException {
		initialise("testGenerateTwoDifferentModels");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n"
				+ "  <Node name=\"Node A\"/>\r\n" + "  <Node name=\"Node 02\">\r\n"
				+ "    <valNodes name=\"Node 03\"/>\r\n" + "    <valNodes name=\"Node 04\"/>\r\n" + "  </Node>\r\n"
				+ "  <Node name=\"Node 05\"/>\r\n" + "</xmi:XMI>";
		try {
			// origin
			originalScript.add("var root = new Node;");
			originalScript.add("root.name = \"ROOT\";");
			originalScript.add("var node1 = new Node;");
			originalScript.add("node1.name = \"Node 01\";");
			originalScript.add("var node2 = new Node;");
			originalScript.add("node2.name = \"Node 02\";");
			originalScript.add("var node3 = new Node;");
			originalScript.add("node3.name = \"Node 03\";");
			originalScript.add("var node4 = new Node;");
			originalScript.add("node4.name = \"Node 04\";");
			originalScript.add("node2.valNodes.add(node3);");

			originalScript.add("var node5 = new Node;");
			originalScript.add("node5.name = \"Node 05\";");
			originalScript.add("var node6 = new Node;");
			originalScript.add("node6.name = \"Node 06\";");
			
			originalScript.add("root.valNodes.add(node1);");
			originalScript.add("root.valNodes.add(node2);");
			originalScript.add("root.valNodes.add(node4);");
			originalScript.add("root.valNodes.add(node5);");
			originalScript.add("root.valNodes.add(node6);");
			
			// left
			leftScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("node1.name = \"Node A\";");
			leftScript.add("var node2 = Node.allInstances.selectOne(node | node.name == \"Node 02\");");
			leftScript.add("var node5 = Node.allInstances.selectOne(node | node.name == \"Node 05\");");
			leftScript.add("node2.valNodes.add(1, node5);");

			// right
			rightScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			rightScript.add("node1.name = \"Node B\";");
			rightScript.add("var node2 = Node.allInstances.selectOne(node | node.name == \"Node 02\");");
			rightScript.add("var node4 = Node.allInstances.selectOne(node | node.name == \"Node 04\");");
			rightScript.add("node2.valNodes.add(node4);");
			rightScript.add("node2.valNodes.move(0,1);");
			rightScript.add("var root = Node.allInstances.selectOne(node | node.name == \"ROOT\");");
			rightScript.add("var node7 = new Node;");
			rightScript.add("node7.name = \"Node 07\";");
			rightScript.add("root.valNodes.add(node7);");

			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(actualValue, actualValue);
	}

	@Test
	public void testConflictMoveVsRemoveWithAllRightSolutions() throws IOException {
		initialise("testConflictMoveVsRemoveWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n"
				+ "  <Node name=\"Node 01\" refNodes=\"/3 /1 /2 /4\"/>\r\n" + "  <Node name=\"Node 02\"/>\r\n"
				+ "  <Node name=\"Node 03\"/>\r\n" + "  <Node name=\"Node 04\"/>\r\n" + "  <Node name=\"Node 05\"/>\r\n"
				+ "</xmi:XMI>";
		try {
			// origin
			originalScript.add("var node1 = new Node;");
			originalScript.add("node1.name = \"Node 01\";");
			originalScript.add("var node2 = new Node;");
			originalScript.add("node2.name = \"Node 02\";");
			originalScript.add("var node3 = new Node;");
			originalScript.add("node3.name = \"Node 03\";");
			originalScript.add("var node4 = new Node;");
			originalScript.add("node4.name = \"Node 04\";");
			originalScript.add("var node5 = new Node;");
			originalScript.add("node5.name = \"Node 05\";");
			originalScript.add("node1.refNodes.add(node2);");
			originalScript.add("node1.refNodes.add(node3);");
			originalScript.add("node1.refNodes.add(node4);");
			originalScript.add("node1.refNodes.add(node5);");

			// left
			leftScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("var node4 = Node.allInstances.selectOne(node | node.name == \"Node 04\");");
			leftScript.add("node1.refNodes.remove(node4);");

			// right
			rightScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			rightScript.add("node1.refNodes.move(0,2);");

			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllRightSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictMoveVsRemoveWithAllLeftSolutions() throws IOException {
		initialise("testConflictMoveVsRemoveWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n"
				+ "  <Node name=\"Node 01\" refNodes=\"/1 /2 /4\"/>\r\n" + "  <Node name=\"Node 02\"/>\r\n"
				+ "  <Node name=\"Node 03\"/>\r\n" + "  <Node name=\"Node 04\"/>\r\n" + "  <Node name=\"Node 05\"/>\r\n"
				+ "</xmi:XMI>";
		try {
			// origin
			originalScript.add("var node1 = new Node;");
			originalScript.add("node1.name = \"Node 01\";");
			originalScript.add("var node2 = new Node;");
			originalScript.add("node2.name = \"Node 02\";");
			originalScript.add("var node3 = new Node;");
			originalScript.add("node3.name = \"Node 03\";");
			originalScript.add("var node4 = new Node;");
			originalScript.add("node4.name = \"Node 04\";");
			originalScript.add("var node5 = new Node;");
			originalScript.add("node5.name = \"Node 05\";");
			originalScript.add("node1.refNodes.add(node2);");
			originalScript.add("node1.refNodes.add(node3);");
			originalScript.add("node1.refNodes.add(node4);");
			originalScript.add("node1.refNodes.add(node5);");

			// left
			leftScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("var node4 = Node.allInstances.selectOne(node | node.name == \"Node 04\");");
			leftScript.add("node1.refNodes.remove(node4);");

			// right
			rightScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			rightScript.add("node1.refNodes.move(0,2);");

			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictAddVsRemoveWithAllLeftSolutions() throws IOException {
		initialise("testConflictAddVsRemoveWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n"
				+ "  <Node name=\"Node 01\" refNodes=\"/1\"/>\r\n" + "  <Node name=\"Node 02\"/>\r\n"
				+ "  <Node name=\"Node 03\"/>\r\n" + "</xmi:XMI>";
		try {
			// origin
			originalScript.add("var node1 = new Node;");
			originalScript.add("node1.name = \"Node 01\";");
			originalScript.add("var node2 = new Node;");
			originalScript.add("node2.name = \"Node 02\";");
			originalScript.add("var node3 = new Node;");
			originalScript.add("node3.name = \"Node 03\";");
			originalScript.add("node1.refNodes.add(node2);");
			originalScript.add("node1.refNodes.add(node3);");

			// left
			leftScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 03\");");
			leftScript.add("node1.refNodes.remove(node3);");

			// right
			rightScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			rightScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 03\");");
			rightScript.add("node1.refNodes.remove(node3);");
			rightScript.add("node1.refNodes.add(node3);");

			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictAddVsRemoveWithAllRightSolutions() throws IOException {
		initialise("testConflictAddVsRemoveWithAllRightSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n"
				+ "  <Node name=\"Node 01\" refNodes=\"/1 /2\"/>\r\n" + "  <Node name=\"Node 02\"/>\r\n"
				+ "  <Node name=\"Node 03\"/>\r\n" + "</xmi:XMI>";
		try {
			// origin
			originalScript.add("var node1 = new Node;");
			originalScript.add("node1.name = \"Node 01\";");
			originalScript.add("var node2 = new Node;");
			originalScript.add("node2.name = \"Node 02\";");
			originalScript.add("var node3 = new Node;");
			originalScript.add("node3.name = \"Node 03\";");
			originalScript.add("node1.refNodes.add(node2);");
			originalScript.add("node1.refNodes.add(node3);");

			// left
			leftScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 03\");");
			leftScript.add("node1.refNodes.remove(node3);");

			// right
			rightScript.add("var node1 = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			rightScript.add("var node3 = Node.allInstances.selectOne(node | node.name == \"Node 03\");");
			rightScript.add("node1.refNodes.remove(node3);");
			rightScript.add("node1.refNodes.add(node3);");

			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllRightSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testDoubleConflictsSetVsDeleteEventsWithAllLeftSolutions() throws IOException {
		initialise("testDoubleConflictsSetVsDeleteEventsWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Node B\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Node 01\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("node.name = \"Node A\";");
			leftScript.add("node.name = \"Node B\";");
			// right
			rightScript = new Script(rightResource);
			rightScript.deleteElement("Node 01");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictSetVsDeleteEventsWithAllLeftSolutions() throws IOException {
		initialise("testConflictSetVsDeleteEventsWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" xmi:id=\"0\" name=\"Node A\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Node 01\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("node.name = \"Node A\";");
			// right
			rightScript = new Script(rightResource);
			rightScript.deleteElement("Node 01");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictSetVsDeleteEventsWithAllRightSolutions() throws IOException {
		initialise("testConflictSetVsDeleteEventsWithAllRightSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Node 01\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Node 01\");");
			leftScript.add("node.name = \"Node A\";");
			// right
			rightScript = new Script(rightResource);
			rightScript.deleteElement("Node 01");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllRightSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictSetVsSetEventsWithAllLeftSolutions() throws IOException {
		initialise("testConflictSetVsSetEventsWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Left Node\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Original Node\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			leftScript.add("node.name = \"Left Node\";");
			// right
			rightScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			rightScript.add("node.name = \"Right Node\";");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictSetVsSetEventsWithAllRightSolutions() throws IOException {
		initialise("testConflictSetVsSetEventsWithAllRightSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Right Node\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Original Node\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			leftScript.add("node.name = \"Left Node\";");
			// right
			rightScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			rightScript.add("node.name = \"Right Node\";");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllRightSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictSetVsUnsetEventsWithAllLeftSolutions() throws IOException {
		initialise("testConflictSetVsUnsetEventsWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Left Node\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Original Node\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			leftScript.add("node.name = \"Left Node\";");
			// right
			rightScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			rightScript.add("node.name = null;");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllLeftSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testConflictSetVsUnsetEventsWithAllRightSolutions() throws IOException {
		initialise("testConflictSetVsUnsetEventsWithAllRightSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\"/>";
		try {
			// origin
			originalScript.add("var node = new Node;");
			originalScript.add("node.name = \"Original Node\";");
			// left
			leftScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			leftScript.add("node.name = \"Left Node\";");
			// right
			rightScript.add("var node = Node.allInstances.selectOne(node | node.name == \"Original Node\");");
			rightScript.add("node.name = null;");
			// merge
			targetCbpFile = executeTest(originalScript, leftScript, rightScript,
					MergeMode.UpdateLeftWithAllRightSolutions);
			actualValue = getXMIString(targetCbpFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expectedValue, actualValue);
	}

	public File executeTest(Script originalScript, Script leftScript, Script rightScript, MergeMode mergeMode)
			throws Exception {
		// ROOT--------------------------------------------------
		originResource.startNewSession("ROOT");
		originalScript.run();
		originResource.save(null);
		originResource.unload();

		// LEFT--------------------------------------------------
		leftResource.closeCBPOutputStream();
		Files.copy(new FileInputStream(originXmiFile), leftXmiFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(new FileInputStream(originCbpFile), leftCbpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		leftResource.openCBPOutputStream(new FileOutputStream(leftCbpFile, true));
//		leftResource.loadFromCBP(new FileInputStream(leftCbpFile));
		leftResource.load(null);
		leftResource.startNewSession("LEFT");
		leftScript.run();
		leftResource.save(null);

		Resource resource = (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI(leftXmiFileNoId.getAbsolutePath()));
		resource.getContents().addAll(EcoreUtil.copyAll(leftResource.getContents()));
		resource.save(null);
		resource.unload();

		leftResource.unload();

		// RIGHT--------------------------------------------------
		rightResource.closeCBPOutputStream();
		Files.copy(new FileInputStream(originXmiFile), rightXmiFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(new FileInputStream(originCbpFile), rightCbpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		rightResource.openCBPOutputStream(new FileOutputStream(rightCbpFile, true));
//		rightResource.loadFromCBP(new FileInputStream(rightCbpFile));
		rightResource.load(null);
		rightResource.startNewSession("RIGHT");
		rightScript.run();
		rightResource.save(null);

		resource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFileNoId.getAbsolutePath()));
		resource.getContents().addAll(EcoreUtil.copyAll(rightResource.getContents()));
		resource.save(null);
		resource.unload();

		rightResource.unload();

		// COMPARE--------------------------------------------------
		cbpComparison = new CBPComparisonOld(leftCbpFile, rightCbpFile);
		cbpComparison.compare();

		// MERGE/UPDATE--------------------------------------------------
		switch (mergeMode) {
		case UpdateLeftWithAllLeftSolutions:
			targetFile = cbpComparison.updateLeftWithAllLeftSolutions(true);
			break;
		case UpdateLeftWithAllRightSolutions:
			targetFile = cbpComparison.updateLeftWithAllRightSolutions(true);
			break;
		case UpdateRightWithAllLeftSolutions:
			targetFile = cbpComparison.updateLeftWithAllLeftSolutions(true);
			break;
		case UpdateRightWithAllRightSolutions:
			targetFile = cbpComparison.updateLeftWithAllRightSolutions(true);
			break;
		default:
			break;
		}

		return targetFile;
	}

	class Script {
		private String text = new String();
		private String toBeDeletedName = null;
		private Resource resource;

		public Script(Resource resource) {
			this.resource = resource;
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

		public void run() throws Exception {
			if (toBeDeletedName != null) {
				TreeIterator<EObject> rightIterator = resource.getAllContents();
				while (rightIterator.hasNext()) {
					Node node = (Node) rightIterator.next();
					if (node.getName().equals(toBeDeletedName)) {
						((HybridResource) resource).deleteElement(node);
						break;
					}
				}
				toBeDeletedName = null;
			} else {
				module = new EolModule();
				module.parse(text);
				model = new InMemoryEmfModel("M", resource, ePackage);
				module.getContext().getModelRepository().addModel(model);
				module.execute();
			}
		}

		public void deleteElement(String elementName) {
			toBeDeletedName = elementName;
		}
	}
}
