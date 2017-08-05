package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class LoadingPerformanceTests {

	private final String extension;

	protected String eol = "";
	protected String cbpXml = "";
	protected StringBuilder errorMessage = new StringBuilder();
	protected StringBuilder outputText = new StringBuilder();
	protected StringOutputStream xmiOutputStream = null;
	protected StringOutputStream cbpOutputStream = null;
	protected Thread threadSaveXMI = null;
	protected Thread threadLoadXMI = null;
	protected Thread threadSaveCBP = null;
	protected Thread threadLoadCBP = null;
	protected Thread threadLoadOptCBP = null;
	protected long beforeSaveXMI = 0;
	protected long afterSaveXMI = 0;
	protected long beforeLoadXMI = 0;
	protected long afterLoadXMI = 0;
	protected long beforeSaveCBP = 0;
	protected long afterSaveCBP = 0;
	protected long beforeLoadCBP = 0;
	protected long afterLoadCBP = 0;
	protected long beforeLoadOptCBP = 0;
	protected long afterLoadOptCBP = 0;
	protected long numberOfNodes = 0;
	protected long numOfLineCBP = 0;
	protected long numOfLineOptCBP = 0;

	protected Set<Integer> ignoreList = null;

	public LoadingPerformanceTests(String extension) {
		this.extension = extension;
	}

	public abstract EPackage getEPackage();
	public abstract Class<?> getNodeClass();

	// Save XMI -------------------------------------------
	public void testSaveXMI(String eol, String extension, boolean debug) throws Exception {
		threadSaveXMI = new Thread() {
			@Override
			public void run() {
				try {
					super.run();
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
					beforeSaveXMI = System.currentTimeMillis();
					xmiResource1.save(xmiOutputStream, null);
					afterSaveXMI = System.currentTimeMillis();
				} catch (Exception e) {
					errorMessage.append(e.toString() + "\n");
					e.printStackTrace();
				}
			}
		};
		threadSaveXMI.start();
		// threadSaveXMI.join();
	}

	public void testLoadXMI(String eol, String extension, boolean debug, Thread previousThread) throws Exception {
		threadLoadXMI = new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					ResourceSet xmiResourceSet2 = createResourceSet();
					xmiResourceSet2.setPackageRegistry(EPackage.Registry.INSTANCE);
					XMIResourceFactoryImpl xmiFactory = new XMIResourceFactoryImpl();
					xmiResourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", xmiFactory);
					Resource xmiResource2 = xmiResourceSet2.createResource(URI.createURI("foo.xmi"));

					if (previousThread != null) {
						while (previousThread.isAlive()) {
							try {
								previousThread.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					String sXMI = xmiOutputStream.toString();
					beforeLoadXMI = System.currentTimeMillis();
					xmiResource2.load(new ByteArrayInputStream(sXMI.getBytes()), null);
					afterLoadXMI = System.currentTimeMillis();
				} catch (IOException e) {
					errorMessage.append(e.toString() + "\n");
					e.printStackTrace();
				}
			}
		};
		threadLoadXMI.start();
		threadLoadXMI.join();
	}

	public void testSaveCBP(String eol, String extension, boolean debug) throws Exception {
		threadSaveCBP = new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					EolModule moduleCbp = new EolModule();
					moduleCbp.parse(eol);
					ResourceSet cbpResourceSet1 = createResourceSet();
					Resource cbpResource1 = cbpResourceSet1.createResource(URI.createURI("foo3." + extension));
					InMemoryEmfModel modelCbp = new InMemoryEmfModel("M", cbpResource1, getEPackage());
					moduleCbp.getContext().getModelRepository().addModel(modelCbp);
					// moduleCbp.getContext().getModelRepository().getModels().addAll(getExtraModels());
					moduleCbp.execute();

					ignoreList = ((CBPResource) cbpResource1).getIgnoreList();

					cbpOutputStream = new StringOutputStream();
					beforeSaveCBP = System.currentTimeMillis();
					cbpResource1.save(cbpOutputStream, null);
					afterSaveCBP = System.currentTimeMillis();
				} catch (Exception e) {
					errorMessage.append(e.toString() + "\n");
					e.printStackTrace();
				}
			}
		};
		threadSaveCBP.start();
	}

	public void testLoadCBP(String eol, String extension, boolean debug, Thread previousThread) throws Exception {
		threadLoadCBP = new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					ResourceSet cbpResourceSet3 = createResourceSet();
					cbpResourceSet3.setPackageRegistry(EPackage.Registry.INSTANCE);
					CBPXMLResourceFactory factory3 = new CBPXMLResourceFactory();
					cbpResourceSet3.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory3);
					Resource cbpResource3 = cbpResourceSet3.createResource(URI.createURI("foo4." + extension));

					if (previousThread != null) {
						while (previousThread.isAlive()) {
							try {
								previousThread.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					String sCBP2 = cbpOutputStream.toString();
					((CBPResource) cbpResource3).setIgnoreList(new TreeSet<>(ignoreList));
					Map<Object, Object> options = new HashMap<>();
					options.put("optimise", false);
					beforeLoadCBP = System.currentTimeMillis();
					cbpResource3.load(new ByteArrayInputStream(sCBP2.getBytes()), options);
					afterLoadCBP = System.currentTimeMillis();
				} catch (IOException e) {
					errorMessage.append(e.toString() + "\n");
					e.printStackTrace();
				}
			}
		};
		threadLoadCBP.start();
		threadLoadCBP.join();
	}

	public void testLoadOptimisedCBP(String eol, String extension, boolean debug, Thread previousThread)
			throws Exception {
		threadLoadOptCBP = new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					ResourceSet cbpResourceSet2 = createResourceSet();
					cbpResourceSet2.setPackageRegistry(EPackage.Registry.INSTANCE);
					CBPXMLResourceFactory factory2 = new CBPXMLResourceFactory();
					cbpResourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory2);
					Resource cbpResource2 = cbpResourceSet2.createResource(URI.createURI("foo5." + extension));

					if (previousThread != null) {
						while (previousThread.isAlive()) {
							try {
								previousThread.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					cbpXml = cbpOutputStream.toString();
					String sCBP1 = cbpOutputStream.toString();

					numOfLineCBP = 0;
					numOfLineCBP = sCBP1.split("\r\n|\r|\n").length;
					numOfLineOptCBP = numOfLineCBP - ignoreList.size();

					((CBPResource) cbpResource2).setIgnoreList(new TreeSet<>(ignoreList));
					beforeLoadOptCBP = System.currentTimeMillis();
					cbpResource2.load(new ByteArrayInputStream(sCBP1.getBytes()), null);
					afterLoadOptCBP = System.currentTimeMillis();
				} catch (IOException e) {
					errorMessage.append(e.toString() + "\n");
					e.printStackTrace();
					try {
						saveErrorMessages();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						saveOutputText();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}
		};
		threadLoadOptCBP.start();
		threadLoadOptCBP.join();
	}

	public void run(String eol, String extension, boolean debug) throws Exception {

		try {
			this.eol = eol;
			int iteration = 10;
			double deltaSaveXMI = 0;
			double deltaLoadXMI = 0;
			double deltaSaveCBP = 0;
			double deltaLoadOCBP = 0;
			double deltaLoadCBP = 0;
			double sumNumOfNodes = 0;
			double sumNumOfLineCBP = 0;
			double sumNumOfLineOptCBP = 0;

			for (int i = 1; i <= iteration; i++) {
				int xmiorcbp = ThreadLocalRandom.current().nextInt(2);
				if (xmiorcbp == 0) {
					this.testSaveXMI(eol, extension, debug);
					this.testLoadXMI(eol, extension, debug, threadSaveXMI);
					this.testSaveCBP(eol, extension, debug);
					int optcbporcbp = ThreadLocalRandom.current().nextInt(2);
					if (optcbporcbp == 0) {
						this.testLoadOptimisedCBP(eol, extension, debug, threadSaveCBP);
						this.testLoadCBP(eol, extension, debug, threadSaveCBP);
					} else {
						this.testLoadCBP(eol, extension, debug, threadSaveCBP);
						this.testLoadOptimisedCBP(eol, extension, debug, threadSaveCBP);
					}
				} else {
					this.testSaveCBP(eol, extension, debug);
					int optcbporcbp = ThreadLocalRandom.current().nextInt(2);
					if (optcbporcbp == 0) {
						this.testLoadOptimisedCBP(eol, extension, debug, threadSaveCBP);
						this.testLoadCBP(eol, extension, debug, threadSaveCBP);
					} else {
						this.testLoadCBP(eol, extension, debug, threadSaveCBP);
						this.testLoadOptimisedCBP(eol, extension, debug, threadSaveCBP);
					}
					this.testSaveXMI(eol, extension, debug);
					this.testLoadXMI(eol, extension, debug, threadSaveXMI);
				}

				threadSaveCBP.join();
				threadLoadCBP.join();
				threadSaveXMI.join();
				threadLoadXMI.join();
				threadLoadOptCBP.join();

				// appendLineToOutputText(xmiOutputStream.toString());
				// appendLineToOutputText(cbpOutputStream.toString());

				deltaSaveXMI = deltaSaveXMI + ((double) (afterSaveXMI - beforeSaveXMI)) / 1000;
				deltaLoadXMI = deltaLoadXMI + ((double) (afterLoadXMI - beforeLoadXMI)) / 1000;
				deltaSaveCBP = deltaSaveCBP + ((double) (afterSaveCBP - beforeSaveCBP)) / 1000;
				deltaLoadOCBP = deltaLoadOCBP + ((double) (afterLoadOptCBP - beforeLoadOptCBP)) / 1000;
				deltaLoadCBP = deltaLoadCBP + ((double) (afterLoadCBP - beforeLoadCBP)) / 1000;
				sumNumOfNodes = sumNumOfNodes + numberOfNodes;
				sumNumOfLineOptCBP = sumNumOfLineOptCBP + numOfLineOptCBP;
				sumNumOfLineCBP = sumNumOfLineCBP + numOfLineCBP;

				xmiOutputStream.reset();
				cbpOutputStream.reset();
			}

			deltaSaveXMI = deltaSaveXMI / iteration;
			deltaLoadXMI = deltaLoadXMI / iteration;
			deltaSaveCBP = deltaSaveCBP / iteration;
			deltaLoadOCBP = deltaLoadOCBP / iteration;
			deltaLoadCBP = deltaLoadCBP / iteration;
			sumNumOfNodes = sumNumOfNodes / iteration;
			sumNumOfLineOptCBP = sumNumOfLineOptCBP / iteration;
			sumNumOfLineCBP = sumNumOfLineCBP / iteration;

			appendLineToOutputText(String.format("%1$.4f\t%2$.4f\t%3$.4f\t%4$.4f\t%5$.4f\t%6$.0f\t%7$.0f\t%8$.0f",
					deltaSaveXMI, deltaSaveCBP, deltaLoadXMI, deltaLoadOCBP, deltaLoadCBP, sumNumOfNodes,
					sumNumOfLineOptCBP, sumNumOfLineCBP));

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
	
	protected void appendToOutputText(String text){
		outputText.append(text);
		System.out.print(text);
	}
	
	protected void appendLineToOutputText(String text){
		outputText.append(text+ "\n");
		System.out.println(text);
	}
	
	protected void saveErrorMessages() throws FileNotFoundException{
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
	
	protected void saveOutputText() throws FileNotFoundException{
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
