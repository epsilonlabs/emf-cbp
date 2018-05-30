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
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class ECMFATest {

	final int SLEEP_TIME = 1000;
	final int ITERATION = 25;
	final int START_FROM = 3;
	final String outputPath = "D:\\TEMP\\ECMFA\\output";
	final String sourcePath = "D:\\TEMP\\ECMFA\\cbp";

	public ECMFATest() {
		MoDiscoXMLPackage.eINSTANCE.eClass();
		UMLPackage.eINSTANCE.eClass();
	}

	@Test
	public void testComparison() throws FileNotFoundException, InterruptedException {
		System.out.println("Start: " + (new Date()).toString());

		List<String> models = new ArrayList<>();
		List<File> cbpFiles = new ArrayList<>();
		List<File> ignoreFiles = new ArrayList<>();
		List<File> xmiFiles = new ArrayList<>();

		// MODELS
//		models.add("bpmn2");
//		cbpFiles.add(new File(sourcePath + File.separator + "BPMN2.cbpxml"));
//		ignoreFiles.add(new File(sourcePath + File.separator + "BPMN2.ignoreset"));
//		xmiFiles.add(new File(sourcePath + File.separator + "BPMN2.xmi"));
//
		models.add("epsilon");
		cbpFiles.add(new File(sourcePath + File.separator + "epsilon.947.cbpxml"));
		ignoreFiles.add(new File(sourcePath + File.separator + "epsilon.947.ignoreset"));
		xmiFiles.add(new File(sourcePath + File.separator + "epsilon.947.xmi"));

//		models.add("wikipedia");
//		cbpFiles.add(new File(sourcePath + File.separator + "wikipedia.9180.cbpxml"));
//		ignoreFiles.add(new File(sourcePath + File.separator + "wikipedia.9180.ignoreset"));
//		xmiFiles.add(new File(sourcePath + File.separator + "wikipedia.9180.xmi"));

		// Generating data description
//		for (int i = 0; i < models.size(); i++) {
//			String model = models.get(i);
//			File cbpFile = cbpFiles.get(i);
//			File ignoreFile = ignoreFiles.get(i);
//			File xmiFile = xmiFiles.get(i);
//
//			String filePath = outputPath + File.separator + "data_description_" + model + ".csv";
//			File outputFile = new File(filePath);
//			DataDescriptionTask task = new DataDescriptionTask(model, cbpFile, ignoreFile, xmiFile, outputFile);
//			// task.setDaemon(true);
//			task.setName(outputFile.getName());
//			task.start();
//			task.join();
//		}

		for (int i = 0; i < models.size(); i++) {
			String model = models.get(i);
			System.out.println("Processing " + model + "...");

			File cbpFile = cbpFiles.get(i);
			File ignoreFile = ignoreFiles.get(i);
			File xmiFile = xmiFiles.get(i);

			List<File> outputFileList = new ArrayList<>();

			File outputLoadTimeFile = new File(outputPath + File.separator + "load_time_" + model + ".csv");
			outputFileList.add(outputLoadTimeFile);
			File outputLoadMemoryFile = new File(outputPath + File.separator + "load_memory_" + model + ".csv");
			outputFileList.add(outputLoadMemoryFile);
			File outputSaveTimeFile = new File(outputPath + File.separator + "save_time_" + model + ".csv");
			outputFileList.add(outputSaveTimeFile);
			File outputSaveMemoryFile = new File(outputPath + File.separator + "save_memory_" + model + ".csv");
			outputFileList.add(outputSaveMemoryFile);

			for (File outputFile : outputFileList) {
				PrintWriter printer = new PrintWriter(new FileOutputStream(outputFile, false));
				printer.println("Value,Group");
				printer.flush();
				printer.close();
			}

//			for (int n = 0; n < ITERATION; n++) {
//				System.out.println("Filling " + (n + 1) + " - CBP Load Time and Memory");
//				CBPLoadTimeMemoryTask task = new CBPLoadTimeMemoryTask(model, cbpFile, outputLoadTimeFile,
//						outputLoadMemoryFile, n);
//				// task.setDaemon(true);
//				task.setName("CBPLoadTimeMemory");
//				System.gc();
//				task.start();
//				task.join();
//			}

			for (int n = 0; n < ITERATION; n++) {
				System.out.println("Filling " + (n + 1) + " - XMI Load and Save Time and Memory");
				XMILoadSaveTimeMemoryTask task = new XMILoadSaveTimeMemoryTask(model, xmiFile, outputLoadTimeFile,
						outputLoadMemoryFile, outputSaveTimeFile, outputSaveMemoryFile, n);
				task.setName("XMILoadSaveTimeMemory");
				System.gc();
				task.start();
				task.join();
			}

//			for (int n = 0; n < ITERATION; n++) {
//				System.out.println("Filling " + (n + 1) + " - OCBP Load Time and Memory");
//				OCBPLoadTimeMemoryTask task = new OCBPLoadTimeMemoryTask(model, cbpFile, ignoreFile, outputLoadTimeFile,
//						outputLoadMemoryFile, n);
//				// task.setDaemon(true);
//				task.setName("OCBPLoadTimeMemory");
//				System.gc();
//				task.start();
//				task.join();
//			}
//
//			System.out.println("Filling OCBP Save Time and Memory");
//			OCBPSaveTimeMemoryTask task1 = new OCBPSaveTimeMemoryTask(model, cbpFile, ignoreFile, outputSaveTimeFile,
//					outputSaveMemoryFile);
//			// task.setDaemon(true);
//			task1.setName("OCBPSaveTimeMemory");
//			System.gc();
//			task1.start();
//			task1.join();
//
//			System.out.println("Filling CBP Save Time and Memory");
//			CBPSaveTimeMemoryTask task2 = new CBPSaveTimeMemoryTask(model, cbpFile, outputSaveTimeFile,
//					outputSaveMemoryFile);
//			// task.setDaemon(true);
//			task2.setName("CBPSaveTimeMemory");
//			System.gc();
//			task2.start();
//			task2.join();

		}

		System.out.println("End: " + (new Date()).toString());
		assertEquals(true, true);
	}

	abstract class Task extends Thread {
		String type;
		String model;
		File cbpFile;
		File ignoreFile;
		File outputFile;
		int iteration;

		public Task(String model, File cbpFile, File ignorFile, File outputFile) {
			this.model = model;
			this.cbpFile = cbpFile;
			this.ignoreFile = ignorFile;
			this.outputFile = outputFile;
		}
	}

	class XMILoadSaveTimeMemoryTask extends Task {
		File xmiFile;
		File xmiOutputLoadTimeFile;
		File xmiOutputLoadMemoryFile;
		File xmiOutputSaveTimeFile;
		File xmiOutputSaveMemoryFile;

		public XMILoadSaveTimeMemoryTask(String model, File xmiFile, File xmiOutputLoadTimeFile,
				File xmiOutputLoadMemoryFile, File xmiOutputSaveTimeFile, File xmiOutputSaveMemoryFile, int iteration) {
			super(null, null, null, null);
			this.type = "XMI";
			this.model = model;
			this.xmiFile = xmiFile;
			this.xmiOutputLoadTimeFile = xmiOutputLoadTimeFile;
			this.xmiOutputLoadMemoryFile = xmiOutputLoadMemoryFile;
			this.xmiOutputSaveTimeFile = xmiOutputSaveTimeFile;
			this.xmiOutputSaveMemoryFile = xmiOutputSaveMemoryFile;
			this.iteration = iteration;
		}

		@Override
		public void run() {
			super.run();
			try {
				PrintStream printer = null;

				Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
				xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
						XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

				XMIResource xmiResource = (XMIResource) (new XMIResourceFactoryImpl())
						.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));

				System.gc();
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();
				xmiResource.load(xmiOptions);
				long endTime = System.nanoTime();
				System.gc();
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				double xmiLoadTime = (endTime - startTime) / 1000000000.0;
				double xmiLoadMemory = 0;
				if (endMemory - startMemory < 0) {
					xmiLoadMemory = 0;
				} else {
					xmiLoadMemory = Math.abs(endMemory - startMemory) / (1024.0 * 1024.0);
				}

				if (iteration >= START_FROM) {
					printer = new PrintStream(new FileOutputStream(xmiOutputLoadTimeFile, true));
					printer.printf("%f,%s\n", xmiLoadTime, type);
					printer.flush();
					printer.close();

					printer = new PrintStream(new FileOutputStream(xmiOutputLoadMemoryFile, true));
					printer.printf("%f,%s\n", xmiLoadMemory, type);
					printer.flush();
					printer.close();
				}

				System.gc();
				startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				startTime = System.nanoTime();
				xmiResource.save(xmiOptions);
				endTime = System.nanoTime();
				Thread.sleep(SLEEP_TIME);
				System.gc();
				endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				double xmiSaveTime = (endTime - startTime) / 1000000000.0;
				double xmiSaveMemory = 0;
				if (endMemory - startMemory < 0) {
					xmiSaveMemory = 0;
				} else {
					xmiSaveMemory = Math.abs(endMemory - startMemory) / (1024.0 * 1024.0);
				}
				xmiResource.unload();

				if (iteration >= START_FROM) {
					printer = new PrintStream(new FileOutputStream(xmiOutputSaveTimeFile, true));
					printer.printf("%.9f,%s\n", xmiSaveTime, type);
					printer.flush();
					printer.close();

					printer = new PrintStream(new FileOutputStream(xmiOutputSaveMemoryFile, true));
					printer.printf("%.9f,%s\n", xmiSaveMemory, type);
					printer.flush();
					printer.close();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class CBPSaveTimeMemoryTask extends Task {
		File cbpOutputTimeFile;
		File cbpOutputMemoryFile;

		public CBPSaveTimeMemoryTask(String model, File cbpFile, File cbpOutputTimeFile, File cbpOutputMemoryFile) {
			super(null, null, null, null);
			this.type = "CBP";
			this.model = model;
			this.cbpFile = cbpFile;
			this.cbpOutputTimeFile = cbpOutputTimeFile;
			this.cbpOutputMemoryFile = cbpOutputMemoryFile;
		}

		@Override
		public void run() {
			super.run();
			try {
				File dummyCbpFile = new File(sourcePath + File.separator + "_temp.cbpxml");
				if (dummyCbpFile.exists())
					dummyCbpFile.delete();

				Map<Object, Object> cbpSaveOptions = new HashMap<>();
				cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
				CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
						.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

				cbpResource.load(new ByteArrayInputStream(new byte[0]), cbpSaveOptions);
				cbpResource.setURI(URI.createFileURI(dummyCbpFile.getAbsolutePath()));

				cbpResource.getChangeEvents().clear();

				LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(cbpFile));
				lineNumberReader.skip(Long.MAX_VALUE);
				int totalEventCount = lineNumberReader.getLineNumber();
				lineNumberReader.close();

				BufferedReader reader = new BufferedReader(new FileReader(cbpFile));
				String line;
				int lineCount = 0;
				int iterationCount = 0;
				while ((line = reader.readLine()) != null) {

					if (lineCount % 100000 == 0)
						System.out.println(lineCount);

					if (iterationCount >= ITERATION)
						break;

					InputStream stream = new ByteArrayInputStream(line.getBytes());
					try {
						cbpResource.loadAdditionalEvents(stream);
					} catch (Exception e) {
						e.printStackTrace();
					}
					stream.close();

					if (lineCount >= totalEventCount - ITERATION + START_FROM) {
						System.out.println(line);
						System.gc();
						long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
						long startTime = System.nanoTime();
						cbpResource.save(cbpSaveOptions);
						long endTime = System.nanoTime();
						System.gc();
						Thread.sleep(SLEEP_TIME);
						long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
						double optSaveTime = (endTime - startTime) / 1000000000.0;
						double optSaveMemory = 0;
						if (endMemory - startMemory < 0) {
							optSaveMemory = 0;
						} else {
							optSaveMemory = Math.abs(endMemory - startMemory) / 1000000.0;
						}

						PrintStream printer = new PrintStream(new FileOutputStream(cbpOutputTimeFile, true));
						printer.printf("%f,%s\n", optSaveTime, type);
						printer.flush();
						printer.close();

						printer = new PrintStream(new FileOutputStream(cbpOutputMemoryFile, true));
						printer.printf("%f,%s\n", optSaveMemory, type);
						printer.flush();
						printer.close();

					} else {
						cbpResource.save(new ByteArrayOutputStream(), null);
						// cbpResource.save(null);
					}
					lineCount += 1;
				}
				reader.close();
				cbpResource.unload();

				if (dummyCbpFile.exists())
					dummyCbpFile.delete();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class OCBPSaveTimeMemoryTask extends Task {
		File cbpOutputTimeFile;
		File cbpOutputMemoryFile;

		public OCBPSaveTimeMemoryTask(String model, File cbpFile, File ignoreFile, File cbpOutputTimeFile,
				File cbpOutputMemoryFile) {
			super(null, null, null, null);
			this.type = "OCBP";
			this.model = model;
			this.cbpFile = cbpFile;
			this.ignoreFile = ignoreFile;
			this.cbpOutputTimeFile = cbpOutputTimeFile;
			this.cbpOutputMemoryFile = cbpOutputMemoryFile;
		}

		@Override
		public void run() {
			super.run();
			try {
				File dummyCbpFile = new File(sourcePath + File.separator + "_temp.cbpxml");
				File dummyIgnoreFile = new File(sourcePath + File.separator + "_temp.ignoreset");
				if (dummyCbpFile.exists())
					dummyCbpFile.delete();
				if (dummyIgnoreFile.exists())
					dummyIgnoreFile.delete();

				Map<Object, Object> cbpSaveOptions = new HashMap<>();
				cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
				CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
						.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

				cbpResource.loadIgnoreSet(new FileInputStream(ignoreFile));
				int ignoredEventCount = cbpResource.getIgnoreSet().size();

				cbpResource.load(new ByteArrayInputStream(new byte[0]), cbpSaveOptions);
				cbpResource.setURI(URI.createFileURI(dummyCbpFile.getAbsolutePath()));

				cbpResource.getChangeEvents().clear();

				LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(cbpFile));
				lineNumberReader.skip(Long.MAX_VALUE);
				int totalEventCount = lineNumberReader.getLineNumber();
				lineNumberReader.close();

				BufferedReader reader = new BufferedReader(new FileReader(cbpFile));
				String line;
				int lineCount = 0;
				int iterationCount = 0;
				int unignoredLineCount = 0;
				while ((line = reader.readLine()) != null) {

					if (lineCount % 100000 == 0)
						System.out.println(lineCount);

					if (iterationCount >= ITERATION)
						break;

					if (cbpResource.getIgnoreSet().contains(lineCount) == false) {

						InputStream stream = new ByteArrayInputStream(line.getBytes());
						try {
							cbpResource.loadAdditionalEvents(stream);
						} catch (Exception e) {
							e.printStackTrace();
						}
						stream.close();

						if (unignoredLineCount >= totalEventCount - ignoredEventCount - ITERATION + START_FROM) {
							System.out.println(line);
							System.gc();
							long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
							long startTime = System.nanoTime();
							cbpResource.save(cbpSaveOptions);
							cbpResource.saveIgnoreSet(new FileOutputStream(dummyIgnoreFile, true));
							long endTime = System.nanoTime();
							System.gc();
							Thread.sleep(SLEEP_TIME);
							long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
							double optSaveTime = (endTime - startTime) / 1000000000.0;
							double optSaveMemory = 0;
							if (endMemory - startMemory < 0) {
								optSaveMemory = 0;
							} else {
								optSaveMemory = Math.abs(endMemory - startMemory) / 1000000.0;
							}

							PrintStream printer = new PrintStream(new FileOutputStream(cbpOutputTimeFile, true));
							printer.printf("%f,%s\n", optSaveTime, type);
							printer.flush();
							printer.close();

							printer = new PrintStream(new FileOutputStream(cbpOutputMemoryFile, true));
							printer.printf("%f,%s\n", optSaveMemory, type);
							printer.flush();
							printer.close();

							iterationCount += 1;
						} else {
							cbpResource.save(new ByteArrayOutputStream(), null);
							// FileOutputStream baos = new
							// FileOutputStream(dummyIgnoreFile, true);
							cbpResource.clearIgnoreSet();
							// baos.close();
						}

						unignoredLineCount += 1;
					}
					lineCount += 1;
				}
				reader.close();
				cbpResource.unload();

				if (dummyCbpFile.exists())
					dummyCbpFile.delete();
				if (dummyIgnoreFile.exists())
					dummyIgnoreFile.delete();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class OCBPLoadTimeMemoryTask extends Task {
		File cbpOutputTimeFile;
		File cbpOutputMemoryFile;
		File xmiOutputTimeFile;
		File xmiOutputMemoryFile;

		public OCBPLoadTimeMemoryTask(String model, File cbpFile, File ignoreFile, File cbpOutputTimeFile,
				File cbpOutputMemoryFile, int iteration) {
			super(null, null, null, null);
			this.type = "OCBP";
			this.model = model;
			this.cbpFile = cbpFile;
			this.ignoreFile = ignoreFile;
			this.cbpOutputTimeFile = cbpOutputTimeFile;
			this.cbpOutputMemoryFile = cbpOutputMemoryFile;
			this.iteration = iteration;
		}

		@Override
		public void run() {
			super.run();
			try {
				CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
						.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

				System.gc();
				Thread.sleep(1000);
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();
				cbpResource.loadIgnoreSet(new FileInputStream(ignoreFile));
				cbpResource.load(null);
				long endTime = System.nanoTime();
				System.gc();
				Thread.sleep(1000);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				double nonOptLoadTime = (endTime - startTime) / 1000000000.0;
				double nonOptLoadMemory;
				if (endMemory - startMemory < 0) {
					nonOptLoadMemory = 0;
				} else {
					nonOptLoadMemory = Math.abs(endMemory - startMemory) / (1024.0 * 1024.0);
				}

				if (iteration >= START_FROM) {
					PrintStream printer = new PrintStream(new FileOutputStream(cbpOutputTimeFile, true));
					printer.printf("%f,%s\n", nonOptLoadTime, type);
					printer.flush();
					printer.close();

					printer = new PrintStream(new FileOutputStream(cbpOutputMemoryFile, true));
					printer.printf("%f,%s\n", nonOptLoadMemory, type);
					printer.flush();
					printer.close();
				}
				cbpResource.unload();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class CBPLoadTimeMemoryTask extends Task {
		File cbpOutputTimeFile;
		File cbpOutputMemoryFile;
		File xmiOutputTimeFile;
		File xmiOutputMemoryFile;

		public CBPLoadTimeMemoryTask(String model, File cbpFile, File cbpOutputTimeFile, File cbpOutputMemoryFile,
				int iteration) {
			super(null, null, null, null);
			this.type = "CBP";
			this.model = model;
			this.cbpFile = cbpFile;
			this.cbpOutputTimeFile = cbpOutputTimeFile;
			this.cbpOutputMemoryFile = cbpOutputMemoryFile;
			this.iteration = iteration;
		}

		@Override
		public void run() {
			super.run();
			try {
				Map<Object, Object> cbpLoadOptions = new HashMap<>();
				cbpLoadOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, false);
				CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
						.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

				System.gc();
				Thread.sleep(1000);
				long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				long startTime = System.nanoTime();
				cbpResource.load(cbpLoadOptions);
				long endTime = System.nanoTime();
				System.gc();
				Thread.sleep(1000);
				long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				double nonOptLoadTime = (endTime - startTime) / 1000000000.0;
				double nonOptLoadMemory;
				if (endMemory - startMemory < 0) {
					nonOptLoadMemory = 0;
				} else {
					nonOptLoadMemory = Math.abs(endMemory - startMemory) / (1024.0 * 1024.0);
				}
				if (iteration >= START_FROM) {
					PrintStream printer = new PrintStream(new FileOutputStream(cbpOutputTimeFile, true));
					printer.printf("%f,%s\n", nonOptLoadTime, type);
					printer.flush();
					printer.close();

					printer = new PrintStream(new FileOutputStream(cbpOutputMemoryFile, true));
					printer.printf("%f,%s\n", nonOptLoadMemory, type);
					printer.flush();
					printer.close();
				}

				cbpResource.unload();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class DataDescriptionTask extends Task {
		File xmiFile;

		public DataDescriptionTask(String model, File cbpFile, File ignoreFile, File xmiFile, File outputFile) {
			super(model, cbpFile, ignoreFile, outputFile);
			this.xmiFile = xmiFile;
		}

		@Override
		public void run() {
			super.run();
			try {
				System.out.println("Generating " + outputFile.getName());
				Map<Object, Object> cbpSaveOptions = new HashMap<>();
				// cbpSaveOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD,
				// false);
				CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
						.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

				// ignore event count
				cbpResource.loadIgnoreSet(new FileInputStream(ignoreFile));
				int ignoredEventCount = cbpResource.getIgnoreSet().size();

				cbpResource.load(cbpSaveOptions);

				// total event count
				LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(cbpFile));
				lineNumberReader.skip(Long.MAX_VALUE);
				int totalEventCount = lineNumberReader.getLineNumber();
				lineNumberReader.close();

				// session count
				int sessionCount = 0;
				sessionCount = cbpResource.getSessionCount();

				// element count
				int elementCount = 0;
				TreeIterator<EObject> iterator = cbpResource.getAllContents();
				while (iterator.hasNext()) {
					iterator.next();
					elementCount += 1;
				}

				PrintStream printer = new PrintStream(outputFile);
				printer.println("Model,TotalEvents,IgnoredEvents,ElementCount,SessionCount");
				printer.println(model + "," + totalEventCount + "," + ignoredEventCount + "," + elementCount + ","
						+ sessionCount);
				printer.flush();
				printer.close();

				// save xmi
				Map<Object, Object> xmiOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
				xmiOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF,
						XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
				XMIResourceImpl xmiResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
						.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
				xmiResource.getContents().addAll(cbpResource.getContents());
				xmiResource.save(xmiOptions);
				xmiResource.unload();

				cbpResource.unload();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
