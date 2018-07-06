package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.junit.After;
import org.junit.Test;

public class CBPComparisonTest {

	public enum MergeMode {
		UpdateLeftWithAllLeftSolutions, UpdateLeftWithAllRightSolutions, UpdateRightWithAllLeftSolutions, UpdateRightWithAllRightSolutions
	}

	private File originFile;
	private File leftFile;
	private File rightFile;
	private CBPResource originResource;
	private CBPResource leftResource;
	private CBPResource rightResource;
	private CBPComparison cbpComparison;
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

	// @Test
	public void testTextFileReader() throws IOException, XMLStreamException {
		File file = new File("D:\\TEMP\\COMPARISON\\cbp-root\\cbp-comparison\\model.cbpxml");

		RandomAccessFile raf = new RandomAccessFile(file, "rw");

		// FileChannel channel = (FileChannel)
		// Files.newByteChannel(aFile.toPath(), StandardOpenOption.READ);
		FileChannel channel = raf.getChannel();

		ByteBuffer buffer = ByteBuffer.allocate(1);
		long byteCount = 0;
		while (channel.read(buffer) != -1) {
			buffer.flip();
			// System.out.print(Charset.defaultCharset().decode(buffer));
			String s = Charset.defaultCharset().decode(buffer).toString();
			System.out.println(s.toCharArray()[0]);
			buffer.clear();
			byteCount += 1;
			if (byteCount == 2000) {
				System.out.println(channel.position());
				break;
			}
		}
		System.out.println(byteCount);
		System.out.println();

		FileChannel channel2 = raf.getChannel();
		ByteBuffer buffer2 = ByteBuffer.allocate(1);
		channel2.position(byteCount);
		while (channel2.read(buffer2) != -1) {
			buffer2.flip();
			System.out.print(Charset.defaultCharset().decode(buffer2));
			buffer2.clear();
		}

		raf.close();
		assertEquals(true, true);
	}

	public void initialise(String testName) {
		System.out
				.println("\n###### " + testName + " ##############################################################\n");
		originFile = new File("D:\\TEMP\\COMPARISON\\temp\\" + testName + "-root.cbpxml");
		if (originFile.exists())
			originFile.delete();
		leftFile = new File("D:\\TEMP\\COMPARISON\\temp\\" + testName + "-left.cbpxml");
		if (leftFile.exists())
			leftFile.delete();
		rightFile = new File("D:\\TEMP\\COMPARISON\\temp\\" + testName + "-right.cbpxml");
		if (rightFile.exists())
			rightFile.delete();

		NodePackage.eINSTANCE.eClass();

		originResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(originFile.getPath()));
		leftResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(leftFile.getPath()));
		rightResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(rightFile.getPath()));

		originalScript = new Script(originResource);
		leftScript = new Script(leftResource);
		rightScript = new Script(rightResource);
	}

	@After
	public void tearDown() throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
		// print results
		List<String> leftLines = Files.readAllLines(leftFile.toPath());
		List<String> rightLines = Files.readAllLines(rightFile.toPath());

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
		CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(targetFile.getAbsolutePath()));
		cbpResource.load(null);

		Resource xmiMergedResource = (new XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
		xmiMergedResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
		StringOutputStream output = new StringOutputStream();
		xmiMergedResource.save(output, null);
		String outputString = output.toString();
		// System.out.println(outputString);
		return outputString.trim();
	}

	@Test
	public void testConflictMoveVsRemoveWithAllRightSolutions() throws IOException {
		initialise("testConflictMoveVsRemoveWithAllLeftSolutions");
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n" + 
				"  <Node name=\"Node 01\" refNodes=\"/3 /1 /2 /4\"/>\r\n" + 
				"  <Node name=\"Node 02\"/>\r\n" + 
				"  <Node name=\"Node 03\"/>\r\n" + 
				"  <Node name=\"Node 04\"/>\r\n" + 
				"  <Node name=\"Node 05\"/>\r\n" + 
				"</xmi:XMI>";
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n" + 
				"  <Node name=\"Node 01\" refNodes=\"/1 /2 /4\"/>\r\n" + 
				"  <Node name=\"Node 02\"/>\r\n" + 
				"  <Node name=\"Node 03\"/>\r\n" + 
				"  <Node name=\"Node 04\"/>\r\n" + 
				"  <Node name=\"Node 05\"/>\r\n" + 
				"</xmi:XMI>";
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n" + 
				"  <Node name=\"Node 01\" refNodes=\"/1\"/>\r\n" + 
				"  <Node name=\"Node 02\"/>\r\n" + 
				"  <Node name=\"Node 03\"/>\r\n" + 
				"</xmi:XMI>";
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\">\r\n" + 
				"  <Node name=\"Node 01\" refNodes=\"/1 /2\"/>\r\n" + 
				"  <Node name=\"Node 02\"/>\r\n" + 
				"  <Node name=\"Node 03\"/>\r\n" + 
				"</xmi:XMI>";
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n"
				+ "<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Node A\"/>";
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
			//merge
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Left Node\"/>";
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
			//merge
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Right Node\"/>";
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
			//merge
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\" name=\"Left Node\"/>";
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
			//merge
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
		expectedValue = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\r\n" + 
				"<Node xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns=\"node\"/>";
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
			//merge
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
		Files.copy(new FileInputStream(originFile), leftFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		leftResource.load(null);
		leftResource.startNewSession("LEFT");

		leftScript.run();

		leftResource.save(null);
		Resource xmiResource = (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI("D:\\TEMP\\COMPARISON\\left.xmi"));
		xmiResource.getContents().addAll(leftResource.getContents());
		xmiResource.save(null);
		xmiResource.unload();
		leftResource.unload();

		// RIGHT--------------------------------------------------
		Files.copy(new FileInputStream(originFile), rightFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		rightResource.load(null);
		rightResource.startNewSession("RIGHT");

		rightScript.run();

		rightResource.save(null);
		xmiResource = (new XMIResourceFactoryImpl())
				.createResource(URI.createFileURI("D:\\TEMP\\COMPARISON\\right.xmi"));
		xmiResource.getContents().addAll(rightResource.getContents());
		xmiResource.save(null);
		xmiResource.unload();
		rightResource.unload();

		// COMPARE--------------------------------------------------
		cbpComparison = new CBPComparison(leftFile, rightFile);
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
					((CBPResource) resource).deleteElement(node);
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
