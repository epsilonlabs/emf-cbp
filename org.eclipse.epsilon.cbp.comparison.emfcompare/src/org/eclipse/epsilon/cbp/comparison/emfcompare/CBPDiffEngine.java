package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodeFactory;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.MultiValueEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SingleValueEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SingleValueEReferenceEvent;

import com.google.common.collect.Lists;

public class CBPDiffEngine extends DefaultDiffEngine {

    public CBPDiffEngine() {
	super(new DiffBuilder());
    }

    public CBPDiffEngine(IDiffProcessor iDiffProcessor) {
	super(new DiffBuilder());
    }

    @Override
    public void diff(Comparison comparison, Monitor monitor) {
	printMatches(comparison.getMatches(), 0);

	// super.diff(comparison, monitor);
	this.cbpDiff(comparison, monitor);
	// EList<Diff> x = comparison.getDifferences();
	// System.out.println();
    }

    /**
     * @param comparison
     */
    protected void printMatches(List<Match> matches, int level) {
	for (Match match : matches) {
	    Object left = match.getLeft();
	    Object right = match.getRight();
	    String leftName = null;
	    String rightName = null;
	    if (left != null)
		leftName = ((Node) match.getLeft()).getName();
	    if (right != null)
		rightName = ((Node) match.getRight()).getName();
	    for (int i = 0; i < level; i++) {
		System.out.print("+--");
	    }
	    System.out.println(leftName + " vs " + rightName);
	    printMatches(match.getSubmatches(), level + 1);
	}
    }

