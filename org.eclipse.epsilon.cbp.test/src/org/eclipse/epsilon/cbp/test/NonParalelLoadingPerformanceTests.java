package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class NonParalelLoadingPerformanceTests {

	private final String extension;

	protected int topLimit = 0;
	protected int topLimitIncrement = 500;
	protected List<String> eolOperations = null;
	protected String cbpXml = "";
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

	protected Set<Integer> ignoreList = null;

	private EolModule moduleXmi;
	private ResourceSet xmiResourceSet1;
	private Resource xmiResource1;
	private InMemoryEmfModel modelXmi;

	private ResourceSet xmiResourceSet2;
	private XMIResourceFactoryImpl xmiFactory;
	private Resource xmiResource2;

	private EolModule moduleCbp;
	private ResourceSet cbpResourceSet1;
	private Resource cbpResource1;
	private InMemoryEmfModel modelCbp;

	private ResourceSet cbpResourceSet2;
	private CBPXMLResourceFactory cbpFactory2;
	private Resource cbpResource2;

	private ResourceSet cbpResourceSet3;
	private CBPXMLResourceFactory factory3;
	private Resource cbpResource3;

	public abstract EPackage getEPackage();

	public abstract Class<?> getNodeClass();

	public NonParalelLoadingPerformanceTests(String extension) {
		this.extension = extension;
	}

	public void initialiseAll() {
		moduleXmi = new EolModule();
		xmiResourceSet1 = createResourceSet();
		xmiResource1 = xmiResourceSet1.createResource(URI.createURI("foo.xmi"));
		modelXmi = new InMemoryEmfModel("M", xmiResource1, getEPackage());
		moduleXmi.getContext().getModelRepository().addModel(modelXmi);

		xmiResourceSet2 = createResourceSet();
		xmiResourceSet2.setPackageRegistry(EPackage.Registry.INSTANCE);
		xmiFactory = new XMIResourceFactoryImpl();
		xmiResourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", xmiFactory);
		xmiResource2 = xmiResourceSet2.createResource(URI.createURI("foo.xmi"));

		moduleCbp = new EolModule();
		cbpResourceSet1 = createResourceSet();
		cbpResource1 = cbpResourceSet1.createResource(URI.createURI("foo3." + extension));
		modelCbp = new InMemoryEmfModel("M", cbpResource1, getEPackage());
		moduleCbp.getContext().getModelRepository().addModel(modelCbp);
		cbpOutputStream = new StringOutputStream();

		cbpResourceSet2 = createResourceSet();
		cbpResourceSet2.setPackageRegistry(EPackage.Registry.INSTANCE);
		cbpFactory2 = new CBPXMLResourceFactory();
		cbpResourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", cbpFactory2);
		cbpResource2 = cbpResourceSet2.createResource(URI.createURI("foo5." + extension));

		cbpResourceSet3 = createResourceSet();
		cbpResourceSet3.setPackageRegistry(EPackage.Registry.INSTANCE);
		factory3 = new CBPXMLResourceFactory();
		cbpResourceSet3.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory3);
		cbpResource3 = cbpResourceSet3.createResource(URI.createURI("foo4." + extension));
	}

	// Save XMI -------------------------------------------
	public void testSaveXMI(String eol, String extension, boolean debug) throws Exception {
		try {
			moduleXmi.parse(eol);
			moduleXmi.execute();

			if (xmiOutputStream != null) {
				xmiOutputStream.reset();
				xmiOutputStream.close();
			} else {
				xmiOutputStream = new StringOutputStream();
			}

			numberOfNodes = 0;
			TreeIterator<EObject> iterator = xmiResource1.getAllContents();
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				EList<EClass> list = eObject.eClass().getEAllSuperTypes();
				EClassifier eClassifier = ConferencePackage.eINSTANCE.getEClassifier(getNodeClass().getSimpleName());
				if (list.contains(eClassifier)) {
					numberOfNodes += 1;
				}
			}

			beforeSaveXMI = System.currentTimeMillis();
			xmiResource1.save(xmiOutputStream, null);
			afterSaveXMI = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLoadXMI(String eol, String extension, boolean debug) throws Exception {
		try {
			String sXMI = xmiOutputStream.toString();
			beforeLoadXMI = System.currentTimeMillis();
			xmiResource2.load(new ByteArrayInputStream(sXMI.getBytes()), null);
			afterLoadXMI = System.currentTimeMillis();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testSaveCBP(String eol, String extension, boolean debug) throws Exception {
		try {
			moduleCbp.parse(eol);
			moduleCbp.execute();

			ignoreList = ((CBPResource) cbpResource1).getIgnoreSet();

			beforeSaveCBP = System.currentTimeMillis();
			cbpResource1.save(cbpOutputStream, null);
			afterSaveCBP = System.currentTimeMillis();

			valDelete = ((CBPResource) cbpResource1).getAvgTimeDelete();
			valAttributeAddRemoveMove = ((CBPResource) cbpResource1).getAvgTimeAttributeAddRemoveMove();
			valAttributeSetUnset = ((CBPResource) cbpResource1).getAvgTimeAttributeSetUnset();
			valReferenceAddRemoveMove = ((CBPResource) cbpResource1).getAvgTimeReferenceAddRemoveMove();
			valReferenceSetUnset = ((CBPResource) cbpResource1).getAvgTimeReferenceSetUnset();

			cbpXml = cbpOutputStream.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLoadOptimisedCBP(String eol, String extension, boolean debug) throws Exception {
		try {
			String sCBP1 = cbpOutputStream.toString();

			numOfLineCBP = 0;
			numOfLineCBP = sCBP1.split("\r\n|\r|\n").length;
			numOfLineOptCBP = numOfLineCBP - ignoreList.size();

			((CBPResource) cbpResource2).setIgnoreSet(ignoreList);
			beforeLoadOptCBP = System.currentTimeMillis();
			cbpResource2.load(new ByteArrayInputStream(sCBP1.getBytes()), null);
			afterLoadOptCBP = System.currentTimeMillis();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testLoadCBP(String eol, String extension, boolean debug) throws Exception {
		try {
			String sCBP2 = cbpOutputStream.toString();
			((CBPResource) cbpResource3).setIgnoreSet(ignoreList);
			Map<Object, Object> options = new HashMap<>();
			options.put("optimise", false);
			beforeLoadCBP = System.currentTimeMillis();
			cbpResource3.load(new ByteArrayInputStream(sCBP2.getBytes()), options);
			afterLoadCBP = System.currentTimeMillis();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(List<String> eolOperations, String extension, boolean debug) throws Exception {

		try {
			this.eolOperations = eolOperations;
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

			this.initialiseAll();
			for (String eolOperation : eolOperations) {
				this.testSaveXMI(eolOperation, extension, debug);
				this.testLoadXMI(eolOperation, extension, debug);
				this.testSaveCBP(eolOperation, extension, debug);
				this.testLoadCBP(eolOperation, extension, debug);
				this.testLoadOptimisedCBP(eolOperation, extension, debug);

				if (numberOfNodes >= topLimit) {
					deltaSaveXMI = deltaSaveXMI + ((double) (afterSaveXMI - beforeSaveXMI)) / 1000;
					deltaLoadXMI = deltaLoadXMI + ((double) (afterLoadXMI - beforeLoadXMI)) / 1000;
					deltaSaveCBP = deltaSaveCBP + ((double) (afterSaveCBP - beforeSaveCBP)) / 1000;
					deltaLoadOCBP = deltaLoadOCBP + ((double) (afterLoadOptCBP - beforeLoadOptCBP)) / 1000;
					deltaLoadCBP = deltaLoadCBP + ((double) (afterLoadCBP - beforeLoadCBP)) / 1000;
					sumNumOfNodes = numberOfNodes;
					sumNumOfLineOptCBP = numOfLineOptCBP;
					sumNumOfLineCBP = numOfLineCBP;
					sumAttributeSetUnset = valAttributeSetUnset;
					sumReferenceSetUnset = valReferenceSetUnset;
					sumAttributeAddRemoveMove = valAttributeAddRemoveMove;
					sumReferenceAddRemoveMove = valReferenceAddRemoveMove;
					sumDelete = sumDelete + valDelete;

					System.out.println(String.format(
							"%1$.4f\t%2$.4f\t%3$.4f\t%4$.4f\t%5$.4f\t%6$.0f\t%7$.0f"
									+ "\t%8$.0f\t%9$.0f\t%10$.0f\t%11$.0f\t%12$.0f\t%13$.0f",
							deltaSaveXMI, deltaSaveCBP, deltaLoadXMI, deltaLoadOCBP, deltaLoadCBP, sumNumOfNodes,
							sumNumOfLineOptCBP, sumNumOfLineCBP, sumAttributeSetUnset, sumReferenceSetUnset,
							sumAttributeAddRemoveMove, sumReferenceAddRemoveMove, sumDelete));

					topLimit += topLimitIncrement;
				}
			}
		} catch (

		Exception e) {
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

	protected void run(List<String> eol) throws Exception {
		run(eol, false);
	}

	protected void debug(List<String> eol) throws Exception {
		run(eol, true);
	}

	protected void run(List<String> eol, boolean debug) throws Exception {
		run(eol, extension, debug);
	}

}
