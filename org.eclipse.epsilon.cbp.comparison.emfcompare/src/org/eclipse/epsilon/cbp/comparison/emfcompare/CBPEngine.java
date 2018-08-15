package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
import org.eclipse.emf.common.util.TreeIterator;
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
    private static Set<EObject> dummyObjects = new HashSet<>();

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

	    leftTracker = addLeftSideObjectEventsToTracker(left, leftComparisonEvents);
	    rightTracker = addRightSideObjectEventsToTracker(right, rightComparisonEvents);

	    leftTrackedObjects = createTrackedObjectList(leftTracker);
	    rightTrackedObjects = createTrackedObjectList(rightTracker, leftTrackedObjects);

	    if (CBPEngine.mode == CBPEngine.PARTIAL_MODE) {
		System.out.println("Left Partial Resource");
		leftPartialResource = createLeftPartialResource(leftResource, leftResource.getURI(), leftTrackedObjects);
		System.out.println("\nRight Partial Resource");
		rightPartialResource = createLeftPartialResource(leftResource, rightResource.getURI(), rightTrackedObjects);
		System.out.println("");
		// rightPartialResource = createPartialResource(leftResource,
		// rightResource.getURI(), rightTrackedObjects,
		// leftPartialResource);
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

    private static EObject recursiveCreateLeftPartialObject(Resource leftResource, Resource leftPartialResource, Map<String, TrackedObject> trackedObjects, TrackedObject trackedObject) {
	XMIResource partialResource = (XMIResource) leftPartialResource;
	XMIResource xmiLeftResource = ((XMIResource) leftResource);

	String id = null;
	String className = null;
	String container = null;
	String containingFeature = null;
	EObject partialEObject = null;
	EObject leftResourceEObject = null;
	int pos = -1;

	if (trackedObject != null) {
	    id = trackedObject.getId();
	    className = trackedObject.getClassName();
	    container = trackedObject.getContainer();
	    containingFeature = trackedObject.getContainingFeature();
	    pos = trackedObject.getPosition();
	    partialEObject = partialResource.getEObject(id);
	    if (partialEObject != null && partialEObject.eContainingFeature() != null) {
		containingFeature = partialEObject.eContainingFeature().getName();
	    }
	}

	// if object does not exist in current partial resource
	if (partialEObject == null) {

	    // create the an empty object for the eObject
	    if (className != null) {
		EClass eClass = (EClass) ePackage.getEClassifier(className);
		partialEObject = ePackage.getEFactoryInstance().create(eClass);
	    } else {
		leftResourceEObject = xmiLeftResource.getEObject(id);
		if (leftResourceEObject != null) {
		    EClass eClass = leftResourceEObject.eClass();
		    partialEObject = ePackage.getEFactoryInstance().create(eClass);
		    trackedObject.setClassName(eClass.getName());

		    if (leftResourceEObject.eContainingFeature() != null) {
			containingFeature = leftResourceEObject.eContainingFeature().getName();
		    }

		    if (container == null && leftResourceEObject.eContainer() != null) {
			container = xmiLeftResource.getID(leftResourceEObject.eContainer());
		    }

		    if (pos == -1) {
			if (leftResourceEObject.eContainingFeature() != null && leftResourceEObject.eContainingFeature().isMany()) {
			    EList<EObject> list = (EList<EObject>) leftResourceEObject.eContainer().eGet(leftResourceEObject.eContainingFeature());
			    int newPos = list.indexOf(leftResourceEObject);
			    pos = newPos;
			} else if (leftResourceEObject.eResource() != null) {
			    int newPos = leftResourceEObject.eResource().getContents().indexOf(leftResourceEObject);
			    pos = newPos;
			}
		    }
		}
	    }
	    // // get the eObject from the xmiLeftResource / local resource
	    // if (eObject == null) {
	    // eObject = EcoreUtil.copy(xmiLeftResource.getEObject(id));
	    //
	    // }
	    // at this point eObject should have been created

	    // should check container, if it's not null then create
	    // container object
	    if (container != null && containingFeature != null) {
		TrackedObject containerTrackedObject = trackedObjects.get(container);
		EObject containerEObject = recursiveCreateLeftPartialObject(xmiLeftResource, leftPartialResource, trackedObjects, containerTrackedObject);

		addEObjectToItsContainer(partialResource, id, containingFeature, partialEObject, pos, containerEObject);
	    }
	    // if the eObject is contained in the
	    else if (leftResourceEObject != null && leftResourceEObject.eContainer() != null) {
		String containerId = xmiLeftResource.getID(leftResourceEObject);
		TrackedObject containerTrackedObject = trackedObjects.get(containerId);
		EObject containerEObject = recursiveCreateLeftPartialObject(xmiLeftResource, leftPartialResource, trackedObjects, containerTrackedObject);

		addEObjectToItsContainer(partialResource, id, containingFeature, partialEObject, pos, containerEObject);

	    }
	    // add the object to partial resource if it doesn't have any
	    // container
	    else if (partialEObject != null) {

		if (pos > partialResource.getContents().size()) {
		    for (int i = partialResource.getContents().size(); i < pos; i++) {
			EClass eClass = partialEObject.eClass();
			EObject dummy = ePackage.getEFactoryInstance().create(eClass);
			partialResource.getContents().add(i, dummy);
			String dummyId = "resource." + i;
			partialResource.setID(dummy, dummyId);
			setEObjectName(dummy, id);
		    }
		}

		if (pos < partialResource.getContents().size() && dummyObjects.contains(partialResource.getContents().get(pos))) {
		    dummyObjects.remove(partialResource.getContents().get(pos));
		    partialResource.getContents().set(pos, partialEObject);
		} else {
		    partialResource.getContents().add(pos, partialEObject);
		}
		partialResource.setID(partialEObject, id);
		setEObjectName(partialEObject, id);
	    }
	}

	return partialEObject;
    }

    /**
     * @param partialResource
     * @param id
     * @param containingFeature
     * @param partialEObject
     * @param pos
     * @param containerEObject
     */
    protected static void addEObjectToItsContainer(XMIResource partialResource, String id, String containingFeature, EObject partialEObject, int pos, EObject containerEObject) {
	// add current object to it's container
	EStructuralFeature feature = containerEObject.eClass().getEStructuralFeature(containingFeature);
	if (feature instanceof EReference && ((EReference) feature).isContainment()) {
	    if (feature.isMany() == false) {
		containerEObject.eSet(feature, partialEObject);
		partialResource.setID(partialEObject, id);
		setEObjectName(partialEObject, id);
	    } else {
		EList<EObject> list = (EList<EObject>) containerEObject.eGet(feature);
		if (pos <= -1) {
		    list.add(partialEObject);
		    partialResource.setID(partialEObject, id);
		    setEObjectName(partialEObject, id);
		} else {
		    if (pos > list.size()) {
			for (int i = list.size(); i < pos; i++) {
			    EClass eClass = (EClass) feature.getEType();
			    EObject dummy = ePackage.getEFactoryInstance().create(eClass);
			    list.add(i, dummy);
			    String dummyId = partialResource.getID(containerEObject) + "." + feature.getName() + "." + i;
			    partialResource.setID(dummy, "dummy");
			    setEObjectName(dummy, "dummy");
			    dummyObjects.add(dummy);
			}
		    }
		    if (pos < list.size() && dummyObjects.contains(list.get(pos))) {
			dummyObjects.remove(list.get(pos));
			list.set(pos, partialEObject);
		    } else {
			list.add(pos, partialEObject);
		    }
		    partialResource.setID(partialEObject, id);
		    setEObjectName(partialEObject, id);
		}
	    }
	}
    }

    private static Resource createLeftPartialResource(Resource leftResource, URI uri, Map<String, TrackedObject> trackedObjects) {
	XMIResource partialResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(uri);
	XMIResource xmiLeftResource = ((XMIResource) leftResource);

	for (TrackedObject trackedObject : trackedObjects.values()) {
	    System.out.println(trackedObject.getId() + " pos = " + trackedObject.getPosition());
	    recursiveCreateLeftPartialObject(xmiLeftResource, partialResource, trackedObjects, trackedObject);
	}

	printTree(partialResource);

	return partialResource;
    }

    private static Resource createPartialResource(Resource leftResource, URI uri, Map<String, TrackedObject> trackedObjects) {
	return createPartialResource(leftResource, uri, trackedObjects, null);
    }

    private static Resource createPartialResource(Resource leftResource, URI uri, Map<String, TrackedObject> trackedObjects, Resource leftPartialResource) {
	XMIResource partialResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(uri);
	XMIResource xmiLeftResource = ((XMIResource) leftResource);
	XMIResource xmiLeftPartialResource = ((XMIResource) leftPartialResource);

	System.out.println(partialResource.getURI().toString());

	// Creating all objects first
	for (TrackedObject trackedObject : trackedObjects.values()) {
	    System.out.println(trackedObject.getId());
	    createPartialEObject(xmiLeftResource, partialResource, trackedObjects, trackedObject, xmiLeftPartialResource);
	    EObject eObject = partialResource.getEObject(trackedObject.getId());
	    if (eObject != null) {
		eObject.eSet(eObject.eClass().getEStructuralFeature("name"), trackedObject.getId());
	    }
	}

	// moving all objects to its proper position
	for (TrackedObject trackedObject : trackedObjects.values()) {
	    if (trackedObject.getContainer() != null) {
		EObject container = partialResource.getEObject(trackedObject.getContainer());
		if (container != null) {
		    EObject eObject = partialResource.getEObject(trackedObject.getId());
		    EStructuralFeature feature = container.eClass().getEStructuralFeature(trackedObject.getContainingFeature());
		    if (feature instanceof EReference && ((EReference) feature).isContainment()) {
			if (feature.isMany() == true) {
			    int pos = trackedObject.getPosition();
			    if (pos == -1) {
				((EList<EObject>) container.eGet(feature)).add(pos, eObject);
			    } else {
				((EList<EObject>) container.eGet(feature)).add(eObject);
			    }
			} else {
			    container.eSet(feature, eObject);
			}
		    }
		}
	    }
	}

	// setting all their attributes' values
	for (TrackedObject trackedObject : trackedObjects.values()) {
	    EObject eObject = partialResource.getEObject(trackedObject.getId());
	    if (eObject != null) {
		for (TrackedFeature trackedFeature : trackedObject.getFeatures().values()) {
		    String featureName = trackedFeature.getFeatureName();
		    EStructuralFeature feature = eObject.eClass().getEStructuralFeature(featureName);
		    if (feature instanceof EReference && ((EReference) feature).isContainment() == false) {
			if (feature.isMany() == false) {
			    String value = trackedFeature.getValue();
			    eObject.eSet(feature, value);
			} else {
			    EList<EObject> list = (EList<EObject>) eObject.eGet(feature);
			    for (Entry<Integer, String> entry : trackedFeature.getValues().entrySet()) {
				int pos = entry.getKey();
				String value = entry.getValue();
				EObject valueObject = partialResource.getEObject(value);
				list.add(pos, valueObject);
			    }

			}
		    } else if (feature instanceof EAttribute) {
			if (feature.isMany() == false) {
			    String value = trackedFeature.getValue();
			    eObject.eSet(feature, value);
			} else {
			    EList<Object> list = (EList<Object>) eObject.eGet(feature);
			    for (Entry<Integer, String> entry : trackedFeature.getValues().entrySet()) {
				int pos = entry.getKey();
				String value = entry.getValue();
				list.add(pos, value);
			    }

			}
		    }

		}
	    }
	}

	// for (TrackedFeature trackedFeature :
	// trackedObject.getFeatures().values()) {
	// EStructuralFeature eStructuralFeature =
	// eObject.eClass().getEStructuralFeature(trackedFeature.getFeatureName());
	// Object value = eObject.eGet(eStructuralFeature);
	// if (eStructuralFeature instanceof EAttribute) {
	// if (eStructuralFeature.isMany() == false) {
	// eObject.eSet(eStructuralFeature, trackedFeature.getValue());
	// } else if (eStructuralFeature.isMany() == true) {
	// EList<Object> eList = (EList<Object>) value;
	// for (Entry<Integer, String> entry :
	// trackedFeature.getValues().entrySet()) {
	// int pos = entry.getKey();
	// String valueString = entry.getValue();
	// if (pos + 1 > eList.size()) {
	// for (int i = eList.size(); i < pos + 1; i++) {
	// eList.add(i, null);
	// }
	// eList.add(valueString);
	// } else {
	// eList.remove(pos);
	// eList.add(pos, valueString);
	// }
	// }
	// }
	// } else if (eStructuralFeature instanceof EReference) {
	// if (eStructuralFeature.isMany() == false) {
	// eObject.eSet(eStructuralFeature, trackedFeature.getValue());
	// } else if (eStructuralFeature.isMany() == true) {
	// EList<Object> eList = (EList<Object>) value;
	// for (Entry<Integer, String> entry :
	// trackedFeature.getValues().entrySet()) {
	// int pos = entry.getKey();
	// String valueString = entry.getValue();
	// if (pos + 1 > eList.size()) {
	// for (int i = eList.size(); i < pos + 1; i++) {
	// EObject dummy = ePackage.getEFactoryInstance().create(eClass);
	// eList.add(i, dummy);
	// }
	// EObject dummy = partialResource.getEObject(valueString);
	// eList.add(dummy);
	// } else {
	// eList.remove(pos);
	// EObject dummy = partialResource.getEObject(valueString);
	// eList.add(pos, dummy);
	// }
	// }
	// }
	// }
	// }

	// EObject eObject =
	// xmiLeftResource.getEObject(trackedObject.getId());
	// EObject partialEObject = null;
	// if (eObject != null) {
	// partialEObject = EcoreUtil.copy(eObject);
	// partialResource.getContents().add(partialEObject);
	// partialResource.setID(partialEObject, trackedObject.getId());
	//
	// }

	return partialResource;
    }

    private static void createPartialEObject(XMIResource xmiLeftResource, XMIResource partialResource, Map<String, TrackedObject> trackedObjects, TrackedObject trackedObject,
	    XMIResource leftPartialResource) {

	if (trackedObject.getId().equals(ComparisonEvent.RESOURCE_STRING)) {
	    return;
	}

	EObject eObject = partialResource.getEObject(trackedObject.getId());
	EObject leftResourceEObject = null;
	String className = null;

	// get class name from the current partial resource
	if (trackedObject.getClassName() != null) {
	    className = trackedObject.getClassName();
	}
	// get class name from the left resource
	if (className == null) {
	    if (leftPartialResource != null) {
		leftResourceEObject = leftPartialResource.getEObject(trackedObject.getId());
		if (leftResourceEObject != null)
		    className = leftResourceEObject.eClass().getName();
	    }
	    leftResourceEObject = xmiLeftResource.getEObject(trackedObject.getId());
	    if (leftResourceEObject != null)
		className = leftResourceEObject.eClass().getName();
	}

	if (eObject == null && className != null) {
	    EClass eClass = (EClass) ePackage.getEClassifier(className);
	    eObject = ePackage.getEFactoryInstance().create(eClass);
	}

	// if (trackedObject.getContainer() != null &&
	// !ComparisonEvent.RESOURCE_STRING.equals(trackedObject.getContainer()))
	// {
	// EObject container =
	// partialResource.getEObject(trackedObject.getContainer());
	// if (container == null) {
	// createPartialEObject(xmiLeftResource, partialResource,
	// trackedObjects, trackedObjects.get(trackedObject.getContainer()),
	// leftPartialResource);
	// container = partialResource.getEObject(trackedObject.getContainer());
	// if (container == null) {
	// container =
	// EcoreUtil.copy(xmiLeftResource.getEObject(trackedObject.getContainer()));
	// }
	// }
	//
	// if (container != null) {
	// EStructuralFeature feature =
	// container.eClass().getEStructuralFeature(trackedObject.getContainingFeature());
	// if (feature instanceof EReference) {
	// if (feature.isMany() == true) {
	// int pos = trackedObject.getPosition();
	// if (pos == -1) {
	// ((EList<EObject>) container.eGet(feature)).add(pos, eObject);
	// } else {
	// ((EList<EObject>) container.eGet(feature)).add(eObject);
	// }
	// } else {
	// container.eSet(feature, eObject);
	// }
	// }
	// }
	// }
	//
	//
	// eObject.eSet(eObject.eClass().getEStructuralFeature("name"),
	// trackedObject.getId());
	partialResource.getContents().add(eObject);
	partialResource.setID(eObject, trackedObject.getId());

    }

    private static CBPObjectEventTracker addLeftSideObjectEventsToTracker(final Resource resource, final List<ComparisonEvent> comparisonEvents)
	    throws ParserConfigurationException, TransformerException {
	return addObjectToTracker(resource, comparisonEvents);
    }

    private static CBPObjectEventTracker addRightSideObjectEventsToTracker(final Resource resource, List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
	return addObjectToTracker(resource, comparisonEvents);
    }

    private static CBPObjectEventTracker addObjectToTracker(final Resource resource, List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {

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
	return createTrackedObjectList(rightTracker, null);
    }

    private static Map<String, TrackedObject> createTrackedObjectList(CBPObjectEventTracker rightTracker, Map<String, TrackedObject> leftTrackedObjects)
	    throws ParserConfigurationException, TransformerException {
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
		    if (event.getTargetId() != null && !id.equals(event.getTargetId())) {
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
		    if (event.getPosition() != -1) {
			trackedObject.setPosition(event.getPosition());
		    } else if (event.getTo() != -1) {
			trackedObject.setPosition(event.getTo());
		    }

		    // set containing feature name
		    if (event.getFeatureName() != null) {
			if (trackedObject.getOldContainingFeature() == null) {
			    trackedObject.setOldContainingFeature(event.getFeatureName());
			}
			trackedObject.setContainingFeature(event.getFeatureName());
		    }

		    // if (trackedObject.getContainer() != null &&
		    // trackedObject.getPosition() != -1 &&
		    // trackedObject.getContainingFeature() != null) {
		    // break;
		    // }
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

    private static void printTree(XMIResource resource) {
	for (EObject eObject : resource.getContents()) {
	    int level = 0;
	    recursivePrint(level, resource, eObject);
	}
    }

    private static void recursivePrint(int level, XMIResource resource, EObject eObject) {
	String id = resource.getID(eObject);
	for (int i = 0; i < level; i++) {
	    System.out.print("+--");
	}
	System.out.println(id);
	EClass eClass = eObject.eClass();
	EList<EStructuralFeature> features = eClass.getEAllStructuralFeatures();
	for (EStructuralFeature feature : features) {
	    if (feature instanceof EReference && ((EReference) feature).isContainment()) {
		if (feature.isMany() == false) {
		    EObject value = (EObject) eObject.eGet(feature);
		    if (value != null) {
			recursivePrint(level + 1, resource, value);
		    }
		} else {
		    EList<EObject> values = (EList<EObject>) eObject.eGet(feature);
		    for (EObject value : values) {
			if (value != null) {
			    recursivePrint(level + 1, resource, value);
			}
		    }
		}
	    }
	}
    }

    private static void setEObjectName(EObject eObject, String name) {
	if (eObject != null) {
	    EStructuralFeature feature = eObject.eClass().getEStructuralFeature("name");
	    if (feature != null) {
		eObject.eSet(feature, name);
	    }
	}
    }

}