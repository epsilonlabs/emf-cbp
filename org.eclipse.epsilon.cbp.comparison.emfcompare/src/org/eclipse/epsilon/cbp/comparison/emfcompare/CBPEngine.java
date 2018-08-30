package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.print.attribute.standard.MediaSize.Other;
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
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;

public class CBPEngine {

    public static final String PARTIAL_MODE = "PARTIAL_MODE";
    public static final String FULL_MODE = "FULL_MODE";

    private static EPackage ePackage;

    private static String mode = FULL_MODE;
    private static Resource leftResource;
    private static Resource rightResource;
    private static Resource originResource;
    private static Resource leftPartialResource;
    private static Resource rightPartialResource;
    private static Resource originPartialResource;
    private static List<ComparisonEvent> localComparisonEvents;
    private static List<ComparisonEvent> rightComparisonEvents;
    private static CBPObjectEventTracker rightTracker;
    private static CBPObjectEventTracker localTracker;
    private static Map<String, TrackedObject> rightTrackedObjects;
    private static Map<String, TrackedObject> leftTrackedObjects;
    private static Map<EObject, String> dummyObjects = new HashMap<>();

    public enum CurrentResourceOrigin {
	LOCAL, MASTER
    }

    public static void createCBPEngine(Resource left, Resource right, Resource origin, String treeMode) {

	try {
	    mode = treeMode;
	    leftResource = left;
	    rightResource = right;

	    String localPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(left.getURI().toPlatformString(true))).getRawLocation().toOSString();
	    localPath = localPath.replaceAll(".xmi", ".cbpxml");
	    String masterPath = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(right.getURI().toPlatformString(true))).getRawLocation().toOSString();
	    masterPath = masterPath.replaceAll(".xmi", ".cbpxml");

	    CBPComparison cbpComparison = new CBPComparison(new File(localPath), new File(masterPath));
	    cbpComparison.compare();

	    if (ePackage == null && cbpComparison.getRightNsURI() != null) {
		ePackage = EPackage.Registry.INSTANCE.getEPackage(cbpComparison.getRightNsURI());
	    } else if (ePackage == null && cbpComparison.getLeftNsURI() != null) {
		ePackage = EPackage.Registry.INSTANCE.getEPackage(cbpComparison.getLeftNsURI());
	    } else if (ePackage == null && left.getContents().size() > 0) {
		ePackage = left.getContents().get(0).eClass().getEPackage();
	    }

