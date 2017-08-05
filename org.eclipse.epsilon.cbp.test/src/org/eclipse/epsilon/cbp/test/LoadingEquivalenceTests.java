package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.history.EObjectHistory;
import org.eclipse.epsilon.cbp.history.EObjectHistoryAdapter;
import org.eclipse.epsilon.cbp.history.Line;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.models.IModel;

public abstract class LoadingEquivalenceTests {

	private final String extension;

	public LoadingEquivalenceTests(String extension) {
		this.extension = extension;
	}

	public abstract EPackage getEPackage();

	public void run(String eol, String extension, boolean debug) throws Exception {
		// Run the code against an XMI model
		System.out.println("SAVE XMI");
		EolModule module = new EolModule();
		module.parse(eol);

		ResourceSet xmiResourceSet = createResourceSet();
		Resource xmiResource = xmiResourceSet.createResource(URI.createURI("foo.xmi"));
		InMemoryEmfModel model = new InMemoryEmfModel("M", xmiResource, getEPackage());
		module.getContext().getModelRepository().addModel(model);
		module.getContext().getModelRepository().getModels().addAll(getExtraModels());
		module.execute();

		StringOutputStream xmiSos = new StringOutputStream();
		long beforeSaveXMI = System.currentTimeMillis();
		xmiResource.save(xmiSos, null);
		long afterSaveXMI = System.currentTimeMillis();
		// inspect(xmiResource);

		// Load XMI
		System.out.println("LOAD XMI");
		xmiResourceSet = createResourceSet();
		xmiResourceSet.setPackageRegistry(EPackage.Registry.INSTANCE);
		XMIResourceFactoryImpl xmiFactory = new XMIResourceFactoryImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", xmiFactory);
		xmiResource = xmiResourceSet.createResource(URI.createURI("foo.xmi"));
		final String sXMI = xmiSos.toString();

		long beforeLoadXMI = System.currentTimeMillis();
		xmiResource.load(new ByteArrayInputStream(sXMI.getBytes()), null);
		long afterLoadXMI = System.currentTimeMillis();

		// Run the code against a change-based resource
		System.out.println("SAVE CBP");
		module = new EolModule();
		module.parse(eol);

		ResourceSet cbpResourceSet = createResourceSet();
		Resource cbpResource1 = cbpResourceSet.createResource(URI.createURI("foo." + extension));

		model = new InMemoryEmfModel("M", cbpResource1, getEPackage());
		module.getContext().getModelRepository().addModel(model);
		module.getContext().getModelRepository().getModels().addAll(getExtraModels());
		module.execute();
		// inspect(cbpResource);

		StringOutputStream cbpSos = new StringOutputStream();
		long beforeSaveCBP = System.currentTimeMillis();
		cbpResource1.save(cbpSos, null);
		long afterSaveCBP = System.currentTimeMillis();

		Set<Integer> ignoreList = ((CBPResource) cbpResource1).getIgnoreList();

		int actualTotalLines = 0;
		int count2 = 0;
		if (debug) {
			System.out.println("");
			System.out.println("DATA STRUCTURE");
			EObjectHistoryAdapter eObjectHistoryList = ((CBPResource) cbpResource1).getEObjectHistoryList();

			for (Entry<EObject, EObjectHistory> entry1 : eObjectHistoryList.geteObjectHistoryList().entrySet()) {
				EObject eObject = entry1.getKey();
				EObjectHistory eObjectEventLineHistory = entry1.getValue();
				System.out.println("EObject: " + cbpResource1.getURIFragment(eObject) + " -------------------");
				for (Entry<String, List<Line>> entry2 : eObjectEventLineHistory.getEventRecords().entrySet()) {
					String eventName = entry2.getKey();
					List<Line> lines = entry2.getValue();
					System.out.println("    " + eventName + " = " + lines);
				}
				// attributes
				Map<EObject, EObjectHistory> attributeList = eObjectEventLineHistory.getAttributes();
				System.out.println("    EAttribute:");
				for (Entry<EObject, EObjectHistory> entry2 : attributeList.entrySet()) {
					EAttribute eAttribute = (EAttribute) entry2.getKey();
					EObjectHistory eAttributeHistory = entry2.getValue();
					System.out.println("        " + eAttribute.getName() + " -------------------");
					for (Entry<String, List<Line>> entry3 : eAttributeHistory.getEventRecords().entrySet()) {
						String eventName = entry3.getKey();
						List<Line> lines = entry3.getValue();
						System.out.println("            " + eventName + " = " + lines);
					}
				}
				// references
				Map<EObject, EObjectHistory> referenceList = eObjectEventLineHistory.getReferences();
				System.out.println("    EReference:");
				for (Entry<EObject, EObjectHistory> entry2 : referenceList.entrySet()) {
					EReference eReference = (EReference) entry2.getKey();
					EObjectHistory eReferenceHistory = entry2.getValue();
					System.out.println("        " + eReference.getName() + " -------------------");
					for (Entry<String, List<Line>> entry3 : eReferenceHistory.getEventRecords().entrySet()) {
						String eventName = entry3.getKey();
						List<Line> lines = entry3.getValue();
						System.out.println("            " + eventName + " = " + lines);
					}
				}
			}
			System.out.println("");
			System.out.println("XML BEFORE REMOVED");
			String[] list1 = cbpSos.toString().split(System.getProperty("line.separator"));
			int count1 = 0;
			for (String line : list1) {
				System.out.println(String.valueOf(count1) + "\t" + line);
				count1 += 1;
			}

			System.out.println("\nXML AFTER REMOVED");
			String[] list2 = cbpSos.toString().split(System.getProperty("line.separator"));
			for (String line : list2) {
				if (ignoreList.contains(count2)) {
					count2 += 1;
					continue;
				}
				System.out.println(String.valueOf(count2) + "\t" + line);
				count2 += 1;
				actualTotalLines += 1;
			}

			System.out.println("");
			System.out.println("IGNORE LIST = " + ignoreList);
			System.out.println("");

			System.out.println("XMIResourceImpl");
			System.out.println(xmiSos.toString());
			System.out.println();

		}

		// Create a new change-based resource and load what was saved before
		System.out.println("LOAD OPTIMISED CBP");
		cbpResourceSet = createResourceSet();
		cbpResourceSet.setPackageRegistry(EPackage.Registry.INSTANCE);
		CBPXMLResourceFactory factory = new CBPXMLResourceFactory();
		cbpResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", factory);
		Resource cbpResource2 = cbpResourceSet.createResource(URI.createURI("foo." + extension));
		final String sCBP = cbpSos.toString();

		// get ignoreList from cbpResource1 and apply it cbpResource2
		((CBPResource) cbpResource2).setIgnoreList(ignoreList);

		long beforeLoadCBP = System.currentTimeMillis();
		cbpResource2.load(new ByteArrayInputStream(sCBP.getBytes()), null);
		long afterLoadCBP = System.currentTimeMillis();

		xmiResourceSet = createResourceSet();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		XMIResourceImpl copyXmiResource = (XMIResourceImpl) xmiResourceSet.createResource(URI.createURI("foo.xmi"));
		copyXmiResource.getContents().addAll(EcoreUtil.copyAll(cbpResource2.getContents()));
		// inspect(copyXmiResource);

		StringOutputStream copyXmiResourceSos = new StringOutputStream();
		copyXmiResource.doSave(copyXmiResourceSos, null);

		if (debug) {
			System.out.println("CBPResource");
			System.out.println(copyXmiResourceSos.toString());
			System.out.println("---");
		}

		System.out.println("");
		System.out.println("LOAD CBP");
		Resource cbpResource3 = cbpResourceSet.createResource(URI.createURI("foo." + extension));
		long beforeLoadIgnoreCBP = System.currentTimeMillis();
		Map<String, Boolean> options = new HashMap<>();
		options.put("optimise", false);
		cbpResource3.load(new ByteArrayInputStream(sCBP.getBytes()), options);
		long afterLoadIgnoreCBP = System.currentTimeMillis();
		// inspect(cbpResource);

		// DEBUG
		if (debug) {
			

			System.out.println("");
			System.out.println("STATISTICS");
			System.out.println("Removed lines: " + ignoreList + " = " + ignoreList.size());
			System.out.println("Total lines: " + actualTotalLines);
			DecimalFormat df = new DecimalFormat("#.00");
			double percentage = ignoreList.size() * 1.0 / count2 * 100.0;
			System.out.println("removed lines: " + df.format(percentage) + " %");
			System.out.println("");
			System.out.println("XMI Save Time = " + (afterSaveXMI - beforeSaveXMI) + " ms");
			System.out.println("XMI Load Time = " + (afterLoadXMI - beforeLoadXMI) + " ms");
			System.out.println("CBP Save Time = " + (afterSaveCBP - beforeSaveCBP) + " ms");
			System.out.println("CBP Load Time = " + (afterLoadCBP - beforeLoadCBP) + " ms");
			System.out.println("Ignore CBP Load Time = " + (afterLoadIgnoreCBP - beforeLoadIgnoreCBP) + " ms");
		}

		assertEquals(xmiSos.toString(), copyXmiResourceSos.toString());
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

	protected void inspect(Resource resource) throws Exception {
		EolModule module = new EolModule();
		module.parse("var c = EClass.all.first(); c.eSuperTypes.size().println();");
		InMemoryEmfModel model = new InMemoryEmfModel("M", resource, getEPackage());
		model.load();
		module.getContext().getModelRepository().addModel(model);
		module.execute();
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

	protected Collection<IModel> getExtraModels() {
		return Collections.emptyList();
	}

}