    private void cbpDiff(Comparison comparison, Monitor monitor) {
	Resource left = null;
	Resource right = null;
	left = comparison.getMatchedResources().get(0).getLeft();
	right = (comparison.getMatchedResources().get(0).getRight() != null) ? comparison.getMatchedResources().get(0).getRight() : comparison.getMatchedResources().get(1).getRight();

	String leftPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(left.getURI().toPlatformString(true))).getRawLocation().toOSString();
	leftPath = leftPath.replaceAll(".xmi", ".cbpxml");
	String rightPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(right.getURI().toPlatformString(true))).getRawLocation().toOSString();
	rightPath = rightPath.replaceAll(".xmi", ".cbpxml");

	try {
	    CBPComparison cbpComparison = new CBPComparison(new File(leftPath), new File(rightPath));
	    cbpComparison.compare();
	    List<ComparisonEvent> leftComparisonEvents = cbpComparison.getLeftComparisonEvents();
	    List<ComparisonEvent> rightComparisonEvents = cbpComparison.getRightComparisonEvents();

	    CBPObjectEventTracker rightTracker = addRightSideObjectEventsToTracker(right, rightComparisonEvents);
	    CBPObjectEventTracker leftTracker = addLeftSideObjectEventsToTracker(left, leftComparisonEvents);

	    Map<String, TrackedObject> rightTrackedObjects = createTrackedObjectList(rightTracker);
	    Map<String, TrackedObject> leftTrackedObjects = createTrackedObjectList(leftTracker);

	    computeDifferences(comparison, left, right, rightTrackedObjects, leftTrackedObjects, DifferenceSource.LEFT, monitor);

	    // computeDifferences(comparison, left, right, leftTracker,
	    // rightTracker, DifferenceSource.LEFT, monitor);

	    // EObjectEventTracker leftTracker = createEObjectEventTracker(left,
	    // leftComparisonEvents);
	    // EObjectEventTracker rightTracker =
	    // createEObjectEventTracker(right, rightComparisonEvents);
	    //
	    // computeDifferences(comparison, left, leftTracker,
	    // DifferenceSource.LEFT, monitor);
	    // computeDifferences(comparison, right, rightTracker,
	    // DifferenceSource.RIGHT, monitor);

	} catch (IOException | XMLStreamException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	}

	System.out.println();
    }

    private void computeDifferences(Comparison comparison, Resource left, Resource right, Map<String, TrackedObject> rightTrackedObjects, Map<String, TrackedObject> leftTrackedObjects,
	    DifferenceSource source, Monitor monitor) {

	Set<String> ids = new HashSet<>(leftTrackedObjects.keySet());
	ids.addAll(rightTrackedObjects.keySet());

	for (String id : ids) {

	    System.out.println("Identifying differences of object with id = " + id);

	    TrackedObject leftTrackedObject = leftTrackedObjects.get(id);
	    TrackedObject rightTrackedObject = rightTrackedObjects.get(id);

	    if (leftTrackedObject == null) {
		EObject leftEObject = left.getEObject(id);
		if (leftEObject != null) {
		    leftTrackedObject = createTrackedEObject(id, left, leftEObject, leftTrackedObjects);
		}
	    }
	    if (rightTrackedObject == null) {
		EObject rightEObject = right.getEObject(id);
		if (rightEObject != null) {
		    rightTrackedObject = createTrackedEObject(id, left, rightEObject, rightTrackedObjects);
		}
	    }

	    // set right container of the right tracked object based on the left
	    // object container
	    if (rightTrackedObject.getContainer() == null && leftTrackedObject.getOldContainer() != null) {
		rightTrackedObject.setContainer(leftTrackedObject.getOldContainer());
	    }

	    // if an object only exists in one of the resources or does not
	    // exists at all
	    if (leftTrackedObject == null || rightTrackedObject == null) {
		// handle objects that don't exist in one of the resources
		if (leftTrackedObject == null && rightTrackedObject != null) {
		    EObject value = right.getEObject(id);
		    Match match = comparison.getMatch(value);
		    EStructuralFeature feature = value.eClass().getEStructuralFeature(rightTrackedObject.getContainingFeature());
		    if (feature instanceof EReference) {
			EReference reference = (EReference) feature;
			getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.DELETE, source);
		    }
		} else if (leftTrackedObject != null && rightTrackedObject == null) {
		    EObject value = left.getEObject(id);
		    Match match = comparison.getMatch(value);
		    EStructuralFeature feature = value.eClass().getEStructuralFeature(leftTrackedObject.getContainingFeature());
		    if (feature instanceof EReference) {
			EReference reference = (EReference) feature;
			getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.ADD, source);
		    }
		}
	    }

	    // if an object exists in both resources
	    else {

		// handle objects that have been moved cross containers
		if (leftTrackedObject.getContainer() != null && rightTrackedObject.getContainer() != null && leftTrackedObject.getContainingFeature() != null
			&& rightTrackedObject.getContainingFeature() != null
			&& !leftTrackedObject.getContainer().equals(rightTrackedObject.getContainer()) ^ !leftTrackedObject.getContainingFeature().equals(rightTrackedObject.getContainingFeature())) {
		    EObject value = right.getEObject(id);
		    Match match = comparison.getMatch(value);
		    EReference reference = (EReference) value.eClass().getEStructuralFeature(leftTrackedObject.getContainingFeature());
		    getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.MOVE, source);
		}
		// handle objects that have been moved in the same containers
		else if (leftTrackedObject.getContainer() != null && rightTrackedObject.getContainer() != null && leftTrackedObject.getContainer().equals(rightTrackedObject.getContainer())
			&& leftTrackedObject.getContainingFeature().equals(rightTrackedObject.getContainingFeature()) && leftTrackedObject.getPosition() != rightTrackedObject.getPosition()) {
		    EObject value = right.getEObject(id);
		    Match match = comparison.getMatch(value);
		    EReference reference = (EReference) value.eClass().getEStructuralFeature(leftTrackedObject.getContainingFeature());
		    getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.MOVE, source);
		}

		// handle objects that have been modified on their features
		EObject leftTargetEObject = left.getEObject(id);
		Match match = comparison.getMatch(leftTargetEObject);
		EObject rightTargetEObject = match.getRight();
		EObject targetEObject = leftTargetEObject != null ? leftTargetEObject : rightTargetEObject;

		Set<String> featureNameSet = new HashSet<>(leftTrackedObject.getFeatures().keySet());
		featureNameSet.addAll(rightTrackedObject.getFeatures().keySet());

		for (String featureName : featureNameSet) {
		    TrackedFeature leftFeature = leftTrackedObject.getFeature(featureName);
		    TrackedFeature rightFeature = rightTrackedObject.getFeature(featureName);

		    EStructuralFeature leftEStructuralFeature = null;
		    EStructuralFeature rightEStructuralFeature = null;
		    EStructuralFeature feature = null;
		    boolean isMany = false;
		    boolean isAttribute = true;

		    if (leftFeature != null) {
			leftEStructuralFeature = leftTargetEObject.eClass().getEStructuralFeature(leftFeature.getName());
			feature = leftEStructuralFeature;
			isMany = leftEStructuralFeature.isMany();
			if (leftEStructuralFeature instanceof EReference) {
			    isAttribute = false;
			}
		    }
		    if (rightFeature != null) {
			rightEStructuralFeature = rightTargetEObject.eClass().getEStructuralFeature(rightFeature.getName());
			if (leftEStructuralFeature == null) {
			    feature = rightEStructuralFeature;
			    isMany = rightEStructuralFeature.isMany();
			    if (rightEStructuralFeature instanceof EReference) {
				isAttribute = false;
			    }
			}
		    }

		    // handle single-value attribute
		    if (isMany == false && isAttribute == true) {
			Object value = targetEObject.eGet(feature);
			getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.CHANGE, source);
		    }

		    // handle multi-value attribute
		    else if (isMany == true && isAttribute == true) {
			Map<Integer, String> leftValues = leftFeature.getValues();
			Map<Integer, String> rightValues = rightFeature.getValues();

			Set<Integer> positions = new HashSet<>(leftValues.keySet());
			positions.addAll(rightValues.keySet());

			for (int pos : positions) {
			    String leftValue = leftFeature.getValue(pos);
			    String rightValue = rightFeature.getValue(pos);
			    if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				getDiffProcessor().featureMapChange(match, (EAttribute) feature, leftValue, DifferenceKind.CHANGE, source);
			    } else if (leftValue != null && rightValue == null) {
				getDiffProcessor().featureMapChange(match, (EAttribute) feature, rightValue, DifferenceKind.DELETE, source);
			    } else if (leftValue == null && rightValue != null) {
				getDiffProcessor().featureMapChange(match, (EAttribute) feature, leftValue, DifferenceKind.ADD, source);
			    }
			}

		    }
		    // handle singe-value reference
		    else if (isMany == false && isAttribute == false) {
			EObject value = (EObject) targetEObject.eGet(feature);
			getDiffProcessor().referenceChange(match, (EReference) feature, value, DifferenceKind.CHANGE, source);
		    }
		}
	    }

	}

    }

    /***
     * 
     * @param id
     * @param resource
     * @param eObject
     * @param trackedObjects
     * @return
     */
    private TrackedObject createTrackedEObject(String id, Resource resource, EObject eObject, Map<String, TrackedObject> trackedObjects) {
	TrackedObject trackedObject = new TrackedObject(id);

	// get the containing eObject
	if (resource instanceof XMIResource && eObject.eContainer() != null) {
	    trackedObject.setContainer(((XMIResource) resource).getID(eObject.eContainer()));
	}
	// get the containing feature name
	if (eObject.eContainingFeature() != null) {
	    trackedObject.setContainingFeature(eObject.eContainingFeature().getName());
	}
	// get the position of the eObject
	if (eObject.eContainer() != null && eObject.eContainingFeature() != null) {
	    Object value = eObject.eContainer().eGet(eObject.eContainingFeature());
	    if (value instanceof EList<?>) {
		int pos = ((EList<?>) value).indexOf(value);
		trackedObject.setPosition(pos);
	    } else {
		trackedObject.setPosition(0);
	    }

	}

	trackedObjects.put("id", trackedObject);
	return trackedObject;
    }

    private Map<String, TrackedObject> createTrackedObjectList(CBPObjectEventTracker rightTracker) {
	Map<String, TrackedObject> trackedObjects = new HashedMap<>();

	for (Entry<String, List<ComparisonEvent>> entry : rightTracker.entrySet()) {
	    String id = entry.getKey();
	    List<ComparisonEvent> eventList = entry.getValue();

	    TrackedObject trackedObject = trackedObjects.get(id);
	    if (trackedObject == null) {
		trackedObject = new TrackedObject(id);
		trackedObjects.put(id, trackedObject);
	    }

	    int eventPos = eventList.size() - 1;

	    for (ComparisonEvent event : eventList) {

		// handle from the the value object's point of view
		if (event.getTargetId() != null && trackedObject.getContainer() == null && !id.equals(event.getTargetId())) {
		    if (trackedObject.getOldContainer() == null) {
			trackedObject.setOldContainer(event.getTargetId());
		    }
		    trackedObject.setContainer(event.getTargetId());
		}

		// set position
		if (event.getPosition() != -1 && trackedObject.getPosition() == -1) {
		    trackedObject.setPosition(event.getPosition());
		} else if (event.getTo() != -1 && trackedObject.getPosition() == -1) {
		    trackedObject.setPosition(event.getTo());
		}

		// set containing feature name
		if (id.equals(event.getValueId()) && event.getFeatureName() != null && trackedObject.getContainingFeature() == null) {
		    if (trackedObject.getOldContainingFeature() == null) {
			trackedObject.setOldContainingFeature(event.getFeatureName());
		    }
		    trackedObject.setContainingFeature(event.getFeatureName());
		}

		if (trackedObject.getContainer() != null && trackedObject.getPosition() != -1 && trackedObject.getContainingFeature() != null) {
		    break;
		}

		// handle from the target object point of view
		if (event.getTargetId() != null) {
		    TrackedObject targetTrackedObject = trackedObjects.get(event.getTargetId());
		    String value = (event.getValue() == null) ? event.getValueId() : event.getValue().toString();
		    int pos = (event.getPosition() == -1) ? event.getPosition() : event.getTo();
		    if (pos == -1) {
			targetTrackedObject.addValue(event.getFeatureName(), value, false);
		    } else {
			targetTrackedObject.addValue(event.getFeatureName(), value, pos, false);
		    }
		}
	    }
	}

	return trackedObjects;
    }

    /**
     * @param comparison
     * @param resource
     * @param source
     * @param target
     * @param leftComparisonEvents
     * @param rightComparisonEvents
     */
    protected void checkObjectResourceDifferences(Comparison comparison, Resource resource, DifferenceSource source, EObject target, List<ComparisonEvent> leftComparisonEvents,
	    List<ComparisonEvent> rightComparisonEvents) {
	Match match = comparison.getMatch(target);
	String uri = resource.getURI().toString();
	if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
	    if (rightComparisonEvents != null && rightComparisonEvents.size() > 0) {
		for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
		    if (comparisonEvent.getEventType() == CreateEObjectEvent.class) {
			getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD, source);
		    } else if (comparisonEvent.getEventType() == DeleteEObjectEvent.class) {
			getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE, source);
		    }
		}
	    }
	    for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
		if (comparisonEvent.getEventType() == CreateEObjectEvent.class) {
		    getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD, source);
		} else if (comparisonEvent.getEventType() == DeleteEObjectEvent.class) {
		    getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE, source);
		}
	    }
	}
    }

    /**
     * @param comparison
     * @param source
     * @param value
     * @param target
     * @param feature
     * @param leftComparisonEvents
     * @param rightComparisonEvents
     */
    protected void checkMultiValueFeatureDifferences(Comparison comparison, DifferenceSource source, Object value, EObject target, EStructuralFeature feature,
	    List<ComparisonEvent> leftComparisonEvents, List<ComparisonEvent> rightComparisonEvents, Resource left, Resource right) {
	if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
	    Match match = comparison.getMatch(target);
	    if (feature instanceof EAttribute) {
		checkMultiValueAttributeDifferences(source, value, feature, leftComparisonEvents, rightComparisonEvents, match);
	    } else if (feature instanceof EReference) {

		checkMultiValueReferenceDifferences(source, (String) value, feature, leftComparisonEvents, rightComparisonEvents, match, left, right);
	    }
	}
    }

    /**
     * @param source
     * @param value
     * @param feature
     * @param leftComparisonEvents
     * @param match
     */
    protected void checkMultiValueReferenceDifferences(DifferenceSource source, String valueId, EStructuralFeature feature, List<ComparisonEvent> leftComparisonEvents,
	    List<ComparisonEvent> rightComparisonEvents, Match match, Resource left, Resource right) {

	if (rightComparisonEvents != null && rightComparisonEvents.size() > 0) {
	    for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
		EObject value = right.getEObject(comparisonEvent.getValueId());
		if (comparisonEvent.getEventType() == AddToEReferenceEvent.class) {
		    getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.ADD, source);
		}
		if (comparisonEvent.getEventType() == RemoveFromEReferenceEvent.class) {
		    getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.DELETE, DifferenceSource.LEFT);
		}
		if (comparisonEvent.getEventType() == MoveWithinEReferenceEvent.class) {
		    getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.MOVE, source);
		}
	    }
	}
	if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
	    EObject value = left.getEObject(valueId);
	    for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
		if (comparisonEvent.getEventType() == AddToEReferenceEvent.class) {
		    getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.ADD, source);
		}
		if (comparisonEvent.getEventType() == RemoveFromEReferenceEvent.class) {
		    getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.DELETE, source);
		}
		if (comparisonEvent.getEventType() == MoveWithinEReferenceEvent.class) {
		    getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.MOVE, source);
		}
	    }
	}
    }

    /**
     * @param source
     * @param value
     * @param feature
     * @param leftComparisonEvents
     * @param rightComparisonEvents
     * @param match
     */
    protected void checkMultiValueAttributeDifferences(DifferenceSource source, Object value, EStructuralFeature feature, List<ComparisonEvent> leftComparisonEvents,
	    List<ComparisonEvent> rightComparisonEvents, Match match) {

	if (rightComparisonEvents != null && rightComparisonEvents.size() > 0) {
	    for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
		if (comparisonEvent.getEventType() == AddToEAttributeEvent.class) {
		    getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.ADD, source);
		}
		if (comparisonEvent.getEventType() == RemoveFromEAttributeEvent.class) {
		    getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.DELETE, source);
		}
		if (comparisonEvent.getEventType() == MoveWithinEAttributeEvent.class) {
		    getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.MOVE, source);
		}
	    }
	}
	if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
	    for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
		if (comparisonEvent.getEventType() == AddToEAttributeEvent.class) {
		    getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.ADD, source);
		}
		if (comparisonEvent.getEventType() == RemoveFromEAttributeEvent.class) {
		    getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.DELETE, source);
		}
		if (comparisonEvent.getEventType() == MoveWithinEAttributeEvent.class) {
		    getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.MOVE, source);
		}
	    }
	}
    }

    /**
     * @param comparison
     * @param source
     * @param value
     * @param target
     * @param feature
     * @param leftComparisonEvents
     * @param rightComparisonEvents
     */
    protected void checkSingleValueFeatureDifferences(Comparison comparison, DifferenceSource source, Object value, EObject target, EStructuralFeature feature,
	    List<ComparisonEvent> leftComparisonEvents, List<ComparisonEvent> rightComparisonEvents) {

	if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
	    Match match = comparison.getMatch(target);
	    if (feature instanceof EAttribute) {
		getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.CHANGE, source);
	    } else if (feature instanceof EReference) {
		getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.CHANGE, source);
	    }
	}
    }

    /**
     * @param left
     * @param source
     * @param eObject
     * @param match
     * @param eventType
     * @param comparisonEvent
     */
    protected void checkForDifferences(Resource left, DifferenceSource source, EObject eObject, Match match, Class<?> eventType, ComparisonEvent comparisonEvent, Monitor monitor) {

	if (monitor.isCanceled()) {
	    throw new ComparisonCanceledException();
	}
	checkResourceAttachment(match, monitor);

	if (eventType == SingleValueEAttributeEvent.class) {

	    EAttribute attribute = (EAttribute) eObject.eClass().getEStructuralFeature(comparisonEvent.getFeatureName());
	    Object value = comparisonEvent.getValue();
	    getDiffProcessor().attributeChange(match, attribute, value, DifferenceKind.CHANGE, source);

	} else if (eventType == SingleValueEReferenceEvent.class) {

	    EReference attribute = (EReference) eObject.eClass().getEStructuralFeature(comparisonEvent.getFeatureName());
	    EObject value = ((XMIResource) left).getEObject(comparisonEvent.getValueId());
	    getDiffProcessor().referenceChange(match, attribute, value, DifferenceKind.CHANGE, source);

	} else if (eventType == MultiValueEReferenceEvent.class) {

	    EReference reference = (EReference) eObject.eClass().getEStructuralFeature(comparisonEvent.getFeatureName());
	    EObject value = ((XMIResource) left).getEObject(comparisonEvent.getValueId());
	    DifferenceKind kind = null;

	    if (comparisonEvent.getEventType() == AddToEReferenceEvent.class) {
		kind = DifferenceKind.ADD;
	    } else if (comparisonEvent.getEventType() == RemoveFromEReferenceEvent.class) {
		kind = DifferenceKind.DELETE;
	    }
	    getDiffProcessor().referenceChange(match, reference, value, kind, source);

	} else if (eventType == ResourceEvent.class) {

	    DifferenceKind kind = null;

	    if (comparisonEvent.getEventType() == AddToResourceEvent.class) {
		kind = DifferenceKind.ADD;
	    } else if (comparisonEvent.getEventType() == RemoveFromResourceEvent.class) {
		kind = DifferenceKind.DELETE;
	    }

	    getDiffProcessor().resourceAttachmentChange(match, left.getURI().toString(), kind, source);

	}

	// for (Match submatch : match.getSubmatches()) {
	// checkForDifferences(left, source, eObject, submatch, eventType,
	// comparisonEvent, monitor);
	// }
    }

    protected void checkForDifferences(Match match, Monitor monitor, EStructuralFeature feature) {
	if (monitor.isCanceled()) {
	    throw new ComparisonCanceledException();
	}
	checkResourceAttachment(match, monitor);

	final FeatureFilter featureFilter = createFeatureFilter();

	if (feature instanceof EReference) {
	    final boolean considerOrdering = featureFilter.checkForOrderingChanges((EReference) feature);
	    computeDifferences(match, (EReference) feature, considerOrdering);
	} else if (feature instanceof EAttribute) {
	    final boolean considerOrdering = featureFilter.checkForOrderingChanges((EAttribute) feature);
	    computeDifferences(match, (EAttribute) feature, considerOrdering);
	}

	for (Match submatch : match.getSubmatches()) {
	    checkForDifferences(submatch, monitor);
	}
    }

    private CBPObjectEventTracker addLeftSideObjectEventsToTracker(final Resource resource, final List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
	return this.addObjectToTracker(resource, comparisonEvents, false);
    }

    private CBPObjectEventTracker addRightSideObjectEventsToTracker(final Resource resource, List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
	return this.addObjectToTracker(resource, comparisonEvents, true);
    }

    private CBPObjectEventTracker addObjectToTracker(final Resource resource, List<ComparisonEvent> comparisonEvents, boolean isRight) throws ParserConfigurationException, TransformerException {

	CBPObjectEventTracker tracker = new CBPObjectEventTracker();

	// reverse if the comparison events come from the right side
	// if (isRight) {
	// comparisonEvents = this.reverseComparisonEvents(comparisonEvents);
	// }

	for (ComparisonEvent event : comparisonEvents) {
	    if (event.getTargetId() != null && !event.getTargetId().equals(ComparisonEvent.RESOURCE_STRING)) {

		tracker.addEvent(event.getTargetId(), event);
	    }
	    if (event.getValueId() != null && !event.getTargetId().equals(ComparisonEvent.RESOURCE_STRING)) {
		tracker.addEvent(event.getValueId(), event);
	    }
	}

	return tracker;
    }

    private List<ComparisonEvent> reverseComparisonEvents(List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
	comparisonEvents = Lists.reverse(comparisonEvents);
	for (int i = 0; i < comparisonEvents.size(); i++) {
	    comparisonEvents.set(i, comparisonEvents.get(i).reverse());
	}
	return comparisonEvents;
    }
}