	    if (CBPEngine.mode == CBPEngine.PARTIAL_MODE) {

		localComparisonEvents = cbpComparison.getLeftComparisonEvents();
		rightComparisonEvents = cbpComparison.getRightComparisonEvents();

		localTracker = addLeftSideObjectEventsToTracker(left, localComparisonEvents);
		rightTracker = addRightSideObjectEventsToTracker(right, rightComparisonEvents);

		leftTrackedObjects = createTrackedObjectList(localTracker);
		rightTrackedObjects = createTrackedObjectList(rightTracker, leftTrackedObjects);
		
		leftTrackedObjects.remove(ComparisonEvent.RESOURCE_STRING);
		rightTrackedObjects.remove(ComparisonEvent.RESOURCE_STRING);
		

		leftPartialResource = (new XMIResourceFactoryImpl()).createResource(leftResource.getURI());
		rightPartialResource = (new XMIResourceFactoryImpl()).createResource(rightResource.getURI());
		originPartialResource = (new XMIResourceFactoryImpl()).createResource(URI.createURI("origin.xmi"));

		constructPartialResources();

		// rightPartialResource = originPartialResource;

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

    private static void constructPartialResources() {

	System.out.println("Create ORIGIN model using information from LEFT CBP");
	for (TrackedObject trackedObject : leftTrackedObjects.values()) {
	    System.out.println("Processing id = " + trackedObject.getId() + ", old-pos = " + trackedObject.getOldPosition() + ", new-pos = " + trackedObject.getPosition());
	    if (trackedObject.isNew() == false) {
		recursiveCreateOldPartialObject(originPartialResource, leftTrackedObjects, trackedObject);
	    }
	}

	System.out.println("Create ORIGIN model using information from RIGHT CBP");
	for (TrackedObject trackedObject : rightTrackedObjects.values()) {
	    System.out.println("Processing id = " + trackedObject.getId() + ", old-pos = " + trackedObject.getOldPosition() + ", new-pos = " + trackedObject.getPosition());
	    if (trackedObject.isNew() == false) {
		recursiveCreateOldPartialObject(originPartialResource, rightTrackedObjects, trackedObject);
	    }
	}

	// copy origin to left resource
	leftPartialResource.getContents().addAll(EcoreUtil.copyAll(originPartialResource.getContents()));
	Iterator<EObject> originIterator = originPartialResource.getAllContents();
	Iterator<EObject> leftIterator = leftPartialResource.getAllContents();
	while (originIterator.hasNext() && leftIterator.hasNext()) {
	    EObject originEObject = originIterator.next();
	    EObject leftEObject = leftIterator.next();
	    String id = ((XMIResource) originPartialResource).getID(originEObject);
	    System.out.println("Copying EObject id = " + id + " to left resource ...");
	    ((XMIResource) leftPartialResource).setID(leftEObject, id);
	}

	System.out.println("Create LATEST model using information from LEFT CBP");
	Iterator<Entry<String, TrackedObject>>  iteratorLeft = leftTrackedObjects.entrySet().iterator();
	while(iteratorLeft.hasNext()) {
	    TrackedObject trackedObject = iteratorLeft.next().getValue();
	    System.out.println("Processing id = " + trackedObject.getId() + ", old-pos = " + trackedObject.getOldPosition() + ", new-pos = " + trackedObject.getPosition());
	    if (trackedObject.isNew() == false && trackedObject.isDeleted() == false && (trackedObject.getOldPosition() > -1 && trackedObject.getPosition() > -1)
		    && (trackedObject.getOldContainer() != null && trackedObject.getOldContainingFeature() != null) && (trackedObject.getOldPosition() != trackedObject.getPosition()
			    || !trackedObject.getContainingFeature().equals(trackedObject.getOldContainingFeature()) || !trackedObject.getContainer().equals(trackedObject.getOldContainer()))) {

		EObject eObject = ((XMIResource) leftPartialResource).getEObject(trackedObject.getId());
		EcoreUtil.delete(eObject);
		recursiveCreatePartialObject(leftPartialResource, leftTrackedObjects, trackedObject);
		iteratorLeft.remove();
	    }else if (trackedObject.isNew() == false) {
		iteratorLeft.remove();
	    }
	}
	
	for (TrackedObject trackedObject : leftTrackedObjects.values()) {
	    if (trackedObject.isNew() == true && trackedObject.isDeleted() == false) {
		recursiveCreatePartialObject(leftPartialResource, leftTrackedObjects, trackedObject);
	    }
	}

	// copy origin to right resource
	rightPartialResource.getContents().addAll(EcoreUtil.copyAll(originPartialResource.getContents()));
	Iterator<EObject> originIterator2 = originPartialResource.getAllContents();
	Iterator<EObject> leftIterator2 = rightPartialResource.getAllContents();
	while (originIterator2.hasNext() && leftIterator2.hasNext()) {
	    EObject originEObject = originIterator2.next();
	    EObject leftEObject = leftIterator2.next();
	    String id = ((XMIResource) originPartialResource).getID(originEObject);
	    System.out.println("Copying EObject id = " + id + " to right resource ...");
	    ((XMIResource) rightPartialResource).setID(leftEObject, id);
	}

	System.out.println("Create LATEST model using information from RIGHT CBP");
	Iterator<Entry<String, TrackedObject>>  iteratorRight = rightTrackedObjects.entrySet().iterator();
	while(iteratorRight.hasNext()) {
	    TrackedObject trackedObject = iteratorRight.next().getValue();
	    System.out.println("Processing id = " + trackedObject.getId() + ", old-pos = " + trackedObject.getOldPosition() + ", new-pos = " + trackedObject.getPosition());
	    if (trackedObject.isNew() == false && trackedObject.isDeleted() == false && (trackedObject.getOldPosition() > -1 && trackedObject.getPosition() > -1)
		    && (trackedObject.getOldContainer() != null && trackedObject.getOldContainingFeature() != null) && (trackedObject.getOldPosition() != trackedObject.getPosition()
			    || !trackedObject.getContainingFeature().equals(trackedObject.getOldContainingFeature()) || !trackedObject.getContainer().equals(trackedObject.getOldContainer()))) {

		EObject eObject = ((XMIResource) rightPartialResource).getEObject(trackedObject.getId());
		EcoreUtil.delete(eObject);
		recursiveCreatePartialObject(rightPartialResource, rightTrackedObjects, trackedObject);
		iteratorRight.remove();
	    } else if (trackedObject.isNew() == false) {
		iteratorRight.remove();
	    }
	}
	
	for (TrackedObject trackedObject : rightTrackedObjects.values()) {
	    if (trackedObject.isNew() == true && trackedObject.isDeleted() == false) {
		recursiveCreatePartialObject(rightPartialResource, rightTrackedObjects, trackedObject);
	    }
	}
    }

    private static EObject recursiveCreateOldPartialObject(Resource partialResource, Map<String, TrackedObject> trackedObjects, TrackedObject trackedObject) {
	XMIResource xmipartialResource = (XMIResource) partialResource;
	XMIResource xmiResource = ((XMIResource) leftResource);

	String id = null;
	String className = null;
	String oldContainer = null;
	String oldContainingFeature = null;
	EObject partialEObject = null;
	EObject localResourceEObject = null;
	int oldPos = -1;

	if (trackedObject != null) {
	    id = trackedObject.getId();
	    className = trackedObject.getClassName();
	    oldContainer = trackedObject.getOldContainer();
	    oldContainingFeature = trackedObject.getOldContainingFeature();
	    oldPos = trackedObject.getOldPosition();
	    partialEObject = xmipartialResource.getEObject(id);
	    if (partialEObject != null && partialEObject.eContainingFeature() != null) {
		oldContainingFeature = partialEObject.eContainingFeature().getName();
	    }
	}

	// if object does not exist in current partial resource
	if (partialEObject == null) {
	    // create the an empty object for the eObject
	    if (className != null) {
		EClass eClass = (EClass) ePackage.getEClassifier(className);
		partialEObject = ePackage.getEFactoryInstance().create(eClass);
		// get the object
	    } else if ((localResourceEObject = xmiResource.getEObject(id)) != null) {

		EClass eClass = localResourceEObject.eClass();
		partialEObject = ePackage.getEFactoryInstance().create(eClass);
		trackedObject.setClassName(eClass.getName());

		if (localResourceEObject.eContainingFeature() != null) {
		    oldContainingFeature = localResourceEObject.eContainingFeature().getName();
		    trackedObject.setContainingFeature(oldContainingFeature);
		}

		if (oldContainer == null && localResourceEObject.eContainer() != null) {
		    oldContainer = xmiResource.getID(localResourceEObject.eContainer());
		    trackedObject.setContainer(oldContainer);
		}

		if (oldPos == -1) {
		    if (localResourceEObject.eContainingFeature() != null && localResourceEObject.eContainingFeature().isMany()) {
			EList<EObject> list = (EList<EObject>) localResourceEObject.eContainer().eGet(localResourceEObject.eContainingFeature());
			int pos = list.indexOf(localResourceEObject);
			oldPos = pos;
			trackedObject.setOldPosition(oldPos);
			trackedObject.setPosition(pos);
		    } else if (localResourceEObject.eResource() != null) {
			int pos = localResourceEObject.eResource().getContents().indexOf(localResourceEObject);
			oldPos = pos;
			trackedObject.setOldPosition(oldPos);
			trackedObject.setPosition(pos);
		    }
		}
	    }
	    // get the eObject from the xmiLeftResource / local resource
	    else if (oldContainer != null && oldContainingFeature != null) {
		EObject containerEObject = xmiResource.getEObject(oldContainer);
		if (containerEObject != null) {
		    EClass eClass = (EClass) containerEObject.eClass().getEStructuralFeature(oldContainingFeature).getEType();
		    partialEObject = ePackage.getEFactoryInstance().create(eClass);
		    trackedObject.setClassName(eClass.getName());
		}
	    }

	    // at this point eObject should have been created

	    // should check container, if it's not null then create
	    // container object
	    if (oldContainer != null && oldContainingFeature != null) {

		TrackedObject containerTrackedObject = trackedObjects.get(oldContainer);
		if (containerTrackedObject == null) {
		    containerTrackedObject = new TrackedObject(oldContainer);
		}

		EObject containerEObject = recursiveCreateOldPartialObject(xmipartialResource, trackedObjects, containerTrackedObject);

		addEObjectToItsContainer(xmipartialResource, id, oldContainingFeature, partialEObject, oldPos, containerEObject, trackedObjects);
	    }
	    // if the eObject is contained in the
	    else if (localResourceEObject != null && localResourceEObject.eContainer() != null) {
		String containerId = xmiResource.getID(localResourceEObject);
		TrackedObject containerTrackedObject = trackedObjects.get(containerId);
		EObject containerEObject = recursiveCreateOldPartialObject(xmipartialResource, trackedObjects, containerTrackedObject);

		trackedObject.setContainer(containerId);

		addEObjectToItsContainer(xmipartialResource, id, oldContainingFeature, partialEObject, oldPos, containerEObject, trackedObjects);

	    }

	    // add the object to partial resource if it doesn't have any
	    // container
	    else if (partialEObject != null) {

		if (oldPos > xmipartialResource.getContents().size()) {
		    for (int i = xmipartialResource.getContents().size(); i < oldPos; i++) {
			EClass eClass = partialEObject.eClass();
			EObject dummy = ePackage.getEFactoryInstance().create(eClass);
			xmipartialResource.getContents().add(i, dummy);
			String dummyId = "resource." + i;
			xmipartialResource.setID(dummy, dummyId);
			setEObjectName(dummy, id);
		    }
		}

		if (oldPos > -1 && oldPos < xmipartialResource.getContents().size() && dummyObjects.containsKey(xmipartialResource.getContents().get(oldPos))) {
		    dummyObjects.remove(xmipartialResource.getContents().get(oldPos));
		    xmipartialResource.getContents().set(oldPos, partialEObject);
		} else if (oldPos > -1) {
		    xmipartialResource.getContents().add(oldPos, partialEObject);
		} else {
		    xmipartialResource.getContents().add(partialEObject);
		}
		xmipartialResource.setID(partialEObject, id);
		setEObjectName(partialEObject, id);

	    }
	}

	return partialEObject;
    }

    private static EObject recursiveCreatePartialObject(Resource partialResource, Map<String, TrackedObject> trackedObjects, TrackedObject trackedObject) {
	XMIResource xmipartialResource = (XMIResource) partialResource;
	XMIResource xmiResource = ((XMIResource) leftResource);

	String id = null;
	String className = null;
	String container = null;
	String containingFeature = null;
	EObject partialEObject = null;
	EObject localResourceEObject = null;
	int pos = -1;

	if (trackedObject != null) {
	    id = trackedObject.getId();
	    className = trackedObject.getClassName();
	    container = trackedObject.getContainer();
	    containingFeature = trackedObject.getContainingFeature();
	    pos = trackedObject.getPosition();
	    partialEObject = xmipartialResource.getEObject(id);
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
		// get the object
	    } else if ((localResourceEObject = xmiResource.getEObject(id)) != null) {

		EClass eClass = localResourceEObject.eClass();
		partialEObject = ePackage.getEFactoryInstance().create(eClass);
		trackedObject.setClassName(eClass.getName());

		if (localResourceEObject.eContainingFeature() != null) {
		    containingFeature = localResourceEObject.eContainingFeature().getName();
		    trackedObject.setContainingFeature(containingFeature);
		}

		if (container == null && localResourceEObject.eContainer() != null) {
		    container = xmiResource.getID(localResourceEObject.eContainer());
		    trackedObject.setContainer(container);
		}

		if (pos == -1) {
		    if (localResourceEObject.eContainingFeature() != null && localResourceEObject.eContainingFeature().isMany()) {
			EList<EObject> list = (EList<EObject>) localResourceEObject.eContainer().eGet(localResourceEObject.eContainingFeature());
			int newPos = list.indexOf(localResourceEObject);
			pos = newPos;
			trackedObject.setPosition(pos);
		    } else if (localResourceEObject.eResource() != null) {
			int newPos = localResourceEObject.eResource().getContents().indexOf(localResourceEObject);
			pos = newPos;
			trackedObject.setPosition(pos);
		    }
		}
	    }
	    // get the eObject from the xmiLeftResource / local resource
	    else if (container != null && containingFeature != null) {
		EObject containerEObject = xmiResource.getEObject(container);
		if (containerEObject != null) {
		    EClass eClass = (EClass) containerEObject.eClass().getEStructuralFeature(containingFeature).getEType();
		    partialEObject = ePackage.getEFactoryInstance().create(eClass);
		    trackedObject.setClassName(eClass.getName());
		}
	    }

	    // at this point eObject should have been created

	    // should check container, if it's not null then create
	    // container object
	    if (container != null && containingFeature != null) {

		TrackedObject containerTrackedObject = trackedObjects.get(container);
		if (containerTrackedObject == null) {
		    containerTrackedObject = new TrackedObject(container);
		}

		EObject containerEObject = recursiveCreatePartialObject(xmipartialResource, trackedObjects, containerTrackedObject);

		addEObjectToItsContainer(xmipartialResource, id, containingFeature, partialEObject, pos, containerEObject, trackedObjects);
	    }
	    // if the eObject is contained in the
	    else if (localResourceEObject != null && localResourceEObject.eContainer() != null) {
		String containerId = xmiResource.getID(localResourceEObject);
		TrackedObject containerTrackedObject = trackedObjects.get(containerId);
		EObject containerEObject = recursiveCreatePartialObject(xmipartialResource, trackedObjects, containerTrackedObject);

		trackedObject.setContainer(containerId);

		addEObjectToItsContainer(xmipartialResource, id, containingFeature, partialEObject, pos, containerEObject, trackedObjects);

	    }

	    // add the object to partial resource if it doesn't have any
	    // container
	    else if (partialEObject != null) {

		if (pos > xmipartialResource.getContents().size()) {
		    for (int i = xmipartialResource.getContents().size(); i < pos; i++) {
			EClass eClass = partialEObject.eClass();
			EObject dummy = ePackage.getEFactoryInstance().create(eClass);
			xmipartialResource.getContents().add(i, dummy);
			String dummyId = "resource." + i;
			xmipartialResource.setID(dummy, dummyId);
			setEObjectName(dummy, id);
		    }
		}

		if (pos > -1 && pos < xmipartialResource.getContents().size() && dummyObjects.containsKey(xmipartialResource.getContents().get(pos))) {
		    dummyObjects.remove(xmipartialResource.getContents().get(pos));
		    xmipartialResource.getContents().set(pos, partialEObject);
		} else if (pos > -1) {
		    xmipartialResource.getContents().add(pos, partialEObject);
		} else {
		    xmipartialResource.getContents().add(partialEObject);
		}
		xmipartialResource.setID(partialEObject, id);
		setEObjectName(partialEObject, id);

	    }
	}

	return partialEObject;
    }

