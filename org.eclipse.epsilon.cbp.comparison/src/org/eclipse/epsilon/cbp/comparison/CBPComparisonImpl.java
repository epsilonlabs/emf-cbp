package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
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

public class CBPComparisonImpl implements ICBPComparison {

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private EPackage ePackage = null;

    private List<CBPDiff> diffs = null;
    private List<CBPChangeEvent<?>> leftEvents = null;
    private List<CBPChangeEvent<?>> rightEvents = null;
    private Map<String, CBPObject> objects = null;

    public void compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	this.compare(leftFile, rightFile, null);
    }

    public void compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	long skip = 0;
	if (originFile != null) {
	    skip = originFile.length();
	}

	this.readFiles(leftFile, rightFile, skip);
	ePackage = this.getEPackageFromFiles(leftFile, rightFile);

	diffs = new ArrayList<>();
	objects = new HashMap<>();

	long start = System.nanoTime();
	System.out.println("Construct Right-Side Object Tree");
	this.contructObjectTree(rightEvents, CBPSide.RIGHT);
	System.out.println("Construct Left-Side Object Tree");
	this.contructObjectTree(leftEvents, CBPSide.LEFT);
	System.out.println("Determine Differences");
	this.determineDifferences(CBPSide.LEFT);
	long end = System.nanoTime();
	System.out.println("Compute differences time = " + ((end - start) / 1000000000.0));

	System.out.println("\nOBJECT TREE:");
	printObjectTree();

	System.out.println("\nDIFFERENCES:");
	printDifferences();
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
	CBPDiff diff = null;

	Iterator<Entry<String, CBPObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPObject> objectEntry = iterator.next();
	    CBPObject object = objectEntry.getValue();

	    if (!object.getLeftIsCreated() && object.getLeftIsDeleted() && !object.getLeftIsCreated() && !object.getRightIsDeleted()) {
		CBPObject container = object.getLeftContainer();
		CBPFeature feature = object.getLeftContainingFeature();
		int position = object.getLeftPosition();
		diff = new CBPDiff(container, feature, position, object, CBPDifferenceKind.DELETE, CBPSide.LEFT);
		diffs.add(diff);
		continue;
	    } else if (object.getRightIsCreated() && !object.getRightIsDeleted()) {
		continue;
	    }

	    for (Entry<String, CBPFeature> featureEntry : object.getFeatures().entrySet()) {
		CBPFeature feature = featureEntry.getValue();
		Map<Integer, Object> leftValues = feature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = feature.getValues(CBPSide.RIGHT);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);

		    if (leftValue == null && rightValue == null) {
			continue;
		    } else if (leftValue != null && rightValue != null && leftValue.equals(rightValue)) {
			continue;
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.isContainment()) {
			if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
			    diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
			    diffs.add(diff);
			    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
			    diffs.add(diff);
			    continue;
			} else if (leftValue != null && rightValue == null) {
			    if (!((CBPObject) leftValue).getLeftIsCreated()) {
				diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.MOVE, referenceSide);
			    } else {
				diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
			    }
			    diffs.add(diff);
			    continue;
			} else if (leftValue == null && rightValue != null) {
			    if (!((CBPObject) rightValue).getRightIsCreated() && !((CBPObject) rightValue).getRightIsDeleted()) {
				diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.MOVE, referenceSide);
			    } else {
				diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
			    }
			    diffs.add(diff);
			    continue;
			}
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && !feature.isContainment() && feature.isMany()) {
			if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
			    diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
			    diffs.add(diff);
			    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
			    diffs.add(diff);
			    continue;
			} else if (leftValue != null && rightValue == null) {
			    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
			    diffs.add(diff);
			    continue;
			} else if (leftValue == null && rightValue != null) {
			    diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
			    diffs.add(diff);
			    continue;
			}
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.REFERENCE && !feature.isContainment() && !feature.isMany()) {
			diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
			diffs.add(diff);
			continue;
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.ATTRIBUTE && feature.isMany()) {
			if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
			    diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
			    diffs.add(diff);
			    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
			    diffs.add(diff);
			    continue;
			} else if (leftValue != null && rightValue == null) {
			    diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.ADD, referenceSide);
			    diffs.add(diff);
			    continue;
			} else if (leftValue == null && rightValue != null) {
			    diff = new CBPDiff(object, feature, pos, rightValue, CBPDifferenceKind.DELETE, referenceSide);
			    diffs.add(diff);
			    continue;
			}
		    }
		    // ---
		    else if (feature.getFeatureType() == CBPFeatureType.ATTRIBUTE && !feature.isMany()) {
			diff = new CBPDiff(object, feature, pos, leftValue, CBPDifferenceKind.CHANGE, referenceSide);
			diffs.add(diff);
			continue;
		    }
		    // ---
		    else {
			System.out.println();
		    }
		}

	    }
	}
    }

    protected void printDifferences() {
	for (CBPDiff diff : diffs) {
	    System.out.println(diff.toString());
	}
	System.out.println("Diffs size = " + diffs.size());
    }

    /**
     * 
     */
    protected void printObjectTree() {
	// Check
	Iterator<Entry<String, CBPObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPObject> objectEntry = iterator.next();
	    CBPObject object = objectEntry.getValue();
	    System.out.println(objectEntry.getKey() + " : " + object.isCreated(CBPSide.LEFT) + ", " + object.isDeleted(CBPSide.LEFT) + " : " + object.isCreated(CBPSide.RIGHT) + ", "
		    + object.isDeleted(CBPSide.RIGHT));
	    for (Entry<String, CBPFeature> featureEntry : object.getFeatures().entrySet()) {
		CBPFeature feature = featureEntry.getValue();
		Map<Integer, Object> leftValues = feature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = feature.getValues(CBPSide.RIGHT);
		int leftSize = leftValues.size();
		int rightSize = rightValues.size();
		System.out.println("+--" + feature.getName() + "; left size = " + leftSize + ", right size = " + rightSize);
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = (valueEntry.getValue() instanceof CBPObject) ? ((CBPObject) valueEntry.getValue()).getId() : valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);
		    rightValue = (rightValue instanceof CBPObject) ? ((CBPObject) rightValue).getId() : rightValue;
		    System.out.println("+--+-- " + pos + "; left value = " + leftValue + ", right value = " + rightValue);
		}
	    }
	}
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
		    // get or create new object
		    valueObject = objects.get(valueId);
		    if (valueObject == null) {
			valueObject = new CBPObject(valueId);
			objects.put(valueId, valueObject);
		    }
		}
	    }

	    // get value literal
	    Object valueLiteral = null;
	    if (event instanceof CBPEAttributeEvent) {
		valueLiteral = event.getValue();
	    }

	    // get affected feature
	    CBPFeature feature = null;
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
		    CBPObject previousTargetObject = objects.get(unsetEvent.getTarget());
		    CBPFeature previousFeature = previousTargetObject.getFeatures().get(unsetEvent.getEReference());
		    targetObject.setContainer(previousTargetObject, side);
		    targetObject.setContainingFeature(previousFeature, side);
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
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		valueObject.setPosition(position, side);
		feature.removeValue(valueObject, position, side);
	    } else

	    if (event instanceof CBPMoveWithinEReferenceEvent) {
		int fromPosition = ((CBPMoveWithinEReferenceEvent) event).getFromPosition();
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		valueObject.setPosition(position, side);
		feature.moveValue(valueObject, fromPosition, position, side);
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
		valueObject.setContainer(targetObject, side);
		valueObject.setContainingFeature(feature, side);
		valueObject.setPosition(position, side);
		feature.setValue(valueObject, side);
	    }

	    if (event instanceof CBPUnsetEReferenceEvent) {
		// valueObject.setContainer(targetObject, side);
		// valueObject.setContainingFeature(feature, side);
		// valueObject.setPosition(-1, side);
		feature.unsetValue(valueObject, side);
	    } else

	    if (event instanceof CBPSetEAttributeEvent) {
		feature.setValue(valueLiteral, side);
	    } else

	    if (event instanceof CBPUnsetEAttributeEvent) {
		feature.unsetValue(valueLiteral, side);
	    }

	    previousEvent = event;

	    // System.out.println("\nEVENT: " + event);
	    // printObjectTree();
	}

    }

    private void readFiles(File leftFile, File rightFile, long skip) {
	try {
	    long start = System.nanoTime();
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
	    long end = System.nanoTime();
	    System.out.println("Read lines until they are different time = " + ((end - start) / 1000000000.0));

	    leftReader.reset();
	    rightReader.reset();

	    leftEvents = new ArrayList<>();
	    rightEvents = new ArrayList<>();
	    start = System.nanoTime();
	    convertLinesToEvents(leftEvents, leftFile, leftReader);
	    convertLinesToEvents(rightEvents, rightFile, rightReader);
	    end = System.nanoTime();
	    System.out.println("Convert lines to events time = " + ((end - start) / 1000000000.0));

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
}