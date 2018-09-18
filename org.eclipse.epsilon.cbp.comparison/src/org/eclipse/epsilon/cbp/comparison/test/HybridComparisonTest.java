package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.internal.spec.MatchSpec;
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
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.comparison.HybridComparison;
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
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.ModelImpl;
import org.eclipse.uml2.uml.internal.impl.OperationImpl;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.junit.Test;

public class HybridComparisonTest {

	private File leftXmiFile;
	private File rightXmiFile;
	private File diffFile;
	private File cbpFile;
	private File leftCbpFile;
	private File rightCbpFile;
	private UMLFactory factory;

	private int ITERATION = 25;
	private int START_FROM = 3;

	public HybridComparisonTest() {
		Logger.getRootLogger().setLevel(Level.OFF);

		UMLPackage.eINSTANCE.eClass();
		factory = UMLFactory.eINSTANCE;

		leftXmiFile = new File("D:\\TEMP\\COMPARISON\\left.xmi");
		rightXmiFile = new File("D:\\TEMP\\COMPARISON\\right.xmi");
		diffFile = new File("D:\\TEMP\\COMPARISON\\diffs.xmi");
		cbpFile = new File("D:\\TEMP\\COMPARISON\\cbp.cbpxml");
		leftCbpFile = new File("D:\\TEMP\\COMPARISON\\left.cbpxml");
		rightCbpFile = new File("D:\\TEMP\\COMPARISON\\right.cbpxml");
	}

	@Test
	public void testFindDeletedObject() throws IOException {
		try {
			System.out.println("Finding deleted objects ...");

			leftCbpFile = new File("D:\\TEMP\\ASE\\comparison\\epsilon.008.cbpxml");
			rightCbpFile = new File("D:\\TEMP\\ASE\\comparison\\epsilon.044.cbpxml");

			long[] results = new long[ITERATION];

			CBPResource leftResource = (CBPResource) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
			System.out.println("Loading " + leftResource.getURI().lastSegment() + "...");
			leftResource.load(null);

			CBPResource rightResource = (CBPResource) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
			System.out.println("Loading " + rightResource.getURI().lastSegment() + "...");
			rightResource.load(null);

			Set<EObject> deletedObjects = new HashSet<>();

			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + (i + 1) + " ...");
				deletedObjects.clear();

				long start = System.nanoTime();

				TreeIterator<EObject> leftIterator = leftResource.getAllContents();
				while (leftIterator.hasNext()) {
					EObject leftEObject = leftIterator.next();
//					String leftId = leftResource.getEObjectId(leftEObject);

//					//// element by element comparison
//					String leftName = null;
//					String leftClass = null;
//					if (leftEObject instanceof NamedElement) {
//						leftClass = leftEObject.getClass().getSimpleName();
//						leftName = (String) leftEObject.eGet(leftEObject.eClass().getEStructuralFeature("name"));
//					}
//					boolean stillExists = false;
//					TreeIterator<EObject> rightIterator = rightResource.getAllContents();
//					while (rightIterator.hasNext()) {
//
//						EObject rightEObject = rightIterator.next();
//						String rightName = null;
//						String rightClass = null;
//						if (rightEObject instanceof NamedElement) {
//							rightClass = rightEObject.getClass().getSimpleName();
//							rightName = (String) rightEObject.eGet(rightEObject.eClass().getEStructuralFeature("name"));
//						}
//
//						if (leftClass == rightClass && leftName == rightName) {
//							stillExists = true;
//							break;
//						}
//					}
//					if (stillExists) {
//						continue;
//					}
//					deletedObjects.add(leftEObject);

					// //// element by element comparison
					// boolean stillExists = false;
					// TreeIterator<EObject> rightIterator =
					// rightResource.getAllContents();
					// while (rightIterator.hasNext()) {
					//
					// EObject rightEObject = rightIterator.next();
					// String rightId =
					// rightResource.getEObjectId(rightEObject);
					//
					// if (leftId.equals(rightId)) {
					// stillExists = true;
					// break;
					// }
					// }
					// if (stillExists) {
					// continue;
					// }
					// deletedObjects.add(leftEObject);

					// //// using index
					// EObject rightEObject = rightResource.getEObject(leftId);
					// if (rightEObject == null) {
					// deletedObjects.add(leftEObject);
					// } else {
					// Resource rightEObjectResource = rightEObject.eResource();
					// if (rightEObjectResource == null /*
					// * && rightEObject.
					// * eCrossReferences(
					// * ).size() == 0
					// */) {
					// deletedObjects.add(leftEObject);
					// }
					// }
				}

				long end = System.nanoTime();
				long deltaTime = end - start;
				results[i] = deltaTime;

				System.out.println("Deleted Objects Count = " + deletedObjects.size());
				System.out.println(results[i] / 1000000000.0);

			}

