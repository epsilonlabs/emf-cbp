package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class ECMFATest4 {

	@Test
	public void testEquality() throws FileNotFoundException, IOException {
		try {
			System.out.println("Running ... ");
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			File cbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.cbpxml");
			File cbpDummyFile1 = new File("D:\\TEMP\\ECMFA\\cbp\\_temp1.cbpxml");
			File cbpDummyFile2 = new File("D:\\TEMP\\ECMFA\\cbp\\_temp2.cbpxml");
			File ignoreSetFile = new File("D:\\TEMP\\ECMFA\\cbp\\_temp.ignoreset");
			if (cbpDummyFile1.exists()) {
				cbpDummyFile1.delete();
			}
			if (cbpDummyFile2.exists()) {
				cbpDummyFile2.delete();
			}
			if (ignoreSetFile.exists()) {
				ignoreSetFile.delete();
			}
			cbpDummyFile1.createNewFile();
			cbpDummyFile2.createNewFile();
			ignoreSetFile.createNewFile();

			Resource xmi1 = null;
			Resource xmi2 = null;
			Set<Integer> ignoreSet = null;

			CBPXMLResourceImpl cbp0 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

			Map<Object, Object> loadOptions = new HashMap<>();
			loadOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, true);
			loadOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
			cbp0.load(loadOptions);

			Map<Object, Object> saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cbpFile)));
			String strLine;
			int line = 0;
			while ((strLine = br.readLine()) != null) {
				try {
//					System.out.println(strLine);
//					if (strLine.contains("<session")) System.out.println(strLine);
					strLine = strLine + "\n";

					FileOutputStream fos1 = new FileOutputStream(cbpDummyFile1, true);
					fos1.write(strLine.getBytes());
					fos1.flush();
					fos1.close();

					if (line >= 1236752 && line % 1 == 0) {
						System.out.println(line);
						CBPXMLResourceImpl cbp1 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
								.createResource(URI.createFileURI(cbpDummyFile1.getAbsolutePath()));
						CBPXMLResourceImpl cbp2 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
								.createResource(URI.createFileURI(cbpDummyFile1.getAbsolutePath()));

						StringOutputStream sos0 = new StringOutputStream();
						cbp1.load(loadOptions);
						// System.out.print(cbp1.getChangeEvents().size());
						cbp1.save(sos0, null);
						// System.out.println(" " + cbp1.getIgnoreSet());
						if (cbp1.getIgnoreSet().size() > 0) {
							ignoreSet = new HashSet<>(cbp1.getIgnoreSet());
//							System.out.println(cbp1.getIgnoreSet());
						}
						sos0.close();
						cbp1.saveIgnoreSet(new FileOutputStream(ignoreSetFile, true));

						StringOutputStream sos1 = new StringOutputStream();
						xmi1 = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI("dummy.xmi"));
						xmi1.getContents().addAll(cbp1.getContents());
						xmi1.save(sos1, saveOptions);

						cbp2.loadIgnoreSet(new FileInputStream(ignoreSetFile));
						cbp2.load(null);

						StringOutputStream sos2 = new StringOutputStream();
						xmi2 = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI("dummy.xmi"));
						xmi2.getContents().addAll(cbp2.getContents());
						xmi2.save(sos2, saveOptions);

						if (!sos1.toString().equals(sos2.toString())) {
							System.out.println("Line = " + line);
							System.out.println("FAIL!!!");
							throw new Exception();
						}

						cbp1.unload();
						cbp2.unload();
						xmi1.unload();
						xmi2.unload();

					}
					line += 1;
				} catch (Exception e) {
					File xmiFile1 = new File("D:\\TEMP\\ECMFA\\cbp\\_temp1.xmi");
					File xmiFile2 = new File("D:\\TEMP\\ECMFA\\cbp\\_temp2.xmi");
					xmi1.save(new FileOutputStream(xmiFile1), saveOptions);
					xmi2.save(new FileOutputStream(xmiFile2), saveOptions);

					BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(cbpDummyFile1)));
					String strLine1;
					int line1 = 0;
					FileOutputStream fos2 = new FileOutputStream(cbpDummyFile2, true);
					while ((strLine1 = br1.readLine()) != null) {
						if (ignoreSet.contains(line1)) {
							line1 += 1;
							continue;
						}
						strLine1 = strLine1 + "\n";
						fos2.write(strLine1.getBytes());
						line1 += 1;
					}
					fos2.flush();
					fos2.close();
					br1.close();

					throw new Exception(e);
				}
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertEquals(true, true);
	}

	@Test
	public void test() throws FileNotFoundException, IOException {

		// System.out.println(Integer.MAX_VALUE);

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
			options.put(CBPXMLResourceImpl.OPTION_GENERATE_MODEL_HISTORY, true);
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

		File ignoreFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.ignoreset");

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
			dis1.close();
			// System.out.print(set.size() + "\t");

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
			dis2.close();
			// System.out.print(set.size() + "\t");

			// method3
			System.gc();
			long c1 = System.nanoTime();
			set.clear();
			list.clear();
			FileInputStream fis = new FileInputStream(ignoreFile);
			FileChannel inChannel = (fis).getChannel();
			ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			int[] result = new int[(int) (inChannel.size() / 4)];
			buffer.order(ByteOrder.BIG_ENDIAN);
			IntBuffer intBuffer = buffer.asIntBuffer();
			intBuffer.get(result);
			for (int x = 0; x < result.length; x++)
				list.add(result[x]);
			set = new HashSet<>(list);
			// Integer[] vals = new Integer[result.length];
			// for (int x = 0; x < result.length; i++) {
			// result[x] = Integer.valueOf(result[x]);
			// }
			// set = new HashSet<Integer>(Arrays.asList(vals));
			// list = new ArrayList<>(set);
			long c2 = System.nanoTime();
			long c = c2 - c1;
			fis.close();
			// System.out.println(set.size());

			System.gc();
			System.out.println(a / 1000000000.0 + "\t" + b / 1000000000.0 + "\t" + c / 1000000000.0);
		}

		assertEquals(true, true);
	}

}
