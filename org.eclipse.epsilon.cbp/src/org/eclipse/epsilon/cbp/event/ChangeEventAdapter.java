package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList.UnmodifiableEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.DanglingHREFException;
import org.eclipse.epsilon.cbp.history.ModelHistory;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class ChangeEventAdapter extends EContentAdapter {

    protected Notification previousNotification;
    protected List<ChangeEvent<?>> changeEvents = new ArrayList<ChangeEvent<?>>();
    protected boolean enabled = true;
    protected HashSet<EPackage> ePackages = new HashSet<EPackage>();
    protected CBPResource resource = null;
    protected ModelHistory modelHistory = null;
    protected int eventNumber = 0;

    protected int setAttCount = 0;
    protected int unsetAttCount = 0;
    protected int addAttCount = 0;
    protected int removeAttCount = 0;
    protected int moveAttCount = 0;
    protected int setRefCount = 0;
    protected int unsetRefCount = 0;
    protected int addRefCount = 0;
    protected int removeRefCount = 0;
    protected int moveRefCount = 0;
    protected int deleteCount = 0;
    protected int addResCount = 0;
    protected int removeResCount = 0;
    protected int packageCount = 0;
    protected int sessionCount = 0;
    protected int createCount = 0;

    protected EObject monitoredObject;

    protected String compositeId = null;

    public boolean isEnabled() {
	return enabled;
    }

    public HashSet<EPackage> getePackages() {
	return ePackages;
    }

    public CBPResource getResource() {
	return resource;
    }

    public ModelHistory getModelHistory() {
	return modelHistory;
    }

    public int getEventNumber() {
	return eventNumber;
    }

    public int getSetAttCount() {
	return setAttCount;
    }

    public int getUnsetAttCount() {
	return unsetAttCount;
    }

    public int getAddAttCount() {
	return addAttCount;
    }

    public int getRemoveAttCount() {
	return removeAttCount;
    }

    public int getMoveAttCount() {
	return moveAttCount;
    }

    public int getSetRefCount() {
	return setRefCount;
    }

    public int getUnsetRefCount() {
	return unsetRefCount;
    }

    public int getAddRefCount() {
	return addRefCount;
    }

    public int getRemoveRefCount() {
	return removeRefCount;
    }

    public int getMoveRefCount() {
	return moveRefCount;
    }

    public int getDeleteCount() {
	return deleteCount;
    }

    public int getAddResCount() {
	return addResCount;
    }

    public int getRemoveResCount() {
	return removeResCount;
    }

    public int getPackageCount() {
	return packageCount;
    }

    public int getSessionCount() {
	return sessionCount;
    }

    public int getCreateCount() {
	return createCount;
    }

    public ChangeEventAdapter(CBPResource resource) {
	this.resource = resource;
    }

    public ChangeEventAdapter(CBPResource resource, ModelHistory modelHistory) {
	this.resource = resource;
	this.modelHistory = modelHistory;
    }

    public List<ChangeEvent<?>> getChangeEvents() {
	return changeEvents;
    }

    @Override
    public void notifyChanged(Notification n) {

	// this.debugEvents(n);
	super.notifyChanged(n);

	if (n.isTouch() || !enabled) {
	    return;
	}

	if (n.getFeature() != null) {
	    EStructuralFeature feature = (EStructuralFeature) n.getFeature();
	    if (!feature.isChangeable() || feature.isDerived())
		return;
	}

	if (n.getNewValue() != null && n.getNewValue() instanceof DanglingHREFException) {
	    return;
	}

	if (n.getOldValue() != null && n.getOldValue() instanceof DanglingHREFException) {
	    return;
	}

	ChangeEvent<?> event = null;

	switch (n.getEventType()) {
	case Notification.ADD: {
	    if (n.getNotifier() instanceof Resource) {
		if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
		    event = new AddToResourceEvent();
		    event.setComposite(compositeId);
		    addResCount++;
		}
	    } else if (n.getNotifier() instanceof EObject) {
		if (n.getFeature() != null) {
		    EStructuralFeature feature = (EStructuralFeature) n.getFeature();
		    if (feature instanceof EAttribute) {
			event = new AddToEAttributeEvent();
			event.setComposite(compositeId);
			addAttCount++;
		    } else if (n.getFeature() instanceof EReference) {
			// if (previousNotification.getEventType() ==
			// Notification.REMOVE) {
			// } else {
			event = new AddToEReferenceEvent();
			event.setComposite(compositeId);
			addRefCount++;
			// }
		    }
		} else {
		    event = new AddToResourceEvent();
		    event.setComposite(compositeId);
		    addResCount++;
		}
	    }
	    event.setValues(n.getNewValue());
	    if (n.getOldValue() != null) {
		event.setOldValues(n.getOldValue());
	    }
	    break;
	}

	case Notification.UNSET: {
	    if (n.getNotifier() instanceof EObject) {
		EStructuralFeature feature = (EStructuralFeature) n.getFeature();
		if (feature instanceof EAttribute) {
		    event = new UnsetEAttributeEvent();
		    event.setComposite(compositeId);
		    // event.setValues(n.getOldValue());
		    event.setOldValues(n.getOldValue());
		    unsetAttCount++;
		} else if (feature instanceof EReference) {
		    event = new UnsetEReferenceEvent();
		    event.setComposite(compositeId);
		    // event.setValues(n.getOldValue());
		    event.setOldValues(n.getOldValue());
		    unsetRefCount++;
		}
	    }
	    break;
	}

	case Notification.SET: {
	    if (n.getNotifier() instanceof EObject) {
		EStructuralFeature feature = (EStructuralFeature) n.getFeature();
		if (n.getNewValue() != null) {
		    if (feature instanceof EAttribute) {
			event = new SetEAttributeEvent();
			event.setComposite(compositeId);
			setAttCount++;
		    } else if (feature instanceof EReference) {
			EReference opposite = ((EReference) feature).getEOpposite();
			if (opposite == null || !opposite.isMany() || !opposite.isChangeable()) {
			    // In arrangements with 1:N pairs of opposite
			    // references,
			    // ignore the "1" side as it contains less
			    // information.
			    event = new SetEReferenceEvent();
			    event.setComposite(compositeId);
			    setRefCount++;
			}
		    }

		    if (event != null) {
			event.setValues(n.getNewValue());
			event.setOldValues(n.getOldValue());
		    }
		} else {
		    if (feature instanceof EAttribute) {
			event = new UnsetEAttributeEvent();
			event.setComposite(compositeId);
			// event.setValues(n.getNewValue());
			event.setOldValues(n.getOldValue());
			unsetAttCount++;
		    } else if (feature instanceof EReference) {
			event = new UnsetEReferenceEvent();
			event.setComposite(compositeId);
			// event.setValues(n.getNewValue());
			event.setOldValues(n.getOldValue());
			unsetRefCount++;
		    }
		}
	    }
	    break;
	}

	case Notification.ADD_MANY: {
	    @SuppressWarnings("unchecked")
	    Collection<Object> values = (Collection<Object>) n.getNewValue();
	    int position = n.getPosition();
	    for (Object value : values) {
		ChangeEvent<?> evt = null;
		if (n.getNotifier() instanceof Resource) {
		    evt = new AddToResourceEvent();
		    evt.setComposite(compositeId);
		    addResCount++;
		} else if (n.getNotifier() instanceof EObject) {
		    if (n.getFeature() instanceof EAttribute) {
			evt = new AddToEAttributeEvent();
			evt.setComposite(compositeId);
			addAttCount++;
		    } else if (n.getFeature() instanceof EReference) {
			evt = new AddToEReferenceEvent();
			evt.setComposite(compositeId);
			addRefCount++;
		    }
		}
		if (evt != null) {
		    evt.setValues(value);
		    addEventToList(evt, n, position++);
		}
	    }

	    break;
	}

	case Notification.REMOVE: {
	    if (n.getOldValue() instanceof EObject) {
		if (n.getNotifier() instanceof Resource) {
		    event = new RemoveFromResourceEvent();
		    event.setComposite(compositeId);
		    removeResCount++;
		} else if (n.getNotifier() instanceof EObject) {
		    // if (((EObject) n.getOldValue()).eContainer() != null) {
		    // System.out.println("Mark for move");
		    // } else {
		    // System.out.println("Mark for deletion");
		    // }
		    event = new RemoveFromEReferenceEvent();
		    event.setComposite(compositeId);
		    removeRefCount++;
		}
	    } else if (n.getFeature() instanceof EAttribute) {
		event = new RemoveFromEAttributeEvent();
		event.setComposite(compositeId);
		removeAttCount++;
	    }
	    event.setValues(n.getOldValue());
	    break;
	}

	case Notification.REMOVE_MANY: {
	    @SuppressWarnings("unchecked")
	    Collection<Object> values = (Collection<Object>) n.getOldValue();
	    int position = n.getPosition();
	    for (Object value : values) {
		if (value instanceof DanglingHREFException) {
		    continue;
		}
		ChangeEvent<?> evt = null;
		if (n.getNotifier() instanceof Resource) {
		    evt = new RemoveFromResourceEvent();
		    evt.setComposite(compositeId);
		    removeResCount++;
		} else if (n.getNotifier() instanceof EObject) {
		    if (n.getFeature() instanceof EAttribute) {
			evt = new RemoveFromEAttributeEvent();
			evt.setComposite(compositeId);
			removeAttCount++;
		    } else if (n.getFeature() instanceof EReference) {
			evt = new RemoveFromEReferenceEvent();
			evt.setComposite(compositeId);
			removeRefCount++;
		    }
		}

		evt.setValues(value);
		addEventToList(evt, n, position++);
	    }
	    break;
	}

	case Notification.MOVE: {
	    if (n.getNotifier() instanceof EObject) {
		FromPositionEvent fromEv = null;
		if (n.getFeature() instanceof EAttribute) {
		    MoveWithinEAttributeEvent moveEvent = new MoveWithinEAttributeEvent();
		    moveEvent.setComposite(compositeId);
		    moveAttCount++;
		    fromEv = moveEvent;
		    event = moveEvent;
		    event.setValues(n.getNewValue());
		} else if (n.getFeature() instanceof EReference) {
		    MoveWithinEReferenceEvent moveEvent = new MoveWithinEReferenceEvent();
		    moveEvent.setComposite(compositeId);
		    moveRefCount++;
		    fromEv = moveEvent;
		    event = moveEvent;
		    event.setValues(n.getNewValue());
		}
		if (fromEv != null) {
		    fromEv.setFromPosition(((Number) n.getOldValue()).intValue());
		}
	    }
	    break;
	}

	default: {
	    System.err.println("EventAdapter: Unhandled notification!" + n.toString());
	    break;
	}
	}
	this.addEventToList(event, n);

	previousNotification = n;
    }

    /**
     * @param n
     */
    private void debugEvents(Notification n) {
	switch (n.getEventType()) {
	case Notification.SET: {
	    System.out.print("SET");
	}
	    break;
	case Notification.UNSET: {
	    System.out.print("UNSET");
	}
	    break;
	case Notification.ADD: {
	    System.out.print("ADD");
	}
	    break;
	case Notification.REMOVE: {
	    System.out.print("REMOVE");
	}
	    break;
	case Notification.ADD_MANY: {
	    System.out.print("ADD_MANY");
	}
	    break;
	case Notification.REMOVE_MANY: {
	    System.out.print("REMOVE_MANY");
	}
	    break;
	case Notification.MOVE: {
	    System.out.print("MOVE");
	}
	    break;
	case Notification.REMOVING_ADAPTER: {
	    System.out.print("REMOVING_ADAPTER");
	}
	    break;
	case Notification.RESOLVE: {
	    System.out.print("RESOLVE");
	}
	    break;
	case Notification.EVENT_TYPE_COUNT: {
	    System.out.print("EVENT_TYPE_COUNT");
	}
	    break;
	case Notification.CREATE: {
	    System.out.print("CREATE");
	}
	    break;

	case Notification.NO_FEATURE_ID: {
	    System.out.print("NO_FEATURE_ID");
	}
	    break;
	default: {
	    System.out.print("OTHER");
	}
	    break;
	}
	System.out.print(": " + n.getNotifier());
	System.out.print(" : " + n.getOldValue());

	if (n.getOldValue() != null && n.getOldValue() instanceof EObject) {
	    System.out.print(" XX: " + ((EObject) n.getOldValue()).eResource());
	}
	if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
	    System.out.print(" YY: " + ((EObject) n.getNewValue()).eResource());
	}

	System.out.println(" : " + n.getNewValue());
    }

    protected void addEventToList(ChangeEvent<?> event, Notification n) {
	this.addEventToList(event, n, -1);
    }

    protected void addEventToList(ChangeEvent<?> event, Notification n, int position) {

	// Features which are not meant to be serialised are defined as "unset"
	if (event instanceof SetEReferenceEvent || event instanceof AddToEReferenceEvent) {
	    EStructuralFeature feature = (EStructuralFeature) n.getFeature();
	    if (!((EObject) n.getNotifier()).eIsSet(feature))
		return;
	}

	if (event instanceof EObjectValuesEvent) {
	    if (((EObjectValuesEvent) event).getValues().isEmpty()) {
	    } else {
		for (EObject obj : ((EObjectValuesEvent) event).getValues()) {
		    handleEPackageOf(obj);
		    if (n.getNotifier() instanceof Resource && (Resource) n.getNotifier() == obj.eResource()) {
			handleCreateEObject(obj);
		    } else if (n.getNotifier() instanceof EObject && ((EObject) n.getNotifier()).eResource() == obj.eResource()) {
			handleCreateEObject(obj);
		    }
		}
	    }
	}

	if (event instanceof EStructuralFeatureEvent<?>) {
	    ((EStructuralFeatureEvent<?>) event).setEStructuralFeature((EStructuralFeature) n.getFeature());
	    ((EStructuralFeatureEvent<?>) event).setTarget(n.getNotifier());
	}
	
	handleOppositeReference(event);

	if (event != null) {
	    if (position > 0) {
		event.setPosition(position);
	    } else {
		event.setPosition(n.getPosition());
	    }
	    if (!(event instanceof UnsetEReferenceEvent) && !(event instanceof RemoveFromResourceEvent) && !(event instanceof RemoveFromEReferenceEvent)) {
		changeEvents.add(event);
	    }
	    // this.addToModelHistory(event, position);
	}

	if (n.getOldValue() instanceof EObject || n.getOldValue() instanceof EList) {
	    if (event instanceof UnsetEReferenceEvent || event instanceof RemoveFromResourceEvent || event instanceof RemoveFromEReferenceEvent) {
		if (n.getOldValue() instanceof EObject) {
		    handleDeletedEObject(event, (EObject) n.getOldValue());
		} else if (n.getOldValue() instanceof EList) {
		    handleDeletedEObject(event, (EObject) event.getValue());
		}
	    }
	}
    }

    /**
     * @param event
     */
    private void handleOppositeReference(ChangeEvent<?> event) {
	if (event instanceof SetEReferenceEvent || event instanceof AddToEReferenceEvent) {
	    EReference eOpposite = ((EReferenceEvent) event).getEReference().getEOpposite();
	    if (eOpposite != null) {
		EObject eTarget = ((EReferenceEvent) event).getTarget();
		EObject eValue = null;
		if (event instanceof AddToEReferenceEvent) {
		    eValue = (EObject) event.getValue();
		} else if (event instanceof SetEReferenceEvent) {
		    eValue = (EObject) event.getValue();
		}
		if (eOpposite.isMany()) {
		    EList<EObject> list = ((EList<EObject>) eValue.eGet(eOpposite));
		    
		    AddToEReferenceEvent e = new AddToEReferenceEvent();
		    e.setComposite(compositeId);
		    addRefCount++;
		    e.setEStructuralFeature(eOpposite);
		    e.setValue(eTarget);
		    e.setTarget(eValue);
		    e.setPosition(list.size());
		    changeEvents.add(e);
		} else {
		    //eValue.eSet(eOpposite, eTarget);
		    
		    SetEReferenceEvent e = new SetEReferenceEvent();
		    e.setComposite(compositeId);
		    addRefCount++;
		    e.setEStructuralFeature(eOpposite);
		    e.setValue(eTarget);
		    e.setTarget(eValue);
		    changeEvents.add(e);
		}
	    }
	}
    }

    public void handleEPackageOf(EObject eObject) {
	EPackage ePackage = eObject.eClass().getEPackage();
	if (!ePackages.contains(ePackage)) {
	    ePackages.add(ePackage);
	    ChangeEvent<?> event = new RegisterEPackageEvent(ePackage, this);
	    event.setComposite(compositeId);
	    packageCount++;
	    changeEvents.add(event);
	    // this.addToModelHistory(event, -1);
	}
    }

    public void handleDeletedEObject(ChangeEvent<?> event, EObject removedObject) {
	this.handleDeletedEObject(event, removedObject, null, null);
    }

    public void handleDeletedEObject(ChangeEvent<?> event, EObject removedObject, Object parent, Object feature) {
	if (removedObject.eResource() == null
	// && removedObject.eAdapters().stream().noneMatch(adapter -> adapter
	// instanceof ChangeEventAdapter)
	) {
	    // System.out.println(removedObject + " : " +
	    // removedObject.eCrossReferences().size());
	    // && (removedObject.eCrossReferences() == null ||
	    // removedObject.eCrossReferences().size() == 0)) {

	    Boolean localComposite = null;
	    if (compositeId == null) {
		localComposite = true;
		startCompositeOperation();
	    }
	    event.setComposite(compositeId);

	    unsetAllEReferences(removedObject);
	    unsetOppositeEReference(event, removedObject);

	    changeEvents.add(event);

	    String id = resource.getURIFragment(removedObject);
	    ChangeEvent<?> deletedEvent = new DeleteEObjectEvent(removedObject, id);
	    deletedEvent.setComposite(compositeId);
	    changeEvents.add(deletedEvent);
	    if (localComposite != null && localComposite == true) {
		endCompositeOperation();
	    }

	} else {
	    changeEvents.add(event);
	}
    }

    private void unsetOppositeEReference(ChangeEvent<?> event, EObject removedObject) {
	EObject eTarget = null;
	EReference eReference = ((EReferenceEvent) event).getEReference();
	EReference eOpposite = eReference.getEOpposite();
	if (eOpposite != null) {
	    if (event instanceof RemoveFromEReferenceEvent) {
		eTarget = ((RemoveFromEReferenceEvent) event).getTarget();
	    } else if (event instanceof UnsetEReferenceEvent) {
		eTarget = ((UnsetEReferenceEvent) event).getTarget();
	    }

	    if (eOpposite.isMany()) {
		int position = ((EList<EObject>) removedObject.eGet(eOpposite)).indexOf(eTarget);
		((EList<EObject>) removedObject.eGet(eOpposite)).remove(eTarget);

		RemoveFromEReferenceEvent e = new RemoveFromEReferenceEvent();
		e.setComposite(compositeId);
		e.setEStructuralFeature(eOpposite);
		e.setValue(eTarget);
		e.setTarget(removedObject);
		e.setPosition(position);
		changeEvents.add(e);
	    } else {
		removedObject.eUnset(eOpposite);

		UnsetEReferenceEvent e = new UnsetEReferenceEvent();
		e.setComposite(compositeId);
		e.setEStructuralFeature(eOpposite);
		e.setOldValue(eTarget);
		e.setTarget(removedObject);
		changeEvents.add(e);
	    }
	}
    }

    public void setEnabled(boolean bool) {
	enabled = bool;
    }

    public void handleStartNewSession(String id) {
	changeEvents.add(new StartNewSessionEvent(id));
	sessionCount++;
    }

    public void handleStartNewSession() {
	changeEvents.add(new StartNewSessionEvent());
	sessionCount++;
    }

    @SuppressWarnings("unchecked")
    public void handleCreateEObject(EObject obj) {
	if (!resource.isRegistered(obj)) {
	    ChangeEvent<?> event = new CreateEObjectEvent(obj, resource.register(obj));
	    event.setComposite(compositeId);
	    createCount++;
	    changeEvents.add(event);
	    // this.addToModelHistory(event, -1);

	    // Include prior attribute values into the resource
	    for (EAttribute eAttr : obj.eClass().getEAllAttributes()) {
		if (eAttr.isChangeable() && obj.eIsSet(eAttr) && !eAttr.isDerived()) {
		    if (eAttr.isMany()) {
			Collection<?> values = (Collection<?>) obj.eGet(eAttr);
			int i = 0;
			for (Object value : values) {
			    AddToEAttributeEvent e = new AddToEAttributeEvent();
			    event.setComposite(compositeId);
			    addAttCount++;
			    e.setEStructuralFeature(eAttr);
			    e.setValue(value);
			    e.setTarget(obj);
			    e.setPosition(i++);
			    changeEvents.add(e);
			    // this.addToModelHistory(e, -1);
			}
		    } else {
			Object value = obj.eGet(eAttr);
			SetEAttributeEvent e = new SetEAttributeEvent();
			event.setComposite(compositeId);
			setAttCount++;
			e.setEStructuralFeature(eAttr);
			e.setValue(value);
			e.setTarget(obj);
			changeEvents.add(e);
			// this.addToModelHistory(e, -1);
		    }
		}
	    }

	    // Include prior reference values into the resource
	    for (EReference eRef : obj.eClass().getEAllReferences()) {

		if (eRef.isChangeable() && obj.eIsSet(eRef) && !eRef.isDerived()) {
		    if (eRef.getEOpposite() != null && eRef.getEOpposite().isMany() && eRef.getEOpposite().isChangeable()) {
			// If this is the "1" side of an 1:N pair of references,
			// ignore it:
			// the "N" side has more information.
			continue;
		    }

		    if (eRef.isMany()) {
			Collection<EObject> values = (Collection<EObject>) obj.eGet(eRef);
			int i = 0;
			for (EObject value : values) {
			    if (value.eResource() == obj.eResource()) {
				handleCreateEObject(value);
			    }

			    AddToEReferenceEvent e = new AddToEReferenceEvent();
			    e.setComposite(compositeId);
			    addRefCount++;
			    e.setEStructuralFeature(eRef);
			    e.setValue(value);
			    e.setTarget(obj);
			    e.setPosition(i++);
			    changeEvents.add(e);
			    // this.addToModelHistory(e, -1);
			}
		    } else {
			EObject value = (EObject) obj.eGet(eRef);
			if (value.eResource() == obj.eResource()) {
			    handleCreateEObject(value);
			}
			SetEReferenceEvent e = new SetEReferenceEvent();
			e.setComposite(compositeId);
			setRefCount++;
			e.setEStructuralFeature(eRef);
			e.setValue(value);
			e.setTarget(obj);
			changeEvents.add(e);
		    }
		}
	    }
	}
    }

    public void startCompositeOperation() {
	compositeId = EcoreUtil.generateUUID();
    }

    public void endCompositeOperation() {
	compositeId = null;
    }

    // ----------
    public void deleteElement(EObject targetEObject) {
	recursiveDeleteEvent(targetEObject);
	removeFromExternalRefferences(targetEObject);
	unsetAllEReferences(targetEObject);
	unsetAllAttributes(targetEObject);
	// EcoreUtil.remove(targetEObject);
    }

    private void recursiveDeleteEvent(EObject targetEObject) {
	for (EReference eRef : targetEObject.eClass().getEAllReferences()) {
	    if (eRef.isChangeable() && targetEObject.eIsSet(eRef) && !eRef.isDerived() && eRef.isContainment()) {
		if (eRef.isMany()) {
		    List<EObject> values = (List<EObject>) targetEObject.eGet(eRef);
		    while (values.size() > 0) {
			EObject value = values.get(values.size() - 1);
			recursiveDeleteEvent(value);
			removeFromExternalRefferences(value);
			unsetAllEReferences(value);
			unsetAllAttributes(value);
			values.remove(value);
		    }
		} else {
		    EObject value = (EObject) targetEObject.eGet(eRef);
		    if (value != null) {
			recursiveDeleteEvent(value);
			removeFromExternalRefferences(value);
			unsetAllEReferences(value);
			unsetAllAttributes(value);
			targetEObject.eUnset(eRef);
		    }
		}
	    }
	}
    }

    private void unsetAllEReferences(EObject targetEObject) {
	// if (targetEObject.eClass().getName().equals("Property")) {
	// System.out.println();
	// }
	for (EReference eRef : targetEObject.eClass().getEAllReferences()) {
	    // if (eRef.getName().equals("owningAssociation")) {
	    // System.out.println(eRef.getName() + ", " + eRef.isRequired() + ",
	    // " + eRef.isUnsettable() + ", " + eRef.isChangeable() + ", " +
	    // targetEObject.eIsSet(eRef) + ", " + eRef.isDerived());
	    // }
	    if (!eRef.isUnsettable() && eRef.isChangeable() && targetEObject.eIsSet(eRef) && !eRef.isDerived() && eRef.isContainment() == false) {
		if (eRef.isMany()) {
		    EList<EObject> values = (EList<EObject>) targetEObject.eGet(eRef);
		    while (values.size() > 0) {
			int position = values.size() - 1;
			EObject value = values.remove(position);

			RemoveFromEReferenceEvent e = new RemoveFromEReferenceEvent();
			e.setComposite(compositeId);
			e.setEStructuralFeature(eRef);
			e.setValue(value);
			e.setTarget(targetEObject);
			e.setPosition(position);
			changeEvents.add(e);
		    }
		} else {
		    EObject value = (EObject) targetEObject.eGet(eRef);
		    if (value != null) {
			targetEObject.eUnset(eRef);

			UnsetEReferenceEvent e = new UnsetEReferenceEvent();
			e.setComposite(compositeId);
			e.setEStructuralFeature(eRef);
			e.setOldValue(value);
			e.setTarget(targetEObject);
			changeEvents.add(e);
		    }
		}
	    }
	}
    }

    private void unsetAllAttributes(EObject targetEObject) {
	for (EAttribute eAttr : targetEObject.eClass().getEAllAttributes()) {
	    if (eAttr.isChangeable() && targetEObject.eIsSet(eAttr) && !eAttr.isDerived()) {
		if (eAttr.isMany()) {
		    EList<?> valueList = (EList<?>) targetEObject.eGet(eAttr);
		    while (valueList.size() > 0) {
			valueList.remove(valueList.size() - 1);
		    }
		} else {
		    Object value = targetEObject.eGet(eAttr);
		    targetEObject.eUnset(eAttr);
		}
	    }
	}
    }

    private void removeFromExternalRefferences(EObject refferedEObject) {
	Iterator<EObject> iterator = resource.getAllContents();
	while (iterator.hasNext()) {
	    EObject refferingEObject = iterator.next();
	    for (EReference eRef : refferingEObject.eClass().getEAllReferences()) {
		if (eRef.isContainment() == false && eRef.isChangeable() == true) {
		    if (eRef.isMany()) {
			List<EObject> valueList = (List<EObject>) refferingEObject.eGet(eRef);
			if (!(valueList instanceof UnmodifiableEList<?>)) {
			    valueList.remove(refferedEObject);
			}
		    } else {
			EObject value = (EObject) refferingEObject.eGet(eRef);
			if (value != null && value.equals(refferedEObject)) {
			    refferingEObject.eUnset(eRef);
			}
		    }
		}
	    }
	}
    }
}