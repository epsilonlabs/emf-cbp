package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;

public class CBPEngine {

    public static final String PARTIAL_MODE = "PARTIAL_MODE";
    public static final String FULL_MODE = "FULL_MODE";

    private static EPackage ePackage;

    private static String mode = FULL_MODE;
    private static Resource leftResource;
    private static Resource rightResource;
    private static Resource leftPartialResource;
    private static Resource rightPartialResource;
    private static List<ComparisonEvent> leftComparisonEvents;
    private static List<ComparisonEvent> rightComparisonEvents;
    private static CBPObjectEventTracker rightTracker;
    private static CBPObjectEventTracker leftTracker;
    private static Map<String, TrackedObject> rightTrackedObjects;
    private static Map<String, TrackedObject> leftTrackedObjects;

    public static void createCBPEngine(Resource left, Resource right, String treeMode) {
	try {

	    mode = treeMode;
	    leftResource = left;
	    rightResource = right;

	    String leftPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(left.getURI().toPlatformString(true))).getRawLocation().toOSString();
	    leftPath = leftPath.replaceAll(".xmi", ".cbpxml");
	    String rightPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(right.getURI().toPlatformString(true))).getRawLocation().toOSString();
	    rightPath = rightPath.replaceAll(".xmi", ".cbpxml");

	    CBPComparison cbpComparison = new CBPComparison(new File(leftPath), new File(rightPath));
	    cbpComparison.compare();

	    if (ePackage == null && cbpComparison.getRightNsURI() != null) {
		ePackage = EPackage.Registry.INSTANCE.getEPackage(cbpComparison.getRightNsURI());
	    } else if (ePackage == null && cbpComparison.getLeftNsURI() != null) {
		ePackage = EPackage.Registry.INSTANCE.getEPackage(cbpComparison.getLeftNsURI());
	    } else if (ePackage == null && left.getContents().size() > 0) {
		ePackage = left.getContents().get(0).eClass().getEPackage();
	    }

	    leftComparisonEvents = cbpComparison.getLeftComparisonEvents();
	    rightComparisonEvents = cbpComparison.getRightComparisonEvents();

	    rightTracker = addRightSideObjectEventsToTracker(right, rightComparisonEvents);
	    leftTracker = addLeftSideObjectEventsToTracker(left, leftComparisonEvents);

	    rightTrackedObjects = createTrackedObjectList(rightTracker);
	    leftTrackedObjects = createTrackedObjectList(leftTracker);

