package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.bowling.Referee;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPLifeline;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPAddToResourceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPCreateEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPDeleteEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.ICBPEObjectValuesEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.comparison.event.ICBPFromPositionEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
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

public class CBPComparison {

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    private List<CBPChangeEvent<?>> leftEvents = null;
    private List<CBPChangeEvent<?>> rightEvents = null;
    private Map<String, CBPMatch> matches = null;
    private Set<String> ignoreSet = null;
    private File leftFile;
    private File rightFile;
    private File originalFile;

    public void compare(File leftFile, File rightFile) {
	this.readFiles(leftFile, rightFile, 0);
    }

    public void compare(File leftFile, File rightFile, File originFile) {
	this.leftFile = leftFile;
	this.rightFile = rightFile;
	this.originalFile = originFile;
	long skip = originFile.length();

	this.readFiles(leftFile, rightFile, skip);

	ignoreSet = new HashSet<>();
	matches = new HashMap<>();

	long start = System.nanoTime();
	this.computeDifferences(leftEvents, CBPDiffSide.LEFT);
	this.computeDifferences(rightEvents, CBPDiffSide.RIGHT);
	long end = System.nanoTime();
	System.out.println("Compute differences time = " + ((end - start) / 1000000000.0));

	System.out.println("Ignore Set size = " + ignoreSet.size());

	for (Entry<String, CBPMatch> entry : matches.entrySet()) {
	    String matchId = entry.getKey();
	    CBPMatch match = entry.getValue();
	    System.out.println(matchId);
	    for (Entry<String, CBPDiff> subentry : match.getDiffs().entrySet()) {
		String diffId = subentry.getKey();
		CBPDiff diff = subentry.getValue();
		CBPDifferenceKind leftKind = CBPDifferenceKind.UNDEFINED;
		String leftValue = "";
		CBPDifferenceKind rightKind = CBPDifferenceKind.UNDEFINED;
		String rightValue = "";
		if (diff.getLeftSide() != null) {
		    leftKind = diff.getLeftSide().getKind();
		    leftValue = diff.getLeftSide().getValue();
		}
		if (diff.getRightSide() != null) {
		    rightKind = diff.getRightSide().getKind();
		    rightValue = diff.getRightSide().getValue();
		}

		System.out.println("+-- " + diffId + "; LEFT: " + leftKind + ", " + leftValue + "; RIGHT: " + rightKind + ", " + rightValue);

	    }
	}
    }

