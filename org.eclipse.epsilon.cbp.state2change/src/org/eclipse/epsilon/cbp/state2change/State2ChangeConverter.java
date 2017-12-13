package org.eclipse.epsilon.cbp.state2change;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.FeatureMapChange;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.ResourceLocationChange;
import org.eclipse.emf.compare.internal.spec.ReferenceChangeSpec;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.ExecutionSpecificationChange;
import org.eclipse.emf.compare.uml2.internal.ExtendChange;
import org.eclipse.emf.compare.uml2.internal.GeneralizationSetChange;
import org.eclipse.emf.compare.uml2.internal.IntervalConstraintChange;
import org.eclipse.emf.compare.uml2.internal.MessageChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.OpaqueElementBodyChange;
import org.eclipse.emf.compare.uml2.internal.ProfileApplicationChange;
import org.eclipse.emf.compare.uml2.internal.StereotypeApplicationChange;
import org.eclipse.emf.compare.uml2.internal.StereotypeAttributeChange;
import org.eclipse.emf.compare.uml2.internal.StereotypeReferenceChange;
import org.eclipse.emf.compare.uml2.internal.StereotypedElementChange;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.InverseUpdatingFeatureMapEntry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.uml2.common.util.CacheAdapter;
import org.eclipse.uml2.uml.UMLPackage;

import com.google.common.collect.ImmutableSet;

public class State2ChangeConverter {

	private File sourceDirectory;

	public State2ChangeConverter(File xmiDirectory) throws FileNotFoundException {
		if (!xmiDirectory.exists()) {
			throw new FileNotFoundException();
		}
		this.sourceDirectory = xmiDirectory;
	}

	public boolean generate(File cbpFile, File diffDirectory) throws Exception {
		Resource cbpResource;
		Resource rootXmiResource = null;
		File rootXmiFile;
		ResourceSet rootXmiResourceSet = null;
		String rootXmiFilePath;
		URI rootXmiFileUri;
		URI commonXmiURI = URI.createURI("umlmodel.xmi");
		URI commonCbpxmlURI = URI.createURI("umlmodel.cbpxml");

		UMLPackage.eINSTANCE.eClass();
		// JavaPackage.eINSTANCE.eClass();
		final Map<Object, Object> saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

		// -----

		ResourceSet cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		cbpResource = new CBPXMLResourceImpl();
		URI cbpUri = URI.createFileURI(cbpFile.getAbsolutePath());

		// File file = new File(cbpUri.toFileString());
		// if (file.exists()) {
		// file.delete();
		// }
		cbpResource.setURI(cbpUri);
		cbpResourceSet.getResources().add(cbpResource);
		cbpResource.load(null);

		IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
		BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
				Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
		postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
		Builder builder = EMFCompare.builder();
		builder.setPostProcessorRegistry(postProcessorRegistry);
		EMFCompare comparator = builder.build();

		IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
		UMLMerger umlMerger = new UMLMerger();
		umlMerger.setRanking(11);
		registry.add(umlMerger);
		IBatchMerger batchMerger = new BatchMerger(registry);
		// -----

		ResourceSet xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		ResourceSet comparisonResourceSet = new ResourceSetImpl();
		comparisonResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		ResourceSet cbpTestResourceSet = new ResourceSetImpl();
		cbpTestResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		ResourceSet cbpStateResourceSet = new ResourceSetImpl();
		cbpStateResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		String cbpStatePath = "D:/TEMP/BigModel/cbp-state/";
		Resource cbpStateResource = cbpStateResourceSet.createResource(URI.createURI("cbp-state.xmi"));

		File[] sourceFiles = sourceDirectory.listFiles();
		System.out.println("Converting " + sourceFiles.length + " file(s) to CBP");

		for (int i = 0; i < sourceFiles.length; i++) {
			System.gc();
			System.out.print("Converting file " + sourceFiles[i].getName() + " to CBP");

			File xmiFile = sourceFiles[i];

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
			FileOutputStream diffFileOutputStream = new FileOutputStream(diffFile);
			comparisonResource.save(diffFileOutputStream, saveOptions);
			clearCacheAdapter(comparisonResource);
			comparisonResource.getContents().clear();
			comparisonResource.eAdapters().clear();
			comparisonResource.unload();
			CacheAdapter.getInstance().clear(comparisonResource);
			diffFileOutputStream.flush();
			diffFileOutputStream.close();

			// copy all right to left
			CBPXMLResourceImpl cbpImpl = ((CBPXMLResourceImpl) cbpResource);
			cbpImpl.startNewSession(xmiFile.getName());
			System.out.print(" " + diffs.size());
			batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
			cbpResource.save(null);

			// //---test by reload
			((CBPResource) cbpResource).getModelHistory().clear();
			((CBPResource) cbpResource).getIgnoreSet().clear();
			cbpResource.getContents().clear();
			cbpResource.unload();
			cbpResource.load(null);

			cbpStateResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
			URI cbpStateFileUri = URI.createFileURI(cbpStatePath + "Cbp-state-" + xmiFile.getName());
			cbpStateResource.setURI(cbpStateFileUri);
			cbpStateResource.save(null);
			clearCacheAdapter(cbpStateResource);
			cbpStateResource.getContents().clear();
			cbpStateResource.eAdapters().clear();
			cbpStateResource.unload();
			CacheAdapter.getInstance().clear(cbpStateResource);

			clearCacheAdapter(cbpResource);

			// after merging, to check if there are no more differences
			scope = createComparisonScope(cbpResource, xmiResource);
			comparison = comparator.compare(scope);
			diffs = comparison.getDifferences();
			System.out.println(" " + diffs.size());
			for (Diff diff : diffs) {
				System.out.println(diff);
			}
			if (diffs.size() > 2) {
				throw new Exception("There are still differences between the CBP and the XMI.");
			}

			clearCacheAdapter(xmiResource);
			xmiResource.getContents().clear();
			xmiResource.eAdapters().clear();
			xmiResource.unload();

			xmiResourceSet.getResources().clear();
			cbpTestResourceSet.getResources().clear();
			comparisonResourceSet.getResources().clear();
			cbpStateResourceSet.getResources().clear();

			((CBPResource) cbpResource).getModelHistory().clear();
			((CBPResource) cbpResource).getIgnoreSet().clear();

		}
		System.out.println("Finished!");
		return true;
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

	private void clearCacheAdapter(Resource resource) {
		TreeIterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject item = iterator.next();
			CacheAdapter.getInstance().getInverseReferences(item).clear();
			CacheAdapter.getInstance().getNonNavigableInverseReferences(item).clear();
		}
		CacheAdapter.getInstance().clear(resource);
		CacheAdapter.getInstance().clear();
	}

}
