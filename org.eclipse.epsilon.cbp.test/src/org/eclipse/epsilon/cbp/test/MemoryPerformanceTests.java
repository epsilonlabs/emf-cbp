package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.github.jamm.MemoryMeter;

public abstract class MemoryPerformanceTests {

	private final String extension;

	protected String eol = "";
	protected String cbpXml = "";
	protected StringBuilder errorMessage = new StringBuilder();
	protected StringBuilder outputText = new StringBuilder();
	protected StringOutputStream xmiOutputStream = null;

	protected long beforeSaveXMI = 0;
	protected long afterSaveXMI = 0;
	protected long beforeSaveCBP = 0;
	protected long afterSaveCBP = 0;
	protected long numberOfNodes = 0;

	protected long ignoreListSize = 0;
	protected long changeEventsSize = 0;

	protected long optimisedCbpSize = 0;
	protected long xmiSize = 0;

	protected Set<Integer> optimisedIgnoreList = null;
	protected StringOutputStream optimisedCbpOutputStream = null;
	protected MemoryMeter optimisedMemoryMeter = new MemoryMeter();
	protected long optimisedModelHistorySize = 0;

	protected Set<Integer> nonOptimisedIgnoreList = null;
	protected StringOutputStream nonOptimisedCbpOutputStream = null;
	protected MemoryMeter nonOptimisedMemoryMeter = new MemoryMeter();
	protected long nonOptimisedCbpSize = 0;

	public MemoryPerformanceTests(String extension) {
		this.extension = extension;
	}

	public abstract EPackage getEPackage();

	public abstract Class<?> getNodeClass();

