package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.merge.IMerger.Registry;
import org.eclipse.emf.compare.merge.IMerger2;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.uml2.uml.UMLPackage;

public class State2ChangeConverter {

	private File sourceDirectory;
	private int copyCounter = 0;

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
		URI commonXmiURI = URI.createURI("umlmodel.xmi");
		URI commonCbpxmlURI = URI.createURI("umlamodel.cbpxml");

		UMLPackage.eINSTANCE.eClass();
		// JavaPackage.eINSTANCE.eClass();

		ResourceSet cbpResourceSet = new ResourceSetImpl();
		cbpResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		cbpResource = new CBPXMLResourceImpl();
		cbpResourceSet.getResources().add(cbpResource);

		File[] sourceFiles = sourceDirectory.listFiles();
		System.out.println("\nConverting " + sourceFiles.length + " file(s) to CBP\n");

		for (int i = 0; i < sourceFiles.length; i++) {

			System.out.println("\nConverting file " + sourceFiles[i].getName() + " to CBP\n");

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

				// IMatchEngine.Factory.Registry matchRegistry =
				// MatchEngineFactoryRegistryImpl.createStandaloneInstance();
				// MatchEngineFactoryImpl matchEngineFactory = new
				// MatchEngineFactoryImpl(UseIdentifiers.NEVER);
				// matchEngineFactory.setRanking(20); // default engine ranking
				// is 10, must be higher to override.
				// matchRegistry.add(matchEngineFactory);
				// EMFCompare comparator =
				// EMFCompare.builder().setMatchEngineFactoryRegistry(matchRegistry).build();

				EMFCompare comparator = EMFCompare.builder().build();
				Comparison comparison = comparator.compare(scope);
				EList<Diff> diffs = comparison.getDifferences();
				final IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();

				// copy all right to left
				((CBPXMLResourceImpl) cbpResource).startNewSession();

				System.out.println("Size Diffs = " + diffs.size());
				Set<Diff> mergedDiffs = new HashSet<>();
				try {
					customMerge(diffs, mergerRegistry, mergedDiffs);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

//				 IBatchMerger merger = new BatchMerger(mergerRegistry);
//				 merger.copyAllRightToLeft(diffs, null);

				// after merging, to check if there are no more differences
				scope = new DefaultComparisonScope(cbpResource, xmiResource, null);
				comparator = EMFCompare.builder().build();
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

	private void customMerge(EList<Diff> diffs, final IMerger.Registry mergerRegistry, Set<Diff> mergedDiffs) {
		System.out.println("Before Merge:");
		for (Diff diff : diffs) {
			System.out.println(diff);
		}
		System.out.println("On Merging:");
		for (Diff diff : diffs) {
			System.out.println(diff);
			merge(diff, true, mergerRegistry, mergedDiffs);
		}
	}

	private void merge(Diff diff, boolean mergeRightToLeft, Registry registry, Set<Diff> handledDiffs) {
		if (!handledDiffs.contains(diff)) {
			IMerger merger = registry.getHighestRankingMerger(diff);
			IMerger2 merger2 = (IMerger2) merger;
			Set<Diff> parentDiffs = merger2.getDirectMergeDependencies(diff, mergeRightToLeft);
			
			
			
			for (Diff itemDiff : parentDiffs) {
				System.out.println("+--"+itemDiff);
				if (!itemDiff.equals(diff) && !handledDiffs.contains(itemDiff)) {
					this.merge(itemDiff, mergeRightToLeft, registry, handledDiffs);
				}
				handledDiffs.add(diff);
			}

			Set<Diff> rejectedDiffs = merger2.getDirectResultingRejections(diff, mergeRightToLeft);
			handledDiffs.addAll(rejectedDiffs);
			if (copyCounter == 21){
				System.out.println();
			}
			merger2.copyRightToLeft(diff, null);
			copyCounter += 1;
			System.out.println("Copy Counter = " + copyCounter);
			handledDiffs.add(diff);
			
//			Set<Diff> resultingDiffs = merger2.getDirectResultingMerges(diff, mergeRightToLeft);
//			for (Diff resultingDiff : resultingDiffs){
//				merger2.copyRightToLeft(resultingDiff, null);
//				handledDiffs.add(resultingDiff);
//			}
		}
	}

}
