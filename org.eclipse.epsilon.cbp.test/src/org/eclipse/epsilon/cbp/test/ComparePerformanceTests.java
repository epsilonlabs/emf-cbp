package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;

public abstract class ComparePerformanceTests {

	private final String extension;
	private final Factory factory;

	long beforeEMFCompare = 0;
	long afterEMFCompare = 0;
	long beforeCBPCompare = 0;
	long afterCBPCompare = 0;
	long emfCompareSize = 0;
	long cbpCompareSize = 0;
	long numberOfNodes = 0;
	long limit = 0;
	long limitIncrement = 250;

	private Map<String, List<Record>> objectList = new HashMap<String, List<Record>>();
	private CBPResource initialResource;
	private ResourceSet initialResourceSet;
	private StringOutputStream initialOutput;
	private ByteArrayOutputStream initialIgnoreListOutput;
	private EMFCompare comparator;

	public abstract Class<?> getNodeClass();

	public ComparePerformanceTests(String extension, Resource.Factory factory) {
		this.extension = extension;
		this.factory = factory;
	}

	public void run(String modifierCode, int operationCount) throws Exception {
		runImpl(extension, modifierCode, false, operationCount);
	}

	public void debug(String modifierCode, int operationCount) throws Exception {
		runImpl(extension, modifierCode, true, operationCount);
	}

	public void createInitialModel(String initialCode) throws Exception {
		initialOutput = new StringOutputStream();
		initialIgnoreListOutput = new ByteArrayOutputStream();
		EolModule initialModule = new EolModule();

		initialResourceSet = new ResourceSetImpl();
		initialResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		initialResource = (CBPResource) initialResourceSet.createResource(URI.createURI("foo." + extension));
		InMemoryEmfModel initialModel = new InMemoryEmfModel("M", initialResource, getEPackage());
		initialModule.getContext().getModelRepository().addModel(initialModel);
		initialModule.parse(initialCode);
		initialResource.startNewSession();
		initialModule.execute();

		initialResource.save(initialOutput, null);
		initialResource.saveIgnoreSet(initialIgnoreListOutput);

//		// Configure EMF Compare
//		IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.NEVER);
//		IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
//		IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
//		matchEngineFactory.setRanking(20);
//		IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
//		matchEngineRegistry.add(matchEngineFactory);
//		comparator = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry).build();

	}

	public void runImpl(String extension, String modifierCode, boolean debug, int operationCount) throws Exception {

		// initialisation
		StringOutputStream modifiedOutput = new StringOutputStream();
		ByteArrayOutputStream modifiedIgnoreListOutput = new ByteArrayOutputStream();
		EolModule modifierModule = new EolModule();

		// CBP
		// modifier
		ResourceSet modifiedResourceSet = new ResourceSetImpl();
		modifiedResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		CBPResource modifiedResource = (CBPResource) modifiedResourceSet
				.createResource(URI.createURI("foo." + extension));

		// modifiedResource.getContents().addAll(initialResource.getContents());

		modifiedResource.loadIgnoreSet(new ByteArrayInputStream(initialIgnoreListOutput.toByteArray()));
		modifiedResource.load(new ByteArrayInputStream(initialOutput.toString().getBytes()), null);

		InMemoryEmfModel modifiedModel = new InMemoryEmfModel("M", modifiedResource, getEPackage());
		modifierModule.getContext().getModelRepository().addModel(modifiedModel);

		 modifiedIgnoreListOutput.write(initialIgnoreListOutput.toByteArray());
		 modifiedOutput.write(initialOutput.toString().getBytes());

		// ((CBPResource) modifiedResource).getModelHistory().printStructure();
		// System.out.println("IgnoreList 1 = " + ((CBPResource)
		// initialResource).getIgnoreList().toString());
		// System.out.println("IgnoreList 2 = " + ((CBPResource)
		// modifiedResource).getIgnoreList().toString());
		modifierModule.parse(modifierCode);
		modifiedResource.startNewSession();
		modifierModule.execute();
		modifiedResource.save(modifiedOutput, null);
		modifiedResource.saveIgnoreSet(modifiedIgnoreListOutput);

		// Compare the two models
		// ----EMF Compare
		Comparison comparison = null;
		IComparisonScope scope = new DefaultComparisonScope(initialResourceSet, modifiedResourceSet, null);
		comparator = EMFCompare.builder().build();
		comparison = comparator.compare(scope);
		
		beforeEMFCompare = System.nanoTime();
		comparison = comparator.compare(scope);
		afterEMFCompare = System.nanoTime();

		if (comparison != null && comparison.getDifferences() != null) {
			emfCompareSize = comparison.getDifferences().size();
		}

		// -----CBP
		List<ChangeEvent<?>> cbpDiffs = null;
		beforeCBPCompare = System.nanoTime();
		cbpDiffs = identifyChanges(initialResource, modifiedResource);
		afterCBPCompare = System.nanoTime();

		if (cbpDiffs != null) {
			 cbpCompareSize = cbpDiffs.size();

		}

		numberOfNodes = 0;
		TreeIterator<EObject> iterator = modifiedResource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			if (getNodeClass().isInstance(eObject)) {
				numberOfNodes += 1;
			}
		}