    protected static void addEObjectToItsContainer(XMIResource partialResource, String id, String containingFeature, EObject partialEObject, int pos, EObject containerEObject,
	    Map<String, TrackedObject> trackedObjects) {

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
			String containerId = partialResource.getID(containerEObject);
			for (int i = list.size(); i < pos; i++) {
			    EClass eClass = (EClass) feature.getEType();
			    EObject dummy = ePackage.getEFactoryInstance().create(eClass);
			    list.add(i, dummy);
			    String dummyId = containerId + "." + feature.getName() + "." + i;
			    partialResource.setID(dummy, dummyId);
			    setEObjectName(dummy, dummyId);
			    dummyObjects.put(dummy, dummyId);

			}
		    }
		    if (pos < list.size() && dummyObjects.containsKey(list.get(pos))) {
			String dummyId = dummyObjects.get(list.get(pos));
			dummyObjects.remove(list.get(pos));

			list.set(pos, partialEObject);
		    } else {
			if (pos < list.size() && list.get(pos) != null) {
			    list.add(pos + 1, partialEObject);
			} else {
			    list.add(pos, partialEObject);
			}
		    }
		    partialResource.setID(partialEObject, id);
		    setEObjectName(partialEObject, id);
		}
	    }
	}
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

		    // set flag isNew if the object is new or created in the
		    // recent changes
		    if (event.getEventType() == CreateEObjectEvent.class) {
			trackedObject.setNew(true);
		    }

		    // set flag isDeleted if the object is deleted in the recent
		    // changes
		    if (event.getEventType() == DeleteEObjectEvent.class) {
			trackedObject.setDeleted(true);
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
	return localComparisonEvents;
    }

    public static List<ComparisonEvent> getRightComparisonEvents() {
	return rightComparisonEvents;
    }

    public static CBPObjectEventTracker getRightTracker() {
	return rightTracker;
    }

    public static CBPObjectEventTracker getLeftTracker() {
	return localTracker;
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

    private static void printTree(Resource resource) {
	for (EObject eObject : resource.getContents()) {
	    int level = 0;
	    recursivePrint(level, resource, eObject);
	}
    }

    private static void recursivePrint(int level, Resource resource, EObject eObject) {
	String id = ((XMIResource) resource).getID(eObject);
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
