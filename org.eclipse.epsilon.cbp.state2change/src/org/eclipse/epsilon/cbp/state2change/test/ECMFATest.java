package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Pack200.Unpacker;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.uml2.common.util.CacheAdapter;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/***
 * 
 * @author Ryano
 *
 */
public class ECMFATest {

	public final static int ITERATION = 4;

	private File cbpFile = new File("D:/TEMP/ECMFA/cbp/ecmfa.cbpxml");
	private File cbpDummyFile = new File("D:/TEMP/ECMFA/cbp/ecmfa-dummy.cbpxml");
	private File sourceDirectory = new File("D:/TEMP/ECMFA/xmi/");
	private File ignoreListFile = new File("D:/TEMP/ECMFA/cbp/ecmfa.ignorelist");
	private File ignoreListDummyFile = new File("D:/TEMP/ECMFA/cbp/ecmfa-dummy.ignorelist");

	protected URI rootXmiFileUri;
	protected URI commonXmiURI = URI.createURI("umlmodel.xmi");
	protected URI commonCbpxmlURI = URI.createURI("umlmodel.cbpxml");

	protected IBatchMerger batchMerger;
	protected EMFCompare comparator;

	protected List<ResourceSet> resourceSetList = new ArrayList<>();

	protected CBPResource runningCbpResource;
	protected ResourceSet cbpResourceSet;
	protected ResourceSet xmiResourceSet;

	protected Map<Object, Object> saveOptions;
	protected String cbpStatePath;

	public StringBuilder results = new StringBuilder();

	/***
	 * 
	 */
	public ECMFATest() {

	}

	/***
	 * 
	 * @param cbpFile
	 * @param diffDirectory
	 * @return
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {

		UMLPackage.eINSTANCE.eClass();
		saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

		// -----Set Up CBP
		cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());

		runningCbpResource = new CBPXMLResourceImpl();
		cbpResourceSet.getResources().add(runningCbpResource);
		if (cbpFile.exists()) {
			cbpFile.delete();
		}
		cbpFile.createNewFile();
		URI cbpUri = URI.createFileURI(cbpFile.getAbsolutePath());
		runningCbpResource.setURI(cbpUri);

		try {
			runningCbpResource.load(null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// --Set up XMI
		xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		resourceSetList.add(xmiResourceSet);

		File[] sourceFiles = sourceDirectory.listFiles();
		System.out.println("Converting " + sourceFiles.length + " file(s) to CBP");
		for (int x : new int[20]) {
			for (int i = 0; i < 3; i++) {
				System.out.print(String.format("Dummy\t"));
				Task task = new Task(cbpFile, sourceFiles[i]);
				task.setDaemon(true);
				task.setName(sourceFiles[i].getName());
				task.start();
				task.join();
			}
		}

		runningCbpResource.unload();
		if (cbpFile.exists()) {
			while (cbpFile.delete() == false) {
				cbpFile.delete();
			}
		}
		while (cbpFile.canWrite() == false) {
			try {
				cbpFile.delete();
				cbpFile.createNewFile();
			} catch (Exception exe) {

			}
		}

		try {
			((CBPResource) runningCbpResource).getModelHistory().clear();
			((CBPResource) runningCbpResource).getIgnoreSet().clear();
			runningCbpResource.load(null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println();
		System.out.println(
				"No\tObCount\tEvents\tDifEvnts\tDiffs\tXmiLoad\tUCbpLoad\tOCbpLoad\tXmiSave\tUCbpSave\tOCbpSave\tXmiMem\tUnCbpMem\tOpCbpMem");
		for (int i = 0; i < sourceFiles.length; i++) {
			// if (i == 15) {
			// return;
			// }
			System.out.print(String.format("%s\t", (i + 1)));
			results.append(String.valueOf(i + 1) + " ");

			Task task = new Task(cbpFile, sourceFiles[i]);
			task.setDaemon(true);
			task.setName(sourceFiles[i].getName());
			task.start();
			task.join();
		}
		System.out.println("Finished!");

		System.out.println(results.toString());
		assertEquals(true, true);
	}

	/***
	 * 
	 * @author Ryano
	 *
	 */
	public class Task extends Thread {

		protected File cbpFile;
		protected File diffDirectory;
		protected File xmiFile;

		/***
		 * 
		 * @param cbpFile
		 * @param diffDirectory
		 * @param sourceFile
		 * @throws IOException
		 */
		public Task(File cbpFile, File sourceFile) throws IOException {
			super();
			this.cbpFile = cbpFile;
			this.xmiFile = sourceFile;
		}

