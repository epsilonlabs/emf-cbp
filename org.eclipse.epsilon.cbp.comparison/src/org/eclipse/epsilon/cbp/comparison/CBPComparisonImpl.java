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
import org.eclipse.epsilon.cbp.comparison.CBPObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPCreateEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPDeleteEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEAttributeEvent;
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
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResource.IdType;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;

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
    private Map<String, CBPObject> objects = null;

    private File objectTreeFile = new File("D:\\TEMP\\COMPARISON2\\test\\object tree.txt");
    private File diffEMFCompareFile = new File("D:\\TEMP\\COMPARISON2\\test\\left.txt");

    private List<ICBPObjectTreePostProcessor> objectTreePostProcessors = new ArrayList<>();

    public void compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	this.compare(leftFile, rightFile, null);
    }

    public void compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException {
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

	System.out.print("Loading Left/Local Model");
	start = System.nanoTime();

	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	ResourceSet resourceSet = new ResourceSetImpl();
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

	File leftResourceFile = new File(leftFile.getAbsolutePath().replaceAll("cbpxml", "xmi"));
	leftResource = resourceSet.createResource(URI.createFileURI(leftResourceFile.getAbsolutePath()));
	leftHybridResource = new HybridXMIResourceImpl(leftResource, new FileOutputStream(leftFile, true));
	leftHybridResource.setIdType(IdType.UUID);
	leftHybridResource.load(options);

	File rightResourceFile = new File(rightFile.getAbsolutePath().replaceAll("cbpxml", "xmi"));
	rightResource = resourceSet.createResource(URI.createFileURI(rightResourceFile.getAbsolutePath()));
	rightHybridResource = new HybridXMIResourceImpl(rightResource, new FileOutputStream(rightFile, true));
	rightHybridResource.setIdType(IdType.UUID);
	rightHybridResource.load(options);

	end = System.nanoTime();
	System.out.println(" = " + df.format(((end - start) / 1000000.0)) + " ms");

	System.out.print("Construct Object Tree ");
	start = System.nanoTime();
	System.out.print("LEFT ");
	this.contructObjectTree(leftEvents, CBPSide.LEFT);
	System.out.print("RIGHT ");
	this.contructObjectTree(rightEvents, CBPSide.RIGHT);
	end = System.nanoTime();
	System.out.println("= " + df.format(((end - start) / 1000000.0)) + " ms");

	System.out.print("Post-process Object Tree");
	start = System.nanoTime();
	for (ICBPObjectTreePostProcessor postProcessor : objectTreePostProcessors) {
	    postProcessor.process(objects);
	}
	end = System.nanoTime();
	System.out.println(" = " + df.format(((end - start) / 1000000.0)) + " ms");

	System.out.print("Determine Differences");
	start = System.nanoTime();
	this.determineDifferences(CBPSide.LEFT);
	end = System.nanoTime();
	System.out.println(" = " + df.format(((end - start) / 1000000.0)) + " ms");

	// System.out.println("\nOBJECT TREE:");
	// System.out.println("Object Tree Size = " + objects.size());
	// printObjectTree();

	//
	// System.out.println("\nDIFFERENCES:");
	printDifferences();

	// System.out.println("\nEXPORT FOR COMPARISON WITH EMF COMPARE:");
	this.exportForComparisonWithEMFCompare();
	//
	{
	    String a = "O-470";
	    EObject eObject = leftResource.getEObject(a);
	    if (eObject != null) {
		EStructuralFeature eFeature = eObject.eContainingFeature();
		boolean isOrdered = eFeature.isOrdered();
		EList<EObject> eList = (EList<EObject>) eObject.eContainer().eGet(eFeature);
		int pos = eList.indexOf(eObject);
		System.out.println("Left Pos = " + pos);
	    }
	}

	{
	    String b = "O-470";
	    EObject eObject = rightResource.getEObject(b);
	    if (eObject != null) {
		EStructuralFeature eFeature = eObject.eContainingFeature();
		boolean isOrdered = eFeature.isOrdered();
		EList<EObject> eList = (EList<EObject>) eObject.eContainer().eGet(eFeature);
		int pos = eList.indexOf(eObject);
		System.out.println("Right Pos = " + pos);
	    }
	    System.out.println();
	}

	// closing
	// leftResource.unload();
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
    protected void determineDifferences(CBPSide referenceSide) {

	Iterator<Entry<String, CBPObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPObject> objectEntry = iterator.next();
	    CBPObject object = objectEntry.getValue();

	    if (object.getId().equals("O-109753")) {
		System.out.println();
	    }

	    // System.out.println(object.getId());

	    // iterate through all features
	    for (Entry<String, CBPFeature> featureEntry : object.getFeatures().entrySet()) {
		CBPFeature feature = featureEntry.getValue();

		Map<Integer, Object> leftValues = feature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = feature.getValues(CBPSide.RIGHT);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);

		    if (leftValue instanceof CBPObject) {
			if (((CBPObject) leftValue).getId().equals("O-471")) {
			    System.out.println();
			}
		    }

		    if (rightValue instanceof CBPObject) {
			if (((CBPObject) rightValue).getId().equals("O-471")) {
			    System.out.println();
			}
		    }

		    if (leftValue == null && rightValue == null) {
			continue;
		    } else if (leftValue != null && rightValue != null && leftValue.equals(rightValue)) {
			continue;
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.isContainment()) {

			// if (leftValue != null)
			// processValueObject(((CBPObject) leftValue));
			// if (rightValue != null)
			// processValueObject(((CBPObject) rightValue));

			if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
			    if (((CBPObject) rightValue).getLeftContainer() != null) {
				continue;
			    }

			    if (((CBPObject) rightValue).getLeftContainer() == null) {
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, pos, rightValue), CBPSide.RIGHT);
				CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);
			    }
			    if (((CBPObject) leftValue).getRightContainer() == null) {
				feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, pos, leftValue), CBPSide.RIGHT);
				CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);
			    }
			    continue;
			} else if (leftValue != null && rightValue == null) {
			    if (!((CBPObject) leftValue).getLeftIsCreated() && !((CBPObject) leftValue).getRightIsDeleted()) {
				continue;
			    } else if (((CBPObject) leftValue).getLeftIsDeleted() && ((CBPObject) leftValue).getRightIsDeleted()) {
				continue;
			    } else {
				CBPDiff diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
				diffs.add(diff);
			    }
			    continue;
			} else if (leftValue == null && rightValue != null) {
			    if (!((CBPObject) rightValue).getRightIsCreated() && !((CBPObject) rightValue).getRightIsDeleted()) {
				// this type of condition will be handled by the
				// respective object of the right value on the
				// left side, since if this one also handle the
				// condition there will be double move diffs for
				// the same move
				continue;
			    } else {
				// feature.addAdjustPositionEvent(new
				// CBPDiffPositionEvent(CBPPositionEventType.REMOVE,
				// pos, rightValue), CBPSide.RIGHT);
				CBPDiff diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
				diffs.add(diff);
			    }

			    continue;
			}
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && !feature.isContainment() && feature.isMany()) {
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
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && !feature.isContainment() && !feature.isMany()) {
			Object value = (CBPObject) feature.getValue(CBPSide.LEFT);
			if (value == null) {
			    value = feature.getOldValue(CBPSide.LEFT);
			    if (value == null) {
				value = feature.getValue(CBPSide.RIGHT);
				if (value == null) {
				    value = feature.getOldValue(CBPSide.RIGHT);
				}
			    }
			}

			if (object.getLeftContainer() != null && object.getLeftContainer().equals(value) && object.getLeftIsDeleted() && ((CBPObject) value).getLeftIsDeleted()) {
			    continue;
			}

			CBPDiff diff = new CBPDiff(object, feature, pos, value, CBPDifferenceKind.CHANGE, referenceSide);
			diffs.add(diff);
			continue;
		    }
		    // ---
		    else if (!object.getLeftIsDeleted() && feature.getFeatureType() == CBPFeatureType.ATTRIBUTE && feature.isMany()) {
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
		    // ---
		    else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()
			    && feature.getFeatureType() == CBPFeatureType.ATTRIBUTE && !feature.isMany()) {
			// if (object.getLeftContainer() != null) {
			Object value = leftValue;
			if (value == null) {
			    value = feature.getOldValue(CBPSide.LEFT);
			    if (value == null) {
				value = feature.getOldValue(CBPSide.RIGHT);
				if (value == null) {
				    feature.getValue(CBPSide.RIGHT);
				}
			    }
			}
			CBPDiff diff = new CBPDiff(object, feature, pos, value, CBPDifferenceKind.CHANGE, referenceSide);
			diffs.add(diff);
			// }
			continue;
		    }
		    // ---
		    else {
			continue;
		    }
		}

	    }
	}

	iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPObject> objectEntry = iterator.next();
	    CBPObject object = objectEntry.getValue();
	    processValueObject(object);
	}
    }

    protected void processValueObject(CBPObject object) {

	if (object.getId().equals("O-471")) {
	    System.out.println();
	}

	if (object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
	    CBPObject container = object.getRightContainer();
	    CBPFeature feature = object.getRightContainingFeature();
	    int position = object.getRightPosition();
	    if (container == null) {
		container = object.getLeftContainer();
		feature = object.getLeftContainingFeature();
		position = object.getLeftPosition();
	    }
	    // feature.addAdjustPositionEvent(new
	    // CBPDiffPositionEvent(CBPPositionEventType.ADD, position, object),
	    // CBPSide.RIGHT);
	    CBPDiff diff = new CBPDiff(container, feature, position, object, CBPDifferenceKind.ADD, CBPSide.LEFT);
	    diffs.add(diff);
	    return;
	} else if (!object.getLeftIsCreated() && object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
	    CBPObject container = object.getRightContainer();
	    CBPFeature feature = object.getRightContainingFeature();
	    int position = object.getRightPosition();
	    if (container == null) {
		container = object.getLeftContainer();
		feature = object.getLeftContainingFeature();
		position = object.getLeftPosition();
	    }
	    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.REMOVE, position, object), CBPSide.RIGHT);
	    CBPDiff diff = new CBPDiff(container, feature, position, object, CBPDifferenceKind.DELETE, CBPSide.LEFT);
	    diffs.add(diff);
	    return;
	} else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && object.getRightIsDeleted()) {
	    CBPObject container = object.getLeftContainer();
	    CBPFeature feature = object.getLeftContainingFeature();
	    int position = object.getLeftPosition();
	    feature.addAdjustPositionEvent(new CBPDiffPositionEvent(CBPPositionEventType.ADD, position, object), CBPSide.RIGHT);
	    CBPDiff diff = new CBPDiff(container, feature, position, object, CBPDifferenceKind.ADD, CBPSide.LEFT);
	    diffs.add(diff);
	    return;
	} else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && object.getRightIsCreated() && !object.getRightIsDeleted()) {

	    CBPObject container = object.getRightContainer();
	    CBPFeature feature = object.getRightContainingFeature();
	    int position = object.getRightPosition();
	    if (container == null) {
		container = object.getLeftContainer();
		feature = object.getLeftContainingFeature();
		position = object.getLeftPosition();
	    }
	    // feature.addAdjustPositionEvent(new
	    // CBPDiffPositionEvent(CBPPositionEventType.REMOVE, position,
	    // object), CBPSide.RIGHT);
	    CBPDiff diff = new CBPDiff(container, feature, position, object, CBPDifferenceKind.DELETE, CBPSide.LEFT);
	    diffs.add(diff);
	    return;
	} else if (!object.getLeftIsCreated() && !object.getLeftIsDeleted() && !object.getRightIsCreated() && !object.getRightIsDeleted()) {
	    CBPObject leftContainer = object.getLeftContainer();
	    CBPObject rightContainer = object.getRightContainer();
	    if (leftContainer != null && rightContainer != null && object.getLeftContainingFeature().isContainment()) {
		CBPFeature feature = object.getLeftContainingFeature();
		int position = object.getLeftPosition();

		if (!object.getLeftContainer().equals(object.getRightContainer()) || !object.getLeftContainingFeature().equals(object.getRightContainingFeature())) {
		    adjustRightPosition(object, feature);
		    CBPDiff diff = new CBPDiff(leftContainer, feature, position, object, CBPDifferenceKind.MOVE, CBPSide.LEFT);
		    diffs.add(diff);
		} else if (object.getRightPosition() == object.getLeftPosition()) {
		    return;
		} else if (object.getRightPosition() != -1) {
		    adjustRightPosition(object, feature);
		    if (position != object.getRightPosition()) {
			CBPDiff diff = new CBPDiff(leftContainer, feature, position, object, CBPDifferenceKind.MOVE, CBPSide.LEFT);
			diffs.add(diff);
		    }

		    if (object.getId().equals("O-467")) {
			System.out.println();
		    }

		} else if (object.getOldLeftPosition() != -1) {
		    adjustRightPosition(object, feature);
		    if (position != object.getOldLeftPosition()) {
			CBPDiff diff = new CBPDiff(leftContainer, feature, position, object, CBPDifferenceKind.MOVE, CBPSide.LEFT);
			diffs.add(diff);
		    }
		} else if (object.getOldRightPosition() != -1) {
		    adjustRightPosition(object, feature);
		    if (position != object.getOldRightPosition()) {
			CBPDiff diff = new CBPDiff(leftContainer, feature, position, object, CBPDifferenceKind.MOVE, CBPSide.LEFT);
			diffs.add(diff);
		    }
		}
		// continue;
	    } else if (leftContainer != null && rightContainer == null) {
		CBPFeature feature = object.getLeftContainingFeature();
		int position = object.getLeftPosition();
		if (object.getLeftPosition() != object.getOldLeftPosition()) {
		    CBPDiff diff = new CBPDiff(leftContainer, feature, position, object, CBPDifferenceKind.MOVE, CBPSide.LEFT);
		    diffs.add(diff);
		}
	    }
	}
    }

    /**
     * @param object
     * @param feature
     */
    protected void adjustRightPosition(CBPObject object, CBPFeature feature) {
	// ADJUST POSITION BECAUSE OF DIFFERENCE STRATEGY - BEGIN
	int oldRightPosition = object.getRightPosition();
	int rightPosition = feature.updatePosition(oldRightPosition, CBPSide.RIGHT);
	object.setRightPosition(rightPosition);
	// ADJUST POSITION BECAUSE OF DIFFERENCE STRATEGY - END
    }

    protected void printDifferences() {
	for (CBPDiff diff : diffs) {
	    System.out.println(diff.toString());
	}
	System.out.println("Diffs size = " + diffs.size());
    }

    protected void exportForComparisonWithEMFCompare() throws IOException {
	Set<String> set = new HashSet<>();
	String str = null;
	for (CBPDiff diff : diffs) {
	    if (diff.getValue() instanceof CBPObject) {
		str = diff.getObject().getId() + "." + diff.getFeature().getName() + "." + ((CBPObject) diff.getValue()).getId() + "." + diff.getKind();
	    } else {
		str = diff.getObject().getId() + "." + diff.getFeature().getName() + "." + diff.getValue() + "." + diff.getKind();
	    }
	    set.add(str.trim());
	}
	List<String> list = new ArrayList<>(set);
	Collections.sort(list);

	FileOutputStream output = new FileOutputStream(diffEMFCompareFile);
	for (String item : list) {
	    // System.out.println(item);
	    if (item.trim() == "")
		continue;
	    output.write(item.getBytes());
	    output.write(System.lineSeparator().getBytes());
	}
	output.close();
	System.out.println("Diffs size = " + list.size());
    }

    /**
     * @throws FileNotFoundException
     * 
     */
    protected void printObjectTree() throws FileNotFoundException {
	// Check
	PrintWriter writer = new PrintWriter(objectTreeFile);
	Iterator<Entry<String, CBPObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPObject> objectEntry = iterator.next();
	    CBPObject object = objectEntry.getValue();
	    writer.println(objectEntry.getKey() + " : " + object.isCreated(CBPSide.LEFT) + ", " + object.isDeleted(CBPSide.LEFT) + " : " + object.isCreated(CBPSide.RIGHT) + ", "
		    + object.isDeleted(CBPSide.RIGHT));
	    // System.out.println(objectEntry.getKey() + " : " +
	    // object.isCreated(CBPSide.LEFT) + ", " +
	    // object.isDeleted(CBPSide.LEFT) + " : " +
	    // object.isCreated(CBPSide.RIGHT) + ", "
	    // + object.isDeleted(CBPSide.RIGHT));
	    for (Entry<String, CBPFeature> featureEntry : object.getFeatures().entrySet()) {
		CBPFeature feature = featureEntry.getValue();
		Map<Integer, Object> leftValues = feature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = feature.getValues(CBPSide.RIGHT);
		int leftSize = leftValues.size();
		int rightSize = rightValues.size();
		// System.out.println("+--" + feature.getName() + "; left size =
		// " + leftSize + ", right size = " + rightSize);
		writer.println("+--" + feature.getName() + "; left size = " + leftSize + ", right size = " + rightSize);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = (valueEntry.getValue() instanceof CBPObject) ? ((CBPObject) valueEntry.getValue()).getId() : valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);
		    rightValue = (rightValue instanceof CBPObject) ? ((CBPObject) rightValue).getId() : rightValue;
		    // System.out.println("+--+-- " + pos + "; left value = " +
		    // leftValue + ", right value = " + rightValue);
		    writer.println("+--+-- " + pos + "; left value = " + leftValue + ", right value = " + rightValue);
		}
	    }
	}
	writer.close();
    }

    private void contructObjectTree(List<CBPChangeEvent<?>> events, CBPSide side) {
	CBPObject resource = objects.get(CBPResource.RESOURCE_STRING);
	if (resource == null) {
	    resource = new CBPResource(CBPResource.RESOURCE_STRING);
	    objects.put(CBPResource.RESOURCE_STRING, resource);
	}

	// get resource feature
	CBPFeature resourceFeature = ((CBPResource) resource).getResourceFeature();

	CBPChangeEvent<?> previousEvent = null;

	for (CBPChangeEvent<?> event : events) {

	    // get target id
	    String targetId = null;
	    if (event instanceof CBPEStructuralFeatureEvent<?>) {
		targetId = ((CBPEStructuralFeatureEvent<?>) event).getTarget();
	    } else if (!(event instanceof CBPStartNewSessionEvent)) {
		targetId = event.getValue();
	    } else if (event instanceof CBPStartNewSessionEvent) {
		continue;
	    }

	    if (targetId.equals("L-100")) {
		System.out.println();
	    }

	    // get target object
	    CBPObject targetObject = objects.get(targetId);
	    if (targetObject == null) {
		targetObject = new CBPObject(targetId);
		objects.put(targetId, targetObject);
	    }

	    // get value object
	    CBPObject valueObject = null;
	    String valueId = null;
	    if (event instanceof CBPEReferenceEvent) {
		valueId = event.getValue();
		if (valueId != null) {

		    if (valueId.equals("O-109764")) {
			System.out.println();
		    }

		    // get or create new object
		    valueObject = objects.get(valueId);
		    if (valueObject == null) {
			valueObject = new CBPObject(valueId);
			objects.put(valueId, valueObject);
		    }

		    // try to get the left-side container and feature of the
		    // added value object
		    if (valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(valueObject);
		    }
		    // ---

		}
	    }

	    // get value literal
	    Object valueLiteral = null;
	    if (event instanceof CBPEAttributeEvent) {
		valueLiteral = event.getValue();
	    }

	    // get affected feature
	    CBPFeature feature = null;
	    feature = createCBPFeature(event, targetObject, feature);

	    // get position
	    int position = event.getPosition();
	    // if (targetObject.getPosition(side) == position) {
	    // continue;
	    // }

	    // if (side == CBPSide.RIGHT && targetId.equals("O-355")) {
	    // System.out.println(feature.getRightValues());
	    // }

	    // start processing events
	    if (event instanceof CBPCreateEObjectEvent) {
		targetObject.setCreated(true, side);
	    } else

	    if (event instanceof CBPDeleteEObjectEvent) {
		targetObject.setDeleted(true, side);
		if (previousEvent instanceof CBPUnsetEReferenceEvent) {
		    CBPUnsetEReferenceEvent unsetEvent = (CBPUnsetEReferenceEvent) previousEvent;
		    CBPObject previousTargetObject = objects.get(unsetEvent.getTarget());
		    CBPFeature previousFeature = previousTargetObject.getFeatures().get(unsetEvent.getEReference());
		    CBPObject previousValueObject = objects.get(event.getValue());
		    targetObject.setContainer(previousTargetObject, side);
		    targetObject.setContainingFeature(previousFeature, side);
		    if (previousValueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(previousValueObject);
		    }
		} else if (previousEvent instanceof CBPRemoveFromEReferenceEvent) {

		    if (targetObject.getId().equals("O-109764")) {
			System.out.println();
		    }

		    CBPRemoveFromEReferenceEvent removeEvent = (CBPRemoveFromEReferenceEvent) previousEvent;
		    CBPObject previousTargetObject = objects.get(removeEvent.getTarget());
		    CBPFeature previousFeature = previousTargetObject.getFeatures().get(removeEvent.getEReference());
		    CBPObject previousValueObject = objects.get(removeEvent.getValue());
		    targetObject.setContainer(previousTargetObject, side);
		    targetObject.setContainingFeature(previousFeature, side);
		    // if (previousValueObject.getLeftContainer() == null &&
		    // side == CBPSide.RIGHT) {
		    if (side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(previousValueObject);
		    }
		}
	    } else

	    if (event instanceof CBPRemoveFromResourceEvent) {
		targetObject.setContainer(resource, side);
		targetObject.setContainingFeature(resourceFeature, side);
		targetObject.setPosition(position, side);
		resourceFeature.removeValue(targetObject, position, side);
	    } else

	    if (event instanceof CBPAddToResourceEvent) {
		targetObject.setContainer(resource, side);
		targetObject.setContainingFeature(resourceFeature, side);
		targetObject.setPosition(position, side);
		resourceFeature.addValue(targetObject, position, side);
	    } else

	    if (event instanceof CBPAddToEReferenceEvent) {
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		valueObject.setPosition(position, side);
		feature.addValue(valueObject, position, side);
	    } else

	    if (event instanceof CBPRemoveFromEReferenceEvent) {
		if (valueId.equals("O-359")) {
		    System.out.println();
		}

		if (feature.isContainment()) {
		    valueObject.setContainer(targetObject, side);
		    valueObject.setContainingFeature(feature, side);
		    valueObject.setPosition(position, side);

		    // if (valueObject.getRightContainer() == null && side ==
		    // CBPSide.LEFT) {
		    // createValueObjectOnTheOppositeSide(valueObject,
		    // CBPSide.LEFT);
		    // }
		} else {
		    if (side == CBPSide.RIGHT) {
			feature.getValues(CBPSide.LEFT).put(position, valueObject);
		    } else if (side == CBPSide.LEFT) {
			feature.getValues(CBPSide.RIGHT).put(position, valueObject);
		    }
		}
		feature.removeValue(valueObject, position, side);
	    } else

	    if (event instanceof CBPMoveWithinEReferenceEvent) {
		int fromPosition = ((CBPMoveWithinEReferenceEvent) event).getFromPosition();
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		valueObject.setOldPosition(fromPosition, side);
		valueObject.setPosition(position, side);
		feature.moveValue(valueObject, fromPosition, position, side);

		if (valueObject.getLeftContainer() == null && side == CBPSide.RIGHT) {
		    createValueObjectOnTheOppositeSide(valueObject);
		}

	    } else

	    if (event instanceof CBPAddToEAttributeEvent) {
		feature.addValue(valueLiteral, position, side);
	    } else

	    if (event instanceof CBPRemoveFromEAttributeEvent) {
		feature.removeValue(valueLiteral, position, side);

	    } else

	    if (event instanceof CBPMoveWithinEAttributeEvent) {
		int fromPosition = ((CBPMoveWithinEAttributeEvent) event).getFromPosition();
		feature.moveValue(valueLiteral, fromPosition, position, side);
	    } else

	    if (event instanceof CBPSetEReferenceEvent) {

		if (valueId.equals("O-5469")) {
		    System.out.println();
		}

		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		valueObject.setPosition(position, side);
		feature.setOldValue(event.getOldValue(), side);
		feature.setValue(valueObject, side);
	    } else

	    if (event instanceof CBPUnsetEReferenceEvent) {

		if (event.getOldValue().equals("O-107361")) {
		    System.out.println();
		}

		CBPObject oldValue = objects.get(event.getOldValue());
		if (oldValue == null) {
		    oldValue = new CBPObject(event.getOldValue());
		    objects.put(event.getOldValue(), oldValue);
		}
		feature.setOldValue(oldValue, side);
		feature.unsetValue(valueObject, side);

		if (feature.isContainment()) {
		    oldValue.setOldContainer(targetObject, side);
		    oldValue.setOldContainingFeature(feature, side);
		    oldValue.setOldPosition(0, side);

		    if (oldValue.getRightContainer() == null && side == CBPSide.LEFT) {
			createValueObjectOnTheOppositeSide(oldValue, CBPSide.LEFT);
		    }
		    if (oldValue.getLeftContainer() == null && side == CBPSide.RIGHT) {
			createValueObjectOnTheOppositeSide(oldValue, CBPSide.RIGHT);
		    }

		} else {
		    if (side == CBPSide.RIGHT) {
			feature.setValue(oldValue, CBPSide.LEFT);
		    } else if (side == CBPSide.LEFT) {
			feature.setValue(oldValue, CBPSide.RIGHT);
		    }
		}
	    } else

	    if (event instanceof CBPSetEAttributeEvent) {
		feature.setOldValue(event.getOldValue(), side);
		feature.setValue(valueLiteral, side);
	    } else

	    if (event instanceof CBPUnsetEAttributeEvent) {
		feature.setOldValue(event.getOldValue(), side);
		feature.unsetValue(valueLiteral, side);
	    }

	    previousEvent = event;
	}

    }

    /**
     * @param event
     * @param targetObject
     * @param feature
     * @return
     */
    private CBPFeature createCBPFeature(CBPChangeEvent<?> event, CBPObject targetObject, CBPFeature feature) {
	String featureName = null;
	if (event instanceof CBPEStructuralFeatureEvent<?>) {
	    featureName = ((CBPEStructuralFeatureEvent<?>) event).getEStructuralFeature();
	    String eClassName = ((CBPEStructuralFeatureEvent<?>) event).getEClass();

	    CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
	    boolean isContainment = false;
	    boolean isMany = false;

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
	    }

	    feature = targetObject.getFeatures().get(featureName);
	    if (feature == null) {
		feature = new CBPFeature(targetObject, featureName, featureType, isContainment, isMany);
		targetObject.getFeatures().put(featureName, feature);
	    }
	}
	return feature;
    }

    protected void createValueObjectOnTheOppositeSide(CBPObject valueObject) {
	createValueObjectOnTheOppositeSide(valueObject, CBPSide.RIGHT);
    }

    protected void createValueObjectOnTheOppositeSide(CBPObject valueObject, CBPSide currentSide) {

	if (!valueObject.getLeftIsCreated() && !valueObject.getRightIsCreated()) {
	    if (currentSide == CBPSide.RIGHT) {
		if (valueObject.getOldRightContainer() != null) {

		    CBPObject leftContainer = valueObject.getOldRightContainer();
		    CBPFeature rightFeature = valueObject.getOldRightContainingFeature();
		    CBPFeature leftFeature = leftContainer.getFeatures().get(rightFeature.getName());
		    if (leftFeature == null) {
			CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
			boolean isContainment = false;
			if (rightFeature.getFeatureType() == CBPFeatureType.REFERENCE) {
			    featureType = CBPFeatureType.REFERENCE;
			    if (rightFeature.isContainment()) {
				isContainment = true;
			    }
			}
			leftFeature = new CBPFeature(leftContainer, rightFeature.getName(), featureType, isContainment, rightFeature.isMany());
			leftContainer.getFeatures().put(new String(rightFeature.getName()), leftFeature);
		    }
		    int pos = valueObject.getOldRightPosition();
		    pos = leftFeature.updatePositionWhenCreatingLeftObject(pos, CBPSide.LEFT);
		    leftFeature.updateValue(valueObject, pos, CBPSide.LEFT);

		    valueObject.setPosition(pos, CBPSide.LEFT);
		    valueObject.setContainer(leftContainer, CBPSide.LEFT);
		    valueObject.setContainingFeature(leftFeature, CBPSide.LEFT);
		}
	    } else {
		if (valueObject.getOldLeftContainer() != null) {

		    CBPObject rightContainer = valueObject.getOldLeftContainer();
		    CBPFeature leftFeature = valueObject.getOldLeftContainingFeature();
		    CBPFeature rightFeature = rightContainer.getFeatures().get(leftFeature.getName());
		    if (rightFeature == null) {
			CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
			boolean isContainment = false;
			if (leftFeature.getFeatureType() == CBPFeatureType.REFERENCE) {
			    featureType = CBPFeatureType.REFERENCE;
			    if (leftFeature.isContainment()) {
				isContainment = true;
			    }
			}
			rightFeature = new CBPFeature(rightContainer, leftFeature.getName(), featureType, isContainment, leftFeature.isMany());
			rightContainer.getFeatures().put(new String(leftFeature.getName()), rightFeature);
		    }
		    int pos = valueObject.getOldLeftPosition();
		    pos = rightFeature.updatePositionWhenCreatingLeftObject(pos, CBPSide.RIGHT);
		    rightFeature.updateValue(valueObject, pos, CBPSide.RIGHT);

		    valueObject.setPosition(pos, CBPSide.RIGHT);
		    valueObject.setContainer(rightContainer, CBPSide.RIGHT);
		    valueObject.setContainingFeature(rightFeature, CBPSide.RIGHT);
		}
	    }
	}
    }

    /**
     * @param valueObject
     */
    // protected void createValueObjectOnTheLeftSide(CBPObject valueObject) {
    // EObject eObject = leftHybridResource.getEObject(valueObject.getId());
    // if (eObject != null) {
    // EObject eContainer = eObject.eContainer();
    // if (eContainer != null) {
    // String containerId =
    // leftHybridResource.getURIFragment(eObject.eContainer());
    //
    // CBPObject leftContainer = objects.get(containerId);
    // if (leftContainer == null) {
    // leftContainer = new CBPObject(containerId);
    // objects.put(containerId, leftContainer);
    // }
    //
    // EStructuralFeature eFeature = eObject.eContainingFeature();
    // CBPFeature leftFeature =
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
    // leftFeature = new CBPFeature(leftContainer, eFeature.getName(),
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
				    String seobject = e.getAttributeByName(new QName("eobject")).getValue();
				    valuesEvent.getOldValues().add(seobject);
				} else if (event instanceof CBPEAttributeEvent) {
				    CBPEAttributeEvent eAttributeEvent = (CBPEAttributeEvent) event;
				    String sliteral = e.getAttributeByName(new QName("literal")).getValue();
				    eAttributeEvent.getOldValues().add(sliteral);
				}
			    } else if (name.equals("value")) {
				if (event instanceof ICBPEObjectValuesEvent) {
				    ICBPEObjectValuesEvent valuesEvent = (ICBPEObjectValuesEvent) event;
				    String seobject = e.getAttributeByName(new QName("eobject")).getValue();
				    valuesEvent.getValues().add(seobject);
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
}
