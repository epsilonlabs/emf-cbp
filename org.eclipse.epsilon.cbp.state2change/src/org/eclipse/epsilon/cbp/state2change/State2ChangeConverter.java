package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.uml2.common.util.CacheAdapter;
import org.eclipse.uml2.uml.UMLPackage;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("restriction")
public class State2ChangeConverter {

	protected URI rootXmiFileUri;
	protected URI commonXmiURI = URI.createURI("umlmodel.xmi");
	protected URI commonCbpxmlURI = URI.createURI("umlmodel.cbpxml");

	protected IBatchMerger batchMerger;
	protected EMFCompare comparator;

	protected List<ResourceSet> resourceSetList = new ArrayList<>();

	protected Resource cbpResource;
	protected Resource cbpStateResource;
	protected ResourceSet cbpResourceSet;
	protected ResourceSet xmiResourceSet;
	protected ResourceSet comparisonResourceSet;
	protected ResourceSet cbpTestResourceSet;
	protected ResourceSet cbpStateResourceSet;

	protected Map<Object, Object> saveOptions;
	protected String cbpStatePath;

	private File sourceDirectory;

	public State2ChangeConverter(File xmiDirectory) throws FileNotFoundException {
		if (!xmiDirectory.exists()) {
			throw new FileNotFoundException();
		}
		this.sourceDirectory = xmiDirectory;
	}

	

	public boolean generate(File cbpFile, File diffDirectory) throws Exception {

		UMLPackage.eINSTANCE.eClass();
		// JavaPackage.eINSTANCE.eClass();
		saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

		// -----
		cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());

		cbpResource = new CBPXMLResourceImpl();
		URI cbpUri = URI.createFileURI(cbpFile.getAbsolutePath());
		File file = new File(cbpUri.toFileString());
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}
		cbpResource.setURI(cbpUri);
		cbpResourceSet.getResources().add(cbpResource);
//		cbpResource.load(null);

		xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		resourceSetList.add(xmiResourceSet);

		comparisonResourceSet = new ResourceSetImpl();
		comparisonResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		resourceSetList.add(comparisonResourceSet);

		cbpStateResourceSet = new ResourceSetImpl();
		cbpStateResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		resourceSetList.add(cbpStateResourceSet);

		cbpStatePath = "D:/TEMP/BigModel/cbp-state/";
		cbpStateResource = cbpStateResourceSet.createResource(URI.createURI("cbp-state.xmi"));

		File[] sourceFiles = sourceDirectory.listFiles();
		System.out.println("Converting " + sourceFiles.length + " file(s) to CBP");
		for (int i = 0; i < sourceFiles.length; i++) {
			Task task = new Task(cbpFile, diffDirectory, sourceFiles[i]);
			task.setDaemon(true);
			task.setName(sourceFiles[i].getName());
			task.start();
			task.join();
		}
		System.out.println("Finished!");
		return true;
	}

	
	public class Task extends Thread {

		protected File cbpFile;
		protected File diffDirectory;
		protected File xmiFile;

		public Task(File cbpFile, File diffDirectory, File sourceFile) throws IOException {
			super();
			this.cbpFile = cbpFile;
			this.diffDirectory = diffDirectory;
			this.xmiFile = sourceFile;

		}

		@SuppressWarnings("restriction")
		@Override
		public void run() {

			System.gc();
			System.out.print("Converting file " + xmiFile.getName() + " to CBP");

			try {
				cbpResource.load(null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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
			xmiResourceSet.getResource(xmiFileUri, true);
			Resource xmiResource = xmiResourceSet.getResources().get(0);
			xmiResource.setURI(commonXmiURI);

			// initialise UML comparison
			IComparisonScope2 scope = createComparisonScope(cbpResource, xmiResource);

			Comparison comparison = comparator.compare(scope);
			EList<Diff> diffs = comparison.getDifferences();

			// persist diffs
			Resource comparisonResource = comparisonResourceSet.createResource(URI.createURI("diffs.xmi"));
			comparisonResource.getContents().addAll(EcoreUtil.copyAll(comparison.getDifferences()));
			String diffFilePath = diffDirectory.getPath() + File.separator + "Diff-" + xmiFile.getName();
			File diffFile = new File(diffFilePath);
			FileOutputStream diffFileOutputStream;
			try {
				diffFileOutputStream = new FileOutputStream(diffFile);
				comparisonResource.save(diffFileOutputStream, saveOptions);
				diffFileOutputStream.flush();
				diffFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// copy all right to left
			List<EObject> list = new ArrayList<EObject>();
			TreeIterator<EObject> iterator = cbpResource.getAllContents();
			while (iterator.hasNext()) {
				EObject eObject = iterator.next();
				list.add(eObject);
			}
			CBPXMLResourceImpl cbpImpl = ((CBPXMLResourceImpl) cbpResource);
			cbpImpl.startNewSession(xmiFile.getName());
			System.out.print(" " + diffs.size());
			batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
			try {
				cbpResource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}

//			// //---test by reload
//			((CBPResource) cbpResource).getModelHistory().clear();
//			((CBPResource) cbpResource).getIgnoreSet().clear();
//			clearCacheAdapter(cbpResource);
//			try {
//				cbpResource.load(null);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

			cbpStateResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
			URI cbpStateFileUri = URI.createFileURI(cbpStatePath + "Cbp-state-" + xmiFile.getName());
			cbpStateResource.setURI(cbpStateFileUri);
			try {
				cbpStateResource.save(null);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// after merging, to check if there are no more differences
			scope = createComparisonScope(cbpResource, xmiResource);
			comparison = comparator.compare(scope);
			diffs = comparison.getDifferences();
			System.out.println(" " + diffs.size());
			for (Diff diff : diffs) {
				System.out.println(diff);
			}
			if (diffs.size() > 0) {
				System.out.println("There are still differences between the CBP and the XMI.");
				return;
			}

			clearCacheAdapter();
			((CBPResource) cbpResource).getModelHistory().clear();
			((CBPResource) cbpResource).getIgnoreSet().clear();
			for (EObject eObject: list) {
				CacheAdapter.getInstance().getInverseReferences(eObject).clear();
				CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject).clear();
				eObject.eAdapters().clear();
				EcoreUtil.remove(eObject);
			}
			list.clear();
			clearCacheAdapter(cbpResource);
		}
	}
	
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
