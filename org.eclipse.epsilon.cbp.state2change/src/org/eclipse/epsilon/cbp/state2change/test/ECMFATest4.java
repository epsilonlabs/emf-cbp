package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class ECMFATest4 {

	@Test
	public void test() throws FileNotFoundException, IOException {

//		System.out.println(Integer.MAX_VALUE);
		
		UMLPackage.eINSTANCE.eClass();
		MoDiscoXMLPackage.eINSTANCE.eClass();

		File cbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.cbpxml");
		File ignoreFile = new File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.ignorelist");

		CBPXMLResourceImpl resource1 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
		CBPXMLResourceImpl resource2 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
		CBPXMLResourceImpl resource3 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
		CBPXMLResourceImpl resource4 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
				.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

		System.out.println("O+IS+MH" + "\t" + "O+IS" + "\t" + "O" + "\t" + "OP");

		for (int i : new int[11]) {
			System.gc();
			long a1 = System.nanoTime();
			Map<Object, Object> options = new HashMap<>();
			options.put(CBPXMLResourceImpl.OPTION_REPOPULATE_MODEL_HISTORY, true);
			resource1.loadIgnoreSet(new FileInputStream(ignoreFile));
			resource1.load(options);
			long a2 = System.nanoTime();
			long a = a2 - a1;
			resource1.unload();

			System.gc();
			long b1 = System.nanoTime();
			resource2.loadIgnoreSet(new FileInputStream(ignoreFile));
			resource2.load(null);
			long b2 = System.nanoTime();
			long b = b2 - b1;
			resource2.unload();

			System.gc();
			// Map<Object, Object> options2 = new HashMap<>();
			// options.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, false);
			long c1 = System.nanoTime();
			// resource3.load(options2);
			resource3.load(null);
			long c2 = System.nanoTime();
			long c = c2 - c1;
			resource3.unload();

			System.gc();
			resource4.loadIgnoreSet(new FileInputStream(ignoreFile));
			long d1 = System.nanoTime();
			resource4.load(null);
			long d2 = System.nanoTime();
			long d = d2 - d1;
			resource4.unload();

			System.gc();
			System.out.println(
					a / 1000000000.0 + "\t" + b / 1000000000.0 + "\t" + c / 1000000000.0 + "\t" + d / 1000000000.0);
		}

		assertEquals(true, true);
	}

	@Test
	public void readBinary() throws IOException {

		File ignoreFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.ignorelist");

		for (int i : new int[10]) {
			System.gc();
			Set<Integer> set = new HashSet<>();
			List<Integer> list = new ArrayList<>();

			// method 1
			System.gc();
			long a1 = System.nanoTime();
			set.clear();
			list.clear();
			DataInputStream dis1 = new DataInputStream(new BufferedInputStream(new FileInputStream(ignoreFile)));
			int count = (int) (ignoreFile.length() / 4);
			Integer[] values = new Integer[count];
			for (int n = 0; n < count; n++) {
				values[n] = dis1.readInt();
			}
			set = new HashSet<Integer>(Arrays.asList(values));
			list = new ArrayList<>(set);
			long a2 = System.nanoTime();
			long a = a2 - a1;
			System.out.print(set.size() + "\t");

			// method2
			System.gc();
			long b1 = System.nanoTime();
			set.clear();
			list.clear();
			DataInputStream dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(ignoreFile)));
			while (dis2.available() > 0) {
				int value = dis2.readInt();
				if (set.add(value)) {
					list.add(value);
				}
			}
			long b2 = System.nanoTime();
			long b = b2 - b1;
			System.out.print(set.size() + "\t");

			// method3
			System.gc();
			long c1 = System.nanoTime();
			set.clear();
			list.clear();
			FileChannel inChannel = (new FileInputStream(ignoreFile)).getChannel();
			ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			int[] result = new int[(int) (inChannel.size() / 4)];
			buffer.order(ByteOrder.BIG_ENDIAN);
			IntBuffer intBuffer = buffer.asIntBuffer();
			intBuffer.get(result);
			for (int x = 0; x < result.length; x++) list.add(x);
			set = new HashSet<>(list);
			// Integer[] vals = new Integer[result.length];
			// for (int x = 0; x < result.length; i++) {
			// result[x] = Integer.valueOf(result[x]);
			// }
			// set = new HashSet<Integer>(Arrays.asList(vals));
			// list = new ArrayList<>(set);
			long c2 = System.nanoTime();
			long c = c2 - c1;
			System.out.println(set.size());

			System.gc();
			System.out.println(a / 1000000000.0 + "\t" + b / 1000000000.0 + "\t" + c / 1000000000.0);
		}

		assertEquals(true, true);
	}

}
