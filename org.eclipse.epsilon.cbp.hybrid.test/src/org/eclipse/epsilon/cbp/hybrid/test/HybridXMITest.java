package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridXMIResourceImpl;
import org.eclipse.gmt.modisco.xml.Node;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class HybridXMITest {

	// private static String TAG = "H-XMI";
	private static String TAG = "XMI";
	public final int ITERATION = 25;
	public final int START_FROM = 3;
	public final int SLEEP_TIME = 1000;

	public HybridXMITest() {

	}
	
	
	@Test
	public void testHybridXMILoad() {
		try {
			TAG = "H-XMI";
			System.out.println("Start ....");

			 File targetXmiFile = new File("D:\\TEMP\\ASE\\epsilon.940.xmi");
			// File targetXmiFile = new File("D:\\TEMP\\ASE\\bpmn2.192.xmi");
//			File targetXmiFile = new File("D:\\TEMP\\ASE\\wikipedia.10187.xmi");
			File targetCbpFile = new File("D:\\TEMP\\ASE\\output_load.bpmn2.192.xmi.cbxpml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\HYBRID\\smalluml.cbpxml");
			File sourceCbpFile = new File("D:\\TEMP\\ASE\\bpmn2.192.cbpxml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\ASE\\epsilon.940.cbpxml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\HYBRID\\wikipedia.003100.ISO.cbpxml");
			System.out.println("Processing " + sourceCbpFile.getName() + "....");

			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			// TEST
			// LOADING---------------------------------------------------------------------------
			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);

			FileInputStream sourceCbpFileStream = new FileInputStream(sourceCbpFile);
			BufferedInputStream sourceCbpBufferedStream = new BufferedInputStream(sourceCbpFileStream);

			System.out.println("Loading from a CBP");
			HybridResource hybridResource = new HybridXMIResourceImpl(xmiResource, targetCbpBufferedStream);
			// hybridResource.loadFromCBP(sourceCbpBufferedStream);
			// hybridResource.load(null);

			//// save xmi first
			// System.out.println("Save to an XMI");
			// hybridResource.save(xmiOptions);
			// hybridResource.unload();

			// reinitialise and reload and measure time and memory
			System.out.println("Reload the XMI Several Times");
			long[] loadTime = new long[ITERATION];
			long[] loadMemory = new long[ITERATION];
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + i);

				xmiResource = (new XMIResourceFactoryImpl())
						.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));

				 hybridResource = new HybridXMIResourceImpl(xmiResource,
				 targetCbpBufferedStream);

				 hybridResource.unload();

				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				 hybridResource.load(xmiOptions);

				long endTime = System.nanoTime();
				endTime = System.nanoTime();
				System.gc();
				// Thread.sleep(SLEEP_TIME);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				loadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					loadMemory[i] += 0;
				} else {
					loadMemory[i] += Math.abs(endMemory - startMemory);
				}
				System.out.println((loadTime[i] / 1000000000.0) + ", " + (loadMemory[i] / 1000000.0));

			}

			// print results
			System.out.println();
			System.out.println("Hybrid XMI Load Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadTime[i] / 1000000000.0 + "," + TAG);
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Load Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadMemory[i] / 1000000.0 + "," + TAG);
				}
			}

			// closing
			targetCbpFileStream.close();
			targetCbpBufferedStream.close();
			sourceCbpFileStream.close();
			sourceCbpBufferedStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.playSound();
		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	@Test
	public void testXMILoad() {
		try {
			TAG = "XMI";
			System.out.println("Start ....");

			 File targetXmiFile = new File("D:\\TEMP\\ASE\\epsilon.940.xmi");
			// File targetXmiFile = new File("D:\\TEMP\\ASE\\bpmn2.192.xmi");
//			File targetXmiFile = new File("D:\\TEMP\\ASE\\wikipedia.10187.xmi");
			File targetCbpFile = new File("D:\\TEMP\\ASE\\output_load.bpmn2.192.xmi.cbxpml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\HYBRID\\smalluml.cbpxml");
			File sourceCbpFile = new File("D:\\TEMP\\ASE\\bpmn2.192.cbpxml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\ASE\\epsilon.940.cbpxml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\HYBRID\\wikipedia.003100.ISO.cbpxml");
			System.out.println("Processing " + sourceCbpFile.getName() + "....");

			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			// TEST
			// LOADING---------------------------------------------------------------------------
			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);

			FileInputStream sourceCbpFileStream = new FileInputStream(sourceCbpFile);
			BufferedInputStream sourceCbpBufferedStream = new BufferedInputStream(sourceCbpFileStream);

			System.out.println("Loading from a CBP");
			HybridResource hybridResource = new HybridXMIResourceImpl(xmiResource, targetCbpBufferedStream);
			// hybridResource.loadFromCBP(sourceCbpBufferedStream);
			// hybridResource.load(null);

			//// save xmi first
			// System.out.println("Save to an XMI");
			// hybridResource.save(xmiOptions);
			// hybridResource.unload();

			// reinitialise and reload and measure time and memory
			System.out.println("Reload the XMI Several Times");
			long[] loadTime = new long[ITERATION];
			long[] loadMemory = new long[ITERATION];
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + i);

				xmiResource = (new XMIResourceFactoryImpl())
						.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));

				// hybridResource = new HybridXMIResourceImpl(xmiResource,
				// targetCbpBufferedStream);

				// hybridResource.unload();
				xmiResource.unload();

				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

				xmiResource.load(xmiOptions);
				// hybridResource.load(xmiOptions);

				long endTime = System.nanoTime();
				endTime = System.nanoTime();
				System.gc();
				// Thread.sleep(SLEEP_TIME);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				loadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					loadMemory[i] += 0;
				} else {
					loadMemory[i] += Math.abs(endMemory - startMemory);
				}
				System.out.println((loadTime[i] / 1000000000.0) + ", " + (loadMemory[i] / 1000000.0));

			}

			// print results
			System.out.println();
			System.out.println("XMI Load Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadTime[i] / 1000000000.0 + "," + TAG);
				}
			}

			System.out.println();
			System.out.println("XMI Load Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadMemory[i] / 1000000.0 + "," + TAG);
				}
			}

			// closing
			targetCbpFileStream.close();
			targetCbpBufferedStream.close();
			sourceCbpFileStream.close();
			sourceCbpBufferedStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.playSound();
		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	@Test
	public void testHybridXMISave() {
		try {
			TAG = "XMI";
			System.out.println("Start ...");

			File targetXmiFile = new File("D:\\TEMP\\ASE\\output_save.xmi");
			File targetCbpFile = new File("D:\\TEMP\\ASE\\output_save.cbpxml");
			// File sourceCbpFile = new File("D:\\TEMP\\ASE\\bpmn2.192.cbpxml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\ASE\\epsilon.940.cbpxml");
			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\wikipedia.003100.ISO.cbpxml");
			System.out.println("Processing " + sourceCbpFile.getName() + "....");

			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			// TEST SAVE----------------------------------------------------

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);

			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));
			HybridResource hybridResource = new HybridXMIResourceImpl(xmiResource, targetCbpBufferedStream);
			// hybridResource.loadFromCBP(new ByteArrayInputStream(new
			// byte[0]));

			// total event count
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(sourceCbpFile));
			lineNumberReader.skip(Long.MAX_VALUE);
			int totalEventCount = lineNumberReader.getLineNumber();
			lineNumberReader.close();

			// iteration
			int lineCount = 0;
			String line = null;
			int measureCount = 0;
			long[] saveTime = new long[ITERATION];
			long[] saveMemory = new long[ITERATION];
			FileReader fileReader = new FileReader(sourceCbpFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {

				if (lineCount % 100000 == 0)
					System.out.println(lineCount);

				ByteArrayInputStream bais = new ByteArrayInputStream(line.getBytes());
				hybridResource.loadAndReplayEvents(bais);
				bais.close();

//				if (lineCount > totalEventCount - ITERATION) {
				if (lineCount > 100000) {

					 if (measureCount == 25) {
						 break;
					 }

					System.gc();
					long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					long startTime = System.nanoTime();

					// hybridResource.save(xmiOptions);
					xmiResource.save(xmiOptions);

					long endTime = System.nanoTime();
					System.gc();
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					saveTime[measureCount] += (endTime - startTime);
					if (endMemory - startMemory < 0) {
						saveMemory[measureCount] += 0;
					} else {
						saveMemory[measureCount] += Math.abs(endMemory - startMemory);
					}

					System.out.println(
							(saveTime[measureCount] / 1000000000.0) + ", " + (saveMemory[measureCount] / 1000000.0));
					targetCbpFileStream.flush();
					targetCbpBufferedStream.flush();

					measureCount += 1;
				}
				lineCount += 1;
			}
			bufferedReader.close();
			targetCbpFileStream.close();
			targetCbpBufferedStream.close();

			// print results
			System.out.println();
			System.out.println("Hybrid XMI Save Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveTime[i] / 1000000000.0 + "," + TAG);
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Save Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveMemory[i] / 1000000.0 + "," + TAG);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.playSound();
		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	@Test
	public void testHybridXMISaveForOnlyXMI() throws InterruptedException {
		try {
			TAG = "XMI";
			System.out.println("Start ...");

			File targetXmiFile = new File("D:\\TEMP\\ASE\\epsilon.940.save.xmi");
//			File targetXmiFile = new File("D:\\TEMP\\ASE\\wikipedia.10187.save.xmi");
			File targetCbpFile = new File("D:\\TEMP\\ASE\\output_save.cbpxml");
//			 File sourceCbpFile = new File("D:\\TEMP\\ASE\\bpmn2.192.cbpxml");
//			File sourceCbpFile = new File("D:\\TEMP\\ASE\\epsilon.940.cbpxml");
			// File sourceCbpFile = new
			// File("D:\\TEMP\\HYBRID\\wikipedia.003100.ISO.cbpxml");
//			System.out.println("Processing " + sourceCbpFile.getName() + "....");

			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			// TEST SAVE----------------------------------------------------

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);

			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));
			xmiResource.load(null);
			
