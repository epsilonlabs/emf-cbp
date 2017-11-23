package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.merge.IMerger.Registry;
import org.eclipse.emf.compare.merge.IMerger2;
import org.eclipse.emf.compare.merge.PseudoConflictMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;

public class State2ChangeConverter {

	private File sourceDirectory;
	private Set<Diff> mergedDiffs;

	public State2ChangeConverter(File xmiDirectory) throws FileNotFoundException {
		if (!xmiDirectory.exists()) {
			throw new FileNotFoundException();
		}
		this.sourceDirectory = xmiDirectory;
	}

	public String generate(OutputStream outputStream) throws Exception {
		Resource cbpResource;
		Resource rootXmiResource = null;
		File rootXmiFile;
		ResourceSet rootXmiResourceSet = null;
		String rootXmiFilePath;
		URI rootXmiFileUri;
		URI commonXmiURI = URI.createURI("javamodel.xmi");
		URI commonCbpxmlURI = URI.createURI("javamodel.cbpxml");

		mergedDiffs = new HashSet<>();

		JavaPackage.eINSTANCE.eClass();

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
				rootXmiResourceSet.getResource(rootXmiFileUri, true);
				rootXmiResource = rootXmiResourceSet.getResources().get(0);
				rootXmiResource.setURI(commonXmiURI);

				cbpResource.setURI(commonCbpxmlURI);
				((CBPXMLResourceImpl) cbpResource).startNewSession();
				cbpResource.getContents().addAll(EcoreUtil.copyAll(rootXmiResource.getContents()));
				cbpResource.save(outputStream, null);

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

				IComparisonScope scope = new DefaultComparisonScope(cbpResource, xmiResource, null);
				EMFCompare comparator = EMFCompare.builder().build();
				Comparison comparison = comparator.compare(scope);
				EList<Diff> diffs = comparison.getDifferences();
				final IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();

				//copy all right to left
				((CBPXMLResourceImpl) cbpResource).startNewSession();
				 IBatchMerger merger = new BatchMerger(registry);
				 merger.copyAllRightToLeft(diffs, null);
				
//				//after merging to check if there are no more differences
//				scope = new DefaultComparisonScope(cbpResource, xmiResource, null);
//				comparator = EMFCompare.builder().build();
//				comparison = comparator.compare(scope);
//				diffs = comparison.getDifferences();
//				if (diffs.size() > 0){
//					throw new Exception("There are still differences between the CBP and the XMI.");
//				}
				cbpResource.save(outputStream, null);
			}
		}
		System.out.println("Finished!");

		return outputStream.toString();
	}

	private void merge(Diff diff, boolean mergeRightToLeft, Registry registry) {
		if (!mergedDiffs.contains(diff)) {
			IMerger merger = registry.getHighestRankingMerger(diff);
			IMerger2 merger2 = (IMerger2) merger;
			Set<Diff> parentDiffs = merger2.getDirectMergeDependencies(diff, mergeRightToLeft);

			for (Diff itemDiff : parentDiffs) {
				if (!itemDiff.equals(diff) && !mergedDiffs.contains(itemDiff)) {
					this.merge(itemDiff, mergeRightToLeft, registry);
				}
			}

			Set<Diff> rejectedDiffs = merger2.getDirectResultingRejections(diff, mergeRightToLeft);
			mergedDiffs.addAll(rejectedDiffs);
			merger2.copyRightToLeft(diff, null);
			mergedDiffs.add(diff);
			
			
		}
		// Set<Diff> a = merger2.getDirectMergeDependencies(diff, true);
		// Set<Diff> b = merger2.getDirectResultingMerges(diff, true);
		// Set<Diff> c = merger2.getDirectResultingRejections(diff, true);
		// System.out.print("");

	}

}
