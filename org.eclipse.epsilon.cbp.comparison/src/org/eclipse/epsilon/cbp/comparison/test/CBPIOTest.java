package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.eclipse.epsilon.cbp.comparison.CBPComparisonOld;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.junit.Test;

public class CBPIOTest {

	@Test
	public void testTextFileReader() throws IOException, XMLStreamException {
		File file = new File("D:\\TEMP\\COMPARISON\\origin.cbpxml");

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

	@Test
	public void testReadingFiles()
			throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {

		NodePackage.eINSTANCE.eClass();

		File originFile = new File("D:\\TEMP\\COMPARISON\\origin.cbpxml");
		File leftFile = new File("D:\\TEMP\\COMPARISON\\left.cbpxml");
		File rightFile = new File("D:\\TEMP\\COMPARISON\\right.cbpxml");

		System.out.println("\n===Left vs Right===");
		CBPComparisonOld comparison = new CBPComparisonOld(leftFile, rightFile);
		comparison.compare();

		System.out.println("\n===Origin vs Right===");
		comparison = new CBPComparisonOld(originFile, rightFile);
		comparison.compare();

		System.out.println("\n===Left vs Origin===");
		comparison = new CBPComparisonOld(leftFile, originFile);
		comparison.compare();

		assertEquals(true, true);

	}
}