//			HybridResource hybridResource = new HybridXMIResourceImpl(xmiResource, targetCbpBufferedStream);
//			hybridResource.load(null);
//			hybridResource.save(xmiOptions);
			
			int count = 0;
			List<EObject> list = new ArrayList<>();
			TreeIterator<EObject> iterator = xmiResource.getAllContents();
			while (iterator.hasNext()) {
				list.add(iterator.next());
				count += 1;
			}
			System.out.println("Element count = " + count);
			
			String line = null;
			int measureCount = 0;
			long[] saveTime = new long[ITERATION];
			long[] saveMemory = new long[ITERATION];
//			FileReader fileReader = new FileReader(sourceCbpFile);
//			BufferedReader bufferedReader = new BufferedReader(fileReader);
			for (; measureCount < ITERATION; measureCount++) {

				// select an object randomly and modify its name to raise an
				// event
				EObject eObject = list.get((new Random()).nextInt(list.size()));
				if (eObject instanceof Node) {
					((Node) eObject).setName("measurement-" + measureCount);
				}
				if (eObject instanceof NamedElement) {
					((NamedElement) eObject).setName("measurement-" + measureCount);
				}
				
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

//				hybridResource.save(xmiOptions);
				xmiResource.save(xmiOptions);

				long endTime = System.nanoTime();
				System.gc();
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				saveTime[measureCount] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					saveMemory[measureCount] += 0;
				} else {
					saveMemory[measureCount] += Math.abs(endMemory - startMemory);
				}

				System.out.println(
						saveTime[measureCount] / 1000000000.0 + ", " + saveMemory[measureCount] / 1000000.0);
				targetCbpFileStream.flush();
				targetCbpBufferedStream.flush();

			}
//			bufferedReader.close();
			targetCbpFileStream.close();
			targetCbpBufferedStream.close();

			// print results
			System.out.println();
			System.out.println("Hybrid XMI Save Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveTime[i] / 1000000000.0 + "," + TAG);
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Save Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveMemory[i] / 1000000.0 + "," + TAG);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.playSound();
		Thread.sleep(3000);
		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	public void playSound() {
		try {
			File soundFile = new File(
					"C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\MEDIA\\LYNC_ringtone2.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (Exception exe) {
		}
	}
}