		/***
		 * 
		 */
		@SuppressWarnings("restriction")
		@Override
		public void run() {

			long beforeXmiLoad = 0;
			long afterXmiLoad = 0;
			long xmiLoadTime = 0;
			long beforeOptCbpLoad = 0;
			long afterOptCbpLoad = 0;
			long optCbpLoadTime = 0;
			long beforeUnoptCbpLoad = 0;
			long afterUnoptCbpLoad = 0;
			long unoptCbpLoadTime = 0;
			long beforeXmiSave = 0;
			long afterXmiSave = 0;
			long xmiSaveTime = 0;
			long beforeUnoptCbpSave = 0;
			long afterUnoptCbpSave = 0;
			long unoptCbpSaveTime = 0;
			long beforeOptCbpSave = 0;
			long afterOptCbpSave = 0;
			long optCbpSaveTime = 0;

			long beforeXmiMemory = 0;
			long afterXmiMemory = 0;
			long xmiMemory = 0;
			long beforeUnoptCbpMemory = 0;
			long afterUnoptCbpMemory = 0;
			long unoptCbpMemory = 0;
			long beforeOptCbpMemory = 0;
			long afterOptCbpMemory = 0;
			long optCbpMemory = 0;

			int deltaEventCount = 1;
			int diffCount = 0;
			long eventCount = 0;

			System.gc();
			// System.out.print("Processing file " + xmiFile.getName() + " to
			// CBP");

			// measure number of event
			IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
			BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
					Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
			postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
			Builder builder = EMFCompare.builder();
			builder.setPostProcessorRegistry(postProcessorRegistry);
			comparator = builder.build();

			IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
			UMLMerger umlMerger = new UMLMerger();
			umlMerger.setRanking(11);
			registry.add(umlMerger);
			batchMerger = new BatchMerger(registry);

			String xmiFilePath = xmiFile.getAbsolutePath();
			URI xmiFileUri = URI.createFileURI(xmiFilePath);
			Resource xmiResource = xmiResourceSet.createResource(xmiFileUri);
			try {
				xmiLoadTime = 0;
				for (int i : new int[ITERATION]) {

					System.gc();
					beforeXmiMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					beforeXmiLoad = System.nanoTime();
					xmiResource.load(null);
					afterXmiLoad = System.nanoTime();
					afterXmiMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

					xmiLoadTime += afterXmiLoad - beforeXmiLoad;
					xmiMemory += afterXmiMemory - beforeXmiMemory;

					xmiResource.unload();
				}
				xmiResource.load(null);
				xmiLoadTime = xmiLoadTime / ITERATION;
				xmiMemory = xmiMemory / ITERATION;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			xmiResource.setURI(commonXmiURI);

			// initialise UML comparison
			IComparisonScope2 scope = createComparisonScope(runningCbpResource, xmiResource);

			Comparison comparison = comparator.compare(scope);
			EList<Diff> diffs = comparison.getDifferences();
			diffCount = diffs.size();

			// count elements
			List<EObject> list = new ArrayList<EObject>();
			TreeIterator<EObject> iterator = xmiResource.getAllContents();
			int elementCount = 0;
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				list.add(eObject);
				elementCount += 1;
			}

			// MERGE: copy all right to left
			CBPXMLResourceImpl cbpImpl = ((CBPXMLResourceImpl) runningCbpResource);
			cbpImpl.startNewSession(xmiFile.getName());
			int beforeEventCount = runningCbpResource.getChangeEvents().size();
			batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
			int afterEventCount = runningCbpResource.getChangeEvents().size();
			deltaEventCount = afterEventCount - beforeEventCount;

			// measure
			try {
				FileOutputStream outputStream = new FileOutputStream(ignoreListFile, true);
				FileOutputStream outputStreamDummy = new FileOutputStream(ignoreListDummyFile,true);

				CBPResource saveCbpResource = new CBPXMLResourceImpl();
				saveCbpResource.setURI(URI.createFileURI(cbpDummyFile.getAbsolutePath()));

				unoptCbpSaveTime = 0;
				optCbpSaveTime = 0;
				if (deltaEventCount == 0) {
					unoptCbpSaveTime = 0;
				} else {
					// dummy

					
					
					// measure memory size
					for (int i : new int[ITERATION]) {
						saveCbpResource.getChangeEvents().clear();
						saveCbpResource.getChangeEvents().addAll(new ArrayList<>(runningCbpResource.getChangeEvents()));

						for (int j : new int[4]) {
							saveCbpResource.save(null);
							runningCbpResource.saveIgnoreSet(outputStreamDummy);
						}
						
						beforeUnoptCbpSave = System.nanoTime();
						saveCbpResource.save(null);
						afterUnoptCbpSave = System.nanoTime();

						beforeOptCbpSave = System.nanoTime();
						runningCbpResource.saveIgnoreSet(outputStreamDummy);
						saveCbpResource.save(null);
						afterOptCbpSave = System.nanoTime();

						unoptCbpSaveTime += ((afterUnoptCbpSave - beforeUnoptCbpSave));
						optCbpSaveTime += ((afterOptCbpSave - beforeOptCbpSave));
					}
					unoptCbpSaveTime = unoptCbpSaveTime / (ITERATION );
					optCbpSaveTime = optCbpSaveTime / (ITERATION );
				}
//				System.out.println(runningCbpResource.getIgnoreSet().size());
				runningCbpResource.saveIgnoreSet(outputStream);
				runningCbpResource.save(null);
				saveCbpResource.getChangeEvents().clear();
				saveCbpResource.unload();

				// count number of events
				try {
					BufferedReader reader = new BufferedReader(new FileReader(cbpFile));
					long lines = 0;
					while (reader.readLine() != null) {
						lines++;
					}
					reader.close();
					eventCount = lines;
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				// measure xmi save time
				xmiSaveTime = 0;
				for (int i : new int[ITERATION]) {
					beforeXmiSave = System.nanoTime();
					xmiResource.save(null);
					afterXmiSave = System.nanoTime();
					xmiSaveTime += (afterXmiSave - beforeXmiSave);
				}
				if (deltaEventCount == 0) {
					xmiSaveTime = 0;
				} else {
					xmiSaveTime = xmiSaveTime / ITERATION;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			// //---test unoptimised CBP by reload
			CBPResource unoptimisedCbpResource = new CBPXMLResourceImpl();
			cbpResourceSet.getResources().add(unoptimisedCbpResource);
			unoptimisedCbpResource.setURI(runningCbpResource.getURI());
			try {
				unoptCbpLoadTime = 0;
				Map<Object, Object> options = new HashMap<>();
				options.put("optimise", false);
				for (int i : new int[ITERATION]) {
					System.gc();
					beforeUnoptCbpMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					beforeUnoptCbpLoad = System.nanoTime();
					unoptimisedCbpResource.load(options);
					afterUnoptCbpLoad = System.nanoTime();
					afterUnoptCbpMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

					unoptCbpLoadTime += afterUnoptCbpLoad - beforeUnoptCbpLoad;
					unoptCbpMemory += afterUnoptCbpMemory - beforeUnoptCbpMemory;

					((CBPResource) unoptimisedCbpResource).getModelHistory().clear();
					((CBPResource) unoptimisedCbpResource).getIgnoreSet().clear();
					unoptimisedCbpResource.unload();
				}
				unoptCbpLoadTime = unoptCbpLoadTime / ITERATION;
				unoptCbpMemory = unoptCbpMemory / ITERATION;
			} catch (IOException e) {
				e.printStackTrace();
			}

			// //---test optimised CBP by reload
			CBPResource optimisedCbpResource = new CBPXMLResourceImpl();
			optimisedCbpResource.setURI(runningCbpResource.getURI());
			cbpResourceSet.getResources().add(optimisedCbpResource);
			try {
				optCbpLoadTime = 0;
//				FileOutputStream fos = new FileOutputStream(ignoreListFile,true);
//				optimisedCbpResource.saveIgnoreSet(fos);
				for (int i : new int[ITERATION]) {
					System.gc();
					beforeOptCbpMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					optimisedCbpResource.setIgnoreSet(new HashSet<>(runningCbpResource.getIgnoreSet()));
					beforeOptCbpLoad = System.nanoTime();
//					optimisedCbpResource.loadIgnoreSet(new FileInputStream(ignoreListFile));
					optimisedCbpResource.load(null);
					afterOptCbpLoad = System.nanoTime();
					afterOptCbpMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

					optCbpLoadTime += afterOptCbpLoad - beforeOptCbpLoad;
					optCbpMemory += afterOptCbpMemory - beforeOptCbpMemory;

					((CBPResource) optimisedCbpResource).getModelHistory().clear();
					optimisedCbpResource.unload();
				}
				optimisedCbpResource.load(null);
				optCbpLoadTime = optCbpLoadTime / ITERATION;
				optCbpMemory = optCbpMemory / ITERATION;
			} catch (IOException e) {
				e.printStackTrace();
			}

			// after merging, to check if there are no more differences
			scope = createComparisonScope(runningCbpResource, xmiResource);
			comparison = comparator.compare(scope);
			diffs = comparison.getDifferences();
			if (diffs.size() > 0) {
				System.out.println("There are still differences between the CBP and the XMI.");
				// return;
			}

			clearCacheAdapter();

			unoptimisedCbpResource.unload();
			cbpResourceSet.getResources().remove(unoptimisedCbpResource);
			optimisedCbpResource.unload();
			cbpResourceSet.getResources().remove(optimisedCbpResource);
			optimisedCbpResource.unload();

			((CBPResource) unoptimisedCbpResource).getModelHistory().clear();
			((CBPResource) unoptimisedCbpResource).getIgnoreSet().clear();
			((CBPResource) optimisedCbpResource).getModelHistory().clear();
			((CBPResource) optimisedCbpResource).getIgnoreSet().clear();
			// for (EObject eObject : list) {
			// CacheAdapter.getInstance().getInverseReferences(eObject).clear();
			// CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
			// eObject.eAdapters().clear();
			// EcoreUtil.remove(eObject);
			// }
			// list.clear();

			String output = String.format("%s\t%s\t%s\t%s\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f",
					elementCount, eventCount, deltaEventCount, diffCount, (double) xmiLoadTime / 1000000,
					(double) unoptCbpLoadTime / 1000000, (double) optCbpLoadTime / 1000000,
					(double) xmiSaveTime / 1000000, (double) unoptCbpSaveTime / 1000000,
					(double) optCbpSaveTime / 1000000, (double) xmiMemory / 1000000, (double) unoptCbpMemory / 1000000,
					(double) optCbpMemory / 1000000);
			System.out.println(output);
			results.append(output + "\n");
		}

	}

	/***
	 * 
	 * @param cbpResource
	 * @param xmiResource
	 * @return
	 */
	private IComparisonScope2 createComparisonScope(Resource cbpResource, Resource xmiResource) {
		IComparisonScope2 scope = new DefaultComparisonScope(cbpResource, xmiResource, null);
		Set<ResourceSet> resourceSets = ImmutableSet.of(cbpResource.getResourceSet(), xmiResource.getResourceSet());
		com.google.common.collect.ImmutableSet.Builder<URI> uriBuilder = ImmutableSet.builder();
		for (ResourceSet set : resourceSets) {
			for (Resource resource : set.getResources()) {
				uriBuilder.add(resource.getURI());
			}
		}
		Set<URI> setUri = uriBuilder.build();
		scope.getAllInvolvedResourceURIs().addAll(setUri);
		return scope;
	}

	/***
	 * 
	 */
	private void clearCacheAdapter() {
		for (ResourceSet resourceSet : resourceSetList) {
			for (Resource resource : resourceSet.getResources()) {
				TreeIterator<EObject> iterator = resource.getAllContents();
				List<EObject> list = new ArrayList<>();
				while (iterator.hasNext()) {
					EObject item = iterator.next();
					list.add(item);
				}
				for (EObject eObject : list) {
					CacheAdapter.getInstance().getInverseReferences(eObject).clear();
					CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
					eObject.eAdapters().clear();
					EcoreUtil.remove(eObject);

				}
				list.clear();
				CacheAdapter.getInstance().clear(resource);
				CacheAdapter.getInstance().clear();

				resource.getContents().clear();
				resource.unload();
			}
			resourceSet.getResources().clear();
		}
	}

	/***
	 * 
	 * @param resource
	 */
	private void clearCacheAdapter(Resource resource) {
		TreeIterator<EObject> iterator = resource.getAllContents();
		List<EObject> list = new ArrayList<>();
		while (iterator.hasNext()) {
			EObject item = iterator.next();
			list.add(item);
		}
		for (EObject eObject : list) {
			CacheAdapter.getInstance().getInverseReferences(eObject).clear();
			CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
			eObject.eAdapters().clear();
			EcoreUtil.remove(eObject);

		}
		list.clear();
		CacheAdapter.getInstance().clear(resource);
		CacheAdapter.getInstance().clear();
		resource.getContents().clear();
		resource.unload();
	}
}