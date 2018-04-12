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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridXmiResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class HybridXmiTest {

	public final int ITERATION = 25;
	public final int START_FROM = 3;
	public final int SLEEP_TIME = 1000;
	
	

	public HybridXmiTest() {
		
	}

	@Test
	public void testHybridXMILoad() {
		try {
			System.out.println("Start ....");
			
			File targetXmiFile = new File("D:\\TEMP\\HYBRID\\output_load.xmi");
			File targetCbpFile = new File("D:\\TEMP\\HYBRID\\output_load.cbxpml");
//			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\smalluml.cbpxml");
//			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\BPMN2.cbpxml");
//			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\epsilon.1009.cbpxml");
			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\wikipedia.003100.ISO.cbpxml");
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
			HybridResource hybridResource = new HybridXmiResourceImpl(xmiResource, targetCbpBufferedStream);
			hybridResource.loadFromCBP(sourceCbpBufferedStream);

			// save xmi first
			System.out.println("Save to an XMI");
			hybridResource.save(xmiOptions);
			hybridResource.unload();
			

			// reinitialise and reload and measure time and memory
			System.out.println("Reload the XMI Several Times");
			long[] loadTime = new long[ITERATION];
			long[] loadMemory = new long[ITERATION];
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + i);
				xmiResource = (new XMIResourceFactoryImpl())
						.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));
				hybridResource = new HybridXmiResourceImpl(xmiResource, targetCbpBufferedStream);
				
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();

//				xmiResource.load(xmiOptions);
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
				
				hybridResource.unload();
			}

			// print results
			System.out.println();
			System.out.println("Hybrid XMI Load Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadTime[i] / 1000000000.0 + ",H-XMI");
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Load Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(loadMemory[i] / 1000000.0 + ",H-XMI");
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

		System.out.println("Finished!!!");
		assertEquals(true, true);
	}

	@Test
	public void testHybridXMISave() {
		try {
			System.out.println("Start ...");
			
			File targetXmiFile = new File("D:\\TEMP\\HYBRID\\output_save.xmi");
			File targetCbpFile = new File("D:\\TEMP\\HYBRID\\output_save.cbpxml");
//			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\BPMN2.cbpxml");
			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\epsilon.1009.cbpxml");
//			File sourceCbpFile = new File("D:\\TEMP\\HYBRID\\wikipedia.003100.ISO.cbpxml");
			System.out.println("Processing " + sourceCbpFile.getName() + "....");
			
			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
			
			UMLPackage.eINSTANCE.eClass();

			// TEST SAVE----------------------------------------------------

			FileOutputStream targetCbpFileStream = new FileOutputStream(targetCbpFile);
			BufferedOutputStream targetCbpBufferedStream = new BufferedOutputStream(targetCbpFileStream);

			Resource xmiResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(targetXmiFile.getAbsolutePath()));
			HybridResource hybridResource = new HybridXmiResourceImpl(xmiResource, targetCbpBufferedStream);
//			hybridResource.loadFromCBP(new ByteArrayInputStream(new byte[0]));

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

				if (lineCount >= totalEventCount - ITERATION) {
									
					System.gc();
					long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					long startTime = System.nanoTime();

					hybridResource.save(xmiOptions);
					
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
					
					System.out.println((saveTime[measureCount] / 1000000000.0) + ", " + (saveMemory[measureCount] / 1000000.0));
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
					System.out.println(saveTime[i] / 1000000000.0 + ",H-XMI");
				}
			}

			System.out.println();
			System.out.println("Hybrid XMI Save Memory");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(saveMemory[i] / 1000000.0 + ",H-XMI");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finished!!!");
		assertEquals(true, true);
	}
}