		// if (operationCount >= limit) {
		System.out.print(String.format("%1$s\t", operationCount));
		System.out.print(String.format("%1$s\t", numberOfNodes));
		System.out.print(String.format("%1$s\t", cbpCompareSize));
		System.out.print(String.format("%1$s\t", emfCompareSize));
		System.out.print(String.format("%1$.6f\t", (double) (afterCBPCompare - beforeCBPCompare) / 1000000));
		System.out.print(String.format("%1$.6f\n", (double) (afterEMFCompare - beforeEMFCompare) / 1000000));
		// limit += limitIncrement;
		// }

		if (debug) {
			System.out.println();
			System.out.println("Initial CBP---------");
			System.out.println(initialOutput.toString());
			System.out.println();
			System.out.println("Modified CBP--------");
			System.out.println(modifiedOutput.toString());
			System.out.println();
			System.out.println("EMF Compare---------");
			if (comparison != null && comparison.getDifferences() != null) {
				for (Diff diff : comparison.getDifferences()) {
					System.out.println(diff);
					if ( diff instanceof ResourceAttachmentChange){
						System.out.println(((ResourceAttachmentChange) diff).getSource());
					}else if ( diff instanceof ReferenceChange){
						System.out.println(((ReferenceChange) diff).getValue());
						System.out.println(((ReferenceChange) diff).getMatch());
					}
					
				}
				System.out.println();
			}
			System.out.println("CBP Compare---------");
			if (cbpDiffs != null) {
				System.out.println("Size = " + cbpDiffs.size());
				System.out.println(cbpDiffs);
			}
		}
	}

	public abstract EPackage getEPackage();

	class Record {
		String eventName;
		Object value;
	}

	public void addToRecordList(ChangeEvent<?> event, String name, Object value) {
		Record record = new Record();
		record.eventName = event.getClass().getSimpleName();
		record.value = value;
		if (!objectList.containsKey(name)) {
			List<Record> list = new ArrayList<>();
			list.add(record);
			objectList.put(name, list);
		} else {
			objectList.get(name).add(record);
		}
	}

	public List<ChangeEvent<?>> identifyChanges(CBPResource initialResource, CBPResource modifiedResource) {
		List<ChangeEvent<?>> changeEvents = modifiedResource.getChangeEvents();
		int pos = 0;
		for(pos = changeEvents.size(); pos >= 0; pos--){
			if (changeEvents.get(pos-1) instanceof StartNewSessionEvent){
				break;
			}
		}
		List<ChangeEvent<?>> recentEvents = changeEvents.subList(pos, changeEvents.size());
		
		return recentEvents;
	}

}
