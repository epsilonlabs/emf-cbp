package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class ECMFATest3 {

	private static final int SLEEP_TIME = 1000;
	final int ITERATION = 6;
	final int startFrom = 0;

	String dirPath = "D:\\TEMP\\ECMFA\\scripts";
	String extension = ".csv";
	private PrintStream printer;
	private String fileName;
	private String prefix;
	private Map<String, Measurement> map = new HashMap<>();

	@Test
	public void testComparison() throws FileNotFoundException {
		System.out.println("Start: " + (new Date()).toString());

		MoDiscoXMLPackage.eINSTANCE.eClass();
		UMLPackage.eINSTANCE.eClass();

		File dummyCbpFileForMeasuringSave = new File("D:\\TEMP\\ECMFA\\cbp\\_temp.cbpxml");
		File dummyIgnoreSet = new File("D:\\TEMP\\ECMFA\\cbp\\_temp.ignoreset");

		 System.out.println();
		 System.out.println("Processing BPMN2 ...");
		 File bpmn2CbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.cbpxml");
		 File bpmn2IgnoreListFile = new
		 File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.ignoreset");
		 Measurement b = new Measurement();
		 b = performMeasure(bpmn2CbpFile, bpmn2IgnoreListFile,
		 dummyCbpFileForMeasuringSave, dummyIgnoreSet);
		 map.put("BPMN2", b);

//		System.out.println();
//		System.out.println("Processing Epsilon ...");
//		File epsilonCbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\epsilon.cbpxml");
//		File epsilonIgnoreListFile = new File("D:\\TEMP\\ECMFA\\cbp\\epsilon.ignoreset");
//		Measurement e = new Measurement();
//		e = performMeasure(epsilonCbpFile, epsilonIgnoreListFile, dummyCbpFileForMeasuringSave, dummyIgnoreSet);
//		map.put("Epsilon", e);

//		 System.out.println();
//		 System.out.println("Processing Wikipedia ...");
//		 File wikipediaCbpFile = new
//		 File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.003100.ISO.cbpxml");
//		 File wikipediaIgnoreListFile = new
//		 File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.003100.ISO.ignoreset");
//		 Measurement w = new Measurement();
//		 w = performMeasure(wikipediaCbpFile, wikipediaIgnoreListFile,
//		 dummyCbpFileForMeasuringSave, dummyIgnoreSet);
//		 map.put("Wikipedia", w);

		System.out.println();
		fileName = dirPath + File.separator + "general_description" + extension;
		printer = new PrintStream(new File(fileName));
		printer.println("Model , Total Event Count , Ignored Event Count , Element Count , Number of Commits");
		 printer.println("BPMN2 , " + b.getTotalEventCount() + " , " +
		 b.getIgnoredEventCount() + " , "
		 + b.getElementCount() + " , " + b.getSessionCount());
//		printer.println("Epsilon , " + e.getTotalEventCount() + " , " + e.getIgnoredEventCount() + " , "
//				+ e.getElementCount() + " , " + e.getSessionCount());
//		 printer.println("Wikipedia , " + w.getTotalEventCount() + " , " +
//		 w.getIgnoredEventCount() + " , "
//		 + w.getElementCount() + " , " + w.getSessionCount());
		printer.flush();
		printer.close();

		/// Printing
		prefix = "load_time_";
		for (Entry<String, Measurement> entry : map.entrySet()) {
			String name = entry.getKey();
			String fileName = dirPath + File.separator + prefix + name + extension;
			printer = new PrintStream(new File(fileName));
			printer.println("Value,Group");
			Measurement m = entry.getValue();
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getNonOptLoadTime()[i] + "," + "CBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getOptLoadTime()[i] + "," + "OCBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getXmiLoadTime()[i] + "," + "XMI");
			}
			printer.flush();
			printer.close();
		}

		prefix = "save_time_";
		for (Entry<String, Measurement> entry : map.entrySet()) {
			String name = entry.getKey();
			String fileName = dirPath + File.separator + prefix + name + extension;
			printer = new PrintStream(new File(fileName));
			printer.println("Value,Group");
			Measurement m = entry.getValue();
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getNonOptSaveTime()[i] + "," + "CBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getOptSaveTime()[i] + "," + "OCBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getXmiSaveTime()[i] + "," + "XMI");
			}
			printer.flush();
			printer.close();
		}

		prefix = "load_memory_";
		for (Entry<String, Measurement> entry : map.entrySet()) {
			String name = entry.getKey();
			String fileName = dirPath + File.separator + prefix + name + extension;
			printer = new PrintStream(new File(fileName));
			printer.println("Value,Group");
			Measurement m = entry.getValue();
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getNonOptLoadMemory()[i] + "," + "CBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getOptLoadMemory()[i] + "," + "OCBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getXmiLoadMemory()[i] + "," + "XMI");
			}
			printer.flush();
			printer.close();
		}

		prefix = "save_memory_";
		for (Entry<String, Measurement> entry : map.entrySet()) {
			String name = entry.getKey();
			String fileName = dirPath + File.separator + prefix + name + extension;
			printer = new PrintStream(new File(fileName));
			printer.println("Value,Group");
			Measurement m = entry.getValue();
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getNonOptSaveMemory()[i] + "," + "CBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getOptSaveMemory()[i] + "," + "OCBP");
			}
			for (int i = startFrom; i < ITERATION; i++) {
				printer.println(m.getXmiSaveMemory()[i] + "," + "XMI");
			}
			printer.flush();
			printer.close();
		}

		System.out.println("End: " + (new Date()).toString());
		assertEquals(true, true);
	}

	private Measurement performMeasure(File cbpFile, File ignoreListFile, File dummyCbpFileForMeasuringSave,
			File dummyIgnoreSet) {

		Measurement m = new Measurement();
		try {
			XMIResource xmiResource = (XMIResource) (new XMIResourceFactoryImpl())
					.createResource(URI.createURI("foo.xmi"));
			CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			double[] optLoadTime = new double[ITERATION];
			double[] nonOptLoadTime = new double[ITERATION];
			double[] xmiLoadTime = new double[ITERATION];

			double[] optLoadMemory = new double[ITERATION];
			double[] nonOptLoadMemory = new double[ITERATION];
			double[] xmiLoadMemory = new double[ITERATION];

			double[] nonOptSaveTime = new double[ITERATION];
			double[] optSaveTime = new double[ITERATION];
			double[] xmiSaveTime = new double[ITERATION];

			double[] nonOptSaveMemory = new double[ITERATION];
			double[] optSaveMemory = new double[ITERATION];
			double[] xmiSaveMemory = new double[ITERATION];

			int elementCount = 0;
			int ignoredEventCount = 0;
			int totalEventCount = 0;
			int sessionCount = 0;

			// Non-optimised CBP

			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Non Optimised Loading Time - " + (i + 1));

				Map<Object, Object> cbpLoadOptions = new HashMap<>();
				cbpLoadOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, false);

				System.out.println("CBP Load Time and Memory");
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();
				cbpResource.load(cbpLoadOptions);
				long endTime = System.nanoTime();
				System.gc();
				// try {
				// Thread.sleep(SLEEP_TIME);
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// }
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				nonOptLoadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					nonOptLoadMemory[i] += 0;
				} else {
					nonOptLoadMemory[i] += Math.abs(endMemory - startMemory);
				}
				// System.out.println(nonOptLoadMemory[i]);

				// xmi save time
				System.out.println("Xmi Save Time and Memory");
				// xmiResource = (XMIResource) (new XMIResourceFactoryImpl())
				// .createResource(URI.createURI("foo.xmi"));
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				xmiResource.getContents().addAll(cbpResource.getContents());
				System.gc();
				startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				startTime = System.nanoTime();
				xmiResource.save(os, xmiOptions);
				endTime = System.nanoTime();
				System.gc();
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				xmiSaveTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					xmiSaveMemory[i] += 0;
				} else {
					xmiSaveMemory[i] += Math.abs(endMemory - startMemory);
				}
				xmiResource.unload();

				// xmi load time
				System.out.println("Xmi Load Time and Memory");
				// xmiResource = (XMIResource) (new XMIResourceFactoryImpl())
				// .createResource(URI.createURI("foo.xmi"));
				ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
				System.gc();
				startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				startTime = System.nanoTime();
				xmiResource.load(is, xmiOptions);
				endTime = System.nanoTime();
				System.gc();
				// Thread.sleep(SLEEP_TIME);
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				xmiLoadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					xmiLoadMemory[i] += 0;
				} else {
					xmiLoadMemory[i] += Math.abs(endMemory - startMemory);
				}

				// clearance
				cbpLoadOptions.clear();
				xmiResource.unload();
				cbpResource.unload();

				nonOptLoadTime[i] = nonOptLoadTime[i] / 1000000000.0;
				xmiSaveTime[i] = xmiSaveTime[i] / 1000000000.0;
				xmiLoadTime[i] = xmiLoadTime[i] / 1000000000.0;
				nonOptLoadMemory[i] = nonOptLoadMemory[i] / 1000000.0;
				xmiSaveMemory[i] = xmiSaveMemory[i] / 1000000.0;
				xmiLoadMemory[i] = xmiLoadMemory[i] / 1000000.0;
			}

			// optimised CBP
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Optimised Loading Time - " + (i + 1));

				FileInputStream inputStream = new FileInputStream(ignoreListFile);

				System.out.println("CBP Optimised Load Time and Memory");
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				cbpResource.loadIgnoreSet(inputStream);
				long startTime = System.nanoTime();
				cbpResource.load(null);
				long endTime = System.nanoTime();
				System.gc();
				// Thread.sleep(SLEEP_TIME);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				optLoadTime[i] += (endTime - startTime);
				if (endMemory - startMemory < 0) {
					optLoadMemory[i] += 0;
				} else {
					optLoadMemory[i] += Math.abs(endMemory - startMemory);
				}

				cbpResource.unload();

				optLoadTime[i] = optLoadTime[i] / 1000000000.0;
				optLoadMemory[i] = optLoadMemory[i] / 1000000.0;
			}

			// // measuring saving time Non-optimised CBP
			// System.out.println("General Description");
			// if (dummyCbpFileForMeasuringSave.exists()) {
			// dummyCbpFileForMeasuringSave.delete();
			// }
			// dummyCbpFileForMeasuringSave.createNewFile();
			//
			// Map<Object, Object> cbpSaveOptions = new HashMap<>();
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD,
			// true);
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD,
			// false);
			// cbpResource.setURI(URI.createFileURI(cbpFile.getAbsolutePath()));
			// cbpResource.load(cbpSaveOptions);
			// cbpResource.setURI(URI.createFileURI(dummyCbpFileForMeasuringSave.getAbsolutePath()));
			// List<ChangeEvent<?>> changeEvents = new
			// ArrayList<>(cbpResource.getChangeEvents());
			//
			// // total event count
			// System.out.println("Total Event Count");
			// totalEventCount = changeEvents.size();
			//
			// // session count
			// System.out.println("Session Count");
			// sessionCount = 0;
			// sessionCount = cbpResource.getSessionCount();
			//
			// // element count
			// System.out.println("Element Count");
			// elementCount = 0;
			// TreeIterator<EObject> iterator = cbpResource.getAllContents();
			// while (iterator.hasNext()) {
			// iterator.next();
			// elementCount += 1;
			// }
			// cbpResource.unload();
			//
			// // measuring saving time optimised CBP
			// System.out.println("General Description 2");
			// if (dummyCbpFileForMeasuringSave.exists()) {
			// dummyCbpFileForMeasuringSave.delete();
			// }
			// dummyCbpFileForMeasuringSave.createNewFile();
			// if (dummyIgnoreSet.exists()) {
			// dummyIgnoreSet.delete();
			// }
			// dummyIgnoreSet.createNewFile();
			//
			// // ignored event count
			// cbpSaveOptions.clear();
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD,
			// true);
			// //
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_GENERATE_MODEL_HISTORY,
			// // true);
			//
			// System.out.println("Ignored Event Count");
			// cbpResource.loadIgnoreSet(new FileInputStream(ignoreListFile));
			// ignoredEventCount = cbpResource.getIgnoreSet().size();
			//
			// System.out.println("Optimised CBP Save Time and Memory");
			// cbpResource.load(new ByteArrayInputStream(new byte[0]),
			// cbpSaveOptions);
			// cbpResource.setURI(URI.createFileURI(dummyCbpFileForMeasuringSave.getAbsolutePath()));
			// totalEventCount = changeEvents.size();
			// cbpResource.getChangeEvents().clear();
			//
			// BufferedReader reader = new BufferedReader(new
			// FileReader(cbpFile));
			// String line;
			// int lineCount = 0;
			// int iterationCount = 0;
			// int unignoredLineCount = 0;
			// while ((line = reader.readLine()) != null) {
			//
			// if (lineCount % 100000 == 0)
			// System.out.println(lineCount);
			//
			// if (iterationCount >= ITERATION)
			// break;
			//
			// if (cbpResource.getIgnoreSet().contains(lineCount) == false) {
			//
			// InputStream stream = new ByteArrayInputStream(line.getBytes());
			// try {
			// cbpResource.loadAdditionalEvents(stream);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// stream.close();
			//
			// if (unignoredLineCount >= totalEventCount - ignoredEventCount -
			// ITERATION) {
			// System.out.println(line);
			// System.gc();
			// long startMemory = Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory();
			// long startTime = System.nanoTime();
			// cbpResource.save(cbpSaveOptions);
			// cbpResource.saveIgnoreSet(new FileOutputStream(dummyIgnoreSet,
			// true));
			// long endTime = System.nanoTime();
			// System.gc();
			// try {
			// Thread.sleep(SLEEP_TIME);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// long endMemory = Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory();
			// optSaveTime[iterationCount] += (endTime - startTime);
			// if (endMemory - startMemory < 0) {
			// optSaveMemory[iterationCount] += 0;
			// } else {
			// optSaveMemory[iterationCount] += Math.abs(endMemory -
			// startMemory);
			// }
			// optSaveTime[iterationCount] = optSaveTime[iterationCount] /
			// 1000000000.0;
			// optSaveMemory[iterationCount] = optSaveMemory[iterationCount] /
			// 1000000.0;
			// iterationCount += 1;
			// } else {
			// // System.out.println("Saving previously replayed
			// // events");
			// // System.out.println(cbpResource.getChangeEvents().size());
			// // System.gc();
			// cbpResource.save(cbpSaveOptions);
			// cbpResource.saveIgnoreSet(new FileOutputStream(dummyIgnoreSet,
			// true));
			// }
			//
			// unignoredLineCount += 1;
			// }
			// lineCount += 1;
			// }
			// reader.close();
			// cbpResource.unload();
			//
			// System.out.println("CBP Save Time and Memory");
			// cbpSaveOptions.clear();
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD,
			// true);
			// //
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_GENERATE_MODEL_HISTORY,
			// // true);
			// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD,
			// false);
			// cbpResource.load(new ByteArrayInputStream(new byte[0]),
			// cbpSaveOptions);
			//
			// reader = new BufferedReader(new FileReader(cbpFile));
			// lineCount = 0;
			// iterationCount = 0;
			// while ((line = reader.readLine()) != null) {
			//
			// if (lineCount % 100000 == 0)
			// System.out.println(lineCount);
			//
			// if (iterationCount >= ITERATION)
			// break;
			//
			// InputStream stream = new ByteArrayInputStream(line.getBytes());
			// try {
			// cbpResource.loadAdditionalEvents(stream);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// stream.close();
			//
			// if (lineCount >= totalEventCount - ITERATION) {
			// System.out.println(line);
			// System.gc();
			// long startMemory = Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory();
			// long startTime = System.nanoTime();
			// cbpResource.save(cbpSaveOptions);
			// long endTime = System.nanoTime();
			// System.gc();
			// try {
			// Thread.sleep(SLEEP_TIME);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// long endMemory = Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory();
			// nonOptSaveTime[iterationCount] += (endTime - startTime);
			// if (endMemory - startMemory < 0) {
			// nonOptSaveMemory[iterationCount] += 0;
			// } else {
			// nonOptSaveMemory[iterationCount] += Math.abs(endMemory -
			// startMemory);
			// }
			// nonOptSaveTime[iterationCount] = nonOptSaveTime[iterationCount] /
			// 1000000000.0;
			// nonOptSaveMemory[iterationCount] =
			// nonOptSaveMemory[iterationCount] / 1000000.0;
			// iterationCount += 1;
			// } else if (lineCount == totalEventCount - ITERATION - 1) {
			// System.out.println("Saving previously replayed events");
			// System.out.println(cbpResource.getChangeEvents().size());
			// System.gc();
			// cbpResource.save(cbpSaveOptions);
			// }
			//
			// lineCount += 1;
			// }
			// reader.close();
			// cbpResource.unload();

			// set measurement values
			m.setElementCount(elementCount);
			m.setSessionCount(sessionCount);
			m.setTotalEventCount(totalEventCount);
			m.setIgnoredEventCount(ignoredEventCount);

			m.setNonOptLoadTime(nonOptLoadTime);
			m.setOptLoadTime(optLoadTime);
			m.setXmiLoadTime(xmiLoadTime);
			m.setNonOptSaveTime(nonOptSaveTime);
			m.setOptSaveTime(optSaveTime);
			m.setXmiSaveTime(xmiSaveTime);

			m.setNonOptLoadMemory(nonOptLoadMemory);
			m.setOptLoadMemory(optLoadMemory);
			m.setXmiLoadMemory(xmiLoadMemory);
			m.setNonOptSaveMemory(nonOptSaveMemory);
			m.setOptSaveMemory(optSaveMemory);
			m.setXmiSaveMemory(xmiSaveMemory);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
		}
		return m;
	}

	public class Measurement {
		double[] optLoadTime = new double[ITERATION];
		double[] nonOptLoadTime = new double[ITERATION];
		double[] xmiLoadTime = new double[ITERATION];

		double[] optLoadMemory = new double[ITERATION];
		double[] nonOptLoadMemory = new double[ITERATION];
		double[] xmiLoadMemory = new double[ITERATION];

		double[] nonOpSaveTime = new double[ITERATION];
		double[] opSaveTime = new double[ITERATION];
		double[] xmiSaveTime = new double[ITERATION];

		double[] nonOpSaveMemory = new double[ITERATION];
		double[] opSaveMemory = new double[ITERATION];
		double[] xmiSaveMemory = new double[ITERATION];

		int elementCount = 0;
		int ignoredEventCount = 0;
		int totalEventCount = 0;
		int sessionCount = 0;

		public double[] getNonOptSaveTime() {
			return nonOpSaveTime;
		}

		public void setNonOptSaveTime(double[] nonOpSaveTime) {
			this.nonOpSaveTime = nonOpSaveTime;
		}

		public double[] getOptSaveTime() {
			return opSaveTime;
		}

		public void setOptSaveTime(double[] opSaveTime) {
			this.opSaveTime = opSaveTime;
		}

		public double[] getNonOptSaveMemory() {
			return nonOpSaveMemory;
		}

		public void setNonOptSaveMemory(double[] nonOpSaveMemory) {
			this.nonOpSaveMemory = nonOpSaveMemory;
		}

		public double[] getOptSaveMemory() {
			return opSaveMemory;
		}

		public void setOptSaveMemory(double[] opSaveMemory) {
			this.opSaveMemory = opSaveMemory;
		}

		public int getTotalEventCount() {
			return totalEventCount;
		}

		public double[] getOptLoadMemory() {
			return optLoadMemory;
		}

		public void setOptLoadMemory(double[] optLoadMemory) {
			this.optLoadMemory = optLoadMemory;
		}

		public double[] getNonOptLoadMemory() {
			return nonOptLoadMemory;
		}

		public void setNonOptLoadMemory(double[] nonOptLoadMemory) {
			this.nonOptLoadMemory = nonOptLoadMemory;
		}

		public double[] getXmiLoadMemory() {
			return xmiLoadMemory;
		}

		public void setXmiLoadMemory(double[] xmiLoadMemory) {
			this.xmiLoadMemory = xmiLoadMemory;
		}

		public double[] getXmiSaveMemory() {
			return xmiSaveMemory;
		}

		public void setXmiSaveMemory(double[] xmiSaveMemory) {
			this.xmiSaveMemory = xmiSaveMemory;
		}

		public int getSessionCount() {
			return sessionCount;
		}

		public void setSessionCount(int sessionCount) {
			this.sessionCount = sessionCount;
		}

		public void setOptLoadTime(double[] optLoadTime) {
			this.optLoadTime = optLoadTime;
		}

		public void setNonOptLoadTime(double[] nonOptLoadTime) {
			this.nonOptLoadTime = nonOptLoadTime;
		}

		public void setXmiLoadTime(double[] xmiLoadTime) {
			this.xmiLoadTime = xmiLoadTime;
		}

		public void setElementCount(int elementCount) {
			this.elementCount = elementCount;
		}

		public void setIgnoredEventCount(int ignoredEventCount) {
			this.ignoredEventCount = ignoredEventCount;
		}

		public void setTotalEventCount(int totalEventCount) {
			this.totalEventCount = totalEventCount;
		}

		public void setCbpSaveTime(double[] cbpSaveTime) {
			this.nonOpSaveTime = cbpSaveTime;
		}

		public void setXmiSaveTime(double[] xmiSaveTime) {
			this.xmiSaveTime = xmiSaveTime;
		}

		public double[] getOptLoadTime() {
			return optLoadTime;
		}

		public double[] getNonOptLoadTime() {
			return nonOptLoadTime;
		}

		public double[] getXmiLoadTime() {
			return xmiLoadTime;
		}

		public int getElementCount() {
			return elementCount;
		}

		public int getIgnoredEventCount() {
			return ignoredEventCount;
		}

		public double[] getXmiSaveTime() {
			return xmiSaveTime;
		}

	}
}
