package org.eclipse.epsilon.cbp.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;

public class ResourceContentsToEventsConverter {
	
	List<Event> events;
	HashSet<EObject> eObjectsCounter = new HashSet<EObject>();

	public ResourceContentsToEventsConverter() {}
	
	public List<Event> convert(Resource resource) {
		
		// if resource is empty, do nothing
		Iterator<EObject> iterator = resource.getAllContents();
		if (!iterator.hasNext()) {
			return Collections.emptyList();
		}

		events = new ArrayList<Event>();
		
		// for all EObjects
		while (iterator.hasNext()) {

			// get obj
			EObject obj = iterator.next();

			if (!eObjectsCounter.contains(obj)) {
				eObjectsCounter.add(obj);

				// create event to add to resource
				AddToResourceEvent e = new AddToResourceEvent(obj);
				events.add(e);

				// handle all attributes
				handleAttributes(obj);

				// handle all references
				handleReferences(obj);
			} else {
				// handle all attributes
				handleAttributes(obj);

				// handle all references
				handleReferences(obj);
			}
		}
		
		return events;
		
	}

	public void handleAttributes(EObject obj) {
		// for each EAttribute
		for (EAttribute attr : obj.eClass().getEAllAttributes()) {
			// if attribute is set, changeable, non-transient and non-volatile
			if (obj.eIsSet(attr) && attr.isChangeable() && !attr.isTransient() && !attr.isVolatile()) {
				if (attr.isMany()) {
					// create add to attribute event
					AddToEAttributeEvent e = new AddToEAttributeEvent(obj, attr, obj.eGet(attr));
					events.add(e);
				} else {
					SetEAttributeEvent e = new SetEAttributeEvent(obj, attr, obj.eGet(attr));
					events.add(e);
				}

			}
		}
	}

	public void handleReferences(EObject obj) {
		for (EReference ref : obj.eClass().getEAllReferences()) {
			if (obj.eIsSet(ref)) {
				if (ref.isContainment()) {
					if (ref.isMany()) {
						handleContainmentManyReference(obj, obj.eGet(ref), ref);
					} else {
						handleContainmentSingleReference(obj, obj.eGet(ref), ref);
					}
				} else {
					if (ref.isMany()) {
						handleNonContainmentManyReference(obj, obj.eGet(ref), ref);
					} else {
						handleNonContainmentSingleReference(obj, obj.eGet(ref), ref);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleNonContainmentManyReference(EObject focusObject, Object value, EReference eRef) {
		// prepare an eobject list
		List<EObject> eObjectList = new ArrayList<EObject>();

		// if value is collection
		if (value instanceof Collection) {
			eObjectList = (List<EObject>) value;
		}
		// if value is single object
		else {
			eObjectList.add((EObject) value);
		}

		AddToEReferenceEvent e = new AddToEReferenceEvent(focusObject, eRef, value);
		events.add(e);
	}

	@SuppressWarnings("unchecked")
	private void handleNonContainmentSingleReference(EObject focusObject, Object value, EReference eRef) {
		// prepare an eobject list
		List<EObject> eObjectList = new ArrayList<EObject>();

		// if value is collection
		if (value instanceof Collection) {
			System.err.println("non-containment-should not happen");
			eObjectList = (List<EObject>) value;
		}
		// if value is single object
		else {
			eObjectList.add((EObject) value);
		}

		SetEReferenceEvent e = new SetEReferenceEvent(focusObject, eRef, value);
		events.add(e);
	}

	@SuppressWarnings("unchecked")
	private void handleContainmentManyReference(EObject focusObject, Object value, EReference eRef) {
		// prepare an eobject list
		List<EObject> eObjectList = new ArrayList<EObject>();

		// if value is collection
		if (value instanceof Collection) {
			eObjectList = (List<EObject>) value;
		}
		// if value is single object
		else {
			eObjectList.add((EObject) value);
		}

		// for each obj in the list, create add to ereference event
		for (EObject obj : eObjectList) {
			if (eObjectsCounter.contains(obj)) {

			} else {
				AddToResourceEvent addEObjectsToResourceEvent = new AddToResourceEvent(obj);
				events.add(addEObjectsToResourceEvent);
				eObjectsCounter.add(obj);
			}
		}
		AddToEReferenceEvent e = new AddToEReferenceEvent(focusObject, eRef, value);
		events.add(e);
	}

	@SuppressWarnings("unchecked")
	private void handleContainmentSingleReference(EObject focusObject, Object value, EReference eRef) {
		// prepare an eobject list
		List<EObject> eObjectList = new ArrayList<EObject>();

		// if value is collection
		if (value instanceof Collection) {
			System.err.println("should not happen");
			eObjectList = (List<EObject>) value;
		}
		// if value is single object
		else {
			eObjectList.add((EObject) value);
		}

		for (EObject obj : eObjectList) {
			if (!eObjectsCounter.contains(obj)) {
				AddToResourceEvent addEObjectsToResourceEvent = new AddToResourceEvent(obj);
				events.add(addEObjectsToResourceEvent);
				eObjectsCounter.add(obj);
			}
		}
		SetEReferenceEvent e = new SetEReferenceEvent(focusObject, eRef, value);
		events.add(e);
	}

}
