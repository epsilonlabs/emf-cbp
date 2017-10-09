package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.github.jamm.MemoryMeter;

import com.javamex.classmexer.MemoryUtil;

import objectexplorer.MemoryMeasurer;

public abstract class MemoryPerformanceTests {

	private final String extension;

	protected String eol = "";
	protected String cbpXml = "";
	protected StringBuilder errorMessage = new StringBuilder();
	protected StringBuilder outputText = new StringBuilder();
	protected StringOutputStream xmiOutputStream = null;
	protected StringOutputStream cbpOutputStream = null;
	protected long beforeSaveXMI = 0;
	protected long afterSaveXMI = 0;
	protected long beforeSaveCBP = 0;
	protected long afterSaveCBP = 0;
	protected long numberOfNodes = 0;

	protected long ignoreListSize = 0;
	protected long changeEventsSize = 0;
	protected long modelHistorySize = 0;
	protected long cbpSize = 0;
	protected long xmiSize = 0;

	protected Set<Integer> ignoreList = null;
	
	MemoryMeter memoryMeter = new MemoryMeter();

	public MemoryPerformanceTests(String extension) {
		this.extension = extension;
	}

	public abstract EPackage getEPackage();

	public abstract Class<?> getNodeClass();

	// Save XMI -------------------------------------------
	public void testSaveXMI(String eol, String extension, boolean debug) {
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
			
			xmiSize = MemoryMeasurer.measureBytes(((XMIResource) xmiResource1));
//		    xmiSize = memoryMeter.measureDeep(((XMIResource) xmiResource1));
//			xmiSize = MemoryUtil.deepMemoryUsageOf(((XMIResource) xmiResource1));

		} catch (Exception e) {
			errorMessage.append(e.toString() + "\n");
			e.printStackTrace();
		}
	}

	public void testSaveCBP(String eol, String extension, boolean debug) {
		try {
			EolModule moduleCbp = new EolModule();
			moduleCbp.parse(eol);
			ResourceSet cbpResourceSet1 = createResourceSet();
			Resource cbpResource1 = cbpResourceSet1.createResource(URI.createURI("foo3." + extension));
			InMemoryEmfModel modelCbp = new InMemoryEmfModel("M", cbpResource1, getEPackage());
			moduleCbp.getContext().getModelRepository().addModel(modelCbp);
			moduleCbp.execute();

			ignoreList = ((CBPResource) cbpResource1).getIgnoreSet();
			cbpOutputStream = new StringOutputStream();
			
			
			List<ChangeEvent<?>> oldList = ((CBPResource) cbpResource1).getChangeEventAdapter().getChangeEvents();
			
			changeEventsSize = MemoryMeasurer.measureBytes(oldList);
			cbpSize = MemoryMeasurer.measureBytes((CBPResource) cbpResource1);
			
//			changeEventsSize = memoryMeter.measureDeep(oldList);
//			cbpSize = memoryMeter.measureDeep((CBPResource) cbpResource1);
//			changeEventsSize = MemoryUtil.deepMemoryUsageOf(oldList);
//			cbpSize = MemoryUtil.deepMemoryUsageOf((CBPResource) cbpResource1);
					
			cbpResource1.save(cbpOutputStream, null);
			
			cbpResource1.load(new ByteArrayInputStream(cbpOutputStream.toString().getBytes()), null);
			modelHistorySize = MemoryMeasurer.measureBytes(((CBPResource) cbpResource1).getModelHistory());
			ignoreListSize = MemoryMeasurer.measureBytes(((CBPResource) cbpResource1).getIgnoreSet());
			
//			modelHistorySize = memoryMeter.measureDeep(((CBPResource) cbpResource1).getModelHistory());
//			ignoreListSize = memoryMeter.measureDeep(((CBPResource) cbpResource1).getIgnoreList());
//			modelHistorySize = MemoryUtil.deepMemoryUsageOf(((CBPResource) cbpResource1).getModelHistory());
//			ignoreListSize = MemoryUtil.deepMemoryUsageOf(((CBPResource) cbpResource1).getIgnoreSet());
		
//			cbpXml = cbpOutputStream.toString();
		} catch (Exception e) {
			errorMessage.append(e.toString() + "\n");
			e.printStackTrace();
		}
	}

	public void run(String eol, String extension, boolean debug) throws Exception {

		try {
			this.eol = eol;
			int iteration = 1;
			double sumNumOfNodes = 0;
			double sumModelMemorySize = 0;
			double sumCbpSize = 0;
			double sumXmiSize = 0;
			double sumIgnoreListSize = 0;
			double sumChangeEventsSize = 0;

			for (int i = 0; i < iteration; i++) {

				this.testSaveCBP(eol, extension, debug);
				this.testSaveXMI(eol, extension, debug);

				sumNumOfNodes = sumNumOfNodes + numberOfNodes;
				sumModelMemorySize = sumModelMemorySize + ((double) modelHistorySize / (1000000));
				sumCbpSize = sumCbpSize + ((double) cbpSize / (1000000));
				sumXmiSize = sumXmiSize + ((double) xmiSize / (1000000));
				sumIgnoreListSize = sumIgnoreListSize + ((double) ignoreListSize / (1000000));
				sumChangeEventsSize = sumChangeEventsSize + ((double) changeEventsSize / (1000000));

				xmiOutputStream.reset();
				cbpOutputStream.reset();
			}

			sumNumOfNodes = sumNumOfNodes / iteration;
			sumModelMemorySize = sumModelMemorySize / iteration;
			sumXmiSize = sumXmiSize / iteration;
			sumCbpSize = sumCbpSize / iteration;
			sumIgnoreListSize = sumIgnoreListSize / iteration;
			sumChangeEventsSize = sumChangeEventsSize / iteration;

			appendLineToOutputText(String.format("%1$.0f\t%2$.6f\t%3$.6f\t%4$.6f\t%5$.6f\t%6$.6f", sumNumOfNodes, sumModelMemorySize,
					sumIgnoreListSize, sumChangeEventsSize, sumCbpSize, sumXmiSize));

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
		// xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpthrift",
		// new CBPThriftResourceFactory());
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
