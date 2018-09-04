package org.eclipse.epsilon.cbp.state2change;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;

public class Xmi2CbpConverter {

	public void convertXmiToCbp(File xmiDirectory, File cbpDirectory) throws Exception {

		// initialisation
		MoDiscoXMLPackage.eINSTANCE.eClass();

		final String cbpFileName = "output.cbpxml";
		final String ignoreFileName = "output.ignorelist";
		if (cbpDirectory.exists() == false)
			cbpDirectory.mkdir();
		File cbpFile = new File(cbpDirectory.getAbsolutePath() + File.separator + cbpFileName);
		if (cbpFile.exists()) {
			cbpFile.delete();
		}
		cbpFile.createNewFile();
		File ignoreFile = new File(cbpDirectory.getAbsolutePath() + File.separator + ignoreFileName);
		if (ignoreFile.exists()) {
			ignoreFile.delete();
		}

		Map<Object, Object> saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
		saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbpxml", new CBPXMLResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

		Resource runningCbpResource = resourceSet.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
		runningCbpResource.load(null);

		// iteration through all xmi files
		File[] xmiFiles = xmiDirectory.listFiles();
		Arrays.sort(xmiFiles);
		for (File xmiFile : xmiFiles) {

			System.out.println(State2ChangeTool.getTimeStamp() + ": Processing file " + xmiFile.getName() + " to CBP");
			Resource xmiResource = resourceSet.createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
			xmiResource.load(null);

			IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
			IBatchMerger batchMerger = new BatchMerger(registry);

			IComparisonScope2 scope = new DefaultComparisonScope(runningCbpResource, xmiResource, null);
			Builder builder = EMFCompare.builder();
			EMFCompare comparator = builder.build();
			System.out.println("Compare ...");
			System.out.println("   Start: " + State2ChangeTool.getTimeStamp());
			Comparison comparison = comparator.compare(scope);
			EList<Diff> diffs = comparison.getDifferences();
			System.out.println("   End: " + State2ChangeTool.getTimeStamp());
			System.out.println("   Diffs: " + diffs.size());

			((CBPXMLResourceImpl) runningCbpResource).startNewSession(xmiFile.getName());

			int prevDiffs = diffs.size() + 1;
			while (diffs.size() > 0 && prevDiffs > diffs.size()) {
				prevDiffs = diffs.size();

				System.out.println("Merge ...");
				System.out.println("   Start: " + State2ChangeTool.getTimeStamp());
				batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());
				System.out.println("   End: " + State2ChangeTool.getTimeStamp());

				
//				Resource tempResource = (new XMIResourceFactoryImpl()).createResource(URI.createURI("temp.xmi"));
//				tempResource.getContents().addAll(runningCbpResource.getContents());
//				
//				StringOutputStream tempOutput = new StringOutputStream();
//				tempResource.save(tempOutput, saveOptions);
//				StringOutputStream tempOutput2 = new StringOutputStream();
//				xmiResource.save(tempOutput2, saveOptions);
//				
//				System.out.println(tempOutput);
//				System.out.println("-----");
//				System.out.println(tempOutput2);
				
				
				System.out.println("Re-compare again for validation ...");
				System.out.println("   Start: " + State2ChangeTool.getTimeStamp());
				scope = new DefaultComparisonScope(runningCbpResource, xmiResource, null);
				comparison = comparator.compare(scope);
				diffs = comparison.getDifferences();
				System.out.println("   End: " + State2ChangeTool.getTimeStamp());
				System.out.println("   Diffs: " + diffs.size());
			}
			// saving CBP
			FileOutputStream ignoreFileOutputStream = new FileOutputStream(ignoreFile, true);
			runningCbpResource.save(saveOptions);
			// Set<Integer> set1 = new HashSet<>(((CBPXMLResourceImpl)
			// runningCbpResource).getIgnoreSet());
			// System.out.println(set1);
			((CBPXMLResourceImpl) runningCbpResource).saveIgnoreSet(ignoreFileOutputStream);

//			// validation
//			System.out.println("Reload and compare again for validation ...");
//			System.out.println("   Start: " + State2ChangeTool.getTimeStamp());
//			Resource validationCbpResource = resourceSet.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
//			FileInputStream ignoreFileInputStream = new FileInputStream(ignoreFile);
//			((CBPXMLResourceImpl) validationCbpResource).loadIgnoreSet(ignoreFileInputStream);
//			Set<Integer> set2 = new HashSet<>(((CBPXMLResourceImpl) validationCbpResource).getIgnoreSet());
//			// System.out.println(set2);
//			validationCbpResource.load(null);
//
//			scope = new DefaultComparisonScope(validationCbpResource, xmiResource, null);
//			comparison = comparator.compare(scope);
//			diffs = comparison.getDifferences();

			// if (set1.toString().equals(set2.toString()) || set1.toString() ==
			// set2.toString()) {
			// System.out.println("SAMA!!");
			// }else {
			// System.out.println("BEDA!!");
			// }

//			//this part is for debug
//			if (diffs.size() > 0) {
//
//				StringOutputStream os1 = new StringOutputStream();
//				StringOutputStream os2 = new StringOutputStream();
//				StringOutputStream os3 = new StringOutputStream();
//				Resource r1 = (new XMIResourceFactoryImpl().createResource(URI.createURI("foo1.xmi")));
//				Resource r2 = (new XMIResourceFactoryImpl().createResource(URI.createURI("foo2.xmi")));
//				r1.getContents().addAll(EcoreUtil.copyAll(runningCbpResource.getContents()));
//				r2.getContents().addAll(EcoreUtil.copyAll(validationCbpResource.getContents()));
//
//				// r1.save(os1, null);
//				// r2.save(os2, null);
//				runningCbpResource.save(os1, null);
//				validationCbpResource.save(os2, null);
//				xmiResource.save(os3, null);
//
//				System.out.println("R0---------------------");
//				try (BufferedReader br = new BufferedReader(new FileReader(cbpFile))) {
//					String line;
//					int i = 0;
//					while ((line = br.readLine()) != null) {
//						if (set2.contains(i) == false)
//							System.out.println(line);
//						i += 1;
//					}
//				}
//
//				// System.out.println("R1---------------------");
//				// System.out.println(os1);
//				// System.out.println("R2---------------------");
//				// System.out.println(os2);
//				// System.out.println("R3---------------------");
//				// System.out.println(os3);
//				// System.out.println("-----------------------");
//
//				ignoreFileInputStream.close();
//				throw new Exception("   Number of differences are not zero!");
//			}
//			System.out.println("   End: " + State2ChangeTool.getTimeStamp());
//			System.out.println("   Diffs: " + diffs.size());

			// cleaning
			ignoreFileOutputStream.close();
//			ignoreFileInputStream.close();
			xmiResource.unload();
//			((CBPXMLResourceImpl) validationCbpResource).getModelHistory().clear();
//			((CBPXMLResourceImpl) validationCbpResource).clearIgnoreSet();
//			validationCbpResource.unload();
			resourceSet.getResources().remove(xmiResource);
//			resourceSet.getResources().remove(validationCbpResource);

			System.out.println();
		}

		// cleaning
		((CBPXMLResourceImpl) runningCbpResource).getModelHistory().clear();
		((CBPXMLResourceImpl) runningCbpResource).clearIgnoreSet();
		runningCbpResource.unload();
		resourceSet.getResources().clear();
	}
}
