package org.eclipse.epsilon.cbp.state2change;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.merge.UMLMerger;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
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

	public String generate(OutputStream outputStream, File diffDirectory) throws Exception {
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
		ResourceSet cbpStateResourceSet = new ResourceSetImpl();
		cbpStateResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
				new XMIResourceFactoryImpl());
		String cbpStatePath = "D:/TEMP/BigModel/cbp-state/";
		Resource cbpStateResource = cbpStateResourceSet.createResource(URI.createURI("cbp-state.xmi"));
		// -----

		ResourceSet cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		cbpResource = new CBPXMLResourceImpl();
		cbpResourceSet.getResources().add(cbpResource);

		File[] sourceFiles = sourceDirectory.listFiles();
		System.out.println("Converting " + sourceFiles.length + " file(s) to CBP");

		for (int i = 0; i < sourceFiles.length; i++) {
			System.out.println("Converting file " + sourceFiles[i].getName() + " to CBP");
			if (i == 0) {
				rootXmiFile = sourceFiles[i];
				rootXmiResourceSet = new ResourceSetImpl();
				rootXmiFilePath = rootXmiFile.getAbsolutePath();
				rootXmiFileUri = URI.createFileURI(rootXmiFilePath);
				rootXmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
						new XMIResourceFactoryImpl());
				rootXmiResource = rootXmiResourceSet.getResource(rootXmiFileUri, true);
				rootXmiResource.setURI(commonXmiURI);

				cbpResource.setURI(commonCbpxmlURI);
				((CBPXMLResourceImpl) cbpResource).startNewSession();

				IComparisonScope scope = new DefaultComparisonScope(cbpResource, rootXmiResource, null);
				EMFCompare comparator = EMFCompare.builder().build();
				Comparison comparison = comparator.compare(scope);

				// persist diffs
				ResourceSet comparisonResourceSet = new ResourceSetImpl();
				comparisonResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
						new XMIResourceFactoryImpl());
				Resource comparisonResource = comparisonResourceSet.createResource(URI.createURI("diffs.xmi"));
				comparisonResource.getContents().addAll(EcoreUtil.copyAll(comparison.getDifferences()));
				String diffFilePath = diffDirectory.getPath() + File.separator + "Diff-" + rootXmiFile.getName();
				File diffFile = new File(diffFilePath);
				FileOutputStream diffFileOutputStream = new FileOutputStream(diffFile);
				comparisonResource.save(diffFileOutputStream, saveOptions);
				diffFileOutputStream.flush();
				diffFileOutputStream.close();

				cbpResource.getContents().addAll(EcoreUtil.copyAll(rootXmiResource.getContents()));
				cbpResource.save(outputStream, null);

				// ----CBP State
				ByteArrayOutputStream output2 = new ByteArrayOutputStream();
				cbpStateResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
				URI cbpStateFileUri = URI.createFileURI(cbpStatePath + "Cbp-state-" + rootXmiFile.getName());
				cbpStateResource.setURI(cbpStateFileUri);
				cbpStateResource.save(null);
				cbpStateResource.save(output2, null);
				cbpStateResource.getContents().clear();
				cbpStateResource.unload();

			} else {
				File xmiFile = sourceFiles[i];
				ResourceSet xmiResourceSet = new ResourceSetImpl();
				String xmiFilePath = xmiFile.getAbsolutePath();
				URI xmiFileUri = URI.createFileURI(xmiFilePath);
				xmiResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
						new XMIResourceFactoryImpl());
				xmiResourceSet.getResource(xmiFileUri, true);
				Resource xmiResource = xmiResourceSet.getResources().get(0);
				xmiResource.setURI(commonXmiURI);

				//initialise UML comparison
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
				
				IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
				BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
						Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
				postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
				Builder builder = EMFCompare.builder();
				builder.setPostProcessorRegistry(postProcessorRegistry);
				
				EMFCompare comparator = builder.build();
				Comparison comparison = comparator.compare(scope);
				EList<Diff> diffs = comparison.getDifferences();
				
				// persist diffs
				ResourceSet comparisonResourceSet = new ResourceSetImpl();
				comparisonResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
						new XMIResourceFactoryImpl());
				Resource comparisonResource = comparisonResourceSet.createResource(URI.createURI("diffs.xmi"));
				comparisonResource.getContents().addAll(EcoreUtil.copyAll(comparison.getDifferences()));
				String diffFilePath = diffDirectory.getPath() + File.separator + "Diff-" + xmiFile.getName();
				File diffFile = new File(diffFilePath);
				FileOutputStream diffFileOutputStream = new FileOutputStream(diffFile);
				comparisonResource.save(diffFileOutputStream, saveOptions);
				diffFileOutputStream.flush();
				diffFileOutputStream.close();
				
				// copy all right to left
				IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
				UMLMerger umlMerger = new UMLMerger();
				umlMerger.setRanking(11);
				registry.add(umlMerger);
				IBatchMerger batchMerger = new BatchMerger(registry);
				((CBPXMLResourceImpl) cbpResource).startNewSession();
				batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());

				// ----CBP State
				cbpStateResource.getContents().addAll(EcoreUtil.copyAll(cbpResource.getContents()));
				URI cbpStateFileUri = URI.createFileURI(cbpStatePath + "Cbp-state-" + xmiFile.getName());
				cbpStateResource.setURI(cbpStateFileUri);
				cbpStateResource.save(null);
				cbpStateResource.getContents().clear();
				cbpStateResource.unload();
	
				// after merging, to check if there are no more differences
				comparison = comparator.compare(scope);
				diffs = comparison.getDifferences();
				if (diffs.size() > 0) {
						throw new Exception("There are still differences between the CBP and the XMI.");
				}
				cbpResource.save(outputStream, null);
			}
		}
		System.out.println("Finished!");

		return outputStream.toString();
	}
}
