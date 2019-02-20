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
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
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
import org.eclipse.epsilon.cbp.conflict.CBPConflict;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResource.IdType;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.eol.parse.OldAstViewer;
import org.eclipse.epsilon.eol.parse.Eol_EolParserRules.featureCall_return;

public class CBPComparisonImpl implements ICBPComparison {

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private EPackage ePackage = null;

    private List<CBPDiff> diffs = null;
    private List<CBPConflict> conflicts = null;
    private List<CBPChangeEvent<?>> leftEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> rightEvents = new ArrayList<>();
    private Map<String, CBPMatchObject> objects = null;

    private File objectTreeFile = new File("D:\\TEMP\\COMPARISON2\\test\\object tree.txt");
    private File diffEMFCompareFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.txt");

    private List<ICBPObjectTreePostProcessor> objectTreePostProcessors = new ArrayList<>();

    private long objectTreeConstructionTime = 0;
    private long diffTime = 0;
    private long comparisonTime = 0;
    private long objectTreeConstructionMemory = 0;
    private long diffMemory = 0;
    private long comparisonMemory = 0;
    private long conflictMemory = 0;
    private long conflictTime = 0;

    private int conflictCount = 0;
    private int diffCount = 0;
    private long loadTime = 0;
    private long loadMemory = 0;

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

    public List<CBPConflict> getConflicts() {
	return conflicts;
    }