    private void computeDifferences(List<CBPChangeEvent<?>> events, int cbpDiffSide) {
	for (int i = events.size() - 1; i >= 0; i--) {
	    CBPChangeEvent<?> event = events.get(i);

	    // // ignoring for faster iteration
	    // if (event instanceof CBPEReferenceEvent) {
	    // String target = ((CBPEReferenceEvent) event).getTarget();
	    // if (ignoreSet.contains(target)) {
	    // continue;
	    // }
	    // String value = event.getValue();
	    // if (ignoreSet.contains(value)) {
	    // continue;
	    // }
	    // } else if (event instanceof CBPEStructuralFeatureEvent) {
	    // String target = ((CBPEStructuralFeatureEvent) event).getTarget();
	    // String value = ((CBPEStructuralFeatureEvent) event).getValue();
	    // String ref = ((CBPEStructuralFeatureEvent)
	    // event).getEStructuralFeature();
	    // int pos = ((CBPEStructuralFeatureEvent) event).getPosition();
	    // if (ignoreSet.contains(target + "." + ref + "." + pos)) {
	    // continue;
	    // }
	    // }

	    // if the event is not ignored
	    if (event instanceof CBPDeleteEObjectEvent) {
		String target = ((CBPDeleteEObjectEvent) event).getValue();
		ignoreSet.add(target);

		String key = target;
		CBPMatch match = matches.get(target);
		if (match != null) {
		    match.setLife(CBPLifeline.DELETED);
		} else {
		    match = new CBPMatch(target);
		    match.setLife(CBPLifeline.DELETED);
		    matches.put(target, match);
		}
	    } else

	    if (event instanceof CBPCreateEObjectEvent) {
		String target = ((CBPCreateEObjectEvent) event).getValue();
		ignoreSet.add(target);

		String key = target;
		CBPMatch match = matches.get(target);
		if (match != null) {
		    if (match.getLife() == CBPLifeline.DEFAULT)
			match.setLife(CBPLifeline.CREATED);
		} else {
		    match = new CBPMatch(target);
		    if (match.getLife() == CBPLifeline.DEFAULT)
			match.setLife(CBPLifeline.CREATED);
		    matches.put(target, match);
		}
	    } else

	    // REFERENCE ------------------------------------------------
	    if (event instanceof CBPRemoveFromEReferenceEvent) {
		String target = ((CBPRemoveFromEReferenceEvent) event).getTarget();
		String value = ((CBPRemoveFromEReferenceEvent) event).getValue();
		String reference = ((CBPRemoveFromEReferenceEvent) event).getEReference();
		int position = ((CBPRemoveFromEReferenceEvent) event).getPosition();

		String key = target + "." + reference + "." + position + "." + value;
		createMatchAndDiff(key, target, reference, value, position, CBPDifferenceKind.DELETE, cbpDiffSide);
	    } else

	    if (event instanceof CBPMoveWithinEReferenceEvent) {
		String target = ((CBPMoveWithinEReferenceEvent) event).getTarget();
		String value = ((CBPMoveWithinEReferenceEvent) event).getValue();
		String reference = ((CBPMoveWithinEReferenceEvent) event).getEReference();
		int position = ((CBPMoveWithinEReferenceEvent) event).getPosition();

		String key = target + "." + reference + "." + position + "." + value;
		createMatchAndDiff(key, target, reference, value, position, CBPDifferenceKind.MOVE, cbpDiffSide);
	    } else

	    if (event instanceof CBPAddToEReferenceEvent) {
		String target = ((CBPAddToEReferenceEvent) event).getTarget();
		String value = ((CBPAddToEReferenceEvent) event).getValue();
		String reference = ((CBPAddToEReferenceEvent) event).getEReference();
		int position = ((CBPAddToEReferenceEvent) event).getPosition();

		String key = target + "." + reference + "." + position + "." + value;
		createMatchAndDiff(key, target, reference, value, position, CBPDifferenceKind.ADD, cbpDiffSide);
	    } else

	    if (event instanceof CBPUnsetEReferenceEvent) {
		String target = ((CBPUnsetEReferenceEvent) event).getTarget();
		String value = ((CBPUnsetEReferenceEvent) event).getValue();
		String reference = ((CBPUnsetEReferenceEvent) event).getEReference();

		String key = target + "." + reference;
		createMatchAndDiff(key, target, reference, value, -1, CBPDifferenceKind.CHANGE, cbpDiffSide);
	    } else

	    if (event instanceof CBPSetEReferenceEvent) {
		String target = ((CBPSetEReferenceEvent) event).getTarget();
		String value = ((CBPSetEReferenceEvent) event).getValue();
		String reference = ((CBPSetEReferenceEvent) event).getEReference();

		String key = target + "." + reference;
		createMatchAndDiff(key, target, reference, value, -1, CBPDifferenceKind.CHANGE, cbpDiffSide);
	    }
	    
	    // ATTRIBUTES---------------------------------------------
	    if (event instanceof CBPRemoveFromEAttributeEvent) {
		String target = ((CBPRemoveFromEAttributeEvent) event).getTarget();
		String value = ((CBPRemoveFromEAttributeEvent) event).getValue();
		String attribute = ((CBPRemoveFromEAttributeEvent) event).getEAttribute();
		int position = ((CBPRemoveFromEAttributeEvent) event).getPosition();

		String key = target + "." + attribute + "." + position + "." + value;
		createMatchAndDiff(key, target, attribute, value, position, CBPDifferenceKind.DELETE, cbpDiffSide);
	    } else

	    if (event instanceof CBPMoveWithinEAttributeEvent) {
		String target = ((CBPMoveWithinEAttributeEvent) event).getTarget();
		String value = ((CBPMoveWithinEAttributeEvent) event).getValue();
		String attribute = ((CBPMoveWithinEAttributeEvent) event).getEAttribute();
		int position = ((CBPMoveWithinEAttributeEvent) event).getPosition();

		String key = target + "." + attribute + "." + position + "." + value;
		createMatchAndDiff(key, target, attribute, value, position, CBPDifferenceKind.MOVE, cbpDiffSide);
	    } else

	    if (event instanceof CBPAddToEAttributeEvent) {
		String target = ((CBPAddToEAttributeEvent) event).getTarget();
		String value = ((CBPAddToEAttributeEvent) event).getValue();
		String attribute = ((CBPAddToEAttributeEvent) event).getEAttribute();
		int position = ((CBPAddToEAttributeEvent) event).getPosition();

		String key = target + "." + attribute + "." + position + "." + value;
		createMatchAndDiff(key, target, attribute, value, position, CBPDifferenceKind.ADD, cbpDiffSide);
	    } else

	    if (event instanceof CBPUnsetEAttributeEvent) {
		String target = ((CBPUnsetEAttributeEvent) event).getTarget();
		String value = ((CBPUnsetEAttributeEvent) event).getValue();
		String attribute = ((CBPUnsetEAttributeEvent) event).getEAttribute();

		String key = target + "." + attribute;
		createMatchAndDiff(key, target, attribute, value, -1, CBPDifferenceKind.CHANGE, cbpDiffSide);
	    } else

	    if (event instanceof CBPSetEAttributeEvent) {
		String target = ((CBPSetEAttributeEvent) event).getTarget();
		String value = ((CBPSetEAttributeEvent) event).getValue();
		String attribute = ((CBPSetEAttributeEvent) event).getEAttribute();

		String key = target + "." + attribute;
		createMatchAndDiff(key, target, attribute, value, -1, CBPDifferenceKind.CHANGE, cbpDiffSide);
	    }

	}

    }

    /**
     * @param target
     * @param id
     */
    protected void createMatchAndDiff(String id, String target, String feature, String value, int position, CBPDifferenceKind kind, int cbpDiffSide) {
	CBPMatch match = matches.get(target);
	if (match != null) {
	    CBPDiff diff = match.getDiffs().get(id);
	    if (diff == null) {
		diff = new CBPDiff(match, id, target, feature, value, position, kind, cbpDiffSide);
		match.getDiffs().put(id, diff);
	    } else {
		// int opposite = (cbpDiffSide + 1) % 2;
		CBPDiffSide side = diff.getSides()[cbpDiffSide];
		if (side == null) {
		    side = new CBPDiffSide(target, feature, value, position, kind);
		    diff.getSides()[cbpDiffSide] = side;
		} else {
		    return;
		}
	    }
	} else {
	    match = new CBPMatch(target);
	    CBPDiff diff = new CBPDiff(match, id, target, feature, value, position, kind, cbpDiffSide);
	    match.getDiffs().put(id, diff);
	    matches.put(target, match);
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
				String sTarget = e.getAttributeByName(new QName("target")).getValue();
				String sName = e.getAttributeByName(new QName("name")).getValue();
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