	    if (CBPEngine.mode == CBPEngine.PARTIAL_MODE) {
		leftPartialResource = createPartialResource(leftResource, leftResource.getURI(), leftTrackedObjects);
		rightPartialResource = createPartialResource(leftResource, rightResource.getURI(), rightTrackedObjects);
	    } else {
		leftPartialResource = leftResource;
		rightPartialResource = rightResource;
	    }

	} catch (IOException | XMLStreamException e) {
	    e.printStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	}
    }

    private static Resource createPartialResource(Resource leftResource, URI uri, Map<String, TrackedObject> trackedObjects) {
	XMIResource partialResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(uri);
	XMIResource xmiLeftResource = ((XMIResource) leftResource);
	for (TrackedObject trackedObject : trackedObjects.values()) {

	    if (trackedObject.getClassName() != null) {
		EClass eClass = null;
		eClass = (EClass) ePackage.getEClassifier(trackedObject.getClassName());
		EObject eObject = ePackage.getEFactoryInstance().create(eClass);
		partialResource.getContents().add(eObject);
		partialResource.setID(eObject, trackedObject.getId());

		for (TrackedFeature trackedFeature : trackedObject.getFeatures().values()) {
		    EStructuralFeature eStructuralFeature = eObject.eClass().getEStructuralFeature(trackedFeature.getFeatureName());
		    Object value = eObject.eGet(eStructuralFeature);
		    if (eStructuralFeature instanceof EAttribute) {
			if (eStructuralFeature.isMany() == false) {
			    eObject.eSet(eStructuralFeature, trackedFeature.getValue());
			} else if (eStructuralFeature.isMany() == true) {
			    EList<Object> eList = (EList<Object>) value;
			    for (Entry<Integer, String> entry : trackedFeature.getValues().entrySet()) {
				int pos = entry.getKey();
				String valueString = entry.getValue();
				if (pos + 1 > eList.size()) {
				    for (int i = eList.size(); i < pos + 1; i++) {
					eList.add(i, null);
				    }
				    eList.add(valueString);
				} else {
				    eList.remove(pos);
				    eList.add(pos, valueString);
				}
			    }
			}
		    } else if (eStructuralFeature instanceof EReference) {
			if (eStructuralFeature.isMany() == false) {
			    eObject.eSet(eStructuralFeature, trackedFeature.getValue());
			} else if (eStructuralFeature.isMany() == true) {
			    EList<Object> eList = (EList<Object>) value;
			    for (Entry<Integer, String> entry : trackedFeature.getValues().entrySet()) {
				int pos = entry.getKey();
				String valueString = entry.getValue();
				if (pos + 1 > eList.size()) {
				    for (int i = eList.size(); i < pos + 1; i++) {
					EObject dummy = ePackage.getEFactoryInstance().create(eClass);
					eList.add(i, dummy);
				    }
				    EObject dummy = partialResource.getEObject(valueString);
				    eList.add(dummy);
				} else {
				    eList.remove(pos);
				    EObject dummy = partialResource.getEObject(valueString);
				    eList.add(pos, dummy);
				}
			    }
			}
		    }
		}

	    } else {

	    }

	    // EObject eObject =
	    // xmiLeftResource.getEObject(trackedObject.getId());
	    // EObject partialEObject = null;
	    // if (eObject != null) {
	    // partialEObject = EcoreUtil.copy(eObject);
	    // partialResource.getContents().add(partialEObject);
	    // partialResource.setID(partialEObject, trackedObject.getId());
	    //
	    // }
	}
	return partialResource;
    }

    private static CBPObjectEventTracker addLeftSideObjectEventsToTracker(final Resource resource, final List<ComparisonEvent> comparisonEvents)
	    throws ParserConfigurationException, TransformerException {
	return addObjectToTracker(resource, comparisonEvents, false);
    }

    private static CBPObjectEventTracker addRightSideObjectEventsToTracker(final Resource resource, List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
	return addObjectToTracker(resource, comparisonEvents, true);
    }

    private static CBPObjectEventTracker addObjectToTracker(final Resource resource, List<ComparisonEvent> comparisonEvents, boolean isRight)
	    throws ParserConfigurationException, TransformerException {

	CBPObjectEventTracker tracker = new CBPObjectEventTracker();

	for (ComparisonEvent event : comparisonEvents) {

	    if (event.getTargetId() != null /*
					     * && !event.getTargetId().equals(
					     * ComparisonEvent.RESOURCE_STRING)
					     */) {

		tracker.addEvent(event.getTargetId(), event);
	    }
	    if (event.getValueId() != null /*
					    * && !event.getTargetId().equals(
					    * ComparisonEvent.RESOURCE_STRING)
					    */) {
		tracker.addEvent(event.getValueId(), event);
	    }
	}

	return tracker;
    }

    private static Map<String, TrackedObject> createTrackedObjectList(CBPObjectEventTracker rightTracker) throws ParserConfigurationException, TransformerException {
	Map<String, TrackedObject> trackedObjects = new HashedMap<>();

	for (Entry<String, List<ComparisonEvent>> entry : rightTracker.entrySet()) {
	    String id = entry.getKey();
	    List<ComparisonEvent> eventList = entry.getValue();

	    TrackedObject trackedObject = trackedObjects.get(id);
	    if (trackedObject == null) {
		trackedObject = new TrackedObject(id);
		trackedObjects.put(id, trackedObject);
	    }

	    for (ComparisonEvent event : eventList) {

		// id as a value
		if (id.equals(event.getValueId())) {

		    // handle from the the value object's point of view
		    if (event.getTargetId() != null && trackedObject.getContainer() == null && !id.equals(event.getTargetId())) {
			if (trackedObject.getOldContainer() == null) {
			    trackedObject.setOldContainer(event.getTargetId());
			}
			trackedObject.setContainer(event.getTargetId());
		    }

		    // set class name
		    if (event.getClassName() != null) {
			trackedObject.setClassName(event.getClassName());
		    }

		    // set position
		    if (event.getPosition() != -1 && trackedObject.getPosition() == -1) {
			trackedObject.setPosition(event.getPosition());
		    } else if (event.getTo() != -1 && trackedObject.getPosition() == -1) {
			trackedObject.setPosition(event.getTo());
		    }

		    // set containing feature name
		    if (event.getFeatureName() != null && trackedObject.getContainingFeature() == null) {
			if (trackedObject.getOldContainingFeature() == null) {
			    trackedObject.setOldContainingFeature(event.getFeatureName());
			}
			trackedObject.setContainingFeature(event.getFeatureName());
		    }

		    if (trackedObject.getContainer() != null && trackedObject.getPosition() != -1 && trackedObject.getContainingFeature() != null) {
			break;
		    }
		}

		// handle from the target object point of view
		// id as a target object
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

    public static Resource getLeftResource() {
	return leftResource;
    }

    public static Resource getRightResource() {
	return rightResource;
    }

    public static List<ComparisonEvent> getLeftComparisonEvents() {
	return leftComparisonEvents;
    }

    public static List<ComparisonEvent> getRightComparisonEvents() {
	return rightComparisonEvents;
    }

    public static CBPObjectEventTracker getRightTracker() {
	return rightTracker;
    }

    public static CBPObjectEventTracker getLeftTracker() {
	return leftTracker;
    }

    public static Map<String, TrackedObject> getRightTrackedObjects() {
	return rightTrackedObjects;
    }

    public static Map<String, TrackedObject> getLeftTrackedObjects() {
	return leftTrackedObjects;
    }

    public static Resource getLeftPartialResource() {
	return leftPartialResource;
    }

    public static Resource getRightPartialResource() {
	return rightPartialResource;
    }

    public static String getMode() {
	return mode;
    }

    public static void setMode(String mode) {
	CBPEngine.mode = mode;
    }

}
