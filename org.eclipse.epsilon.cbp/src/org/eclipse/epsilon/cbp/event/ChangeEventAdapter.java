package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.epsilon.cbp.history.ModelHistory;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class ChangeEventAdapter extends EContentAdapter {

	protected List<ChangeEvent<?>> changeEvents = new ArrayList<ChangeEvent<?>>();
	protected boolean enabled = true;
	protected HashSet<EPackage> ePackages = new HashSet<EPackage>();
	protected CBPResource resource = null;
	protected ModelHistory modelHistory = null;
	protected int eventNumber = 0;
	protected IPreChangeEventHandler preChangeEventHandler = null;

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

	public IPreChangeEventHandler getPostChangeEventHandler() {
		return preChangeEventHandler;
	}

	public void setPostChangeEventHandler(IPreChangeEventHandler postChangeEventHandler) {
		this.preChangeEventHandler = postChangeEventHandler;
	}

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

		super.notifyChanged(n);

		if (n.isTouch() || !enabled) {
			return;
		}

		if (n.getFeature() != null) {
			EStructuralFeature feature = (EStructuralFeature) n.getFeature();
			if (!feature.isChangeable() || feature.isDerived())
				return;
		}

		// if (n.getEventType() == Notification.ADD) {
		// System.out.println("ADD");
		// } else if (n.getEventType() == Notification.ADD_MANY) {
		// System.out.println("ADD MANY");
		// } else if (n.getEventType() == Notification.CREATE) {
		// System.out.println("CREATE");
		// } else if (n.getEventType() == Notification.EVENT_TYPE_COUNT) {
		// System.out.println("EVENT TYPE COUNT");
		// } else if (n.getEventType() == Notification.MOVE) {
		// System.out.println("MOVE");
		// } else if (n.getEventType() == Notification.NO_FEATURE_ID) {
		// System.out.println("NO FEATURE ID");
		// } else if (n.getEventType() == Notification.NO_INDEX) {
		// System.out.println("NO INDEX");
		// } else if (n.getEventType() == Notification.REMOVE) {
		// System.out.println("REMOVE");
		// } else if (n.getEventType() == Notification.REMOVE_MANY) {
		// System.out.println("REMOVE MANY");
		// } else if (n.getEventType() == Notification.REMOVING_ADAPTER) {
		// System.out.println("REMOVING ADAPTER");
		// } else if (n.getEventType() == Notification.RESOLVE) {
		// System.out.println("RESOLVE");
		// } else if (n.getEventType() == Notification.SET) {
		// System.out.println("SET");
		// } else if (n.getEventType() == Notification.UNSET) {
		// System.out.println("UNSET");
		// }
		//
		// if (n.getNotifier() instanceof PropertyImpl) {
		// EStructuralFeature sf = ((EObject)
		// n.getNotifier()).eClass().getEStructuralFeature("opposite");
		// EStructuralFeature sf2 = ((EObject)
		// n.getNotifier()).eClass().getEStructuralFeature("name");
		// System.out.println("+-- " + n.getNotifier());
		// System.out.println("+-- " + n.getFeature());
		// System.out.println("+-- " + n.getNewValue());
		// System.out.println("+-- " + ((EObject) n.getNotifier()).eGet(sf));
		//
		// if (((EObject) n.getNotifier()).eGet(sf2) != null
		// && ((EObject) n.getNotifier()).eGet(sf2).equals("class2")) {
		// monitoredObject = (EObject) n.getNotifier();
		// }
		// }
		//
		// if (monitoredObject != null && monitoredObject instanceof
		// PropertyImpl) {
		// EStructuralFeature sf =
		// monitoredObject.eClass().getEStructuralFeature("opposite");
		// EReference ref = (EReference) sf;
		// System.out.println("x---- " + monitoredObject);
		// // System.out.println("x---- " + sf);
		// System.out.println("x---- " + monitoredObject.eGet(sf));
		// System.out.println("x---- " + ref);
		//
		// if (n.getNotifier() instanceof EObject) {
		// EObject target = (EObject) n.getNotifier();
		// EStructuralFeature sf2 = (EStructuralFeature) n.getFeature();
		// EReference ref2 = (EReference) sf2;
		// Object value = n.getNewValue();
		// System.out.println("v------ " + target);
		// System.out.println("v------ " + sf2);
		// System.out.println("v------ " + value);
		// System.out.println("v------ " + ref2);
		// }
		// }

		ChangeEvent<?> event = null;

		switch (n.getEventType()) {
		case Notification.ADD: {
			if (n.getNotifier() instanceof Resource) {
				if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
					event = new AddToResourceEvent();
					addResCount++;
				}
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() != null) {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature instanceof EAttribute) {
						event = new AddToEAttributeEvent();
						addAttCount++;
					} else if (n.getFeature() instanceof EReference) {
						event = new AddToEReferenceEvent();
						addRefCount++;
					}
				}
			}
			event.setValues(n.getNewValue());
			break;
		}

		case Notification.UNSET: {
			if (n.getNotifier() instanceof EObject) {
				EStructuralFeature feature = (EStructuralFeature) n.getFeature();
				if (feature instanceof EAttribute) {
					event = new UnsetEAttributeEvent();
					unsetAttCount++;
				} else if (feature instanceof EReference) {
					event = new UnsetEReferenceEvent();
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
						setAttCount++;
					} else if (feature instanceof EReference) {
						EReference opposite = ((EReference) feature).getEOpposite();
						if (opposite == null || !opposite.isMany() || !opposite.isChangeable()) {
							// In arrangements with 1:N pairs of opposite
							// references,
							// ignore the "1" side as it contains less
							// information.
							event = new SetEReferenceEvent();
							setRefCount++;
						}
					}

					if (event != null) {
						event.setValues(n.getNewValue());
					}
				} else {
					if (feature instanceof EAttribute) {
						event = new UnsetEAttributeEvent();
						unsetAttCount++;
					} else if (feature instanceof EReference) {
						event = new UnsetEReferenceEvent();
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
					addResCount++;
				} else if (n.getNotifier() instanceof EObject) {
					if (n.getFeature() instanceof EAttribute) {
						evt = new AddToEAttributeEvent();
						addAttCount++;
					} else if (n.getFeature() instanceof EReference) {
						evt = new AddToEReferenceEvent();
						addRefCount++;
					}
				}
				evt.setValues(value);
				addEventToList(evt, n, position++);
			}
			break;
		}

		case Notification.REMOVE: {
			if (n.getOldValue() instanceof EObject) {
				if (n.getNotifier() instanceof Resource) {
					event = new RemoveFromResourceEvent();
					removeResCount++;
				} else if (n.getNotifier() instanceof EObject) {
					event = new RemoveFromEReferenceEvent();
					removeRefCount++;
				}
			} else if (n.getFeature() instanceof EAttribute) {
				event = new RemoveFromEAttributeEvent();
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
				ChangeEvent<?> evt = null;
				if (n.getNotifier() instanceof Resource) {
					evt = new RemoveFromResourceEvent();
					removeResCount++;
				} else if (n.getNotifier() instanceof EObject) {
					if (n.getFeature() instanceof EAttribute) {
						evt = new RemoveFromEAttributeEvent();
						removeAttCount++;
					} else if (n.getFeature() instanceof EReference) {
						evt = new RemoveFromEReferenceEvent();
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
					moveAttCount++;
					fromEv = moveEvent;
					event = moveEvent;
					event.setValues(n.getNewValue());
				} else if (n.getFeature() instanceof EReference) {
					MoveWithinEReferenceEvent moveEvent = new MoveWithinEReferenceEvent();
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

	}

	private void addEventToList(ChangeEvent<?> event, Notification n) {
		this.addEventToList(event, n, -1);
	}

	private void addEventToList(ChangeEvent<?> event, Notification n, int position) {

		// Features which are not meant to be serialised are defined as "unset"
		if (event instanceof SetEReferenceEvent || event instanceof AddToEReferenceEvent) {
			EStructuralFeature feature = (EStructuralFeature) n.getFeature();
			if (!((EObject) n.getNotifier()).eIsSet(feature))
				return;
		}

		if (event instanceof EObjectValuesEvent) {
			if (((EObjectValuesEvent) event).getValues().isEmpty())
				return;
			for (EObject obj : ((EObjectValuesEvent) event).getValues()) {
				handleEPackageOf(obj);
				if (n.getNotifier() instanceof Resource && (Resource) n.getNotifier() == obj.eResource()) {
					handleCreateEObject(obj);
				} else if (n.getNotifier() instanceof EObject
						&& ((EObject) n.getNotifier()).eResource() == obj.eResource()) {
					handleCreateEObject(obj);
				}
			}
		}

		if (event instanceof EStructuralFeatureEvent<?>) {
			((EStructuralFeatureEvent<?>) event).setEStructuralFeature((EStructuralFeature) n.getFeature());
			((EStructuralFeatureEvent<?>) event).setTarget(n.getNotifier());
		}

		if (event != null) {
			if (position > 0) {
				event.setPosition(position);
			} else {
				event.setPosition(n.getPosition());
			}
			changeEvents.add(event);
			// this.addToModelHistory(event, position);
		}

		if (n.getOldValue() instanceof EObject || n.getOldValue() instanceof EList) {
			if (event instanceof UnsetEReferenceEvent || event instanceof RemoveFromResourceEvent
					|| event instanceof RemoveFromEReferenceEvent) {
				if (n.getOldValue() instanceof EObject) {
					handleDeletedEObject((EObject) n.getOldValue());
				} else if (n.getOldValue() instanceof EList) {
					handleDeletedEObject((EObject) event.getValue());
				}
			}
		}
	}

	public void handleEPackageOf(EObject eObject) {
		EPackage ePackage = eObject.eClass().getEPackage();
		if (!ePackages.contains(ePackage)) {
			ePackages.add(ePackage);
			ChangeEvent<?> event = new RegisterEPackageEvent(ePackage, this);
			packageCount++;
			changeEvents.add(event);
			// this.addToModelHistory(event, -1);
		}
	}

	public void handleDeletedEObject(EObject removedObject) {
		this.handleDeletedEObject(removedObject, null, null);
	}

	public void handleDeletedEObject(EObject removedObject, Object parent, Object feature) {
		boolean isDeleted = true;
		TreeIterator<EObject> eAllContents = resource.getAllContents();
		while (eAllContents.hasNext()) {
			EObject eObject = eAllContents.next();
			if (eObject.equals(removedObject)) {
				isDeleted = false;
				break;
			}
		}

		if (isDeleted == true) {
			String id = resource.getEObjectId(removedObject);
			ChangeEvent<?> e = new DeleteEObjectEvent(removedObject, id);
			deleteCount++;
			changeEvents.add(e);
			// this.addToModelHistory(e, -1);
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
			createCount++;
			changeEvents.add(event);
			// this.addToModelHistory(event, -1);

			// Include prior attribute values into the resource
			for (EAttribute eAttr : obj.eClass().getEAllAttributes()) {
				if (eAttr.isChangeable() && obj.eIsSet(eAttr)) {
					if (eAttr.isMany()) {
						Collection<?> values = (Collection<?>) obj.eGet(eAttr);
						int i = 0;
						for (Object value : values) {
							AddToEAttributeEvent e = new AddToEAttributeEvent();
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

				if (eRef.isChangeable() && obj.eIsSet(eRef)) {
					if (eRef.getEOpposite() != null && eRef.getEOpposite().isMany()
							&& eRef.getEOpposite().isChangeable()) {
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
						setRefCount++;
						e.setEStructuralFeature(eRef);
						e.setValue(value);
						e.setTarget(obj);
						if (handlePreChangeEvent(e) == false) {
							return;
						}
						changeEvents.add(e);
					}
				}
			}
		}
	}

	private boolean handlePreChangeEvent(ChangeEvent<?> changeEvent) {
		boolean result = true;
		if (preChangeEventHandler != null && changeEvent != null) {
			if (preChangeEventHandler.isCancelled(changeEvent)) {
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

}