    public List<CBPDiff> compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	return this.compare(leftFile, rightFile, null);
    }

    public List<CBPDiff> compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	DecimalFormat df = new DecimalFormat("###.###");
	long startInterval = 0;
	long endInterval = 0;
	long startMemory = 0;
	long endMemory = 0;
	long skip = 0;
	if (originFile != null) {
	    skip = originFile.length();
	}
	ePackage = this.getEPackageFromFiles(leftFile, rightFile);
	diffs = new ArrayList<>();
	conflicts = new ArrayList<>();
	objects = new HashMap<>();

	System.out.print("Convert CBP String Lines to Events\n");
	int iteration = 6;
	int threshold = 3;
	for (int i = 1; i <= iteration; i++) {
	    leftEvents.clear();
	    rightEvents.clear();
	    System.gc();
	    startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	    startInterval = System.nanoTime();
	    this.readFiles(leftFile, rightFile, skip);
	    endInterval = System.nanoTime();
	    System.gc();
	    endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	    if (i > threshold) {
		System.out.println("time = " + df.format(((endInterval - startInterval)) / 1000000.0) + " ms ");
		System.out.println("memory = " + df.format(((endMemory - startMemory)) / 1000000.0) + " MBs");
		loadTime = loadTime + (endInterval - startInterval);
		loadMemory = loadMemory + (endMemory - startMemory);
	    }
	}
	loadTime = (long) (((double) loadTime) / ((double) (iteration - threshold)));
	loadMemory = (long) (((double) loadMemory) / ((double) (iteration - threshold)));

	System.out.print("Construct Object Tree ");
	System.gc();
	startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	startInterval = System.nanoTime();
	System.out.print("LEFT ");
	this.contructObjectTree(leftEvents, CBPSide.LEFT);
	System.out.print("RIGHT ");
	this.contructObjectTree(rightEvents, CBPSide.RIGHT);
	endInterval = System.nanoTime();
	endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	System.out.println("= " + df.format(((endInterval - startInterval) / 1000000.0)) + " ms");
	objectTreeConstructionTime = endInterval - startInterval;
	objectTreeConstructionMemory = endMemory - startMemory;

	saveObjectTree();

	System.out.print("Determine Differences");
	System.gc();
	startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	startInterval = System.nanoTime();
	this.computeDifferences(CBPSide.LEFT);
	endInterval = System.nanoTime();
	endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	System.out.println(" = " + df.format(((endInterval - startInterval) / 1000000.0)) + " ms");
	diffTime = endInterval - startInterval;
	diffMemory = endMemory - startMemory;

	comparisonTime = objectTreeConstructionTime + diffTime;
	comparisonMemory = objectTreeConstructionMemory + diffMemory;

	// System.out.println("Comparison Time = " + df.format(((comparisonTime)
	// / 1000000.0)) + " ms");

	System.out.print("Determine Conflicts");
	System.gc();
	startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	startInterval = System.nanoTime();
	this.computeConflicts();
	endInterval = System.nanoTime();
	endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	System.out.println(" = " + df.format(((endInterval - startInterval) / 1000000.0)) + " ms");
	conflictTime = endInterval - startInterval;
	conflictMemory = endMemory - startMemory;

	comparisonTime = comparisonTime + conflictTime;
	comparisonMemory = comparisonMemory + conflictMemory;

	System.out.println("Comparison Time = " + df.format(((comparisonTime) / 1000000.0)) + " ms");

	// System.out.println("\nDIFFERENCES:");
	// printDifferences();

	// System.out.println("\nEXPORT FOR COMPARISON WITH EMF COMPARE:");
	// this.exportForComparisonWithEMFCompare();
	this.exportDiffsToFile(true);
	System.out.println();
	this.exportConflictsToFile();
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

    protected void computeConflicts() {
	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject cTarget = objectEntry.getValue();

	    if (cTarget.getLeftIsDeleted() && cTarget.getRightIsDeleted()) {
		continue;
	    }

	    for (Entry<String, CBPMatchFeature> featureEntry : cTarget.getFeatures().entrySet()) {
		CBPMatchFeature cFeature = featureEntry.getValue();

		Map<Integer, Object> leftValues = cFeature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = cFeature.getValues(CBPSide.RIGHT);
		Map<Integer, Object> originalValues = cFeature.getOldLeftValues();
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);
		    Object originalValue = originalValues.get(pos);

		    if (leftValue == null && rightValue == null) {
			continue;
		    } else if (originalValue != null && leftValue != null && rightValue != null && leftValue.equals(rightValue) && leftValue.equals(originalValue)
			    && rightValue.equals(originalValue)) {
			continue;
		    }

		    // CONTAINMENT REFERENCE
		    if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && cFeature.isContainment()) {
			CBPMatchObject cLeftValue = (CBPMatchObject) leftValue;
			CBPMatchObject cRightValue = (CBPMatchObject) rightValue;
			if (originalValue instanceof CBPEObject) {
			    System.console();
			}
			CBPMatchObject cOriginalValue = (CBPMatchObject) originalValue;

			if (cRightValue instanceof CBPMatchObject) {
			    if (!cRightValue.getLeftIsCreated() && !cRightValue.getLeftIsDeleted() && !cRightValue.getLeftIsCreated() && cRightValue.getRightIsDeleted()) {
				CBPConflict conflict = new CBPConflict(cRightValue.getLeftContainer(), cRightValue.getRightContainer(), cRightValue.getOldLeftContainer(), //
					cRightValue.getLeftContainingFeature(), cRightValue.getRightContainingFeature(), cRightValue.getOldLeftContainingFeature(), //
					cRightValue.getLeftPosition(), cRightValue.getRightPosition(), cRightValue.getOldLeftPosition(), //
					rightValue, rightValue, rightValue, cRightValue.getLeftEvents(), cRightValue.getRightTargetEvents());
				conflicts.add(conflict);
			    } else if (!cRightValue.getLeftIsCreated() && cRightValue.getLeftIsDeleted() && !cRightValue.getLeftIsCreated() && !cRightValue.getRightIsDeleted()) {
				CBPConflict conflict = new CBPConflict(cRightValue.getLeftContainer(), cRightValue.getRightContainer(), cRightValue.getOldLeftContainer(), //
					cRightValue.getLeftContainingFeature(), cRightValue.getRightContainingFeature(), cRightValue.getOldLeftContainingFeature(), //
					cRightValue.getLeftPosition(), cRightValue.getRightPosition(), cRightValue.getOldLeftPosition(), //
					rightValue, rightValue, rightValue, cRightValue.getLeftTargetEvents(), cRightValue.getRightEvents());
				conflicts.add(conflict);
			    } else if (!cRightValue.getLeftIsCreated() && !cRightValue.getLeftIsDeleted() && !cRightValue.getLeftIsCreated() && !cRightValue.getRightIsDeleted()) {
				// this will be handled at its left value
				continue;
			    }
			}

			if (cLeftValue instanceof CBPMatchObject) {
			    if (!cLeftValue.getLeftIsCreated() && !cLeftValue.getLeftIsDeleted() && !cLeftValue.getLeftIsCreated() && cLeftValue.getRightIsDeleted()) {
				CBPConflict conflict = new CBPConflict(cLeftValue.getLeftContainer(), cLeftValue.getRightContainer(), cLeftValue.getOldLeftContainer(), //
					cLeftValue.getLeftContainingFeature(), cLeftValue.getRightContainingFeature(), cLeftValue.getOldLeftContainingFeature(), //
					cLeftValue.getLeftPosition(), cLeftValue.getRightPosition(), cLeftValue.getOldLeftPosition(), //
					leftValue, rightValue, originalValue, cLeftValue.getLeftEvents(), cLeftValue.getRightTargetEvents());
				conflicts.add(conflict);
			    } else if (!cLeftValue.getLeftIsCreated() && cLeftValue.getLeftIsDeleted() && !cLeftValue.getLeftIsCreated() && !cLeftValue.getRightIsDeleted()) {
				CBPConflict conflict = new CBPConflict(cLeftValue.getLeftContainer(), cLeftValue.getRightContainer(), cLeftValue.getOldLeftContainer(), //
					cLeftValue.getLeftContainingFeature(), cLeftValue.getRightContainingFeature(), cLeftValue.getOldLeftContainingFeature(), //
					cLeftValue.getLeftPosition(), cLeftValue.getRightPosition(), cLeftValue.getOldLeftPosition(), //
					leftValue, leftValue, leftValue, cLeftValue.getLeftTargetEvents(), cLeftValue.getRightEvents());
				conflicts.add(conflict);
			    } else if (!cLeftValue.getLeftIsCreated() && !cLeftValue.getLeftIsDeleted() && !cLeftValue.getLeftIsCreated() && !cLeftValue.getRightIsDeleted()) {
				if (cLeftValue.getLeftContainer() == null) {
				    continue;
				} else if (cLeftValue.getRightContainer() == null) {
				    continue;
				} else if ((!cLeftValue.getLeftContainer().equals(cLeftValue.getRightContainer()) && !cLeftValue.getLeftContainer().equals(cLeftValue.getOldLeftContainer())
					&& !cLeftValue.getRightContainer().equals(cLeftValue.getOldLeftContainer()))
					|| (!cLeftValue.getLeftContainingFeature().equals(cLeftValue.getRightContainingFeature())
						&& !cLeftValue.getLeftContainingFeature().equals(cLeftValue.getOldLeftContainingFeature())
						&& !cLeftValue.getRightContainingFeature().equals(cLeftValue.getOldLeftContainingFeature()))) {
				    CBPConflict conflict = new CBPConflict(cLeftValue.getLeftContainer(), cLeftValue.getRightContainer(), cLeftValue.getOldLeftContainer(), //
					    cLeftValue.getLeftContainingFeature(), cLeftValue.getRightContainingFeature(), cLeftValue.getOldLeftContainingFeature(), //
					    cLeftValue.getLeftPosition(), cLeftValue.getRightPosition(), cLeftValue.getOldLeftPosition(), //
					    leftValue, leftValue, leftValue, cLeftValue.getLeftValueEvents(), cLeftValue.getRightValueEvents());
				    conflicts.add(conflict);
				} else if (cLeftValue.getLeftPosition() != cLeftValue.getRightPosition() && cLeftValue.getLeftPosition() != cLeftValue.getOldLeftPosition()
					&& cLeftValue.getRightPosition() != cLeftValue.getOldLeftPosition()) {
				    CBPConflict conflict = new CBPConflict(cLeftValue.getLeftContainer(), cLeftValue.getRightContainer(), cLeftValue.getOldLeftContainer(), //
					    cLeftValue.getLeftContainingFeature(), cLeftValue.getRightContainingFeature(), cLeftValue.getOldLeftContainingFeature(), //
					    cLeftValue.getLeftPosition(), cLeftValue.getRightPosition(), cLeftValue.getOldLeftPosition(), //
					    leftValue, leftValue, leftValue, cFeature.getLeftEvents(), cFeature.getRightEvents());
				    conflicts.add(conflict);
				}
			    }
			}

		    }
		    // NON-CONTAINMENT REFERENCE
		    else if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && !cFeature.isContainment()) {
			// MULTIPLE VALUES
			if (cFeature.isMany()) {

			}
			// SINGLE VALUES
			else {

			}
		    }
		    // ATTRIBUTE
		    else if (cFeature.getFeatureType() == CBPFeatureType.ATTRIBUTE) {
			// MULTIPLE VALUES
			if (cFeature.isMany()) {

			}
			// SINGLE VALUES
			else {
			    if (originalValue == null && (leftValue == null || rightValue == null)) {
				continue;
			    } else if (leftValue != null && rightValue != null) {
				if (!leftValue.equals(rightValue) && !leftValue.equals(originalValue) && !rightValue.equals(originalValue)) {
				    CBPConflict conflict = new CBPConflict(cTarget, cTarget, cTarget, cFeature, cFeature, cFeature, 0, 0, 0, //
					    leftValue, rightValue, originalValue, cFeature.getLeftEvents(), cFeature.getRightEvents());
				    conflicts.add(conflict);
				}
			    } else if (leftValue != null && rightValue == null) {
				if (!leftValue.equals(rightValue) && !leftValue.equals(originalValue) && !originalValue.equals(rightValue)) {
				    CBPConflict conflict = new CBPConflict(cTarget, cTarget, cTarget, cFeature, cFeature, cFeature, 0, 0, 0, //
					    leftValue, rightValue, originalValue, cFeature.getLeftEvents(), cFeature.getRightEvents());
				    conflicts.add(conflict);
				}
			    } else if (leftValue == null && rightValue != null) {
				if (!rightValue.equals(leftValue) && !originalValue.equals(leftValue) && !rightValue.equals(originalValue)) {
				    CBPConflict conflict = new CBPConflict(cTarget, cTarget, cTarget, cFeature, cFeature, cFeature, 0, 0, 0, //
					    leftValue, rightValue, originalValue, cFeature.getLeftEvents(), cFeature.getRightEvents());
				    conflicts.add(conflict);
				}
			    }
			}
		    }
		}
	    }

	}
    }

    /**
     * 
     */
    protected void computeDifferences(CBPSide referenceSide) {

	// EClass x = (EClass) ePackage.getEClassifier("Property");
	// EReference b = (EReference)
	// x.getEStructuralFeature("owningAssociation");
	// EReference c = (EReference) x.getEStructuralFeature("association");

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
			if (((CBPMatchObject) leftValue).getId().equals("O-43721")) {
			    System.console();
			}
		    }

		    if (rightValue instanceof CBPMatchObject) {
			// System.out.println(object.getId() + "." +
			// feature.getName() + "." + "." +
			// rightValue.toString());
			if (((CBPMatchObject) rightValue).getId().equals("O-43721")) {
			    System.console();
			}
		    }

		    if (leftValue == null && rightValue == null) {
			continue;
		    } else if (leftValue != null && rightValue != null && leftValue.equals(rightValue)) {
			if (leftValue instanceof CBPMatchObject) {
			    CBPMatchObject leftObjectValue = (CBPMatchObject) leftValue;
			    CBPMatchObject rightObjectValue = (CBPMatchObject) rightValue;
			    if (leftObjectValue.getLeftPosition() == leftObjectValue.getRightPosition()) {
				if (feature.isContainment()) {
				    if (leftObjectValue.getLeftEvents().size() != rightObjectValue.getRightEvents().size()) {
					CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue.getRightPosition(), rightObjectValue.getRightContainingFeature(),
						rightObjectValue.getRightContainer(), rightObjectValue, CBPDifferenceKind.MOVE, referenceSide);
					diffs.add(diff);
					feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.MOVE, pos, pos, rightValue), CBPSide.RIGHT);
				    }
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
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, leftValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (!rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, leftValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (!rightObjectValue.getLeftIsCreated() && rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, rightValue), CBPSide.RIGHT);
				// rightObjectValue.setDiffed(true);
			    } else if (!rightObjectValue.getLeftIsCreated() && !rightObjectValue.getLeftIsDeleted() && !rightObjectValue.getRightIsCreated() && !rightObjectValue.getRightIsDeleted()) {
				if (rightObjectValue.getLeftContainer() == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightObjectValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
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
							rightObjectValue.getRightContainer(), rightObjectValue, leftValue, CBPDifferenceKind.MOVE, referenceSide);
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
				CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, leftValue), CBPSide.RIGHT);
				// leftObjectValue.setDiffed(true);
			    } else if (!leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);

				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, leftValue), CBPSide.RIGHT);
				// leftObjectValue.setDiffed(true);
			    } else if (!leftObjectValue.getLeftIsCreated() && leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {

				if (!leftObjectValue.getLeftContainer().getLeftIsDeleted()) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);

				    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, leftValue), CBPSide.RIGHT);
				    // leftObjectValue.setDiffed(true);
				}

			    } else if (!leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && leftObjectValue.getRightIsDeleted()) {
				CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, leftValue), CBPSide.RIGHT);
				// leftObjectValue.setDiffed(true);
			    } else if (!leftObjectValue.getLeftIsCreated() && !leftObjectValue.getLeftIsDeleted() && !leftObjectValue.getRightIsCreated() && !leftObjectValue.getRightIsDeleted()) {
				if (leftObjectValue.getRightContainer() == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, leftValue), CBPSide.RIGHT);
				} else if (leftObjectValue.getLeftContainer() == null) {
				    continue;
				    // CBPDiff diff = new CBPDiff(object,
				    // feature, pos, leftObjectValue,
				    // rightValue, CBPDifferenceKind.DELETE,
				    // referenceSide);
				    // diffs.add(diff);
				    // feature.addAdjustPositionEvent(new
				    // CBPDiffPositionEvent(CBPPositionEventType.REMOVE,
				    // pos, leftValue), CBPSide.RIGHT);
				} else {
				    // try {
				    if (!leftObjectValue.getLeftContainer().equals(leftObjectValue.getRightContainer())
					    || !leftObjectValue.getLeftContainingFeature().equals(leftObjectValue.getRightContainingFeature())) {

					CBPDiff diff = new CBPDiff(object, feature, pos, leftObjectValue.getRightPosition(), leftObjectValue.getRightContainingFeature(),
						leftObjectValue.getRightContainer(), leftObjectValue, rightValue, CBPDifferenceKind.MOVE, referenceSide);
					diffs.add(diff);

					((CBPMatchObject) leftValue).getLeftContainingFeature()
						.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.MOVEIN, ((CBPMatchObject) leftValue).getLeftPosition(), leftValue), CBPSide.LEFT);

					((CBPMatchObject) leftValue).getRightContainingFeature().addAdjustPositionEvent(
						new CBPDiffPositionEvent(CBPPositionEventType.MOVEOUT, ((CBPMatchObject) leftValue).getRightPosition(), leftValue), CBPSide.RIGHT);

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
							leftObjectValue.getRightContainer(), leftObjectValue, rightValue, CBPDifferenceKind.MOVE, referenceSide);
						diffs.add(diff);

						feature.addAdjustPositionEvent(
							new CBPDiffPositionEvent(CBPPositionEventType.MOVE, leftObjectValue.getLeftPosition(), leftObjectValue.getRightPosition(), leftValue),
							CBPSide.RIGHT);
						// leftObjectValue.setDiffed(true);

					    }
					}
				    }
				    // } catch (Exception e) {
				    // e.printStackTrace();
				    // }
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
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    continue;
				}
			    }
			    if (!object.getLeftIsCreated() && object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
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
				    diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    continue;
				}

				// -----
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				    diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
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
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				} else if (rightValue != null && !rightValue.equals(leftValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // rightValue).addDiff(diff);
				}
			    } else if (!object.getLeftIsCreated() && object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (feature.getOppositeFeatureName() == null) {
				    if (leftValue != null && !leftValue.equals(rightValue)) {
					CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.CHANGE, referenceSide);
					diffs.add(diff);
					// ((CBPMatchObject)
					// leftValue).addDiff(diff);
				    } else if (rightValue != null && !rightValue.equals(leftValue)) {
					CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
					diffs.add(diff);
					// ((CBPMatchObject)
					// rightValue).addDiff(diff);
				    }
				}
			    } else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && object.getRightIsCreated() && !object.getRightIsDeleted()) {
				if (leftValue != null && !leftValue.equals(rightValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				    // ((CBPMatchObject)
				    // leftValue).addDiff(diff);
				} else if (rightValue != null && !rightValue.equals(leftValue)) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
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
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
				    diffs.add(diff);

				    diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);

				    continue;
				} else if (leftValue != null && rightValue == null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, rightValue, CBPDifferenceKind.ADD, referenceSide);
				    diffs.add(diff);

				    continue;
				} else if (leftValue == null && rightValue != null) {
				    CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, leftValue, CBPDifferenceKind.DELETE, referenceSide);
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
				if (leftValue != null && !leftValue.equals(rightValue)) {
				    Object value = leftValue;
				    CBPDiff diff = new CBPDiff(object, feature, pos, value, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				} else if (rightValue != null && !rightValue.equals(leftValue)) {
				    Object value = leftValue;
				    if (value == null)
					value = rightValue;
				    CBPDiff diff = new CBPDiff(object, feature, pos, value, CBPDifferenceKind.CHANGE, referenceSide);
				    diffs.add(diff);
				} else {
				    continue;
				}

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
	    resource = new CBPMatchResource(CBPMatchResource.RESOURCE_STRING, objects);
	    objects.put(CBPMatchResource.RESOURCE_STRING, resource);
	}

	// get resource feature
	CBPMatchFeature resourceFeature = ((CBPMatchResource) resource).getResourceFeature();
	CBPChangeEvent<?> previousEvent = null;

	for (CBPChangeEvent<?> event : events) {

	    // System.out.println(event.toString());
	    // {
	    // CBPMatchObject obj = objects.get("O-410132");
	    // if (obj != null) {
	    // System.out.println(obj.getLeftContainer());
	    // if (obj.getLeftContainer() == null) {
	    // System.out.println();
	    // }
	    // }
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
		targetObject = new CBPMatchObject(targetClassName, targetId, this.objects);
		objects.put(targetId, targetObject);
	    }
	    targetObject.getTargetEvents(side).add(event);
	    targetObject.getEvents(side).add(event);

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

		    // get or create new object
		    valueObject = objects.get(valueId);
		    if (valueObject == null) {
			valueObject = new CBPMatchObject(valueClassName, valueId, this.objects);
			objects.put(valueId, valueObject);
		    }

		    // // try to get the left-side container and feature of the
		    // // added value object
		    if (!valueObject.getRightIsCreated() && !valueObject.getLeftIsDeleted() && valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(valueObject);
		    }
		    valueObject.getEvents(side).add(event);
		    valueObject.getValueEvents(side).add(event);

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

	    // --this is only for UML metamodel. Should be removed to another
	    // class to handle specific case for certain metadata.
	    if (feature != null && feature.getName().equals("owningAssociation")) {
		// no need to handle events related to this feature for UML
		// model
		continue;
	    }
	    // --

	    // get position
	    int position = event.getPosition();

	    // if (targetId != null && targetId.equals("L-304")) {
	    // System.currentTimeMillis();
	    // }

	    // start processing events
	    if (event instanceof CBPCreateEObjectEvent) {
		targetObject.setCreated(true, side);
	    } else

	    if (event instanceof CBPDeleteEObjectEvent) {
		targetObject.setDeleted(true, side);
		if (previousEvent instanceof CBPSetEReferenceEvent) {
		    CBPSetEReferenceEvent setEvent = (CBPSetEReferenceEvent) previousEvent;
		    CBPMatchObject previousTargetObject = objects.get(setEvent.getTarget());
		    CBPMatchFeature previousFeature = previousTargetObject.getFeatures().get(setEvent.getEReference());
		    CBPMatchObject previousValueObject = objects.get(((CBPEObject) setEvent.getOldValue()).getId());
		    targetObject.setContainer(previousTargetObject, side);
		    targetObject.setContainingFeature(previousFeature, side);
		    if (previousValueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(previousValueObject);
		    }
		} else if (previousEvent instanceof CBPUnsetEReferenceEvent) {
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
		if (targetId.equals("R-0")) {
		    System.console();
		}
		if (targetObject.getContainer(side) == null) {
		    targetObject.setContainer(resource, side);
		}
		if (targetObject.getContainingFeature(side) == null) {
		    targetObject.setContainingFeature(resourceFeature, side);
		}
		if (targetObject.getPosition(side) == -1) {
		    targetObject.setPosition(position, side, resourceFeature.isContainment());
		    targetObject.setMergePosition(position, resourceFeature, side);
		}
		resourceFeature.removeValue(targetObject, position, side);
		resourceFeature.setIsSet(position, side);
		resourceFeature.putValueLineNum(targetObject, event.getLineNumber(), side);
		resourceFeature.getEvents(side).add(event);

		if (targetObject.getLeftIsDeleted() == false && targetObject.getRightContainer() == null && side == CBPSide.LEFT) {
		    createValueObjectOnTheOppositeSide(targetObject, CBPSide.LEFT);
		} else if (!targetObject.getLeftIsDeleted() && targetObject.getRightIsDeleted() == false && targetObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
		    createValueObjectOnTheOppositeSide(targetObject, CBPSide.RIGHT);
		}
	    } else
	    // ----------------------
	    if (event instanceof CBPAddToResourceEvent) {
		if (targetId.equals("R-0")) {
		    System.console();
		}

		if (targetObject.isDeleted(side)) {
		    targetObject.setDeleted(false, side);
		}
		targetObject.setContainer(resource, side);
		targetObject.setContainingFeature(resourceFeature, side);
		targetObject.setPosition(position, side, resourceFeature.isContainment());
		targetObject.setMergePosition(position, resourceFeature, side);
		resourceFeature.addValue(targetObject, position, side);
		resourceFeature.setIsSet(position, side);
		resourceFeature.putValueLineNum(targetObject, event.getLineNumber(), side);
		resourceFeature.getEvents(side).add(event);
	    } else

	    // ------------------------
	    if (event instanceof CBPAddToEReferenceEvent) {

		if (valueId.equals("O-41068")) {
		    System.currentTimeMillis();
		}

		if (valueObject.isDeleted(side)) {
		    valueObject.setDeleted(false, side);
		}

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side, feature.isContainment());
		    valueObject.setMergePosition(position, feature, side);
		    if (valueObject.isDeleted(side)) {
			valueObject.setDeleted(false, side);
		    }
		} else {
		    valueObject.setMergePosition(position, feature, side);
		}
		feature.putValueLineNum(valueObject, event.getLineNumber(), side);
		feature.addValue(valueObject, position, side);
		feature.setIsSet(position, side);
	    } else

	    // ------------------
	    if (event instanceof CBPRemoveFromEReferenceEvent) {
		// if (valueId.equals("O-359")) {

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side, feature.isContainment());
		    valueObject.setMergePosition(position, feature, side);

		    // feature.updateMergePosition(event, side);

		    if (valueObject.getLeftIsDeleted() == false && valueObject.getRightIsDeleted() == false && valueObject.getRightContainer() == null && side == CBPSide.LEFT) {
			createValueObjectOnTheOppositeSide(valueObject, CBPSide.LEFT);
		    } else if (!valueObject.getLeftIsDeleted() && valueObject.getRightIsDeleted() == false && valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(valueObject, CBPSide.RIGHT);
		    }
		} else {
		    feature.putValueLineNum(valueObject, event.getLineNumber(), side);
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

		feature.putValueLineNum(valueObject, event.getLineNumber(), side);
		feature.removeValue(valueObject, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -----------------
	    if (event instanceof CBPMoveWithinEReferenceEvent) {

		int fromPosition = ((CBPMoveWithinEReferenceEvent) event).getFromPosition();
		valueObject.setMoved(true, side);
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		if (valueObject.getOldPosition(side) == -1)
		    valueObject.setOldPosition(fromPosition, side);
		valueObject.setPosition(position, side, feature.isContainment());
		valueObject.setMergePosition(position, feature, side);

		// feature.updateMergePosition(event, side);

		if (valueObject.getLeftIsDeleted() == false && valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
		    createValueObjectOnTheOppositeSide(valueObject);
		} else if (valueObject.getRightIsDeleted() == false && valueObject.getRightContainer() == null && side == CBPSide.LEFT) {
		    createValueObjectOnTheOppositeSide(valueObject, CBPSide.LEFT);
		}

		feature.putValueLineNum(valueObject, event.getLineNumber(), side);
		feature.moveValue(valueObject, fromPosition, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -----------------
	    if (event instanceof CBPAddToEAttributeEvent) {
		feature.addValue(valueLiteral, position, side);
		feature.setIsSet(position, side);
		feature.putValueLineNum(valueLiteral, event.getLineNumber(), side);
	    } else
	    // -------------
	    if (event instanceof CBPRemoveFromEAttributeEvent) {
		feature.removeValue(valueLiteral, position, side);
		feature.setIsSet(position, side);
		feature.putValueLineNum(valueLiteral, event.getLineNumber(), side);
	    } else
	    // -----------------
	    if (event instanceof CBPMoveWithinEAttributeEvent) {
		int fromPosition = ((CBPMoveWithinEAttributeEvent) event).getFromPosition();
		feature.moveValue(valueLiteral, fromPosition, position, side);
		feature.setIsSet(position, side);
		feature.putValueLineNum(valueLiteral, event.getLineNumber(), side);
	    } else

	    // ------------
	    if (event instanceof CBPSetEReferenceEvent) {

		if (valueId.equals("O-27474")) {
		    System.console();
		}

		if (valueObject.isDeleted(side)) {
		    valueObject.setDeleted(false, side);
		}

		feature.putValueLineNum(valueObject, event.getLineNumber(), side);
		feature.setValue(valueObject, side);
		feature.setIsSet(side);

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side, feature.isContainment());
		    valueObject.setMergePosition(position, feature, side);
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
			oldValue = new CBPMatchObject(oldClassName, oldId, this.objects);
			objects.put(oldId, oldValue);
		    }
		    feature.setOldValue(oldValue, side);
		    
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

		String oldId = null;
		String oldClassName = null;
		if ((CBPEObject) event.getOldValue() != null) {
		    oldId = ((CBPEObject) event.getOldValue()).getId();
		    oldClassName = ((CBPEObject) event.getOldValue()).getClassName();
		}
		CBPMatchObject oldValue = objects.get(oldId);
		if (oldValue == null) {
		    oldValue = new CBPMatchObject(oldClassName, oldId, this.objects);
		    objects.put(oldId, oldValue);
		}
		if (oldId != null && oldId.equals("O-8375")) {
		    System.console();
		}

		feature.putValueLineNum(oldValue, event.getLineNumber(), side);
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
		feature.putValueLineNum(valueLiteral, event.getLineNumber(), side);
		feature.setOldValue(event.getOldValue(), side);
		feature.setValue(valueLiteral, side);
		feature.setIsSet(side);
		if (event.getOldValue() != null) {
		    if (!feature.getIsSet(CBPSide.RIGHT) && side == CBPSide.LEFT) {
			feature.setValue(event.getOldValue(), CBPSide.RIGHT);
			// feature.setIsSet(CBPSide.RIGHT);
		    } else if (!feature.getIsSet(CBPSide.LEFT) && side == CBPSide.RIGHT) {
			feature.setValue(event.getOldValue(), CBPSide.LEFT);
			// feature.setIsSet(CBPSide.LEFT);
		    }
		}

	    } else
	    // -------------
	    if (event instanceof CBPUnsetEAttributeEvent) {

		feature.putValueLineNum(event.getOldValue(), event.getLineNumber(), side);
		feature.setOldValue(event.getOldValue(), side);
		feature.unsetValue(valueLiteral, side);
		feature.setIsSet(side);
		feature.setIsSet(side);
		if (event.getOldValue() != null) {
		    if (!feature.getIsSet(CBPSide.RIGHT) && side == CBPSide.LEFT) {
			feature.setValue(event.getOldValue(), CBPSide.RIGHT);
			// feature.setIsSet(CBPSide.RIGHT);
		    } else if (!feature.getIsSet(CBPSide.LEFT) && side == CBPSide.RIGHT) {
			feature.setValue(event.getOldValue(), CBPSide.LEFT);
			// feature.setIsSet(CBPSide.LEFT);
		    }
		}
	    }

	    previousEvent = event;
	}

    }

    // private void handleEOppositeReference(CBPMatchObject targetObject,
    // CBPMatchFeature feature, CBPMatchObject valueObject, CBPSide side) {
    // if (feature.getOppositeFeatureName() != null) {
    //
    // String featureName = feature.getOppositeFeatureName();
    //
    // // if (feature.getName().equals("type")) {
    // // System.out.print("");
    // // }
    // //
    // // if (featureName.equals("type")) {
    // // System.out.print("");
    // // }
    //
    // boolean isContainment = false;
    // CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
    // boolean isMany = false;
    // String oppositeFeatureName = null;
    //
    // String eClassName = valueObject.getClassName();
    // EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
    // EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);
    //
    // if (eFeature.isMany()) {
    // isMany = true;
    // }
    // if (eFeature instanceof EReference) {
    // featureType = CBPFeatureType.REFERENCE;
    // if (((EReference) eFeature).isContainment()) {
    // isContainment = true;
    // }
    // if (((EReference) eFeature).getEOpposite() != null) {
    // oppositeFeatureName = ((EReference) eFeature).getEOpposite().getName();
    // }
    // }
    //
    // CBPMatchFeature oppositeFeature =
    // valueObject.getFeatures().get(featureName);
    // if (oppositeFeature == null) {
    // oppositeFeature = new CBPMatchFeature(valueObject, featureName,
    // featureType, isContainment, isMany);
    // oppositeFeature.setOppositeFeatureName(oppositeFeatureName);
    // valueObject.getFeatures().put(featureName, oppositeFeature);
    // }
    //
    // if (oppositeFeature.isMany()) {
    // Map<Integer, Object> values = oppositeFeature.getValues(side);
    // for (Entry<Integer, Object> entry : values.entrySet()) {
    // CBPMatchObject value = (CBPMatchObject) entry.getValue();
    // if (value != null) {
    // int position = entry.getKey();
    // oppositeFeature.removeValue(value, position, side);
    // break;
    // }
    // }
    // } else {
    // Object value = oppositeFeature.getValue(side);
    // oppositeFeature.unsetValue(value, side);
    // }
    //
    // }
    // }

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
	    boolean isUnique = false;
	    String oppositeFeatureName = null;

	    EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
	    EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);
	    EReference eOpposite = null;

	    if (eFeature.isMany()) {
		isMany = true;
	    }
	    isUnique = eFeature.isUnique();
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
		feature = new CBPMatchFeature(targetObject, featureName, featureType, isContainment, isMany, isUnique);
		feature.setOppositeFeatureName(oppositeFeatureName);
		targetObject.getFeatures().put(featureName, feature);
	    }
	    feature.getEvents(side).add(event);

	    // -----------------------------
	    // also create eOpposite feature
	    if (feature.getName().equals("memberEnd") && feature.isMany() && eOpposite != null && valueObject != null) {
		isMany = eOpposite.isMany();
		isContainment = eOpposite.isContainment();
		oppositeFeatureName = eOpposite.getName();
		featureType = CBPFeatureType.REFERENCE;

		CBPMatchFeature oppositeFeature = valueObject.getFeatures().get(oppositeFeatureName);
		if (oppositeFeature == null) {
		    oppositeFeature = new CBPMatchFeature(valueObject, oppositeFeatureName, featureType, isContainment, isMany, isUnique);
		    oppositeFeature.setOppositeFeatureName(oppositeFeatureName);
		    valueObject.getFeatures().put(oppositeFeatureName, oppositeFeature);
		}

		if (isMany) {
		    oppositeFeature.addValue(targetObject, 0, side);
		} else {
		    oppositeFeature.setValue(targetObject, side);
		}

		oppositeFeature.putValueLineNum(targetObject, event.getLineNumber(), side);
		// if (oppositeFeature.isContainment()) {
		// oppositeFeature.putValueLineNum(targetObject,
		// event.getLineNumber() - 1, side);
		// } else {
		// oppositeFeature.putValueLineNum(targetObject,
		// event.getLineNumber() + 2, side);
		// }
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
			leftFeature = new CBPMatchFeature(leftContainer, rightFeature.getName(), featureType, isContainment, rightFeature.isMany(), rightFeature.isUnique());
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
		    valueObject.setMergePosition(pos, leftFeature, CBPSide.LEFT);
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
			rightFeature = new CBPMatchFeature(rightContainer, leftFeature.getName(), featureType, isContainment, leftFeature.isMany(), leftFeature.isUnique());
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
		    valueObject.setMergePosition(pos, rightFeature, CBPSide.RIGHT);
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

	    leftEvents.clear();
	    rightEvents.clear();
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
	    int lineNumber = 0;

	    while (xmlReader.hasNext()) {
		XMLEvent xmlEvent = xmlReader.nextEvent();
		if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
		    StartElement e = xmlEvent.asStartElement();
		    String name = e.getName().getLocalPart();

		    if (!name.equals("value") && !name.equals("old-value") && !name.equals("m")) {
			if (ignore == false) {
			    lineNumber++;
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
			    event.setLineNumber(lineNumber);

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

    protected void exportConflictsToFile() throws IOException {
	exportConflictsToFile(false);
    }

    private void exportConflictsToFile(boolean saveToFile) {
	Set<String> set = new HashSet<>();
	String str = null;
	int num = 0;
	System.out.println("CHANGE-BASED CONFLICTS:");
	for (CBPConflict conflict : conflicts) {
	    num++;
	    Set<CBPChangeEvent<?>> leftEvents = conflict.getLeftEvents();
	    Set<CBPChangeEvent<?>> rightEvents = conflict.getRightEvents();
	    Iterator<CBPChangeEvent<?>> leftIterator = leftEvents.iterator();
	    Iterator<CBPChangeEvent<?>> rightIterator = rightEvents.iterator();
	    while (leftIterator.hasNext() || rightIterator.hasNext()) {
		CBPChangeEvent<?> leftEvent = null;
		if (leftIterator.hasNext()) {
		    leftEvent = leftIterator.next();
		}
		CBPChangeEvent<?> rightEvent = null;
		if (rightIterator.hasNext()) {
		    rightEvent = rightIterator.next();
		}
		String leftString = "";
		String rightString = "";
		if (leftEvent != null) {
		    leftString = leftEvent.toString();
		}
		if (rightEvent != null) {
		    rightString = rightEvent.toString();
		}
		String result = num + ": " + leftString + " <-> " + rightString;
		System.out.println(result);
		set.add(result);
	    }
	}
	System.out.println("Change-based Conflicts size = " + conflicts.size());
    }

    protected void exportDiffsToFile() throws IOException {
	exportDiffsToFile(false);
    }

    protected void exportDiffsToFile(boolean saveToFile) throws IOException {
	Set<String> set = new HashSet<>();
	String str = null;
	for (CBPDiff diff : diffs) {
	    if (diff instanceof MultiplicityElementChange || diff instanceof AssociationChange || diff instanceof DirectedRelationshipChange) {
		continue;
	    }
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
    protected void saveObjectTree() throws FileNotFoundException {
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

    public List<CBPChangeEvent<?>> getLeftEvents() {
	return leftEvents;
    }

    public List<CBPChangeEvent<?>> getRightEvents() {
	return rightEvents;
    }

    public long getObjectTreeConstructionMemory() {
	return objectTreeConstructionMemory;
    }

    public long getDiffMemory() {
	return diffMemory;
    }

    public long getComparisonMemory() {
	return comparisonMemory;
    }

    public long getLoadMemory() {
	return loadMemory;
    }

    public Map<String, CBPMatchObject> getObjectTree() {
	return objects;
    }

}
