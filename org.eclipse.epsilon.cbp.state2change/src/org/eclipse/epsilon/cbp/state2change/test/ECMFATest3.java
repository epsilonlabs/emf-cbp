package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Test
	public void testComparison() {
		MoDiscoXMLPackage.eINSTANCE.eClass();
		UMLPackage.eINSTANCE.eClass();

		File dummyCbpFileForMeasuringSave = new File("D:\\TEMP\\ECMFA\\cbp\\_temp.cbpxml");

		System.out.println("Processing Epsilon ...");
		File epsilonCbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\epsilon.cbpxml");
		File epsilonIgnoreListFile = new File("D:\\TEMP\\ECMFA\\cbp\\epsilon.ignorelist");
		Measurement e = performMeasure(epsilonCbpFile, epsilonIgnoreListFile, dummyCbpFileForMeasuringSave);

		System.out.println("Processing BPMN2 ...");
		File bpmn2CbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.cbpxml");
		File bpmn2IgnoreListFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.ignorelist");
		Measurement b = performMeasure(bpmn2CbpFile, bpmn2IgnoreListFile, dummyCbpFileForMeasuringSave);

		System.out.println("Processing Wikipedia ...");
		File wikipediaCbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.cbpxml");
		File wikipediaIgnoreListFile = new File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.ignorelist");
		Measurement w = performMeasure(wikipediaCbpFile, wikipediaIgnoreListFile, dummyCbpFileForMeasuringSave);

		System.out.println();
		System.out.println("Model \t Total Event Count \t Ignored Event Count \t Element Count \t Number of Commits");
		System.out.println("Epsilon \t " + e.getTotalEventCount() + " \t " + e.getIgnoredEventCount() + " \t "
				+ e.getElementCount() + " \t " + e.getSessionCount());
		System.out.println("BPMN2 \t " + b.getTotalEventCount() + " \t " + b.getIgnoredEventCount() + " \t "
				+ b.getElementCount() + " \t " + b.getSessionCount());
		System.out.println("Wikipedia \t " + w.getTotalEventCount() + " \t " + w.getIgnoredEventCount() + " \t "
				+ w.getElementCount() + " \t " + w.getSessionCount());

		System.out.println();
		System.out.println("Model \t CBP Load Time \t Opt. CBP Load Time \t XMI Load Time");
		System.out.println(
				"Epsilon \t " + e.getNonOptLoadTime() + " \t " + e.getOptLoadTime() + " \t " + e.getXmiLoadTime());
		System.out.println(
				"BPMN2 \t " + b.getNonOptLoadTime() + " \t " + b.getOptLoadTime() + " \t " + b.getXmiLoadTime());
		System.out.println(
				"Wikipedia \t " + w.getNonOptLoadTime() + " \t " + w.getOptLoadTime() + " \t " + w.getXmiLoadTime());

		System.out.println();
		System.out.println("Model \t CBP Save Time \t Opt. CBP Save Time \t XMI Save Time");
		System.out.println(
				"Epsilon \t " + e.getNonOptSaveTime() + " \t " + e.getOptSaveTime() + " \t " + e.getXmiSaveTime());
		System.out.println(
				"BPMN2 \t " + b.getNonOptSaveTime() + " \t " + b.getOptSaveTime() + " \t " + b.getXmiSaveTime());
		System.out.println(
				"Wikipedia \t " + w.getNonOptSaveTime() + " \t " + w.getOptSaveTime() + " \t " + w.getXmiSaveTime());

		System.out.println();
		System.out.println("Model \t CBP Load Memory \t Opt. CBP Load Memory \t XMI Load Memory");
		System.out.println("Epsilon \t " + e.getNonOptLoadMemory() + " \t " + e.getOptLoadMemory() + " \t "
				+ e.getXmiLoadMemory());
		System.out.println(
				"BPMN2 \t " + b.getNonOptLoadMemory() + " \t " + b.getOptLoadMemory() + " \t " + b.getXmiLoadMemory());
		System.out.println("Wikipedia \t " + w.getNonOptLoadMemory() + " \t " + w.getOptLoadMemory() + " \t "
				+ w.getXmiLoadMemory());

		System.out.println();
		System.out.println("Model \t CBP Save Memory \t Opt. CBP Save Memory \t XMI Save Memory");
		System.out.println("Epsilon \t " + e.getNonOptSaveMemory() + " \t " + e.getOptSaveMemory() + " \t "
				+ e.getXmiSaveMemory());
		System.out.println(
				"BPMN2 \t " + b.getNonOptSaveMemory() + " \t " + b.getOptSaveMemory() + " \t " + b.getXmiSaveMemory());
		System.out.println("Wikipedia \t " + w.getNonOptSaveMemory() + " \t " + w.getOptSaveMemory() + " \t "
				+ w.getXmiSaveMemory());

		assertEquals(true, true);
	}

	private Measurement performMeasure(File cbpFile, File ignoreListFile, File dummyCbpFileForMeasuringSave) {
		final int ITERATION = 6;
		final int DENOMINATOR = 60;

		Measurement m = new Measurement();
		try {
			XMIResource xmiResource = (XMIResource) (new XMIResourceFactoryImpl())
					.createResource(URI.createURI("foo.xmi"));
			CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

			Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			double optLoadTime = 0;
			double nonOptLoadTime = 0;
			double xmiLoadTime = 0;

			double optLoadMemory = 0;
			double nonOptLoadMemory = 0;
			double xmiLoadMemory = 0;

			double nonOptSaveTime = 0;
			double optSaveTime = 0;
			double xmiSaveTime = 0;

			double nonOptSaveMemory = 0;
			double optSaveMemory = 0;
			double xmiSaveMemory = 0;

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
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				nonOptLoadTime += (endTime - startTime);
				nonOptLoadMemory += (endMemory - startMemory);

				// xmi save time
				System.out.println("Xmi Save Time and Memory");
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				xmiResource.getContents().addAll(cbpResource.getContents());
				System.gc();
				startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				startTime = System.nanoTime();
				xmiResource.save(os, xmiOptions);
				endTime = System.nanoTime();
				System.gc();
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				xmiSaveTime += (endTime - startTime);
				xmiSaveMemory += (endMemory - startMemory);
				xmiResource.unload();

				// xmi load time
				System.out.println("Xmi Load Time and Memory");
				ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
				System.gc();
				startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				startTime = System.nanoTime();
				xmiResource.load(is, xmiOptions);
				endTime = System.nanoTime();
				System.gc();
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				xmiLoadTime += (endTime - startTime);
				xmiLoadMemory += (endMemory - startMemory);

				// clearance
				cbpLoadOptions.clear();
				xmiResource.unload();
				cbpResource.unload();
			}
			nonOptLoadTime = nonOptLoadTime / ITERATION / 1000000000.0;
			xmiSaveTime = xmiSaveTime / ITERATION / 1000000000.0;
			xmiLoadTime = xmiLoadTime / ITERATION / 1000000000.0;
			nonOptLoadMemory = nonOptLoadMemory / ITERATION / 1000000.0;
			xmiSaveMemory = xmiSaveMemory / ITERATION / 1000000.0;
			xmiLoadMemory = xmiLoadMemory / ITERATION / 1000000.0;

			// optimised CBP
			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Optimised Loading Time - " + (i + 1));

				FileInputStream inputStream = new FileInputStream(ignoreListFile);

				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("CBP Optimised Load Time and Memory");
				cbpResource.loadIgnoreSet(inputStream);

				long startTime = System.nanoTime();
				cbpResource.load(null);
				long endTime = System.nanoTime();
				System.gc();
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				optLoadTime += (endTime - startTime);
				optLoadMemory += (endMemory - startMemory);

				cbpResource.unload();
			}
			optLoadTime = optLoadTime / ITERATION / 1000000000.0;
			optLoadMemory = optLoadMemory / ITERATION / 1000000.0;

			// measuring saving time Non-optimised CBP
			System.out.println("CBP Save Time and Memory");
			if (dummyCbpFileForMeasuringSave.exists()) {
				dummyCbpFileForMeasuringSave.delete();
			}
			dummyCbpFileForMeasuringSave.createNewFile();

			Map<Object, Object> cbpSaveOptions = new HashMap<>();
			cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
			cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, false);
			cbpResource.setURI(URI.createFileURI(cbpFile.getAbsolutePath()));
			cbpResource.load(cbpSaveOptions);
			cbpResource.setURI(URI.createFileURI(dummyCbpFileForMeasuringSave.getAbsolutePath()));
			List<ChangeEvent<?>> changeEvents = new ArrayList<>(cbpResource.getChangeEvents());

			// total event count
			System.out.println("Total Event Count");
			totalEventCount = changeEvents.size();

			// session count
			System.out.println("Session Count");
			sessionCount = 0;
			sessionCount = cbpResource.getSessionCount();

			// element count
			System.out.println("Element Count");
			elementCount = 0;
			TreeIterator<EObject> iterator = cbpResource.getAllContents();
			while (iterator.hasNext()) {
				iterator.next();
				elementCount += 1;
			}

			System.out.println("CBP Save Time and Memory - continue");
			cbpResource.getChangeEvents().clear();
			for (int i = 0; i < DENOMINATOR; i++) {
				cbpResource.getChangeEvents().add(changeEvents.get(i));
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();
				cbpResource.save(cbpSaveOptions);
				long endTime = System.nanoTime();
				System.gc();
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				nonOptSaveTime += (endTime - startTime);
				if ((endMemory - startMemory) < 0) {
					nonOptSaveMemory += 0;
				} else {
					nonOptSaveMemory += (endMemory - startMemory);
				}
			}
			cbpResource.unload();
			nonOptSaveTime = nonOptSaveTime / DENOMINATOR / 1000000000.0;
			nonOptSaveMemory = nonOptSaveMemory / DENOMINATOR / 1000000.0;

			// measuring saving time optimised CBP
			System.out.println("Optimised CBP Save Time and Memory");
			if (dummyCbpFileForMeasuringSave.exists()) {
				dummyCbpFileForMeasuringSave.delete();
			}
			dummyCbpFileForMeasuringSave.createNewFile();

			cbpSaveOptions.clear();
			cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
			cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, true);
			cbpResource.setURI(URI.createFileURI(cbpFile.getAbsolutePath()));
			FileInputStream inputStream = new FileInputStream(ignoreListFile);
			cbpResource.loadIgnoreSet(inputStream);

			// ignored event count
			System.out.println("Ignored Event Count");
			ignoredEventCount = cbpResource.getIgnoreSet().size();

			System.out.println("Optimised CBP Save Time and Memory - continue");
			cbpResource.load(cbpSaveOptions);
			cbpResource.setURI(URI.createFileURI(dummyCbpFileForMeasuringSave.getAbsolutePath()));
			totalEventCount = changeEvents.size();
			cbpResource.getChangeEvents().clear();
			for (int i = 0; i < DENOMINATOR; i++) {
				cbpResource.getChangeEvents().add(changeEvents.get(i));
				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();
				cbpResource.save(cbpSaveOptions);
				long endTime = System.nanoTime();
				System.gc();
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				optSaveTime += (endTime - startTime);
				if ((endMemory - startMemory) < 0) {
					optSaveMemory += 0;
				} else {
					optSaveMemory += (endMemory - startMemory);
				}
			}
			changeEvents.clear();
			cbpResource.unload();
			optSaveTime = optSaveTime / DENOMINATOR / 1000000000.0;
			optSaveMemory = optSaveMemory / DENOMINATOR / 1000000.0;

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
		}
		return m;
	}

	public class Measurement {
		double optLoadTime = 0;
		double nonOptLoadTime = 0;
		double xmiLoadTime = 0;

		double optLoadMemory = 0;
		double nonOptLoadMemory = 0;
		double xmiLoadMemory = 0;

		double nonOpSaveTime = 0;
		double opSaveTime = 0;
		double xmiSaveTime = 0;

		double nonOpSaveMemory = 0;
		double opSaveMemory = 0;
		double xmiSaveMemory = 0;

		int elementCount = 0;
		int ignoredEventCount = 0;
		int totalEventCount = 0;
		int sessionCount = 0;

		public double getNonOptSaveTime() {
			return nonOpSaveTime;
		}

		public void setNonOptSaveTime(double nonOpSaveTime) {
			this.nonOpSaveTime = nonOpSaveTime;
		}

		public double getOptSaveTime() {
			return opSaveTime;
		}

		public void setOptSaveTime(double opSaveTime) {
			this.opSaveTime = opSaveTime;
		}

		public double getNonOptSaveMemory() {
			return nonOpSaveMemory;
		}

		public void setNonOptSaveMemory(double nonOpSaveMemory) {
			this.nonOpSaveMemory = nonOpSaveMemory;
		}

		public double getOptSaveMemory() {
			return opSaveMemory;
		}

		public void setOptSaveMemory(double opSaveMemory) {
			this.opSaveMemory = opSaveMemory;
		}

		public int getTotalEventCount() {
			return totalEventCount;
		}

		public double getOptLoadMemory() {
			return optLoadMemory;
		}

		public void setOptLoadMemory(double optLoadMemory) {
			this.optLoadMemory = optLoadMemory;
		}

		public double getNonOptLoadMemory() {
			return nonOptLoadMemory;
		}

		public void setNonOptLoadMemory(double nonOptLoadMemory) {
			this.nonOptLoadMemory = nonOptLoadMemory;
		}

		public double getXmiLoadMemory() {
			return xmiLoadMemory;
		}

		public void setXmiLoadMemory(double xmiLoadMemory) {
			this.xmiLoadMemory = xmiLoadMemory;
		}

		public double getXmiSaveMemory() {
			return xmiSaveMemory;
		}

		public void setXmiSaveMemory(double xmiSaveMemory) {
			this.xmiSaveMemory = xmiSaveMemory;
		}

		public int getSessionCount() {
			return sessionCount;
		}

		public void setSessionCount(int sessionCount) {
			this.sessionCount = sessionCount;
		}

		public void setOptLoadTime(double optLoadTime) {
			this.optLoadTime = optLoadTime;
		}

		public void setNonOptLoadTime(double nonOptLoadTime) {
			this.nonOptLoadTime = nonOptLoadTime;
		}

		public void setXmiLoadTime(double xmiLoadTime) {
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

		public void setCbpSaveTime(double cbpSaveTime) {
			this.nonOpSaveTime = cbpSaveTime;
		}

		public void setXmiSaveTime(double xmiSaveTime) {
			this.xmiSaveTime = xmiSaveTime;
		}

		public double getOptLoadTime() {
			return optLoadTime;
		}

		public double getNonOptLoadTime() {
			return nonOptLoadTime;
		}

		public double getXmiLoadTime() {
			return xmiLoadTime;
		}

		public int getElementCount() {
			return elementCount;
		}

		public int getIgnoredEventCount() {
			return ignoredEventCount;
		}

		public double getXmiSaveTime() {
			return xmiSaveTime;
		}

	}
}
