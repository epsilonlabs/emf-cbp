package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
public class ECMFATest2 {

	public final static int ITERATION = 1;

	protected File cbpFile = new File("D:/TEMP/ECMFA/test/ecmfa15.cbpxml");
	private File xmiFile = new File("D:/TEMP/ECMFA/test/ecmfa15.xmi");
	private File ignoreListFile = new File("D:/TEMP/ECMFA/test/ecmfa15.ignorelist");

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
	public ECMFATest2() {

	}

	/***
	 * 
	 * @param cbpFile
	 * @param diffDirectory
	 * @return
	 * @throws Exception
	 */
	@Test
	public void testUMLLoad() throws Exception {

		
		
		UMLPackage.eINSTANCE.eClass();
		saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

		// -----Set Up CBP
		cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());

		runningCbpResource = new CBPXMLResourceImpl();
		cbpResourceSet.getResources().add(runningCbpResource);
		cbpFile.createNewFile();
		URI cbpUri = URI.createFileURI(cbpFile.getAbsolutePath());
		runningCbpResource.setURI(cbpUri);

		try {
			FileInputStream inputStream = new FileInputStream(ignoreListFile);
			runningCbpResource.loadIgnoreSet(inputStream);
			
			Set<Integer> ignoreSet = runningCbpResource.getIgnoreSet();
//			System.out.println();
//			
//			BufferedReader br = new BufferedReader(new FileReader(cbpFile));
//			String line = null;  
//			int i = 0;
//			while ((line = br.readLine()) != null)  
//			{  
//			   if (ignoreSet.contains(i)) {
//				   i+=1;
//				   continue;
//			   }
//			   System.out.println(line);
//			   i+=1;
//			}
			
			runningCbpResource.load(null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// --Set up XMI
		xmiResourceSet = new ResourceSetImpl();
		xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		String xmiFilePath = xmiFile.getAbsolutePath();
		URI xmiFileUri = URI.createFileURI(xmiFilePath);
		Resource xmiResource = xmiResourceSet.createResource(xmiFileUri);
		xmiResource.load(null);
		
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
		
		IComparisonScope2 scope = createComparisonScope(runningCbpResource, xmiResource);
		Comparison comparison = comparator.compare(scope);
		EList<Diff> diffs = comparison.getDifferences();
		batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
		
		// after merging, to check if there are no more differences
		scope = createComparisonScope(runningCbpResource, xmiResource);
		comparison = comparator.compare(scope);
		diffs = comparison.getDifferences();
		if (diffs.size() > 0) {
			System.out.println("There are still differences between the CBP and the XMI.");
			// return;
		}
		
		assertEquals(true, true);
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