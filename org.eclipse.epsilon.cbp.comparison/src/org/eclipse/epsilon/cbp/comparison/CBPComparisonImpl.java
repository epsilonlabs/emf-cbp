package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.epsilon.cbp.comparison.event.CBPEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPMoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPMoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPMultiValueEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRegisterEPackageEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPSetEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPSetEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPSingleValueEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPSingleValueEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPStartNewSessionEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPUnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPUnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.ICBPEObjectValuesEvent;
import org.eclipse.epsilon.cbp.comparison.event.ICBPFromPositionEvent;
import org.eclipse.epsilon.cbp.conflict.CBPConflict;
import org.eclipse.epsilon.cbp.conflict.CBPConflictDetector;
import org.eclipse.epsilon.cbp.conflict.test.CBPChangeEventSortComparator;

public class CBPComparisonImpl implements ICBPComparison {

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private EPackage ePackage = null;
    private List<String> conflictStrings = new ArrayList<>();

    public List<String> getConflictStrings() {
	return conflictStrings;
    }

    public long getConflictMemory() {
	return conflictMemory;
    }

    public long getConflictTime() {
	return conflictTime;
    }

    public int getConflictCount() {
	return conflictCount;
    }

    public void setConflicts(List<CBPConflict> conflicts) {
	this.conflicts = conflicts;
    }

    public void setConflictMemory(long conflictMemory) {
	this.conflictMemory = conflictMemory;
    }

    public void setConflictTime(long conflictTime) {
	this.conflictTime = conflictTime;
    }

    public void setConflictCount(int conflictCount) {
	this.conflictCount = conflictCount;
    }

    private List<CBPDiff> diffs = null;
    private List<CBPConflict> conflicts = null;
    private List<CBPChangeEvent<?>> leftEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> rightEvents = new ArrayList<>();
    private Map<String, CBPMatchObject> objects = null;
    private Map<String, Set<CBPChangeEvent<?>>> leftCompositeEvents = new LinkedHashMap<>();
    private Map<String, Set<CBPChangeEvent<?>>> rightCompositeEvents = new LinkedHashMap<>();

    private File objectTreeFile = new File("D:\\TEMP\\COMPARISON2\\test\\object tree.txt");
    private File diffEMFCompareFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.txt");

    private List<ICBPObjectTreePostProcessor> objectTreePostProcessors = new ArrayList<>();

    private Map<Integer, Set<String>> leftEventStrings = new LinkedHashMap<>();
    private Map<Integer, Set<String>> rightEventStrings = new LinkedHashMap<>();

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
    private CBPConflictDetector conflictDetector = new CBPConflictDetector();

    public Map<Integer, Set<String>> getLeftEventStrings() {
	return leftEventStrings;
    }

    public Map<Integer, Set<String>> getRightEventStrings() {
	return rightEventStrings;
    }

    public void setLeftEventStrings(Map<Integer, Set<String>> leftEventStrings) {
	this.leftEventStrings = leftEventStrings;
    }

    public void setRightEventStrings(Map<Integer, Set<String>> rightEventStrings) {
	this.rightEventStrings = rightEventStrings;
    }

    public CBPConflictDetector getConflictDetector() {
	return conflictDetector;
    }

    public void setConflictDetector(CBPConflictDetector conflictDetector) {
	this.conflictDetector = conflictDetector;
    }

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
    
    public List<CBPConflict> getRealConflicts() {
	return conflicts.stream().filter(c -> c.isPseudo() == false).collect(Collectors.toList());
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
	this.contructElementTree(leftEvents, CBPSide.LEFT);
	System.out.print("RIGHT ");
	this.contructElementTree(rightEvents, CBPSide.RIGHT);
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

//	// remove pseudo conflicts
//	Iterator<CBPConflict> iterator = conflicts.iterator();
//	while (iterator.hasNext()) {
//	    CBPConflict conflict = iterator.next();
//	    if (conflict.isPseudo())
//		iterator.remove();
//	}
	
	// System.out.println("\nDIFFERENCES:");
	// printDifferences();

	// System.out.println("\nEXPORT FOR COMPARISON WITH EMF COMPARE:");
	// this.exportForComparisonWithEMFCompare();
	this.exportDiffsToFile(true);
	// System.out.println();
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

	EPackage ePackage = (EPackage) EPackage.Registry.INSTANCE.get(nsUri);
	return ePackage;
    }