	// Save XMI -------------------------------------------
	public void testSaveAndLoadXMI(String eol, String extension, boolean debug) {
		try {
			EolModule moduleXmi = new EolModule();
			moduleXmi.parse(eol);

			ResourceSet xmiResourceSet1 = createResourceSet();
			Resource xmiResource1 = xmiResourceSet1.createResource(URI.createURI("foo.xmi"));
			InMemoryEmfModel modelXmi = new InMemoryEmfModel("M", xmiResource1, getEPackage());
			moduleXmi.getContext().getModelRepository().addModel(modelXmi);
			moduleXmi.execute();

			if (xmiOutputStream != null) {
				xmiOutputStream.reset();
				xmiOutputStream.close();
			} else {
				xmiOutputStream = new StringOutputStream();
			}

			TreeIterator<EObject> iterator = xmiResource1.getAllContents();
			numberOfNodes = 0;
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				if (getNodeClass().isInstance(eObject)) {
					numberOfNodes += 1;
				}
			}
			xmiResource1.save(xmiOutputStream, null);
			xmiResource1.getContents().clear();
			System.gc();
			long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			xmiResource1.load(new ByteArrayInputStream(xmiOutputStream.toString().getBytes()), null);
			long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			xmiSize = after - before;
//			xmiSize = optimisedMemoryMeter.measureDeep(((XMIResource) xmiResource1));

		} catch (Exception e) {
			errorMessage.append(e.toString() + "\n");
			e.printStackTrace();
		}
	}
	
	public void testSaveAndLoadCBP(String eol, String extension, boolean debug) {
		try {
			EolModule moduleCbp = new EolModule();
			moduleCbp.parse(eol);
			ResourceSet cbpResourceSet1 = createResourceSet();
			Resource optimisedCbpResource = cbpResourceSet1.createResource(URI.createURI("foo3." + extension));
			InMemoryEmfModel modelCbp = new InMemoryEmfModel("M", optimisedCbpResource, getEPackage());
			moduleCbp.getContext().getModelRepository().addModel(modelCbp);
			moduleCbp.execute();

			optimisedIgnoreList = ((CBPResource) optimisedCbpResource).getIgnoreSet();
			optimisedCbpOutputStream = new StringOutputStream();

			optimisedCbpResource.save(optimisedCbpOutputStream, null);
			
			optimisedCbpResource.getContents().clear();
			((CBPResource) optimisedCbpResource).getModelHistory().clear();
			System.gc();
			long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			optimisedCbpResource.load(new ByteArrayInputStream(optimisedCbpOutputStream.toString().getBytes()), null);
			long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			optimisedCbpSize = after - before;
//			optimisedCbpSize = optimisedMemoryMeter.measureDeep(((CBPResource) optimisedCbpResource));
			
			
			optimisedCbpResource.getContents().clear();
			((CBPResource) optimisedCbpResource).getModelHistory().clear();
			((CBPResource) optimisedCbpResource).clearIgnoreSet();
			System.gc();
			before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			optimisedCbpResource.load(new ByteArrayInputStream(optimisedCbpOutputStream.toString().getBytes()), null);
			after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			nonOptimisedCbpSize = after - before;
//			nonOptimisedCbpSize = optimisedMemoryMeter.measureDeep(((CBPResource) optimisedCbpResource));

		} catch (Exception e) {
			errorMessage.append(e.toString() + "\n");
			e.printStackTrace();
		}
	}


	public void run(String eol, String extension, boolean debug) throws Exception {

		try {
			this.eol = eol;
			int iteration = 3;
			double sumNumOfNodes = 0;
			double sumNonOptimiseCbpSize = 0;
			double sumOptimiseCbpSize = 0;
			double sumXmiSize = 0;


			for (int i = 0; i < iteration; i++) {
				
				this.testSaveAndLoadCBP(eol, extension, debug);
				this.testSaveAndLoadXMI(eol, extension, debug);

				sumNumOfNodes = sumNumOfNodes + numberOfNodes;
				sumNonOptimiseCbpSize = sumNonOptimiseCbpSize + ((double) nonOptimisedCbpSize / (1000000));
				sumOptimiseCbpSize = sumOptimiseCbpSize + ((double) optimisedCbpSize / (1000000));
				sumXmiSize = sumXmiSize + ((double) xmiSize / (1000000));
				xmiOutputStream.reset();
				optimisedCbpOutputStream.reset();
			}

			sumNumOfNodes = sumNumOfNodes / iteration;
			sumNonOptimiseCbpSize = sumNonOptimiseCbpSize / iteration;
			sumXmiSize = sumXmiSize / iteration;
			sumOptimiseCbpSize = sumOptimiseCbpSize / iteration;

			appendLineToOutputText(String.format("%1$.0f\t%2$.6f\t%3$.6f\t%4$.6f", sumNumOfNodes, sumOptimiseCbpSize,
					sumNonOptimiseCbpSize, sumXmiSize));

		} catch (Exception e) {
			errorMessage.append("\n" + eol);
			errorMessage.append(e.toString() + "\n");
			saveErrorMessages();
			e.printStackTrace();

		}
	}

	protected ResourceSet createResourceSet() {
		ResourceSet xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		return xmiResourceSet;
	}

	protected void appendToOutputText(String text) {
		outputText.append(text);
		System.out.print(text);
	}

	protected void appendLineToOutputText(String text) {
		outputText.append(text + "\n");
		System.out.println(text);
	}

	protected void saveErrorMessages() throws FileNotFoundException {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		PrintWriter out = new PrintWriter("logs" + File.separator + strDate + ".log");
		errorMessage.append("\n" + this.eol + "\n");
		errorMessage.append("\n" + this.cbpXml + "\n");
		this.appendLineToOutputText(this.eol);
		this.appendLineToOutputText(this.cbpXml);
		out.println(errorMessage.toString());
		out.close();
		errorMessage.setLength(0);
	}

	protected void saveOutputText() throws FileNotFoundException {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		PrintWriter out = new PrintWriter("output" + File.separator + strDate + ".txt");
		out.println(outputText.toString());
		out.close();
		outputText.setLength(0);
	}

	protected void run(String eol) throws Exception {
		run(eol, false);
	}

	protected void debug(String eol) throws Exception {
		run(eol, true);
	}

	protected void run(String eol, boolean debug) throws Exception {
		run(eol, extension, debug);
	}

}
