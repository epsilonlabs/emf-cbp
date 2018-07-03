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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodeFactory;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.makeithappen.model.task.TaskPackage;

public class CBPComparisonTest {

	private File originFile;
	private File leftFile;
	private File rightFile;
	private NodeFactory factory;
	private CBPResource originResource;
	private CBPResource leftResource;
	private CBPResource rightResource;
	private CBPComparison cbpComparison;
	private File targetFile;

	@Test
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

	@Before
	public void setUp() {
		originFile = new File("D:\\TEMP\\COMPARISON\\origin.cbpxml");
		if (originFile.exists())
			originFile.delete();
		leftFile = new File("D:\\TEMP\\COMPARISON\\left.cbpxml");
		if (leftFile.exists())
			leftFile.delete();
		rightFile = new File("D:\\TEMP\\COMPARISON\\right.cbpxml");
		if (rightFile.exists())
			rightFile.delete();

		NodePackage.eINSTANCE.eClass();
		factory = NodeFactory.eINSTANCE;

		originResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(originFile.getPath()));
		leftResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(leftFile.getPath()));
		rightResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(rightFile.getPath()));
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

		// test reload
		CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(targetFile.getAbsolutePath()));
		cbpResource.load(null);

		System.out.println();
		System.out.println("MERGED XMI:");
		Resource xmiMergedResource = (new XMIResourceFactoryImpl()).createResource(URI.createURI("dummy.xmi"));
		xmiMergedResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
		StringOutputStream output = new StringOutputStream();
		xmiMergedResource.save(output, null);
		System.out.println(output.toString());
	}

	@Test
	public void testConflictSetVsDeleteEvents() throws IOException {
		try {

			// ROOT--------------------------------------------------
			originResource.startNewSession("ROOT");
			Node node01 = factory.createNode();
			node01.setName("Node 1");
			originResource.getContents().add(node01);
			originResource.save(null);
			originResource.unload();

			// LEFT--------------------------------------------------
			Files.copy(new FileInputStream(originFile), leftFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			leftResource.load(null);
			leftResource.startNewSession("LEFT");

			TreeIterator<EObject> leftIterator = leftResource.getAllContents();
			while (leftIterator.hasNext()) {
				Node node = (Node) leftIterator.next();
				node.setName("Node A");
			}

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

			TreeIterator<EObject> rightIterator = rightResource.getAllContents();
			while (rightIterator.hasNext()) {
				Node node = (Node) rightIterator.next();
				rightResource.deleteElement(node);
			}

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
			targetFile = cbpComparison.updateLeftWithAllLeftSolutions(true);
//			targetFile = cbpComparison.updateLeftWithAllRightSolutions(true);

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
			return;
		}

		assertEquals(true, true);
	}

	
}