    protected void computeConflicts() {
	conflicts.clear();
	conflictDetector.computeConflicts(objects, conflicts, leftCompositeEvents, rightCompositeEvents);
    }

    private boolean isIntersected(List<CBPChangeEvent<?>> leftEvents, List<CBPChangeEvent<?>> rightEvents) {
	for (CBPChangeEvent<?> leftEvent : leftEvents) {
	    if (leftEvent instanceof CBPMoveWithinEReferenceEvent || leftEvent instanceof CBPAddToEReferenceEvent || leftEvent instanceof CBPRemoveFromEReferenceEvent) {
		for (CBPChangeEvent<?> rightEvent : rightEvents) {
		    if (rightEvent instanceof CBPMoveWithinEReferenceEvent || rightEvent instanceof CBPAddToEReferenceEvent || rightEvent instanceof CBPRemoveFromEReferenceEvent) {
			if (leftEvent instanceof CBPMoveWithinEReferenceEvent && rightEvent instanceof CBPMoveWithinEReferenceEvent) {
			    int leftFrom = ((CBPMoveWithinEReferenceEvent) leftEvent).getFromPosition();
			    int leftTo = ((CBPMoveWithinEReferenceEvent) leftEvent).getPosition();
			    int rightFrom = ((CBPMoveWithinEReferenceEvent) rightEvent).getFromPosition();
			    int rightTo = ((CBPMoveWithinEReferenceEvent) rightEvent).getPosition();
			    if (!(leftFrom > rightFrom && leftFrom > rightTo && leftTo > rightFrom && leftTo > rightTo)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPMoveWithinEReferenceEvent && rightEvent instanceof CBPAddToEReferenceEvent) {
			    int leftFrom = ((CBPMoveWithinEReferenceEvent) leftEvent).getFromPosition();
			    int leftTo = ((CBPMoveWithinEReferenceEvent) leftEvent).getPosition();
			    int rightTo = ((CBPAddToEReferenceEvent) rightEvent).getPosition();
			    if (!(rightTo > leftFrom && rightTo > leftTo)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPMoveWithinEReferenceEvent && rightEvent instanceof CBPRemoveFromEReferenceEvent) {
			    int leftFrom = ((CBPMoveWithinEReferenceEvent) leftEvent).getFromPosition();
			    int leftTo = ((CBPMoveWithinEReferenceEvent) leftEvent).getPosition();
			    int rightFrom = ((CBPRemoveFromEReferenceEvent) rightEvent).getPosition();
			    if (!(rightFrom > leftFrom && rightFrom > leftTo)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPAddToEReferenceEvent && rightEvent instanceof CBPMoveWithinEReferenceEvent) {
			    int rightFrom = ((CBPMoveWithinEReferenceEvent) rightEvent).getFromPosition();
			    int rightTo = ((CBPMoveWithinEReferenceEvent) rightEvent).getPosition();
			    int leftTo = ((CBPAddToEReferenceEvent) leftEvent).getPosition();
			    if (!(leftTo > rightFrom && leftTo > rightTo)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPAddToEReferenceEvent && rightEvent instanceof CBPAddToEReferenceEvent) {
			    return true;
			} else if (leftEvent instanceof CBPAddToEReferenceEvent && rightEvent instanceof CBPRemoveFromEReferenceEvent) {
			    int leftTo = ((CBPAddToEReferenceEvent) leftEvent).getPosition();
			    int rightFrom = ((CBPRemoveFromEReferenceEvent) rightEvent).getPosition();
			    if (!(leftTo < rightFrom)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPRemoveFromEReferenceEvent && rightEvent instanceof CBPMoveWithinEReferenceEvent) {
			    int rightFrom = ((CBPMoveWithinEReferenceEvent) rightEvent).getFromPosition();
			    int rightTo = ((CBPMoveWithinEReferenceEvent) rightEvent).getPosition();
			    int leftFrom = ((CBPRemoveFromEReferenceEvent) leftEvent).getPosition();
			    if (!(leftFrom > rightFrom && leftFrom > rightTo)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPRemoveFromEReferenceEvent && rightEvent instanceof CBPAddToEReferenceEvent) {
			    int rightTo = ((CBPAddToEReferenceEvent) rightEvent).getPosition();
			    int leftFrom = ((CBPRemoveFromEReferenceEvent) leftEvent).getPosition();
			    if (!(rightTo < leftFrom)) {
				return true;
			    }
			} else if (leftEvent instanceof CBPRemoveFromEReferenceEvent && rightEvent instanceof CBPRemoveFromEReferenceEvent) {
			    // remove vs remove doe not conflicts to each other
			}
		    } else {
			continue;
		    }
		}
	    } else {
		continue;
	    }
	}
	return false;
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

	    // CBPMatchObject obj = objects.get("O-43742");
	    // if (obj != null) {
	    // if (diffs.size() > 0) {
	    // CBPDiff ddd = diffs.get(diffs.size() - 1);
	    // if (obj.getRightPosition() > 35) {
	    // System.console();
	    // }
	    // }
	    // }

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
			if (((CBPMatchObject) leftValue).getId().equals("O-43852")) {
			    System.console();
			}
		    }

		    if (rightValue instanceof CBPMatchObject) {
			// System.out.println(object.getId() + "." +
			// feature.getName() + "." + "." +
			// rightValue.toString());
			if (((CBPMatchObject) rightValue).getId().equals("O-43852")) {
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
						rightObjectValue.setRightPosition(rightPos); // reset
											     // right
											     // position
											     // to
											     // its
											     // before-adjustment
											     // value
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
						leftObjectValue.setRightPosition(rightPos); // reset
											    // right
											    // position
											    // to
											    // its
											    // before-adjustment
											    // value
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

    private void contructElementTree(List<CBPChangeEvent<?>> events, CBPSide side) {
	CBPMatchObject resource = objects.get(CBPMatchResource.RESOURCE_STRING);
	if (resource == null) {
	    resource = new CBPMatchResource(CBPMatchResource.RESOURCE_STRING, objects);
	    objects.put(CBPMatchResource.RESOURCE_STRING, resource);
	}

	// get resource feature
	CBPMatchFeature resourceFeature = ((CBPMatchResource) resource).getResourceFeature();
	CBPChangeEvent<?> previousEvent = null;

	for (CBPChangeEvent<?> event : events) {

	    // System.out.println(event);

	    // CBPMatchObject targetObj = objects.get("O-5676");
	    // if (targetObj != null) {
	    // if (targetObj.getLeftContainer() != null) {
	    // CBPMatchObject x = targetObj.getOldLeftContainer();
	    // if (x != null && !x.getId().equals("O-5953")) {
	    // System.console();
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

	    if (targetId.equals("O-5676")) {
		System.console();
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

		    if (valueId.equals("O-2")) {
			System.console();
		    }
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

	    // if (targetId.equals("O-5944") &&
	    // feature.getName().equals("typeArguments")) {
	    // System.console();
	    // }

	    // start processing events
	    if (event instanceof CBPCreateEObjectEvent) {
		targetObject.setCreated(true, side);
	    } else

	    if (event instanceof CBPDeleteEObjectEvent) {
		targetObject.setDeleted(true, side);
		targetObject.addDeleteEvents(event, side);
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
		resourceFeature.addObjectEvent(targetObject, event, side);
		targetObject.addValueEvents(event, side);

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
		resourceFeature.addObjectEvent(targetObject, event, side);
		targetObject.addValueEvents(event, side);
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
		if (valueId.equals("O-14396")) {
		    System.console();
		}

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
		feature.addObjectEvent(targetObject, event, side);
		// targetObject.addValueEvents(event, side);
		valueObject.addValueEvents(event, side);

		if (!valueObject.isCreated(side)) {
		    feature.setOldValue(valueObject.getOldPosition(side), valueObject, side);
		}
		feature.putValueLineNum(valueObject, event.getLineNumber(), side);
		feature.removeValue(valueObject, position, side);
		feature.setIsSet(position, side);
	    } else
	    // -----------------
	    if (event instanceof CBPMoveWithinEReferenceEvent) {

		if (valueId.equals("O-14396")) {
		    System.console();
		}

		int fromPosition = ((CBPMoveWithinEReferenceEvent) event).getFromPosition();
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
		if (!valueObject.isCreated(side) && valueObject.isMoved(side) == false) {
		    feature.setOldValue(valueObject.getOldPosition(side), valueObject, side);
		}
		valueObject.setMoved(true, side);
		feature.putValueLineNum(valueObject, event.getLineNumber(), side);
		feature.moveValue(valueObject, fromPosition, position, side);
		feature.setIsSet(position, side);

		valueObject.addValueEvents(event, side);
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

		if (valueId.equals("O-5676")) {
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
		valueObject.addValueEvents(event, side);

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
		    if (!oldValue.isCreated(side) && !feature.isOldSet()) {
			feature.setOldValue(oldValue, side);
			feature.setIsOldSet();
		    }
		    oldValue.addEvents(event, side);
		    oldValue.addValueEvents(event, side);

		    if (feature.isContainment()) {
			// if (!feature.isOldSet()) {
			oldValue.setOldContainer(targetObject, side);
			oldValue.setOldContainingFeature(feature, side);
			oldValue.setOldPosition(0, side);
			// }

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
		} else {
		    feature.setIsOldSet();
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
		if (oldId != null && oldId.equals("O-5676")) {
		    System.console();
		}

		feature.putValueLineNum(oldValue, event.getLineNumber(), side);
		if (!oldValue.isCreated(side) && !feature.isOldSet()) {
		    feature.setOldValue(oldValue, side);
		    feature.setIsOldSet();
		}
		oldValue.addEvents(event, side);
		oldValue.addValueEvents(event, side);
		feature.unsetValue(valueObject, side);
		feature.setIsSet(true, side);

		// handle setting value to other side
		if (feature.isContainment()) {
		    if (oldValue.getOldContainer(side) == null) {
			oldValue.setOldContainer(targetObject, side);
			oldValue.setOldContainingFeature(feature, side);
			oldValue.setOldPosition(0, side);
		    }

		    if (!oldValue.getLeftIsCreated() && oldValue.getRightContainer() == null && side == CBPSide.LEFT) {
			createValueObjectOnTheOppositeSide(oldValue, CBPSide.LEFT);
		    }
		    if (!oldValue.getRightIsCreated() && oldValue.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(oldValue, CBPSide.RIGHT);
		    }
		    System.console();
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
	    boolean isOrdered = false;
	    String oppositeFeatureName = null;

	    EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
	    EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);
	    EReference eOpposite = null;

	    if (eFeature.isMany()) {
		isMany = true;
	    }
	    isUnique = eFeature.isUnique();
	    isOrdered = eFeature.isOrdered();
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
		feature = new CBPMatchFeature(targetObject, featureName, featureType, isContainment, isMany, isUnique, isOrdered);
		feature.setOppositeFeatureName(oppositeFeatureName);
		targetObject.getFeatures().put(featureName, feature);
	    }
	    feature.getEvents(side).add(event);
	    if (valueObject != null) {
		feature.addObjectEvent(valueObject, event, side);
	    }

	    // -----------------------------
	    // also create eOpposite feature
	    if (feature.getName().equals("memberEnd") && feature.isMany() && eOpposite != null && valueObject != null) {
		isMany = eOpposite.isMany();
		isContainment = eOpposite.isContainment();
		oppositeFeatureName = eOpposite.getName();
		featureType = CBPFeatureType.REFERENCE;
		isUnique = eOpposite.isUnique();
		isOrdered = eOpposite.isOrdered();

		CBPMatchFeature oppositeFeature = valueObject.getFeatures().get(oppositeFeatureName);
		if (oppositeFeature == null) {
		    oppositeFeature = new CBPMatchFeature(valueObject, oppositeFeatureName, featureType, isContainment, isMany, isUnique, isOrdered);
		    oppositeFeature.setOppositeFeatureName(oppositeFeatureName);
		    valueObject.getFeatures().put(oppositeFeatureName, oppositeFeature);
		}

		if (isMany) {
		    oppositeFeature.addValue(targetObject, 0, side);
		} else {
		    oppositeFeature.setValue(targetObject, side);
		}
		oppositeFeature.putValueLineNum(targetObject, event.getLineNumber(), side);

		oppositeFeature.getEvents(side).add(event);
		if (oppositeFeature != null) {
		    oppositeFeature.addObjectEvent(targetObject, event, side);
		}
		targetObject.getValueEvents(side).add(event);

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
			leftFeature = new CBPMatchFeature(leftContainer, rightFeature.getName(), featureType, isContainment, rightFeature.isMany(), rightFeature.isUnique(), rightFeature.isOrdered());
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
			rightFeature = new CBPMatchFeature(rightContainer, leftFeature.getName(), featureType, isContainment, leftFeature.isMany(), leftFeature.isUnique(), leftFeature.isOrdered());
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
	    leftCompositeEvents.clear();
	    rightCompositeEvents.clear();

	    BufferedReader leftReader = null;
	    BufferedReader rightReader = null;
	    String leftLine;
	    String rightLine;
	    if (skip <= 0) {
		leftReader = new BufferedReader(new FileReader(leftFile));
		rightReader = new BufferedReader(new FileReader(rightFile));
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
		FileInputStream leftFileInputStream = new FileInputStream(leftFile);
		FileInputStream rightFileInputStream = new FileInputStream(rightFile);
		leftFileInputStream.skip(skip);
		rightFileInputStream.skip(skip);
		InputStreamReader leftInputStreamReader = new InputStreamReader(leftFileInputStream);
		InputStreamReader rightInputStreamReader = new InputStreamReader(rightFileInputStream);
		leftReader = new BufferedReader(leftInputStreamReader);
		rightReader = new BufferedReader(rightInputStreamReader);

		// leftReader.skip(skip);
		// rightReader.skip(skip);
		leftReader.mark(0);
		rightReader.mark(0);
	    }

	    leftReader.reset();
	    rightReader.reset();

	    leftEvents.clear();
	    rightEvents.clear();
	    convertLinesToEvents(leftEvents, leftFile, leftReader, leftCompositeEvents);
	    convertLinesToEvents(rightEvents, rightFile, rightReader, rightCompositeEvents);

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
    private void convertLinesToEvents(List<CBPChangeEvent<?>> eventList, File file, BufferedReader bufferedReader, Map<String, Set<CBPChangeEvent<?>>> compositeEvents)
	    throws IOException, FactoryConfigurationError, XMLStreamException {

	StringBuilder sb = new StringBuilder();
	sb.append("<m>");
	sb.append(bufferedReader.lines().collect(Collectors.joining()));
	sb.append("</m>");

	convertLinesToEvents(eventList, file, sb.toString(), compositeEvents);
    }

    private void convertLinesToEvents(List<CBPChangeEvent<?>> eventList, File cbpFile, String lines, Map<String, Set<CBPChangeEvent<?>>> compositeEvents)
	    throws FactoryConfigurationError, XMLStreamException {
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
				String packageName = e.getAttributeByName(new QName("epackage")).getValue();
				event = new CBPCreateEObjectEvent(className, cbpFile.getAbsolutePath(), id, packageName);
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
				String packageName = e.getAttributeByName(new QName("epackage")).getValue();
				event = new CBPDeleteEObjectEvent(className, cbpFile.getAbsolutePath(), id, packageName);

			    }
				break;
			    }
			    event.setLineNumber(lineNumber);

			    if (e.getAttributeByName(new QName("composite")) != null) {
				String composite = e.getAttributeByName(new QName("composite")).getValue();
				event.setComposite(composite);
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

			if (event.getComposite() != null && event.getComposite().length() > 0) {
			    String id = event.getComposite();
			    Set<CBPChangeEvent<?>> events = compositeEvents.get(id);
			    if (events == null) {
				events = new LinkedHashSet<>();
				compositeEvents.put(id, events);
			    }
			    events.add(event);
			}

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
	conflictStrings = new ArrayList<>();

	List<String> set = new ArrayList<>();
	String str = null;
	int num = 0;
	CBPChangeEventSortComparator comparator = new CBPChangeEventSortComparator();
	// System.out.println("CHANGE-BASED CONFLICTS:");

	for (CBPConflict conflict : conflicts) {

	    CBPChangeEvent<?> leftFirstEvent = null;
	    CBPChangeEvent<?> leftLastEvent = null;
	    CBPChangeEvent<?> rightFirstEvent = null;
	    CBPChangeEvent<?> rightLastEvent = null;

	    num++;
	    List<CBPChangeEvent<?>> leftEvents = new ArrayList<>(conflict.getLeftEvents());
	    List<CBPChangeEvent<?>> rightEvents = new ArrayList<>(conflict.getRightEvents());
	    Collections.sort(leftEvents, comparator);
	    Collections.sort(rightEvents, comparator);
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

		if (leftEvent != null) {
		    if (leftFirstEvent == null) {
			leftFirstEvent = leftEvent;
		    }
		    leftLastEvent = leftEvent;
		}

		if (rightEvent != null) {
		    if (rightFirstEvent == null) {
			rightFirstEvent = rightEvent;
		    }
		    rightLastEvent = rightEvent;
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

		Set<String> levents = leftEventStrings.get(num);
		if (levents == null) {
		    levents = new LinkedHashSet<>();
		    levents.add(leftString.trim());
		    leftEventStrings.put(num, levents);
		} else {
		    levents.add(leftString.trim());
		}

		Set<String> revents = rightEventStrings.get(num);
		if (revents == null) {
		    revents = new LinkedHashSet<>();
		    revents.add(rightString.trim());
		    rightEventStrings.put(num, revents);
		} else {
		    revents.add(rightString.trim());
		}
	    }

	    String leftStr = "";
	    String rightStr = "";
	    // if (leftLastEvent instanceof CBPDeleteEObjectEvent &&
	    // rightLastEvent instanceof CBPDeleteEObjectEvent) {
	    // leftStr = convertChangeEventsToSingleChangeEvent(leftFirstEvent,
	    // leftLastEvent, CBPSide.LEFT);
	    // conflictStrings.add(leftStr + " <-> " );
	    // rightStr =
	    // convertChangeEventsToSingleChangeEvent(rightFirstEvent,
	    // rightLastEvent, CBPSide.RIGHT);
	    // conflictStrings.add(" <-> " + rightStr);
	    // } else {
	    // if (leftLastEvent instanceof CBPDeleteEObjectEvent) {
	    // leftStr = convertChangeEventsToSingleChangeEvent(leftFirstEvent,
	    // leftLastEvent, CBPSide.LEFT);
	    // conflictStrings.add(leftStr + " <-> " + rightStr);
	    // } else if (rightLastEvent instanceof CBPDeleteEObjectEvent) {
	    // rightStr =
	    // convertChangeEventsToSingleChangeEvent(rightFirstEvent,
	    // rightLastEvent, CBPSide.RIGHT);
	    // conflictStrings.add(leftStr + " <-> " + rightStr);
	    // } else {
	    rightStr = convertChangeEventsToSingleChangeEvent(rightFirstEvent, rightLastEvent, CBPSide.RIGHT);
	    leftStr = convertChangeEventsToSingleChangeEvent(leftFirstEvent, leftLastEvent, CBPSide.LEFT);
	    if (conflictStrings.add(leftStr.trim() + " <-> " + rightStr.trim()) == false) {
		System.console();
	    }
	    // }
	    // }

	}
	conflictCount = conflicts.size();
	System.out.println("Change-based Conflicts size = " + conflicts.size());
    }

    /**
     * @param firstEvent
     * @param lastEvent
     * @param rightFirstEvent
     * @return
     */
    private String convertChangeEventsToSingleChangeEvent(CBPChangeEvent<?> firstEvent, CBPChangeEvent<?> lastEvent, CBPSide side) {
	String str = "";
	// left string

	String target = "";
	String value = null;
	String feature = "";
	String oldValue = null;
	String from = "";
	String to = "";

	// SET EREFERENCE
	if (firstEvent instanceof CBPSingleValueEReferenceEvent && lastEvent instanceof CBPSingleValueEReferenceEvent
		&& ((CBPSingleValueEReferenceEvent) firstEvent).getTarget().equals(((CBPSingleValueEReferenceEvent) lastEvent).getTarget())
		&& ((CBPSingleValueEReferenceEvent) firstEvent).getEStructuralFeature().equals(((CBPSingleValueEReferenceEvent) lastEvent).getEStructuralFeature())) {
	    target = ((CBPSingleValueEReferenceEvent) firstEvent).getTarget();
	    if (firstEvent.getOldValue() != null)
		oldValue = firstEvent.getOldValue().toString();
	    if (lastEvent.getValue() != null)
		value = lastEvent.getValue().toString();
	    feature = ((CBPSingleValueEReferenceEvent) firstEvent).getEStructuralFeature();

	    str = "SET " + target + "." + feature + " FROM " + oldValue + " TO " + value;
	} else

	// SET EATTRIBUTE
	if (firstEvent instanceof CBPSingleValueEAttributeEvent && lastEvent instanceof CBPSingleValueEAttributeEvent) {
	    target = ((CBPSingleValueEAttributeEvent) firstEvent).getTarget();
	    oldValue = String.valueOf(firstEvent.getOldValue());
	    value = String.valueOf(lastEvent.getValue());
	    feature = ((CBPSingleValueEAttributeEvent) firstEvent).getEStructuralFeature();

	    str = "SET " + target + "." + feature + " FROM " + oldValue + " TO " + value;
	} else

	// MOVE WITHIN EREFERENCE
	if (firstEvent instanceof CBPMoveWithinEReferenceEvent && lastEvent instanceof CBPMoveWithinEReferenceEvent
		&& (((CBPEReferenceEvent) firstEvent).getTarget().equals(((CBPEReferenceEvent) lastEvent).getTarget())
			&& ((CBPEReferenceEvent) firstEvent).getEStructuralFeature().equals(((CBPEReferenceEvent) lastEvent).getEStructuralFeature()))) {
	    target = ((CBPMoveWithinEReferenceEvent) lastEvent).getTarget();
	    value = ((CBPMoveWithinEReferenceEvent) lastEvent).getValue().toString();
	    feature = ((CBPMoveWithinEReferenceEvent) lastEvent).getEStructuralFeature();

	    CBPMatchObject valueObj = objects.get(value);
	    if (side == CBPSide.LEFT) {
		from = String.valueOf(valueObj.getOldLeftPosition());
		to = String.valueOf(valueObj.getLeftPosition());
	    } else {
		from = String.valueOf(valueObj.getOldRightPosition());
		to = String.valueOf(valueObj.getRightPosition());
	    }

	    str = "MOVE " + value + " IN " + target + "." + feature + " FROM " + from + " TO " + to;
	} else

	// MOVE WITHIN EATTRIBUTE
	if (firstEvent instanceof CBPMoveWithinEAttributeEvent && lastEvent instanceof CBPMoveWithinEAttributeEvent) {
	    target = ((CBPMoveWithinEAttributeEvent) lastEvent).getTarget();
	    value = ((CBPMoveWithinEAttributeEvent) lastEvent).getValue().toString();
	    feature = ((CBPMoveWithinEAttributeEvent) lastEvent).getEStructuralFeature();
	    from = String.valueOf(((CBPMoveWithinEAttributeEvent) firstEvent).getFromPosition());
	    to = String.valueOf(((CBPMoveWithinEAttributeEvent) lastEvent).getPosition());

	    str = "MOVE " + value + " IN " + target + "." + feature + " FROM " + from + " TO " + to;
	} else

	// MOVE CROSS-CONTAINER
	if (firstEvent instanceof CBPEReferenceEvent && lastEvent instanceof CBPEReferenceEvent && (!((CBPEReferenceEvent) firstEvent).getTarget().equals(((CBPEReferenceEvent) lastEvent).getTarget())
		|| !((CBPEReferenceEvent) firstEvent).getEStructuralFeature().equals(((CBPEReferenceEvent) lastEvent).getEStructuralFeature()))) {

	    String firstString = "";
	    String lastString = "";

	    // FIRST EVENT
	    if (firstEvent instanceof CBPSingleValueEReferenceEvent) {
		target = ((CBPSingleValueEReferenceEvent) firstEvent).getTarget();
		if (firstEvent.getOldValue() != null)
		    value = firstEvent.getOldValue().toString();
		feature = ((CBPSingleValueEReferenceEvent) firstEvent).getEStructuralFeature();
		firstString = target + "." + feature;
	    } else if (firstEvent instanceof CBPMultiValueEReferenceEvent) {
		value = firstEvent.getValue().toString();

		if (value.equals("O-16248")) {
		    System.console();
		}

		CBPMatchObject temp = objects.get(lastEvent.getValue().toString());
		if (temp instanceof CBPMatchObject) {
		    if (temp.getContainer(side) instanceof CBPMatchObject) {
			target = temp.getOldContainer(side).getId();
		    } else {
			target = ((CBPMultiValueEReferenceEvent) firstEvent).getTarget();
		    }
		} else {
		    target = ((CBPMultiValueEReferenceEvent) firstEvent).getTarget();
		}
		if (temp instanceof CBPMatchObject) {
		    if (temp.getContainingFeature(side) instanceof CBPMatchFeature) {
			feature = temp.getOldContainingFeature(side).getName();
		    } else {
			feature = ((CBPMultiValueEReferenceEvent) firstEvent).getEStructuralFeature();
		    }
		} else {
		    feature = ((CBPMultiValueEReferenceEvent) firstEvent).getEStructuralFeature();
		}

		CBPMatchObject valueObj = objects.get(lastEvent.getValue().toString());
		// CBPMatchObject valueObj = objects.get(value);
		if (side == CBPSide.LEFT) {
		    from = String.valueOf(valueObj.getOldLeftPosition());
		} else {

		    // if (valueObj.getId().equals("O-14396")){
		    // System.console();
		    // }
		    from = String.valueOf(valueObj.getOldRightPosition());
		}

		// from = String.valueOf(((CBPMultiValueEReferenceEvent)
		// firstEvent).getPosition());
		firstString = target + "." + feature + "." + from;
	    } else {
		System.console();
	    }

	    // LAST EVENT
	    if (lastEvent instanceof CBPSingleValueEReferenceEvent) {
		target = ((CBPSingleValueEReferenceEvent) lastEvent).getTarget();
		if (lastEvent.getOldValue() != null)
		    value = lastEvent.getValue().toString();
		feature = ((CBPSingleValueEReferenceEvent) lastEvent).getEStructuralFeature();
		lastString = target + "." + feature;
	    } else if (lastEvent instanceof CBPMultiValueEReferenceEvent) {
		value = lastEvent.getValue().toString();

		if (value.equals("O-16248")) {
		    System.console();
		}

		CBPMatchObject temp = objects.get(value);
		if (temp instanceof CBPMatchObject) {
		    if (temp.getContainer(side) instanceof CBPMatchObject) {
			target = temp.getContainer(side).getId();
		    } else {
			target = ((CBPMultiValueEReferenceEvent) lastEvent).getTarget();
		    }
		} else {
		    target = ((CBPMultiValueEReferenceEvent) lastEvent).getTarget();
		}
		if (temp instanceof CBPMatchObject) {
		    if (temp.getContainingFeature(side) instanceof CBPMatchFeature) {
			feature = temp.getContainingFeature(side).getName();
		    } else {
			feature = ((CBPMultiValueEReferenceEvent) lastEvent).getEStructuralFeature();
		    }
		} else {
		    feature = ((CBPMultiValueEReferenceEvent) lastEvent).getEStructuralFeature();
		}

		CBPMatchObject valueObj = objects.get(value);
		if (side == CBPSide.LEFT) {
		    to = String.valueOf(valueObj.getLeftPosition());
		} else {
		    to = String.valueOf(valueObj.getRightPosition());
		}

		// to = String.valueOf(((CBPMultiValueEReferenceEvent)
		// lastEvent).getPosition());
		lastString = target + "." + feature + "." + to;
	    } else {
		System.console();
	    }

	    str = "MOVE " + value + " ACROSS FROM " + firstString + " TO " + lastString;
	} else

	// DELETE
	if (lastEvent instanceof CBPDeleteEObjectEvent) {
	    value = ((CBPDeleteEObjectEvent) lastEvent).geteObject();
	    CBPMatchObject deletedElement = objects.get(value);
	    if (side == CBPSide.LEFT) {
		target = deletedElement.getOldLeftContainer().getId();
		feature = deletedElement.getOldLeftContainingFeature().getName();
		from = String.valueOf(deletedElement.getOldLeftPosition());
	    } else {
		target = deletedElement.getOldRightContainer().getId();
		feature = deletedElement.getOldRightContainingFeature().getName();
		from = String.valueOf(deletedElement.getOldRightPosition());
	    }

	    str = "DELETE " + value + " FROM " + target + "." + feature + " AT " + from;
	} else {
	    str = firstEvent.toString();
	}

	return str;
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
		Map<Integer, Object> oldValues = feature.getOldLeftValues();
		int leftSize = leftValues.size();
		int rightSize = rightValues.size();
		int oldSize = oldValues.size();
		// System.out.println("+--" + feature.getName() + "; left size =
		// " + leftSize + ", right size = " + rightSize);
		writer.println("+--" + feature.getName() + ", " + feature.isContainment() + "; left size = " + leftSize + ", right size = " + rightSize + ", old size = " + oldSize);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = (valueEntry.getValue() instanceof CBPMatchObject) ? ((CBPMatchObject) valueEntry.getValue()).getId() : valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);
		    rightValue = (rightValue instanceof CBPMatchObject) ? ((CBPMatchObject) rightValue).getId() : rightValue;
		    Object oldValue = oldValues.get(pos);
		    oldValue = (oldValue instanceof CBPMatchObject) ? ((CBPMatchObject) oldValue).getId() : oldValue;
		    // System.out.println("+--+-- " + pos + "; left value = " +
		    // leftValue + ", right value = " + rightValue);
		    writer.println("+--+-- " + pos + "; left value = " + leftValue + ", right value = " + rightValue + ", old value = " + oldValue);
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
