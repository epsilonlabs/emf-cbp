package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.ChangeEventAdapter;
import org.eclipse.epsilon.cbp.history.ModelHistory;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
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
	protected double valAttributeSetUnset = 0;
	protected double valReferenceSetUnset = 0;
	protected double valAttributeAddRemoveMove = 0;
	protected double valReferenceAddRemoveMove = 0;
	protected double valDelete = 0;

	protected int setAttCount = 0;
	protected int unsetAttCount = 0;
	protected int addAttCount = 0;
	protected int removeAttCount = 0;
	protected int moveAttCount = 0;
	protected int setRefCount = 0;
	protected int unsetRefCount = 0;
	protected int addRefCount = 0;
	protected int removeRefCount = 0;
	protected int moveRefCount = 0;
	protected int deleteCount = 0;
	protected int addResCount = 0;
	protected int removeResCount = 0;
	protected int packageCount = 0;
	protected int sessionCount = 0;
	protected int createCount = 0;

	protected int ignoredSetAttCount = 0;
	protected int ignoredUnsetAttCount = 0;
	protected int ignoredAddAttCount = 0;
	protected int ignoredRemoveAttCount = 0;
	protected int ignoredMoveAttCount = 0;
	protected int ignoredSetRefCount = 0;
	protected int ignoredUnsetRefCount = 0;
	protected int ignoredAddRefCount = 0;
	protected int ignoredRemoveRefCount = 0;
	protected int ignoredMoveRefCount = 0;
	protected int ignoredDeleteCount = 0;
	protected int ignoredAddResCount = 0;
	protected int ignoredRemoveResCount = 0;
	protected int ignoredPackageCount = 0;
	protected int ignoredSessionCount = 0;
	protected int ignoredCreateCount = 0;

	protected Set<Integer> ignoreList = null;

	public LoadingPerformanceTests(String extension) {
		this.extension = extension;
	}

	public abstract EPackage getEPackage();

	public abstract Class<?> getNodeClass();

	// Save XMI -------------------------------------------
	public void testSaveXMI(String eol, String extension, boolean debug, List<String> codeList) throws Exception {

		try {

			ResourceSet xmiResourceSet1 = createResourceSet();
			Resource xmiResource1 = xmiResourceSet1.createResource(URI.createURI("foo.xmi"));
			InMemoryEmfModel modelXmi = new InMemoryEmfModel("M", xmiResource1, getEPackage());

			EolModule moduleXmi = new EolModule();
			moduleXmi.getContext().getModelRepository().addModel(modelXmi);
			if (codeList != null) {
				for (String code : codeList) {
					moduleXmi.parse(code);
					moduleXmi.execute();
				}
			} else {
				moduleXmi.parse(eol);
				moduleXmi.execute();
			}

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

				if (getEPackage().equals(NodePackage.eINSTANCE)) {
					if (getNodeClass().isInstance(eObject)) {
						numberOfNodes += 1;
					}
				} else if (getEPackage().equals(ConferencePackage.eINSTANCE)) {
					EClassifier eClassifier = ConferencePackage.eINSTANCE
							.getEClassifier(getNodeClass().getSimpleName());
					if (eObject.eClass().getEAllSuperTypes().contains(eClassifier)) {
						numberOfNodes += 1;
					}
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

	public void testLoadXMI(String eol, String extension, boolean debug) throws Exception {
		try {

			ResourceSet xmiResourceSet2 = createResourceSet();
			xmiResourceSet2.setPackageRegistry(EPackage.Registry.INSTANCE);
			XMIResourceFactoryImpl xmiFactory = new XMIResourceFactoryImpl();
			xmiResourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", xmiFactory);
			Resource xmiResource2 = xmiResourceSet2.createResource(URI.createURI("foo.xmi"));

			String sXMI = xmiOutputStream.toString();
			beforeLoadXMI = System.currentTimeMillis();
			xmiResource2.load(new ByteArrayInputStream(sXMI.getBytes()), null);
			afterLoadXMI = System.currentTimeMillis();
		} catch (IOException e) {
			errorMessage.append(e.toString() + "\n");
			e.printStackTrace();
		}

	}

	public void testSaveCBP(boolean debug, List<String> codeList) throws Exception {
		try {
			ResourceSet cbpResourceSet1 = createResourceSet();
			Resource cbpResource1 = cbpResourceSet1.createResource(URI.createURI("foo3." + extension));
			InMemoryEmfModel modelCbp = new InMemoryEmfModel("M", cbpResource1, getEPackage());

			EolModule moduleCbp = new EolModule();
			moduleCbp.getContext().getModelRepository().addModel(modelCbp);
			if (codeList != null) {
				for (String code : codeList) {
					moduleCbp.parse(code);
					moduleCbp.execute();
				}
			} else {
				moduleCbp.parse(eol);
				moduleCbp.execute();
			}

			CBPResource cbpResource = (CBPResource) cbpResource1;
			ignoreList = cbpResource.getIgnoreSet();

			cbpOutputStream = new StringOutputStream();
			beforeSaveCBP = System.currentTimeMillis();
			cbpResource1.save(cbpOutputStream, null);
			afterSaveCBP = System.currentTimeMillis();

			valDelete = cbpResource.getAvgTimeDelete();
			valAttributeAddRemoveMove = cbpResource.getAvgTimeAttributeAddRemoveMove();
			valAttributeSetUnset = cbpResource.getAvgTimeAttributeSetUnset();
			valReferenceAddRemoveMove = cbpResource.getAvgTimeReferenceAddRemoveMove();
			valReferenceSetUnset = cbpResource.getAvgTimeReferenceSetUnset();

			ChangeEventAdapter adapter = cbpResource.getChangeEventAdapter();
			setAttCount = adapter.getSetAttCount();
			unsetAttCount = adapter.getUnsetAttCount();
			addAttCount = adapter.getAddAttCount();
			removeAttCount = adapter.getRemoveAttCount();
			moveAttCount = adapter.getMoveAttCount();
			setRefCount = adapter.getSetRefCount();
			unsetRefCount = adapter.getUnsetRefCount();
			addRefCount = adapter.getAddRefCount();
			removeRefCount = adapter.getRemoveRefCount();
			moveRefCount = adapter.getMoveRefCount();
			deleteCount = adapter.getDeleteCount();
			addResCount = adapter.getAddResCount();
			removeResCount = adapter.getRemoveResCount();
			packageCount = adapter.getPackageCount();
			sessionCount = adapter.getSessionCount();
			createCount = adapter.getCreateCount();

			ModelHistory modelHistory = cbpResource.getModelHistory();
			ignoredSetAttCount = modelHistory.getIgnoredSetAttCount();
			ignoredUnsetAttCount = modelHistory.getIgnoredUnsetAttCount();
			ignoredAddAttCount = modelHistory.getIgnoredAddAttCount();
			ignoredRemoveAttCount = modelHistory.getIgnoredRemoveAttCount();
			ignoredMoveAttCount = modelHistory.getIgnoredMoveAttCount();
			ignoredSetRefCount = modelHistory.getIgnoredSetRefCount();
			ignoredUnsetRefCount = modelHistory.getIgnoredUnsetRefCount();
			ignoredAddRefCount = modelHistory.getIgnoredAddRefCount();
			ignoredRemoveRefCount = modelHistory.getIgnoredRemoveRefCount();
			ignoredMoveRefCount = modelHistory.getIgnoredMoveRefCount();
			ignoredDeleteCount = modelHistory.getIgnoredDeleteCount();
			ignoredAddResCount = modelHistory.getIgnoredAddResCount();
			ignoredRemoveResCount = modelHistory.getIgnoredRemoveResCount();
			ignoredPackageCount = modelHistory.getIgnoredPackageCount();
			ignoredSessionCount = modelHistory.getIgnoredSessionCount();
			ignoredCreateCount = modelHistory.getIgnoredCreateCount();

			
			cbpXml = cbpOutputStream.toString();
		} catch (Exception e) {
			errorMessage.append(e.toString() + "\n");
			e.printStackTrace();
		}

	}

	public void testLoadCBP(boolean debug) throws Exception {
		try {
			ResourceSet cbpResourceSet3 = createResourceSet();
			cbpResourceSet3.setPackageRegistry(EPackage.Registry.INSTANCE);
			CBPXMLResourceFactory factory3 = new CBPXMLResourceFactory();
			cbpResourceSet3.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory3);
			Resource cbpResource3 = cbpResourceSet3.createResource(URI.createURI("foo4." + extension));

			String sCBP2 = cbpOutputStream.toString();
			((CBPResource) cbpResource3).setIgnoreSet(ignoreList);
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

	public void testLoadOptimisedCBP(boolean debug) throws Exception {
		try {
			ResourceSet cbpResourceSet2 = createResourceSet();
			cbpResourceSet2.setPackageRegistry(EPackage.Registry.INSTANCE);
			CBPXMLResourceFactory factory2 = new CBPXMLResourceFactory();
			cbpResourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory2);
			Resource cbpResource2 = cbpResourceSet2.createResource(URI.createURI("foo5." + extension));

			String sCBP1 = cbpOutputStream.toString();

			numOfLineOptCBP = 0;
			numOfLineCBP = 0;
			numOfLineCBP = sCBP1.split("\r\n|\r|\n").length;
			numOfLineOptCBP = numOfLineCBP - ignoreList.size();
			((CBPResource) cbpResource2).setIgnoreSet(ignoreList);
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

	public void run(String eol, String extension, boolean debug, List<String> codeList) throws Exception {
		try {
			this.eol = eol;
			int iteration = 1;
			double deltaSaveXMI = 0;
			double deltaLoadXMI = 0;
			double deltaSaveCBP = 0;
			double deltaLoadOCBP = 0;
			double deltaLoadCBP = 0;
			double sumNumOfNodes = 0;
			double sumNumOfLineCBP = 0;
			double sumNumOfLineOptCBP = 0;
			double sumAttributeSetUnset = 0;
			double sumReferenceSetUnset = 0;
			double sumAttributeAddRemoveMove = 0;
			double sumReferenceAddRemoveMove = 0;
			double sumDelete = 0;

			double sumSetAttCount = 0;
			double sumUnsetAttCount = 0;
			double sumAddAttCount = 0;
			double sumRemoveAttCount = 0;
			double sumMoveAttCount = 0;
			double sumSetRefCount = 0;
			double sumUnsetRefCount = 0;
			double sumAddRefCount = 0;
			double sumRemoveRefCount = 0;
			double sumMoveRefCount = 0;
			double sumDeleteCount = 0;
			double sumAddResCount = 0;
			double sumRemoveResCount = 0;
			double sumPackageCount = 0;
			double sumSessionCount = 0;
			double sumCreateCount = 0;

			double sumIgnoredSetAttCount = 0;
			double sumIgnoredUnsetAttCount = 0;
			double sumIgnoredAddAttCount = 0;
			double sumIgnoredRemoveAttCount = 0;
			double sumIgnoredMoveAttCount = 0;
			double sumIgnoredSetRefCount = 0;
			double sumIgnoredUnsetRefCount = 0;
			double sumIgnoredAddRefCount = 0;
			double sumIgnoredRemoveRefCount = 0;
			double sumIgnoredMoveRefCount = 0;
			double sumIgnoredMeleteCount = 0;
			double sumIgnoredAddResCount = 0;
			double sumIgnoredRemoveResCount = 0;
			double sumIgnoredPackageCount = 0;
			double sumIgnoredSessionCount = 0;
			double sumIgnoredCreateCount = 0;

			for (int i = 0; i < iteration; i++) {
				this.testSaveXMI(eol, extension, debug, codeList);
				this.testLoadXMI(eol, extension, debug);
				this.testSaveCBP(debug, codeList);
				this.testLoadCBP(debug);
				this.testLoadOptimisedCBP(debug);

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
				sumAttributeSetUnset = sumAttributeSetUnset + valAttributeSetUnset;
				sumReferenceSetUnset = sumReferenceSetUnset + valReferenceSetUnset;
				sumAttributeAddRemoveMove = sumAttributeAddRemoveMove + valAttributeAddRemoveMove;
				sumReferenceAddRemoveMove = sumReferenceAddRemoveMove + valReferenceAddRemoveMove;
				sumDelete = sumDelete + valDelete;

				sumSetAttCount += setAttCount;
				sumUnsetAttCount += unsetAttCount;
				sumAddAttCount += addAttCount;
				sumRemoveAttCount += addAttCount;
				sumMoveAttCount += moveAttCount;
				sumSetRefCount += setRefCount;
				sumUnsetRefCount += unsetRefCount;
				sumAddRefCount += addRefCount;
				sumRemoveRefCount += removeRefCount;
				sumMoveRefCount += moveRefCount;
				sumDeleteCount += deleteCount;
				sumAddResCount += addResCount;
				sumRemoveResCount += removeResCount;
				sumPackageCount += packageCount;
				sumSessionCount += sessionCount;
				sumCreateCount += createCount;

				sumIgnoredSetAttCount += ignoredSetAttCount;
				sumIgnoredUnsetAttCount += ignoredUnsetAttCount;
				sumIgnoredAddAttCount += ignoredAddAttCount;
				sumIgnoredRemoveAttCount += ignoredRemoveAttCount;
				sumIgnoredMoveAttCount += ignoredMoveAttCount;
				sumIgnoredSetRefCount += ignoredSetRefCount;
				sumIgnoredUnsetRefCount += ignoredUnsetRefCount;
				sumIgnoredAddRefCount += ignoredAddRefCount;
				sumIgnoredRemoveRefCount += ignoredRemoveRefCount;
				sumIgnoredMoveRefCount += ignoredMoveRefCount;
				sumIgnoredMeleteCount += ignoredDeleteCount;
				sumIgnoredAddResCount += ignoredAddResCount;
				sumIgnoredRemoveResCount += ignoredRemoveResCount;
				sumIgnoredPackageCount += ignoredPackageCount;
				sumIgnoredSessionCount += ignoredSessionCount;
				sumIgnoredCreateCount += ignoredCreateCount;

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
			sumAttributeSetUnset = sumAttributeSetUnset / iteration;
			sumReferenceSetUnset = sumReferenceSetUnset / iteration;
			sumAttributeAddRemoveMove = sumAttributeAddRemoveMove / iteration;
			sumReferenceAddRemoveMove = sumReferenceAddRemoveMove / iteration;
			sumDelete = sumDelete / iteration;

			appendLineToOutputText(String.format(
					"%1$.4f\t%2$.4f\t%3$.4f\t%4$.4f\t%5$.4f\t%6$.0f\t%7$.0f"
							+ "\t%8$.0f\t%9$.0f\t%10$.0f\t%11$.0f\t%12$.0f\t%13$.0f"
							
							+ "\t%14$.0f\t%15$.0f\t%16$.0f\t%17$.0f"
							+ "\t%18$.0f\t%19$.0f\t%20$.0f\t%21$.0f"
							+ "\t%22$.0f\t%23$.0f\t%24$.0f\t%25$.0f"
							+ "\t%26$.0f\t%27$.0f\t%28$.0f\t%29$.0f"
							
							+ "\t%30$.0f\t%31$.0f\t%32$.0f\t%33$.0f"
							+ "\t%34$.0f\t%35$.0f\t%36$.0f\t%37$.0f"
							+ "\t%38$.0f\t%39$.0f\t%40$.0f\t%41$.0f"
							+ "\t%42$.0f\t%43$.0f\t%44$.0f\t%45$.0f",							
							
					deltaSaveXMI, deltaSaveCBP, deltaLoadXMI, deltaLoadOCBP, deltaLoadCBP, sumNumOfNodes,
					sumNumOfLineOptCBP, sumNumOfLineCBP,

					sumAttributeSetUnset, sumReferenceSetUnset, sumAttributeAddRemoveMove, sumReferenceAddRemoveMove,
					sumDelete

					, sumSetAttCount, sumUnsetAttCount, sumAddAttCount, sumRemoveAttCount, sumMoveAttCount,
					sumSetRefCount, sumUnsetRefCount, sumAddRefCount, sumRemoveRefCount, sumMoveRefCount,
					sumDeleteCount, sumAddResCount, sumRemoveResCount, sumPackageCount, sumSessionCount, sumCreateCount

					, sumIgnoredSetAttCount, sumIgnoredUnsetAttCount, sumIgnoredAddAttCount, sumIgnoredRemoveAttCount,
					sumIgnoredMoveAttCount, sumIgnoredSetRefCount, sumIgnoredUnsetRefCount, sumIgnoredAddRefCount,
					sumIgnoredRemoveRefCount, sumIgnoredMoveRefCount, sumIgnoredMeleteCount, sumIgnoredAddResCount,
					sumIgnoredRemoveResCount, sumIgnoredPackageCount, sumIgnoredSessionCount, sumIgnoredCreateCount));

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
		run(eol, extension, debug, null);
	}

	protected void run(String eol, boolean debug, List<String> codeList) throws Exception {
		run(eol, extension, debug, codeList);
	}

}
