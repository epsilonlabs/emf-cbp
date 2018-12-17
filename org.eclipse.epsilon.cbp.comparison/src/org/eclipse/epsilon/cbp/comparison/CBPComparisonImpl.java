package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.uml2.internal.postprocessor.OpaqueElementBodyChangePostProcessor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPCreateEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPDeleteEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPMoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPMoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRegisterEPackageEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPSetEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPSetEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPStartNewSessionEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPUnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPUnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.ICBPEObjectValuesEvent;
import org.eclipse.epsilon.cbp.comparison.event.ICBPFromPositionEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResource.IdType;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.eol.parse.OldAstViewer;
import org.eclipse.epsilon.eol.parse.Eol_EolParserRules.featureCall_return;

public class CBPComparisonImpl implements ICBPComparison {

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private EPackage ePackage = null;
    private Resource leftResource = null;
    private HybridResource leftHybridResource = null;
    private Resource rightResource = null;
    private HybridResource rightHybridResource = null;

    private List<CBPDiff> diffs = null;
    private List<CBPChangeEvent<?>> leftEvents = null;
    private List<CBPChangeEvent<?>> rightEvents = null;
    private Map<String, CBPMatchObject> objects = null;

    private File objectTreeFile = new File("D:\\TEMP\\COMPARISON2\\test\\object tree.txt");
    private File diffEMFCompareFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.txt");

    private List<ICBPObjectTreePostProcessor> objectTreePostProcessors = new ArrayList<>();

    private long objectTreeConstructionTime = 0;
    private long diffTime = 0;
    private long comparisonTime = 0;
    private int diffCount = 0;
    private long loadTime = 0;

    public File getObjectTreeFile() {
	return objectTreeFile;
    }

    public void setObjectTreeFile(File objectTreeFile) {
	this.objectTreeFile = objectTreeFile;
    }

    public File getDiffEMFCompareFile() {
	return diffEMFCompareFile;
    }

    public void setDiffEMFCompareFile(File diffEMFCompareFile) {
	this.diffEMFCompareFile = diffEMFCompareFile;
    }

    public long getObjectTreeConstructionTime() {
	return objectTreeConstructionTime;
    }

    public long getLoadTime() {
	return loadTime;
    }

    public long getDiffTime() {
	return diffTime;
    }

    public long getComparisonTime() {
	return comparisonTime;
    }

    public int getDiffCount() {
	return diffCount;
    }