			System.out.println("Identification Time");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(results[i] / 1000000000.0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}
	}

	@Test
	public void testEMFCompareComparisonForASE() throws IOException {
		try {
			System.out.println("Starting State-based Comparsion ...");

			leftXmiFile = new File("D:\\TEMP\\ASE\\comparison\\epsilon.008.xmi");
			rightXmiFile = new File("D:\\TEMP\\ASE\\comparison\\epsilon.388.xmi");

			long[] results = new long[ITERATION];

			Resource leftResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
			System.out.println("Loading " + leftResource.getURI().lastSegment() + "...");
			leftResource.load(null);

			Resource rightResource = (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
			System.out.println("Loading " + rightResource.getURI().lastSegment() + "...");
			rightResource.load(null);

			int objectCount = 0;

			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + (i + 1) + " ...");

				long start = System.nanoTime();

				IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
				BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(),
						Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
				postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);
				Builder builder = EMFCompare.builder();
				builder.setPostProcessorRegistry(postProcessorRegistry);
				EMFCompare comparator = builder.build();

				IComparisonScope2 scope = new DefaultComparisonScope(rightResource, leftResource, null);
//				IComparisonScope2 scope = new DefaultComparisonScope(leftResource, rightResource, null);
				scope.getAllInvolvedResourceURIs().add(rightResource.getURI());
				scope.getAllInvolvedResourceURIs().add(leftResource.getURI());

				Comparison comparison = comparator.compare(scope);
				EList<Diff> diffs = comparison.getDifferences();

				Set<EObject> modifiedObjects = new HashSet<>();

				for (Diff diff : diffs) {
					if (diff.getKind() == DifferenceKind.DELETE) {
						if (diff instanceof ReferenceChange) {
							modifiedObjects.add(((ReferenceChange) diff).getValue());
						} else if (diff.getMatch().getRight() != null) {
							modifiedObjects.add(diff.getMatch().getRight());
						} else if (diff.getMatch().getLeft() != null) {
							modifiedObjects.add(diff.getMatch().getLeft());
						} else {
							System.out.println(diff.getClass().getSimpleName());
						}
					}
				}
				
				long end = System.nanoTime();
				long deltaTime = end - start;
				results[i] = deltaTime;

				System.out.println("Diff count = " + diffs.size());
				objectCount = modifiedObjects.size();
				System.out.println("Object count = " + modifiedObjects.size());
				System.out.println(objectCount + ", " + results[i] / 1000000000.0);

			}

			System.out.println("Value,Group");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(results[i] / 1000000000.0 + ",State-based");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
	}

	@Test
	public void testCBPComparisonForASE() throws IOException {
		try {
			System.out.println("Starting Change-based Comparsion ...");
			// leftXmiFile = new
			// File("D:\\TEMP\\ASE\\comparison\\epsilon.008.with-id.xmi");
			leftCbpFile = new File("D:\\TEMP\\ASE\\comparison\\epsilon.008.cbpxml");
			// rightXmiFile = new
			// File("D:\\TEMP\\ASE\\comparison\\epsilon.044.with-id.xmi");
			rightCbpFile = new File("D:\\TEMP\\ASE\\comparison\\epsilon.388.cbpxml");

			long[] results = new long[ITERATION];

			Resource leftResource = (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
			System.out.println("Loading " + leftResource.getURI().lastSegment() + "...");
			leftResource.load(null);

			Resource rightResource = (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
			System.out.println("Loading " + rightResource.getURI().lastSegment() + "...");
			rightResource.load(null);

			int objectCount = 0;

			for (int i = 0; i < ITERATION; i++) {
				System.out.println("Iteration " + (i + 1) + " ...");

				long start = System.nanoTime();

				HybridComparison hybridComparison = new HybridComparison();
				List<ChangeEvent<?>> changeEvents = hybridComparison.Compare(leftResource, rightResource, leftCbpFile,
						rightCbpFile);

				Set<EObject> modifiedObjects = new HashSet<>();
				for (ChangeEvent<?> event : changeEvents) {
					if (event instanceof DeleteEObjectEvent) {
						EObject eObject = ((DeleteEObjectEvent) event).getValue();
						if (eObject != null) {
							modifiedObjects.add(eObject);
						}
					}
				}
				
				long end = System.nanoTime();
				long deltaTime = end - start;
				results[i] = deltaTime;

				System.out.println("Change Event count = " + changeEvents.size());
				objectCount = modifiedObjects.size();
				System.out.println("Object count = " + modifiedObjects.size());
				System.out.println(objectCount + ", " + results[i] / 1000000000.0);

			}

			System.out.println("Value,Group");
			for (int i = 0; i < ITERATION; i++) {
				if (i >= START_FROM) {
					System.out.println(results[i] / 1000000000.0 + ",Change-based");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
	}

	@Test
	public void testCBPComparison() throws IOException {
		try {
			XMIResourceImpl leftResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
			leftResource.load(null);

			XMIResourceImpl rightResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
			rightResource.load(null);

			HybridComparison hybridComparison = new HybridComparison();

			List<ChangeEvent<?>> diffs = hybridComparison.Compare(leftResource, rightResource, leftCbpFile,
					rightCbpFile);

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
	}

	@Test
	public void testCBPComparison2() throws IOException {
		try {

			XMIResourceImpl leftResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
			leftResource.load(null);

			XMIResourceImpl rightResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
			rightResource.load(null);

			FileReader leftFileReader = new FileReader(leftCbpFile);
			BufferedReader leftBufferedReader = new BufferedReader(leftFileReader);

			FileReader rightFileReader = new FileReader(rightCbpFile);
			BufferedReader rightBufferedReader = new BufferedReader(rightFileReader);

			int leftInt = -1;
			int rightInt = -1;
			boolean keepReading = true;
			while (keepReading) {
				leftBufferedReader.mark(0);
				rightBufferedReader.mark(0);
				leftInt = leftBufferedReader.read();
				rightInt = rightBufferedReader.read();
				if (leftInt != rightInt) {
					break;
				}
				System.out.print((char) leftInt);
			}
			if (leftInt > -1)
				leftBufferedReader.reset();
			if (rightInt > -1)
				rightBufferedReader.reset();

			String leftString = leftBufferedReader.readLine();
			String rightString = rightBufferedReader.readLine();

			System.out.println(leftString);
			System.out.println(rightString);

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
	}

	@Test
	public void testComparison() throws IOException {

		try {
			if (leftXmiFile.exists())
				leftXmiFile.delete();
			if (diffFile.exists())
				diffFile.delete();
			if (cbpFile.exists())
				cbpFile.delete();
			if (rightXmiFile.exists())
				rightXmiFile.delete();

			XMIResourceImpl leftResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
			// leftResource.load(null);

			XMIResourceImpl rightResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(rightXmiFile.getAbsolutePath()));
			// rightResource.load(null);

			StringOutputStream cbpOutput = new StringOutputStream();
			HybridResource hybridResource = new HybridXMIResourceImpl(rightResource, cbpOutput);
			hybridResource.startNewSession("Session-1-Left");

			Package p0 = factory.createPackage();
			p0.setName("p0");
			(new XMIResourceImpl()).setID(p0, "0");
			p0.setVisibility(VisibilityKind.PUBLIC_LITERAL);
			Package p1 = factory.createPackage();
			p1.setName("p1");
			p1.setVisibility(VisibilityKind.PUBLIC_LITERAL);
			Package p2 = factory.createPackage();
			p2.setName("p2");
			p2.setVisibility(VisibilityKind.PUBLIC_LITERAL);
			Package p3 = factory.createPackage();
			p3.setName("p3");
			p3.setVisibility(VisibilityKind.PUBLIC_LITERAL);
			Class c1 = factory.createClass();
			c1.setName("c1");
			c1.setVisibility(VisibilityKind.PUBLIC_LITERAL);

			p2.getPackagedElements().add(c1);
			p1.getPackagedElements().add(p2);
			p1.getPackagedElements().add(p3);
			hybridResource.getContents().add(p0);
			hybridResource.getContents().add(p1);

			leftResource.getContents().addAll(EcoreUtil.copyAll(hybridResource.getContents()));

			hybridResource.startNewSession("Session-2-Right");
			p3.getPackagedElements().add(c1);
			p3.setName("p4");

			System.out.println("Right");
			StringOutputStream leftOutput = new StringOutputStream();
			leftResource.save(leftOutput, null);
			leftResource.save(null);
			System.out.println(leftOutput.toString());

			System.out.println("Left");
			StringOutputStream rightOutput = new StringOutputStream();
			hybridResource.save(rightOutput, null);
			rightResource.save(null);
			System.out.println(rightOutput.toString());

			System.out.println("CBPEAttributeEvent");
			System.out.println(cbpOutput.toString());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cbpFile));
			bos.write(cbpOutput.toString().getBytes());
			bos.close();

			// left parameter is always the references
			// all actions identified are to change right parameter to become
			// left parameter
			IComparisonScope scope = new DefaultComparisonScope(rightResource, leftResource, null);
			EMFCompare comparator = EMFCompare.builder().build();

			Comparison comparison = comparator.compare(scope);
			EList<Diff> diffs = comparison.getDifferences();
			System.out.println("Diffs size = " + diffs.size());
			for (Diff diff : diffs) {
				if (diff instanceof AttributeChange) {
					System.out.println(diff.toString());
					AttributeChange change = (AttributeChange) diff;
					System.out.println(change.getValue());
					EObject eObject = diff.getMatch().getRight();
					EStructuralFeature feature = eObject.eClass().getEStructuralFeature("name");
					Object obj = eObject.eGet(feature);
				} else if (diff instanceof ReferenceChange) {
					System.out.println(diff.toString());
					ReferenceChange change = (ReferenceChange) diff;
					Object value = change.getValue();
					System.out.println(change.getValue());
					EObject left = diff.getMatch().getLeft();
					EObject right = diff.getMatch().getRight();
					if (left != null) {
						System.out.print("Left = " + left.eGet(left.eClass().getEStructuralFeature("name")));
					} else {
						System.out.print("null");
					}
					if (right != null) {
						System.out.print(", Right = " + right.eGet(right.eClass().getEStructuralFeature("name")));
					} else {
						System.out.print(", null");
					}
					if (diff.getMatch().eContainer() instanceof Match) {
						Match containerMatch = (Match) diff.getMatch().eContainer();
						for (Match match : containerMatch.getSubmatches()) {
							System.out.print(", "
									+ match.getRight().eGet(match.getRight().eClass().getEStructuralFeature("name")));
						}
					}
					System.out.println();
				}
			}

			System.out.println();

			XMIResourceImpl diffResource = (XMIResourceImpl) (new XMIResourceFactoryImpl())
					.createResource(URI.createFileURI(diffFile.getAbsolutePath()));
			diffResource.getContents().addAll(EcoreUtil.copyAll(diffs));

			StringOutputStream diffOutput = new StringOutputStream();
			diffResource.save(diffOutput, null);
			System.out.println(diffOutput.toString());

			IMerger.Registry registry = IMerger.RegistryImpl.createStandaloneInstance();
			IBatchMerger batchMerger = new BatchMerger(registry);
			batchMerger.copyAllRightToLeft(diffs, new BasicMonitor());

			comparison = comparator.compare(scope);
			diffs = comparison.getDifferences();
			System.out.println("Diffs size = " + diffs.size());

		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(true, false);
		}

		assertEquals(true, true);
	}

}