    public List<CBPDiff> compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	return this.compare(leftFile, rightFile, null);
    }

    public List<CBPDiff> compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	DecimalFormat df = new DecimalFormat("###.###");
	long start = 0;
	long end = 0;
	long skip = 0;
	if (originFile != null) {
	    skip = originFile.length();
	}
	ePackage = this.getEPackageFromFiles(leftFile, rightFile);
	diffs = new ArrayList<>();
	objects = new HashMap<>();

	System.out.print("Convert CBP String Lines to Events");
	start = System.nanoTime();
	this.readFiles(leftFile, rightFile, skip);
	end = System.nanoTime();
	System.out.println(" = " + df.format(((end - start)) / 1000000.0) + " ms");
	loadTime = end - start;

	// System.out.print("Loading Left/Local Model");
	// start = System.nanoTime();
	//
	// Map<Object, Object> options = new HashMap<>();
	// options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	// ResourceSet resourceSet = new ResourceSetImpl();
	// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi",
	// new XMIResourceFactoryImpl());
	//
	// File leftResourceFile = new
	// File(leftFile.getAbsolutePath().replaceAll("cbpxml", "xmi"));
	// leftResource =
	// resourceSet.createResource(URI.createFileURI(leftResourceFile.getAbsolutePath()));
	// leftHybridResource = new HybridXMIResourceImpl(leftResource, new
	// FileOutputStream(leftFile, true));
	// leftHybridResource.setIdType(IdType.UUID);
	// leftHybridResource.load(options);
	//
	// File rightResourceFile = new
	// File(rightFile.getAbsolutePath().replaceAll("cbpxml", "xmi"));
	// rightResource =
	// resourceSet.createResource(URI.createFileURI(rightResourceFile.getAbsolutePath()));
	// rightHybridResource = new HybridXMIResourceImpl(rightResource, new
	// FileOutputStream(rightFile, true));
	// rightHybridResource.setIdType(IdType.UUID);
	// rightHybridResource.load(options);
	//
	// end = System.nanoTime();
	// System.out.println(" = " + df.format(((end - start) / 1000000.0)) + "
	// ms");

	System.out.print("Construct Object Tree ");
	start = System.nanoTime();
	System.out.print("LEFT ");
	this.contructObjectTree(leftEvents, CBPSide.LEFT);
	System.out.print("RIGHT ");
	this.contructObjectTree(rightEvents, CBPSide.RIGHT);
	end = System.nanoTime();
	System.out.println("= " + df.format(((end - start) / 1000000.0)) + " ms");
	objectTreeConstructionTime = end - start;

	// System.out.print("Post-process Object Tree");
	// start = System.nanoTime();
	// for (ICBPObjectTreePostProcessor postProcessor :
	// objectTreePostProcessors) {
	// postProcessor.process(objects);
	// }
	// end = System.nanoTime();
	// System.out.println(" = " + df.format(((end - start) / 1000000.0)) + "
	// ms");
	// objectTreeConstructionTime = objectTreeConstructionTime + (end -
	// start);

	// System.out.println("\nOBJECT TREE:");
	// System.out.println("Object Tree Size = " + objects.size());
	printObjectTree();

	System.out.print("Determine Differences");
	start = System.nanoTime();
	this.computeDifferences(CBPSide.LEFT);
	end = System.nanoTime();
	System.out.println(" = " + df.format(((end - start) / 1000000.0)) + " ms");
	diffTime = end - start;

	comparisonTime = objectTreeConstructionTime + diffTime;

	System.out.println("Comparison Time = " + df.format(((comparisonTime) / 1000000.0)) + " ms");

	//
	// System.out.println("\nDIFFERENCES:");
	// printDifferences();

	// System.out.println("\nEXPORT FOR COMPARISON WITH EMF COMPARE:");
	// this.exportForComparisonWithEMFCompare();
	this.exportForComparisonWithEMFCompare(true);
	//

	// closing
	// leftResource.unload();

	return diffs;
    }

    private EPackage getEPackageFromFiles(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	BufferedReader reader = new BufferedReader(new FileReader(leftFile));
	String line = null;
	String eventString = null;
	// try to read ePackage from left file
	while ((line = reader.readLine()) != null) {
	    if (line.contains("epackage=")) {
		eventString = line;
		reader.close();
		break;
	    }
	}

	// if line is still null then try from right file
	if (eventString == null) {
	    reader = new BufferedReader(new FileReader(rightFile));
	    while ((line = reader.readLine()) != null) {
		if (line.contains("epackage=")) {
		    eventString = line;
		    reader.close();
		    break;
		}
	    }
	}
	String nsUri = null;
	XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(eventString.getBytes()));
	while (xmlReader.hasNext()) {
	    XMLEvent xmlEvent = xmlReader.nextEvent();
	    if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
		StartElement e = xmlEvent.asStartElement();
		String name = e.getName().getLocalPart();
		if (name.equals("register") || name.equals("create")) {
		    nsUri = e.getAttributeByName(new QName("epackage")).getValue();
		    xmlReader.close();
		    break;
		}
	    }
	}

	EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsUri);
	return ePackage;
    }

    /**
     * 
     */
    protected void computeDifferences(CBPSide referenceSide) {

//	EClass x = (EClass) ePackage.getEClassifier("Property");
//	EReference b = (EReference) x.getEStructuralFeature("owningAssociation");
//	EReference c = (EReference) x.getEStructuralFeature("association");

	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject object = objectEntry.getValue();

	    if (object.getLeftIsCreated() && object.getLeftIsDeleted()) {
		continue;
	    } else if (object.getLeftIsDeleted() && object.getRightIsDeleted()) {
		continue;
	    }
	    // else if (object.getRightIsCreated() &&
	    // object.getRightIsDeleted()) {
	    // continue;
	    // }

	    // if (object.getId().equals("O-48461")) {
	    // System.out.print("");
	    // }

	    // iterate through all features
	    for (Entry<String, CBPMatchFeature> featureEntry : object.getFeatures().entrySet()) {
		CBPMatchFeature feature = featureEntry.getValue();

		Map<Integer, Object> leftValues = feature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = feature.getValues(CBPSide.RIGHT);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);

		    if (leftValue instanceof CBPMatchObject) {
			// System.out.println(object.getId() + "." +
			// feature.getName() + "." + leftValue.toString() +
			// ".");
			if (((CBPMatchObject) leftValue).getId().equals("O-28309")) {
			    System.out.print("");
			}
		    }

		    if (rightValue instanceof CBPMatchObject) {
			// System.out.println(object.getId() + "." +
			// feature.getName() + "." + "." +
			// rightValue.toString());
			if (((CBPMatchObject) rightValue).getId().equals("O-28309")) {
			    System.out.print("");
			}
		    }

		    if (leftValue == null && rightValue == null) {
			continue;
		    } else if (leftValue != null && rightValue != null && leftValue.equals(rightValue)) {
			if (leftValue instanceof CBPMatchObject) {
			    CBPMatchObject leftObjectValue = (CBPMatchObject) leftValue;
			    CBPMatchObject rightObjectValue = (CBPMatchObject) rightValue;
			    if (leftObjectValue.getLeftPosition() == leftObjectValue.getRightPosition() && (leftObjectValue.isLeftIsMoved() || leftObjectValue.isRightIsMoved())) {
				if (feature.isContainment()) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue.getRightPosition(), rightObjectValue.getRightContainingFeature(),
					    rightObjectValue.getRightContainer(), rightObjectValue, CBPDifferenceKind.MOVE, referenceSide);
				    diffs.add(diff);
				    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.MOVE, pos, pos, rightValue), CBPSide.RIGHT);
				} else {
				    // CBPDiff diff = new CBPDiff(object,
				    // feature, pos, rightObjectValue,
				    // CBPDifferenceKind.MOVE, referenceSide);
				    // diffs.add(diff);
				    // feature.addAdjustPositionEvent(
				    // new
				    // CBPDiffPositionEvent(CBPPositionEventType.MOVE,
				    // pos, pos, rightValue), CBPSide.RIGHT);
				}
				continue;

			    }
			}
			continue;
		    }

		    // CONTAINMENT REFERENCE
		    if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.isContainment()) {

			// if (object.getLeftIsCreated() ||
			// object.getLeftIsDeleted() ||
			// object.getRightIsCreated() ||
			// object.getRightIsDeleted()) {
			// continue;
			// } else

			// RIGHT
			if (rightValue instanceof CBPMatchObject) {
			    CBPMatchObject rightObjectValue = (CBPMatchObject) rightValue;
			    if (!rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (!rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (!rightObjectValue.getLeftIsCreated() && rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (!rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				if (rightObjectValue.getLeftContainer() == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, rightValue), CBPSide.RIGHT);
				} else {
				    if (!rightObjectValue.getLeftContainer().equals(rightObjectValue.getRightContainer())
					    || !rightObjectValue.getLeftContainingFeature().equals(rightObjectValue.getRightContainingFeature())) {
					// intentionally blank
					// this will be processed at its left
					// value
				    } else {
					if (pos >= getHighestPosition(rightObjectValue)) {
					    int rightPos = rightObjectValue.getRightPosition();
					    adjustRightPosition(rightObjectValue, feature);
					    if (rightObjectValue.getLeftContainer().equals(rightObjectValue.getRightContainer())
						    && rightObjectValue.getLeftContainingFeature().equals(rightObjectValue.getRightContainingFeature())
						    && rightObjectValue.getLeftPosition() != rightObjectValue.getRightPosition()) {

						CBPDiff diff = new CBPDiff(object, feature, rightObjectValue.getLeftPosition(), rightPos, rightObjectValue.getRightContainingFeature(),
							rightObjectValue.getRightContainer(), rightObjectValue, CBPDifferenceKind.MOVE, referenceSide);
						diffs.add(diff);

						feature.addAdjustPositionEvent(
							new CBPDiffPositionEvent(CBPPositionEventType.MOVE, rightObjectValue.getLeftPosition(), rightObjectValue.getRightPosition(), rightValue),
							CBPSide.RIGHT);
						// rightObjectValue.setDiffed(true);
					    }
					}
				    }
				}
			    }
			}

			// LEFT
			CBPMatchObject leftObjectValue = null;
			if (leftValue instanceof CBPMatchObject) {
			    leftObjectValue = (CBPMatchObject) leftValue;
			    if (leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, leftValue), CBPSide.RIGHT);
				// leftObjectValue.setDiffed(true);
			    } else if (!leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, leftValue), CBPSide.RIGHT);
				// leftObjectValue.setDiffed(true);
			    } else if (!leftObjectValue.getLeftIsCreated() && leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {

				if (!leftObjectValue.getLeftContainer().getLeftIsDeleted()) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);

				    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, leftValue), CBPSide.RIGHT);
				    // leftObjectValue.setDiffed(true);
				}

			    } else if (!leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && leftObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, leftValue), CBPSide.RIGHT);
				// leftObjectValue.setDiffed(true);
			    } else if (!leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {
				if (!leftObjectValue.getLeftContainer().equals(leftObjectValue.getRightContainer())
					|| !leftObjectValue.getLeftContainingFeature().equals(leftObjectValue.getRightContainingFeature())) {

				    CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue.getRightPosition(), leftObjectValue.getRightContainingFeature(),
					    leftObjectValue.getRightContainer(), leftObjectValue, CBPDifferenceKind.MOVE, referenceSide);
				    diffs.add(diff);

				    ((CBPMatchObject) leftValue).getLeftContainingFeature()
					    .addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.MOVEIN, ((CBPMatchObject) leftValue).getLeftPosition(), leftValue), CBPSide.LEFT);
				    ((CBPMatchObject) leftValue).getRightContainingFeature()
					    .addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.MOVEOUT, ((CBPMatchObject) leftValue).getRightPosition(), leftValue), CBPSide.RIGHT);

				    // feature.addAdjustPositionEvent(new
				    // CBPDiffPositionEvent(CBPPositionEventType.MOVE,
				    // pos, leftValue), CBPSide.RIGHT);

				} else {
				    if (pos >= getHighestPosition(leftObjectValue)) {
					int rightPos = leftObjectValue.getRightPosition();
					adjustRightPosition(leftObjectValue, feature);
					if (!leftObjectValue.getLeftContainer().equals(leftObjectValue.getRightContainer())
						|| !leftObjectValue.getLeftContainingFeature().equals(leftObjectValue.getRightContainingFeature())
						|| leftObjectValue.getLeftPosition() != leftObjectValue.getRightPosition()) {
					    CBPDiff diff = new CBPDiff(object, feature, leftObjectValue.getLeftPosition(), rightPos, leftObjectValue.getRightContainingFeature(),
						    leftObjectValue.getRightContainer(), leftObjectValue, CBPDifferenceKind.MOVE, referenceSide);
					    diffs.add(diff);

					    feature.addAdjustPositionEvent(
						    new CBPDiffPositionEvent(CBPPositionEventType.MOVE, leftObjectValue.getLeftPosition(), leftObjectValue.getRightPosition(), leftValue),
						    CBPSide.RIGHT);
					    // leftObjectValue.setDiffed(true);

					}
				    }
				}
			    }
			}
		    }

		    // NON-CONTAINMENT REFERENCE
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && !feature.isContainment()) {

			// NON-CONTAINMENT REFERENCE MULTIPLE VALUES
			if (feature.isMany()) {
			    if (object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    continue;
				}
			    }
			    if (!object.getLeftIsCreated() && object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    continue;
				}
			    }

			    else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    continue;
				}

				// -----
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    continue;
				}
			    }
			    // ----
			    else {
				continue;
			    }
			}
			// NON-CONTAINMENT REFERENCE SINGLE VALUE
			else {
			    if (object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				} else if (rightValue != null && !rightValue.equals(leftValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				}
			    } else if (!object.getLeftIsCreated() && object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (feature.getOppositeFeatureName() == null) {
				    if (leftValue != null && !leftValue.equals(rightValue)) {
					CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
					diffs.add(diff);
					// ((CBPMatchObject)
					// leftValue).addDiff(diff);
				    } else if (rightValue != null && !rightValue.equals(leftValue)) {
					CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.CHANGE, referenceSide);
					diffs.add(diff);
					// ((CBPMatchObject)
					// rightValue).addDiff(diff);
				    }
				}
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				} else if (rightValue != null && !rightValue.equals(leftValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				}
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {

				Object value = leftValue;
				if (value == null)
				    value = rightValue;

				CBPDiff diff = new CBPDiff(object, feature, pos, value, CBPDifferenceKind.CHANGE, referenceSide);
				diffs.add(diff);
				// ((CBPMatchObject) value).addDiff(diff);
			    } else {
				continue;
			    }
			}

		    }
		    // ATTRIBUTE
		    else if (feature.getFeatureType() == CBPFeatureType.ATTRIBUTE) {

			// ATTRIBUTE MULTIPLE VALUES
			if (feature.isMany()) {
			    if (object.getLeftIsCreated() || object.getLeftIsDeleted() || object.getRightIsCreated() || object.getRightIsDeleted()) {
				continue;
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);

				    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);

				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);

				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);

				    continue;
				}
			    }
			}
			// ATTRIBUTE SINGLE VALUE
			else {
			    if (object.getLeftIsCreated() || object.getLeftIsDeleted() || object.getRightIsCreated() || object.getRightIsDeleted()) {
				continue;
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {

				Object value = leftValue;
				if (value == null)
				    value = rightValue;

				CBPDiff diff = new CBPDiff(object, feature, pos, value, CBPDifferenceKind.CHANGE, referenceSide);
				diffs.add(diff);

			    }
			}
		    }

		}
	    }
	}
    }

    private void contructObjectTree(List<CBPChangeEvent<?>> events, CBPSide side) {
	CBPMatchObject resource = objects.get(CBPMatchResource.RESOURCE_STRING);
	if (resource == null) {
	    resource = new CBPMatchResource(CBPMatchResource.RESOURCE_STRING);
	    objects.put(CBPMatchResource.RESOURCE_STRING, resource);
	}

	// get resource feature
	CBPMatchFeature resourceFeature = ((CBPMatchResource) resource).getResourceFeature();

	CBPChangeEvent<?> previousEvent = null;

	for (CBPChangeEvent<?> event : events) {

	    // System.out.println(event.toString());
	    // {
	    // CBPMatchObject obj = objects.get("O-28309");
	    // if (obj != null) {
	    //// if (obj.getRightPosition() == 0) {
	    // System.out.println("X = " + obj.getRightPosition() + " Side = " +
	    // side);
	    // // }
	    // }

	    // CBPMatchObject obj = objects.get("O-43655");
	    // if (obj != null) {
	    // CBPMatchFeature fea = obj.getFeatures().get("packagedElement");
	    // if (fea != null) {
	    // Object val = fea.getRightValues().get(14);
	    // if (val != null && ((CBPMatchObject)
	    // val).getId().equals("O-43691")) {
	    // System.out.println(((CBPMatchObject) val).getId());
	    // }
	    // }
	    // }

	    // get target id
	    String targetId = null;
	    String targetClassName = null;

	    if (event instanceof CBPEStructuralFeatureEvent<?>) {
		targetId = ((CBPEStructuralFeatureEvent<?>) event).getTarget();
		targetClassName = ((CBPEStructuralFeatureEvent<?>) event).getEClass();
	    } else if (!(event instanceof CBPStartNewSessionEvent)) {
		if (event instanceof CBPCreateEObjectEvent) {
		    targetId = ((CBPCreateEObjectEvent) event).getId();
		    targetClassName = ((CBPCreateEObjectEvent) event).getEClass();
		} else if (event instanceof CBPDeleteEObjectEvent) {
		    targetId = ((CBPDeleteEObjectEvent) event).getId();
		    targetClassName = ((CBPDeleteEObjectEvent) event).getEClass();
		} else if (event instanceof CBPAddToResourceEvent) {
		    targetId = ((CBPEObject) event.getValue()).getId();
		    targetClassName = ((CBPEObject) event.getValue()).getClassName();
		} else if (event instanceof CBPRemoveFromResourceEvent) {
		    targetId = ((CBPEObject) event.getValue()).getId();
		    targetClassName = ((CBPEObject) event.getValue()).getClassName();
		}
	    } else if (event instanceof CBPStartNewSessionEvent) {
		continue;
	    }

	    // get target object
	    CBPMatchObject targetObject = objects.get(targetId);
	    if (targetObject == null) {
		targetObject = new CBPMatchObject(targetClassName, targetId);
		objects.put(targetId, targetObject);
	    }

	    // get value object
	    CBPMatchObject valueObject = null;
	    String valueId = null;
	    String valueClassName = null;
	    if (event instanceof CBPEReferenceEvent) {
		if (event.getValue() != null) {
		    valueId = ((CBPEObject) event.getValue()).getId();
		    valueClassName = ((CBPEObject) event.getValue()).getClassName();
		}
		if (valueId != null) {

		    if (valueId.equals("R-36")) {
			System.out.println();
		    }

		    // get or create new object
		    valueObject = objects.get(valueId);
		    if (valueObject == null) {
			valueObject = new CBPMatchObject(valueClassName, valueId);
			objects.put(valueId, valueObject);
		    }

		    // // try to get the left-side container and feature of the
		    // // added value object
		    if (!valueObject.getRightIsCreated() && !valueObject.getLeftIsDeleted() && valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(valueObject);
		    }
		    // // ---

		}
	    }

	    // get value literal
	    Object valueLiteral = null;
	    if (event instanceof CBPEAttributeEvent) {
		valueLiteral = event.getValue();
	    }

	    // get affected feature
	    CBPMatchFeature feature = null;
	    feature = createCBPFeature(event, targetObject, feature, valueObject, side);
	    
	    //--this is only for UML metamodel. Should be removed to another class to handle specific case for certain metadata.
	    if (feature != null && feature.getName().equals("owningAssociation")) {
		//no need to handle events related to this feature for UML metamodel
		continue;
	    }
		//--
	    

	    // get position
	    int position = event.getPosition();

	    
	    
	    
	    
	    // start processing events
	    if (event instanceof CBPCreateEObjectEvent) {
		targetObject.setCreated(true, side);
	    } else

	    if (event instanceof CBPDeleteEObjectEvent) {
		targetObject.setDeleted(true, side);
		if (previousEvent instanceof CBPUnsetEReferenceEvent) {
		    CBPUnsetEReferenceEvent unsetEvent = (CBPUnsetEReferenceEvent) previousEvent;
		    CBPMatchObject previousTargetObject = objects.get(unsetEvent.getTarget());
		    CBPMatchFeature previousFeature = previousTargetObject.getFeatures().get(unsetEvent.getEReference());
		    CBPMatchObject previousValueObject = objects.get(((CBPEObject) unsetEvent.getOldValue()).getId());
		    targetObject.setContainer(previousTargetObject, side);
		    targetObject.setContainingFeature(previousFeature, side);
		    if (previousValueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(previousValueObject);
		    }

		} else if (previousEvent instanceof CBPRemoveFromEReferenceEvent) {

		    CBPRemoveFromEReferenceEvent removeEvent = (CBPRemoveFromEReferenceEvent) previousEvent;
		    CBPMatchObject previousTargetObject = objects.get(removeEvent.getTarget());
		    CBPMatchFeature previousFeature = previousTargetObject.getFeatures().get(removeEvent.getEReference());
		    CBPMatchObject previousValueObject = objects.get(((CBPEObject) removeEvent.getValue()).getId());
		    // targetObject.setContainer(previousTargetObject, side);
		    // targetObject.setContainingFeature(previousFeature, side);

		    targetObject.setContainer(null, side);
		    targetObject.setContainingFeature(null, side);
		    targetObject.setPosition(-1, side, previousFeature.isContainment());

		    if (previousValueObject.getLeftIsDeleted() == false && previousValueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			// if (side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(previousValueObject);
		    }

		    // handleEOppositeReference(previousTargetObject,
		    // previousFeature, previousValueObject, side);
		}
	    } else
	    // -----------------
	    if (event instanceof CBPRemoveFromResourceEvent) {
//		if (valueId.equals("O-12160")) {
//		    System.out.println();
//		}
		if (targetObject.getContainer(side) == null) {
		    targetObject.setContainer(resource, side);
		}
		if (targetObject.getContainingFeature(side) == null) {
		    targetObject.setContainingFeature(resourceFeature, side);
		}
		if (targetObject.getPosition(side) == -1) {
		    targetObject.setPosition(position, side, resourceFeature.isContainment());
		}
		resourceFeature.removeValue(targetObject, position, side);
		resourceFeature.setIsSet(position, side);

		if (targetObject.getLeftIsDeleted() == false && targetObject.getRightContainer() == null && side == CBPSide.LEFT) {
		    createValueObjectOnTheOppositeSide(targetObject, CBPSide.LEFT);
		} else if (!targetObject.getLeftIsDeleted() && targetObject.getRightIsDeleted() == false && targetObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
		    createValueObjectOnTheOppositeSide(targetObject, CBPSide.RIGHT);
		}
	    } else
	    // ----------------------
	    if (event instanceof CBPAddToResourceEvent) {
		if (targetId.equals("L-7")) {
		    System.out.println();
		}
		targetObject.setContainer(resource, side);
		targetObject.setContainingFeature(resourceFeature, side);
		targetObject.setPosition(position, side, resourceFeature.isContainment());
		resourceFeature.addValue(targetObject, position, side);
		resourceFeature.setIsSet(position, side);
	    } else

	    // ------------------------
	    if (event instanceof CBPAddToEReferenceEvent) {
		if (targetId.equals("O-37131") && valueId.equals("O-28706")) {
		    System.out.println();
		}

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side, feature.isContainment());
		    if (valueObject.isDeleted(side)) {
			valueObject.setDeleted(false, side);
		    }
		}
		feature.addValue(valueObject, position, side);
		feature.setIsSet(position, side);
	    } else

	    // ------------------
	    if (event instanceof CBPRemoveFromEReferenceEvent) {
		// if (valueId.equals("O-359")) {
		if (targetId.equals("O-37131") && feature.getName().equals("memberEnd")) {
		    System.out.println();
		}

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side, feature.isContainment());

		    if (valueObject.getLeftIsDeleted() == false && valueObject.getRightIsDeleted() == false && valueObject.getRightContainer() == null && side == CBPSide.LEFT) {
			createValueObjectOnTheOppositeSide(valueObject, CBPSide.LEFT);
		    } else if (!valueObject.getLeftIsDeleted() && valueObject.getRightIsDeleted() == false && valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(valueObject, CBPSide.RIGHT);
		    }
		} else {
		    if (!targetObject.getRightIsCreated() && !valueObject.getRightIsCreated() && side == CBPSide.RIGHT) {
			if (!feature.getIsSet(position, CBPSide.LEFT)) {
			    if (feature.isMany()) {
				feature.addValue(valueObject, position, CBPSide.LEFT);
				feature.setIsSet(position, CBPSide.LEFT);
			    } else {
				feature.updateValue(valueObject, position, CBPSide.LEFT);
				feature.setIsSet(position, CBPSide.LEFT);
			    }
			}
		    } else if (!targetObject.getLeftIsCreated() && !valueObject.getLeftIsCreated() && side == CBPSide.LEFT) {
			if (!feature.getIsSet(position, CBPSide.RIGHT)) {
			    if (feature.isMany()) {
				feature.addValue(valueObject, position, CBPSide.RIGHT);
				feature.setIsSet(position, CBPSide.RIGHT);
			    } else {
				feature.updateValue(valueObject, position, CBPSide.RIGHT);
				feature.setIsSet(position, CBPSide.RIGHT);
			    }
			}
		    }
		}

		feature.removeValue(valueObject, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -----------------
	    if (event instanceof CBPMoveWithinEReferenceEvent) {

		if (valueId.equals("O-52341")) {
		    System.out.println();
		}

		int fromPosition = ((CBPMoveWithinEReferenceEvent) event).getFromPosition();
		valueObject.setMoved(true, side);
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		if (valueObject.getOldPosition(side) == -1)
		    valueObject.setOldPosition(fromPosition, side);
		valueObject.setPosition(position, side, feature.isContainment());

		if (valueObject.getLeftIsDeleted() == false && valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
		    createValueObjectOnTheOppositeSide(valueObject);
		} else if (valueObject.getRightIsDeleted() == false && valueObject.getRightContainer() == null && side == CBPSide.LEFT) {
		    createValueObjectOnTheOppositeSide(valueObject, CBPSide.LEFT);
		}

		feature.moveValue(valueObject, fromPosition, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -----------------
	    if (event instanceof CBPAddToEAttributeEvent) {
		feature.addValue(valueLiteral, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -------------
	    if (event instanceof CBPRemoveFromEAttributeEvent) {
		feature.removeValue(valueLiteral, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -----------------
	    if (event instanceof CBPMoveWithinEAttributeEvent) {
		int fromPosition = ((CBPMoveWithinEAttributeEvent) event).getFromPosition();
		feature.moveValue(valueLiteral, fromPosition, position, side);
		feature.setIsSet(position, side);
	    } else
	    // ------------
	    if (event instanceof CBPSetEReferenceEvent) {

		if (valueId.equals("O-39190")) {
		    System.out.println();
		}

		feature.setOldValue(event.getOldValue(), side);
		feature.setValue(valueObject, side);
		feature.setIsSet(side);

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side, feature.isContainment());
		}

		// handle old value
		if (event.getOldValue() != null) {

		    String oldId = null;
		    String oldClassName = null;
		    if ((CBPEObject) event.getOldValue() != null) {
			oldId = ((CBPEObject) event.getOldValue()).getId();
			oldClassName = ((CBPEObject) event.getOldValue()).getClassName();
		    }
		    CBPMatchObject oldValue = objects.get(oldId);
		    if (oldValue == null) {
			oldValue = new CBPMatchObject(oldClassName, oldId);
			objects.put(oldId, oldValue);
		    }

		    if (feature.isContainment()) {
			oldValue.setOldContainer(targetObject, side);
			oldValue.setOldContainingFeature(feature, side);
			oldValue.setOldPosition(0, side);

			if (!feature.getIsSet(CBPSide.RIGHT) && oldValue.getRightContainer() == null && side == CBPSide.LEFT) {
			    createValueObjectOnTheOppositeSide(oldValue, CBPSide.LEFT);
			}
			if (!feature.getIsSet(CBPSide.LEFT) && oldValue.getLeftContainer() == null && side == CBPSide.RIGHT) {
			    createValueObjectOnTheOppositeSide(oldValue, CBPSide.RIGHT);
			}

		    } else {
			if (!targetObject.getRightIsCreated() && side == CBPSide.RIGHT) {
			    if (!feature.getIsSet(CBPSide.LEFT)) {
				feature.setValue(oldValue, CBPSide.LEFT);
				feature.setIsSet(CBPSide.LEFT);
			    }
			} else if (!targetObject.getLeftIsCreated() && side == CBPSide.LEFT) {
			    if (!feature.getIsSet(CBPSide.RIGHT)) {
				feature.setValue(oldValue, CBPSide.RIGHT);
				feature.setIsSet(CBPSide.RIGHT);
			    }
			}
		    }
		}

	    } else
	    // ---------------
	    if (event instanceof CBPUnsetEReferenceEvent) {

		if (event.getOldValue() != null && event.getOldValue().toString().equals("O-28309")) {
		    System.out.println();
		}

		String oldId = null;
		String oldClassName = null;
		if ((CBPEObject) event.getOldValue() != null) {
		    oldId = ((CBPEObject) event.getOldValue()).getId();
		    oldClassName = ((CBPEObject) event.getOldValue()).getClassName();
		}
		CBPMatchObject oldValue = objects.get(oldId);
		if (oldValue == null) {
		    oldValue = new CBPMatchObject(oldClassName, oldId);
		    objects.put(oldId, oldValue);
		}
		feature.setOldValue(oldValue, side);
		feature.unsetValue(valueObject, side);
		feature.setIsSet(true, side);

		// handle setting value to other side
		if (feature.isContainment()) {
		    oldValue.setOldContainer(targetObject, side);
		    oldValue.setOldContainingFeature(feature, side);
		    oldValue.setOldPosition(0, side);

		    if (!oldValue.getLeftIsCreated() && oldValue.getRightContainer() == null && side == CBPSide.LEFT) {
			createValueObjectOnTheOppositeSide(oldValue, CBPSide.LEFT);
		    }
		    if (!oldValue.getRightIsCreated() && oldValue.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(oldValue, CBPSide.RIGHT);
		    }

		} else {
		    if (!targetObject.getRightIsCreated() && !oldValue.getRightIsCreated() && side == CBPSide.RIGHT) {
			if (!feature.getIsSet(CBPSide.LEFT)) {
			    feature.setValue(oldValue, CBPSide.LEFT);
			    feature.setIsSet(CBPSide.LEFT);
			}
		    } else if (!targetObject.getLeftIsCreated() && !oldValue.getLeftIsCreated() && side == CBPSide.LEFT) {
			if (!feature.getIsSet(CBPSide.RIGHT)) {
			    feature.setValue(oldValue, CBPSide.RIGHT);
			    feature.setIsSet(CBPSide.RIGHT);
			}
		    }
		}
	    } else

	    // -----------
	    if (event instanceof CBPSetEAttributeEvent) {
		feature.setOldValue(event.getOldValue(), side);
		feature.setValue(valueLiteral, side);
		feature.setIsSet(side);
	    } else
	    // -------------
	    if (event instanceof CBPUnsetEAttributeEvent) {
		feature.setOldValue(event.getOldValue(), side);
		feature.unsetValue(valueLiteral, side);
		feature.setIsSet(side);
	    }

	    previousEvent = event;
	}

    }

    private void handleEOppositeReference(CBPMatchObject targetObject, CBPMatchFeature feature, CBPMatchObject valueObject, CBPSide side) {
	if (feature.getOppositeFeatureName() != null) {

	    String featureName = feature.getOppositeFeatureName();

	    // if (feature.getName().equals("type")) {
	    // System.out.print("");
	    // }
	    //
	    // if (featureName.equals("type")) {
	    // System.out.print("");
	    // }

	    boolean isContainment = false;
	    CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
	    boolean isMany = false;
	    String oppositeFeatureName = null;

	    String eClassName = valueObject.getClassName();
	    EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
	    EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);

	    if (eFeature.isMany()) {
		isMany = true;
	    }
	    if (eFeature instanceof EReference) {
		featureType = CBPFeatureType.REFERENCE;
		if (((EReference) eFeature).isContainment()) {
		    isContainment = true;
		}
		if (((EReference) eFeature).getEOpposite() != null) {
		    oppositeFeatureName = ((EReference) eFeature).getEOpposite().getName();
		}
	    }

	    CBPMatchFeature oppositeFeature = valueObject.getFeatures().get(featureName);
	    if (oppositeFeature == null) {
		oppositeFeature = new CBPMatchFeature(valueObject, featureName, featureType, isContainment, isMany);
		oppositeFeature.setOppositeFeatureName(oppositeFeatureName);
		valueObject.getFeatures().put(featureName, oppositeFeature);
	    }

	    if (oppositeFeature.isMany()) {
		Map<Integer, Object> values = oppositeFeature.getValues(side);
		for (Entry<Integer, Object> entry : values.entrySet()) {
		    CBPMatchObject value = (CBPMatchObject) entry.getValue();
		    if (value != null) {
			int position = entry.getKey();
			oppositeFeature.removeValue(value, position, side);
			break;
		    }
		}
	    } else {
		Object value = oppositeFeature.getValue(side);
		oppositeFeature.unsetValue(value, side);
	    }

	}
    }

    /**
     * @param event
     * @param targetObject
     * @param feature
     * @return
     */
    private CBPMatchFeature createCBPFeature(CBPChangeEvent<?> event, CBPMatchObject targetObject, CBPMatchFeature feature, CBPMatchObject valueObject, CBPSide side) {
	String featureName = null;
	if (event instanceof CBPEStructuralFeatureEvent<?>) {

	    featureName = ((CBPEStructuralFeatureEvent<?>) event).getEStructuralFeature();
	    String eClassName = ((CBPEStructuralFeatureEvent<?>) event).getEClass();

	    CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
	    boolean isContainment = false;
	    boolean isMany = false;
	    String oppositeFeatureName = null;

	    EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
	    EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);
	    EReference eOpposite = null;

	    if (eFeature.isMany()) {
		isMany = true;
	    }
	    if (eFeature instanceof EReference) {
		featureType = CBPFeatureType.REFERENCE;
		if (((EReference) eFeature).isContainment()) {
		    isContainment = true;
		}

		eOpposite = ((EReference) eFeature).getEOpposite();
		if (eOpposite != null) {
		    oppositeFeatureName = eOpposite.getName();
		}
	    }

	    feature = targetObject.getFeatures().get(featureName);
	    if (feature == null) {
		feature = new CBPMatchFeature(targetObject, featureName, featureType, isContainment, isMany);
		feature.setOppositeFeatureName(oppositeFeatureName);
		targetObject.getFeatures().put(featureName, feature);
	    }

	    // -----------------------------
	    // also create eOpposite feature
	    if (feature.getName().equals("memberEnd") && feature.isMany() && eOpposite != null && valueObject != null) {
		isMany = eOpposite.isMany();
		isContainment = eOpposite.isContainment();
		oppositeFeatureName = eOpposite.getName();
		featureType = CBPFeatureType.REFERENCE;

		CBPMatchFeature oppositeFeature = valueObject.getFeatures().get(oppositeFeatureName);
		if (oppositeFeature == null) {
		    oppositeFeature = new CBPMatchFeature(valueObject, oppositeFeatureName, featureType, isContainment, isMany);
		    oppositeFeature.setOppositeFeatureName(oppositeFeatureName);
		    valueObject.getFeatures().put(oppositeFeatureName, oppositeFeature);
		}

		if (isMany) {
		    oppositeFeature.addValue(targetObject, 0, side);
		} else {
		    oppositeFeature.setValue(targetObject, side);
		}
	    }
	    // ------------
	}

	return feature;

    }

    protected void createValueObjectOnTheOppositeSide(CBPMatchObject valueObject) {
	createValueObjectOnTheOppositeSide(valueObject, CBPSide.RIGHT);
    }

    protected void createValueObjectOnTheOppositeSide(CBPMatchObject valueObject, CBPSide currentSide) {

	if (!valueObject.getLeftIsCreated() && !valueObject.getRightIsCreated()) {
	    if (currentSide == CBPSide.RIGHT) {
		if (valueObject.getOldRightContainer() != null) {

		    CBPMatchObject leftContainer = valueObject.getOldRightContainer();
		    CBPMatchFeature rightFeature = valueObject.getOldRightContainingFeature();
		    CBPMatchFeature leftFeature = leftContainer.getFeatures().get(rightFeature.getName());
		    if (leftFeature == null) {
			CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
			boolean isContainment = false;
			if (rightFeature.getFeatureType() == CBPFeatureType.REFERENCE) {
			    featureType = CBPFeatureType.REFERENCE;
			    if (rightFeature.isContainment()) {
				isContainment = true;
			    }
			}
			leftFeature = new CBPMatchFeature(leftContainer, rightFeature.getName(), featureType, isContainment, rightFeature.isMany());
			leftContainer.getFeatures().put(new String(rightFeature.getName()), leftFeature);
		    }
		    int pos = valueObject.getOldRightPosition();
		    pos = leftFeature.determineOldPosition(pos, CBPSide.RIGHT);
		    valueObject.setOldPosition(pos, CBPSide.RIGHT);
		    valueObject.setOldPosition(pos, CBPSide.LEFT);
		    pos = leftFeature.updatePositionWhenCreatingObject(pos, CBPSide.LEFT);
		    if (leftFeature.isMany()) {
			if (valueObject.getLeftContainer() == null) {
			    leftFeature.updateValue(valueObject, pos, CBPSide.LEFT);
			} else {
			    leftFeature.addValue(valueObject, pos, CBPSide.LEFT, false);
			}
		    } else {
			leftFeature.updateValue(valueObject, pos, CBPSide.LEFT);
		    }

		    valueObject.setPosition(pos, CBPSide.LEFT, leftFeature.isContainment());
		    valueObject.setContainer(leftContainer, CBPSide.LEFT);
		    valueObject.setContainingFeature(leftFeature, CBPSide.LEFT);
		}
	    } else {
		if (valueObject.getOldLeftContainer() != null) {

		    CBPMatchObject rightContainer = valueObject.getOldLeftContainer();
		    CBPMatchFeature leftFeature = valueObject.getOldLeftContainingFeature();
		    CBPMatchFeature rightFeature = rightContainer.getFeatures().get(leftFeature.getName());
		    if (rightFeature == null) {
			CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
			boolean isContainment = false;
			if (leftFeature.getFeatureType() == CBPFeatureType.REFERENCE) {
			    featureType = CBPFeatureType.REFERENCE;
			    if (leftFeature.isContainment()) {
				isContainment = true;
			    }
			}
			rightFeature = new CBPMatchFeature(rightContainer, leftFeature.getName(), featureType, isContainment, leftFeature.isMany());
			rightContainer.getFeatures().put(new String(leftFeature.getName()), rightFeature);
		    }
		    int pos = valueObject.getOldLeftPosition();
		    pos = rightFeature.determineOldPosition(pos, CBPSide.LEFT);
		    valueObject.setOldPosition(pos, CBPSide.LEFT);
		    valueObject.setOldPosition(pos, CBPSide.RIGHT);
		    pos = rightFeature.updatePositionWhenCreatingObject(pos, CBPSide.RIGHT);
		    if (rightFeature.isMany()) {
			if (valueObject.getRightContainer() == null) {
			    rightFeature.updateValue(valueObject, pos, CBPSide.RIGHT);
			} else {
			    rightFeature.addValue(valueObject, pos, CBPSide.RIGHT, false);
			}
		    } else {
			rightFeature.updateValue(valueObject, pos, CBPSide.RIGHT);
		    }

		    valueObject.setPosition(pos, CBPSide.RIGHT, rightFeature.isContainment());
		    valueObject.setContainer(rightContainer, CBPSide.RIGHT);
		    valueObject.setContainingFeature(rightFeature, CBPSide.RIGHT);
		}
	    }
	}
    }

    /**
     * @param valueObject
     */
    // protected void createValueObjectOnTheLeftSide(CBPMatchObject valueObject)
    // {
    // EObject eObject = leftHybridResource.getEObject(valueObject.getId());
    // if (eObject != null) {
    // EObject eContainer = eObject.eContainer();
    // if (eContainer != null) {
    // String containerId =
    // leftHybridResource.getURIFragment(eObject.eContainer());
    //
    // CBPMatchObject leftContainer = objects.get(containerId);
    // if (leftContainer == null) {
    // leftContainer = new CBPMatchObject(containerId);
    // objects.put(containerId, leftContainer);
    // }
    //
    // EStructuralFeature eFeature = eObject.eContainingFeature();
    // CBPMatchFeature leftFeature =
    // leftContainer.getFeatures().get(eFeature.getName());
    // if (leftFeature == null) {
    // CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
    // boolean isContainment = false;
    // if (eFeature instanceof EReference) {
    // featureType = CBPFeatureType.REFERENCE;
    // if (((EReference) eFeature).isContainment()) {
    // isContainment = true;
    // }
    // }
    // leftFeature = new CBPMatchFeature(leftContainer, eFeature.getName(),
    // featureType, isContainment, eFeature.isMany());
    // leftContainer.getFeatures().put(eFeature.getName(), leftFeature);
    // }
    // int pos = -1;
    // if (eFeature.isMany()) {
    // EList<EObject> list = (EList<EObject>) eContainer.eGet(eFeature);
    // pos = list.indexOf(eObject);
    // }
    //
    // // leftFeature.addValue(valueObject, pos, CBPSide.LEFT);
    // leftFeature.updateValue(valueObject, pos, CBPSide.LEFT);
    //
    // valueObject.setPosition(pos, CBPSide.LEFT);
    // valueObject.setContainer(leftContainer, CBPSide.LEFT);
    // valueObject.setContainingFeature(leftFeature, CBPSide.LEFT);
    //
    // }
    // }
    // }

    private void readFiles(File leftFile, File rightFile, long skip) {
	try {
	    BufferedReader leftReader = new BufferedReader(new FileReader(leftFile));
	    BufferedReader rightReader = new BufferedReader(new FileReader(rightFile));
	    String leftLine;
	    String rightLine;
	    if (skip <= 0) {
		leftReader.mark(0);
		rightReader.mark(0);
		while ((leftLine = leftReader.readLine()) != null && (rightLine = rightReader.readLine()) != null) {
		    if (!leftLine.equals(rightLine)) {
			break;
		    }
		    leftReader.mark(0);
		    rightReader.mark(0);
		}
	    } else {
		leftReader.skip(skip);
		rightReader.skip(skip);
		leftReader.mark(0);
		rightReader.mark(0);
	    }

	    leftReader.reset();
	    rightReader.reset();

	    leftEvents = new ArrayList<>();
	    rightEvents = new ArrayList<>();
	    convertLinesToEvents(leftEvents, leftFile, leftReader);
	    convertLinesToEvents(rightEvents, rightFile, rightReader);

	    leftReader.close();
	    rightReader.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * @param file
     * @param bufferedReader
     * @throws IOException
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    private void convertLinesToEvents(List<CBPChangeEvent<?>> eventList, File file, BufferedReader bufferedReader) throws IOException, FactoryConfigurationError, XMLStreamException {

	StringBuilder sb = new StringBuilder();
	sb.append("<m>");
	sb.append(bufferedReader.lines().collect(Collectors.joining()));
	sb.append("</m>");

	convertLinesToEvents(eventList, file, sb.toString());
    }

    private void convertLinesToEvents(List<CBPChangeEvent<?>> eventList, File cbpFile, String lines) throws FactoryConfigurationError, XMLStreamException {
	try {
	    XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(lines.getBytes()), "ISO-8859-1");

	    CBPChangeEvent<?> event = null;
	    boolean ignore = false;

	    while (xmlReader.hasNext()) {
		XMLEvent xmlEvent = xmlReader.nextEvent();
		if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
		    StartElement e = xmlEvent.asStartElement();
		    String name = e.getName().getLocalPart();

		    if (!name.equals("value") && !name.equals("old-value") && !name.equals("m")) {
			if (ignore == false) {
			    switch (name) {
			    case "session": {
				String sessionId = e.getAttributeByName(new QName("id")).getValue();
				String time = e.getAttributeByName(new QName("time")).getValue();
				event = new CBPStartNewSessionEvent(sessionId, time);
			    }
				break;
			    case "register": {
				String packageName = e.getAttributeByName(new QName("epackage")).getValue();
				event = new CBPRegisterEPackageEvent(packageName);
			    }
				break;
			    case "create": {
				String className = e.getAttributeByName(new QName("eclass")).getValue();
				String id = e.getAttributeByName(new QName("id")).getValue();
				event = new CBPCreateEObjectEvent(className, cbpFile.getAbsolutePath(), id);
			    }
				break;
			    case "add-to-resource":
				event = new CBPAddToResourceEvent();
				break;
			    case "remove-from-resource":
				event = new CBPRemoveFromResourceEvent();
				break;
			    case "add-to-ereference":
				event = new CBPAddToEReferenceEvent();
				break;
			    case "remove-from-ereference":
				event = new CBPRemoveFromEReferenceEvent();
				break;
			    case "set-eattribute":
				event = new CBPSetEAttributeEvent();
				break;
			    case "set-ereference":
				event = new CBPSetEReferenceEvent();
				break;
			    case "unset-eattribute":
				event = new CBPUnsetEAttributeEvent();
				break;
			    case "unset-ereference":
				event = new CBPUnsetEReferenceEvent();
				break;
			    case "add-to-eattribute":
				event = new CBPAddToEAttributeEvent();
				break;
			    case "remove-from-eattribute":
				event = new CBPRemoveFromEAttributeEvent();
				break;
			    case "move-in-eattribute":
				event = new CBPMoveWithinEAttributeEvent();
				break;
			    case "move-in-ereference":
				event = new CBPMoveWithinEReferenceEvent();
				break;
			    case "delete": {
				String className = e.getAttributeByName(new QName("eclass")).getValue();
				String id = e.getAttributeByName(new QName("id")).getValue();
				event = new CBPDeleteEObjectEvent(className, cbpFile.getAbsolutePath(), id);

			    }
				break;
			    }

			    if (event instanceof CBPEStructuralFeatureEvent<?>) {
				String sEClass = e.getAttributeByName(new QName("eclass")).getValue();
				String sTarget = e.getAttributeByName(new QName("target")).getValue();
				String sName = e.getAttributeByName(new QName("name")).getValue();
				((CBPEStructuralFeatureEvent<?>) event).setEClass(sEClass);
				((CBPEStructuralFeatureEvent<?>) event).setTarget(sTarget);
				((CBPEStructuralFeatureEvent<?>) event).setEStructuralFeature(sName);
			    } else if (event instanceof CBPResourceEvent) {
				((CBPResourceEvent) event).setResource(cbpFile.getAbsolutePath());
			    }

			    if (event instanceof CBPAddToEAttributeEvent || event instanceof CBPAddToEReferenceEvent || event instanceof CBPAddToResourceEvent
				    || event instanceof CBPRemoveFromEAttributeEvent || event instanceof CBPRemoveFromEReferenceEvent || event instanceof CBPRemoveFromResourceEvent) {
				String sPosition = e.getAttributeByName(new QName("position")).getValue();
				event.setPosition(Integer.parseInt(sPosition));
			    }
			    if (event instanceof ICBPFromPositionEvent) {
				String sTo = e.getAttributeByName(new QName("to")).getValue();
				String sFrom = e.getAttributeByName(new QName("from")).getValue();
				event.setPosition(Integer.parseInt(sTo));
				((ICBPFromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
			    }
			}

		    } else if (name.equals("old-value") || name.equals("value") && !name.equals("m")) {
			if (ignore == false) {
			    if (name.equals("old-value")) {
				if (event instanceof ICBPEObjectValuesEvent) {
				    ICBPEObjectValuesEvent valuesEvent = (ICBPEObjectValuesEvent) event;
				    String id = e.getAttributeByName(new QName("eobject")).getValue();
				    String className = e.getAttributeByName(new QName("eclass")).getValue();
				    valuesEvent.getOldValues().add(new CBPEObject(className, id));
				} else if (event instanceof CBPEAttributeEvent) {
				    CBPEAttributeEvent eAttributeEvent = (CBPEAttributeEvent) event;
				    String sliteral = e.getAttributeByName(new QName("literal")).getValue();
				    eAttributeEvent.getOldValues().add(sliteral);
				}
			    } else if (name.equals("value")) {
				if (event instanceof ICBPEObjectValuesEvent) {
				    ICBPEObjectValuesEvent valuesEvent = (ICBPEObjectValuesEvent) event;
				    String id = e.getAttributeByName(new QName("eobject")).getValue();
				    String className = e.getAttributeByName(new QName("eclass")).getValue();
				    valuesEvent.getValues().add(new CBPEObject(className, id));
				} else if (event instanceof CBPEAttributeEvent) {
				    CBPEAttributeEvent eAttributeEvent = (CBPEAttributeEvent) event;
				    String sliteral = e.getAttributeByName(new QName("literal")).getValue();
				    eAttributeEvent.getValues().add(sliteral);
				}
			    }
			}
		    }
		}
		if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
		    EndElement ee = xmlEvent.asEndElement();
		    String name = ee.getName().getLocalPart();
		    if (event != null && !name.equals("old-value") && !name.equals("value") && !name.equals("m")) {
			eventList.add(event);
			// return event;
		    }
		}
	    }
	    xmlReader.close();

	} catch (XMLStreamException ex) {
	    String errorString = "Error: " + lines + " : " + ex.getMessage();
	    System.out.println(errorString);
	    ex.printStackTrace();
	    throw ex;

	}
	// return null;
    }

    @Override
    public void addObjectTreePostProcessor(ICBPObjectTreePostProcessor umlObjectTreePostProcessor) {
	objectTreePostProcessors.add(umlObjectTreePostProcessor);
    }

    /**
     * @param object
     * @param feature
     */
    protected void adjustRightPosition(CBPMatchObject object, CBPMatchFeature feature) {
	// ADJUST POSITION BECAUSE OF DIFFERENCE STRATEGY - BEGIN
	int oldRightPosition = object.getRightPosition();
	int rightPosition = feature.updatePosition(oldRightPosition, CBPSide.RIGHT);
	object.setRightPosition(rightPosition, feature.isContainment());
	// ADJUST POSITION BECAUSE OF DIFFERENCE STRATEGY - END
    }

    protected void printDifferences() {
	for (CBPDiff diff : diffs) {
	    System.out.println(diff.toString());
	}
	System.out.println("Diffs size = " + diffs.size());
    }

    protected void exportForComparisonWithEMFCompare() throws IOException {
	exportForComparisonWithEMFCompare(false);
    }

    protected void exportForComparisonWithEMFCompare(boolean saveToFile) throws IOException {
	Set<String> set = new HashSet<>();
	String str = null;
	for (CBPDiff diff : diffs) {
	    if (diff.getValue() instanceof CBPMatchObject) {
		str = diff.getObject().getId() + "." + diff.getFeature().getName() + "." + ((CBPMatchObject) diff.getValue()).getId() + "." + diff.getKind();
	    } else {
		str = diff.getObject().getId() + "." + diff.getFeature().getName() + "." + diff.getValue() + "." + diff.getKind();
	    }
	    set.add(str.trim());
	}
	List<String> list = new ArrayList<>(set);
	Collections.sort(list);

	if (saveToFile == true) {
	    FileOutputStream output = new FileOutputStream(diffEMFCompareFile);
	    for (String item : list) {
		// System.out.println(item);
		if (item.trim() == "")
		    continue;
		output.write(item.getBytes());
		output.write(System.lineSeparator().getBytes());
	    }
	    output.close();
	}
	diffCount = list.size();
	System.out.println("Change-based Diffs size = " + list.size());
    }

    /**
     * @throws FileNotFoundException
     * 
     */
    protected void printObjectTree() throws FileNotFoundException {
	// Check
	PrintWriter writer = new PrintWriter(objectTreeFile);
	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject object = objectEntry.getValue();
	    writer.println(objectEntry.getKey() + " : " + object.isCreated(CBPSide.LEFT) + ", " + object.isDeleted(CBPSide.LEFT) + " : " + object.isCreated(CBPSide.RIGHT) + ", "
		    + object.isDeleted(CBPSide.RIGHT));
	    // System.out.println(objectEntry.getKey() + " : " +
	    // object.isCreated(CBPSide.LEFT) + ", " +
	    // object.isDeleted(CBPSide.LEFT) + " : " +
	    // object.isCreated(CBPSide.RIGHT) + ", "
	    // + object.isDeleted(CBPSide.RIGHT));
	    for (Entry<String, CBPMatchFeature> featureEntry : object.getFeatures().entrySet()) {
		CBPMatchFeature feature = featureEntry.getValue();
		Map<Integer, Object> leftValues = feature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = feature.getValues(CBPSide.RIGHT);
		int leftSize = leftValues.size();
		int rightSize = rightValues.size();
		// System.out.println("+--" + feature.getName() + "; left size =
		// " + leftSize + ", right size = " + rightSize);
		writer.println("+--" + feature.getName() + ", " + feature.isContainment() + "; left size = " + leftSize + ", right size = " + rightSize);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = (valueEntry.getValue() instanceof CBPMatchObject) ? ((CBPMatchObject) valueEntry.getValue()).getId() : valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);
		    rightValue = (rightValue instanceof CBPMatchObject) ? ((CBPMatchObject) rightValue).getId() : rightValue;
		    // System.out.println("+--+-- " + pos + "; left value = " +
		    // leftValue + ", right value = " + rightValue);
		    writer.println("+--+-- " + pos + "; left value = " + leftValue + ", right value = " + rightValue);
		}
	    }
	}
	writer.close();
    }

    private int getHighestPosition(CBPMatchObject object) {
	if (object.getLeftPosition() > object.getRightPosition()) {
	    return object.getLeftPosition();
	} else {
	    return object.getRightPosition();
	}
    }

}
