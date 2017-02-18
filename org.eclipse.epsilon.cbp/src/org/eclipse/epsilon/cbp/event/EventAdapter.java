package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EContentsEList;

public class EventAdapter extends EContentAdapter {
	
	protected List<Event> events = new ArrayList<Event>();
	private boolean enabled = true;
	protected HashSet<EPackage> ePackages = new HashSet<EPackage>();
	private HashSet<EObject> processedEObjects = new HashSet<EObject>();

	// constructor
	public EventAdapter() {
		super();
	}
	
	public List<Event> getEvents() {
		return events;
	}
	
	@Override
	public void notifyChanged(Notification n) {
		super.notifyChanged(n);

		if (n.isTouch() || !enabled) { return; }

		switch (n.getEventType()) {
		
		case Notification.ADD: {
			if (n.getNotifier() instanceof Resource) {
				if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
					// create add to resource event
					EObject newValue = (EObject) n.getNewValue();
					handleEPackageOf(newValue);
					events.add(new AddToResourceEvent(n));
					handleEObject(newValue);
				}
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() != null) {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature.isChangeable() && !feature.isDerived()) {
						if (feature instanceof EAttribute) {
							events.add(new AddToEAttributeEvent(n));
						} else if (n.getFeature() instanceof EReference) {
							events.add(new AddToEReferenceEvent(n));
							handleEObject((EObject) n.getNewValue());
						}
					}
				}
			}
			break;
		}

		//TODO: Create UnsetEAttributeEvent and UnsetEReferenceEvent
		case Notification.UNSET: {
			if (n.getNotifier() instanceof EObject) {
				EStructuralFeature feature = (EStructuralFeature) n.getFeature();
				if (feature != null) {
					if (feature instanceof EAttribute) {
						events.add(new RemoveFromEAttributeEvent(n));
					} else if (feature instanceof EReference) {
						events.add(new RemoveFromEReferenceEvent(n));
					}
				}
			}
			break;
		}

		case Notification.SET: {

			if (n.getNotifier() instanceof Resource) {
				if (n.getNewValue() != null) {
					if (n.getNewValue() instanceof EObject) {
						EObject newValue = (EObject) n.getNewValue();
						handleEPackageOf(newValue);
						events.add(new AddToResourceEvent(n));
						handleEObject(newValue);
					}
				} else {
					events.add(new RemoveFromResourceEvent(n));
				}
			} else if (n.getNotifier() instanceof EObject) {
				processedEObjects.add((EObject) n.getNotifier());

				if (n.getNewValue() != null) {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature != null && feature.isChangeable() && !feature.isDerived()) {
						if (feature instanceof EAttribute) {
							events.add(new SetEAttributeEvent(n));
						} else if (feature instanceof EReference) {
							events.add(new SetEReferenceEvent(n));
							handleEObject((EObject) n.getNewValue());
						}
					}
				} else {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature instanceof EAttribute) {
						events.add(new RemoveFromEAttributeEvent(n));
					} else if (feature instanceof EReference) {
						events.add(new RemoveFromEReferenceEvent(n));
					}
				}
			}
			break;
		}

		case Notification.ADD_MANY: {
			if (n.getNotifier() instanceof Resource) {
				for (EObject obj : (Collection<EObject>) n.getNewValue()) {
					handleEPackageOf(obj);
					events.add(new AddToResourceEvent(obj));
					handleEObject(obj);
				}
			} else if (n.getNotifier() instanceof EObject) {
				EObject focusObject = (EObject) n.getNotifier();
				processedEObjects.add(focusObject);
				if (n.getFeature() instanceof EAttribute) {
					EAttribute attribute = (EAttribute) n.getFeature();
					if (attribute.isChangeable() && !attribute.isDerived()) {
						//List<Object> list = (List<Object>) n.getNewValue();
						//for (Object obj : list) {
							events.add(new AddToEAttributeEvent(focusObject, attribute, (List<Object>) n.getNewValue()));
						//}
					}
				} else if (n.getFeature() instanceof EReference) {
					EReference eReference = (EReference) n.getFeature();
					if (eReference.isChangeable() && !eReference.isDerived()) {
						List<EObject> list = (List<EObject>) n.getNewValue();
						for (EObject obj : list) {
							handleEPackageOf(obj);
						}
						events.add(new AddToEReferenceEvent(focusObject, eReference, list));
						for (EObject obj : list) {
							handleEObject(obj);
						}
					}
				}
			}
			break;
		}
		case Notification.REMOVE: {
			if (n.getOldValue() instanceof EObject) {
				if (n.getNotifier() instanceof Resource) {
					events.add(new RemoveFromResourceEvent(n));
				} else if (n.getNotifier() instanceof EObject) {
					events.add(new RemoveFromEReferenceEvent(n));
				}
			} else if (n.getFeature() instanceof EAttribute) {
				events.add(new RemoveFromEAttributeEvent(n));
			}
			break;
		}
		case Notification.REMOVE_MANY: {
			@SuppressWarnings("unchecked")
			List<Object> removed = (List<Object>) n.getOldValue();
			if (removed.size() > 0) {
				if (n.getNotifier() instanceof Resource) {
					events.add(new RemoveFromResourceEvent(removed));
				} else if (n.getNotifier() instanceof EObject) {
					EObject focusObject = (EObject) n.getNotifier();
					if (n.getFeature() instanceof EAttribute) {
						EAttribute eAttribute = (EAttribute) n.getFeature();
						events.add(new RemoveFromEAttributeEvent(focusObject, eAttribute, removed));
					} else if (n.getFeature() instanceof EReference) {
						EReference eReference = (EReference) n.getFeature();
						events.add(new RemoveFromEReferenceEvent(focusObject, eReference, removed));
					}
				}
			}
			break;
		}
		default: {
			System.err.println("EventAdapter: Unhandled notification!" + n.toString());
			break;
		}
		}
	}

	public void setEnabled(boolean bool) {
		enabled = bool;
	}
	
	@Override
	protected void setTarget(EObject target) {
		if (target.eAdapters().contains(this)) // fixes stack overflow on
												// opposite ref
			return;

		super.setTarget(target);
		for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			Notifier notifier = featureIterator.next();
			addAdapter(notifier);
		}
	}

	@Override
	protected void unsetTarget(EObject target) {
		super.unsetTarget(target);
		for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			Notifier notifier = featureIterator.next();
			removeAdapter(notifier);
		}
	}

	@Override
	protected void selfAdapt(Notification notification) {
		super.selfAdapt(notification);
		if (notification.getNotifier() instanceof EObject) {
			Object feature = notification.getFeature();
			if (feature instanceof EReference) {
				EReference eReference = (EReference) feature;
				if (!eReference.isContainment()) {
					handleContainment(notification);
				}
			}
		}
	}
	
	public void handleEPackageOf(EObject eObject) {
		EPackage ePackage = eObject.eClass().getEPackage();
		if (!ePackages.contains(ePackage)) {
			ePackages.add(ePackage);
			events.add(new RegisterEPackageEvent(ePackage));
		}
	}

	public void handleEObject(EObject obj) {
		if (!processedEObjects.contains(obj)) {
			//add current object to processed objects
			processedEObjects.add(obj);
			
			for (EStructuralFeature feature : obj.eClass().getEAllStructuralFeatures()) {
				if (feature.isChangeable() && !feature.isDerived()) {
					if (obj.eIsSet(feature)) {
						if (feature instanceof EAttribute) {
							if (feature.isMany()) {
								events.add(new AddToEAttributeEvent(obj, (EAttribute) feature, obj.eGet(feature)));
							} else {
								events.add(new SetEAttributeEvent(obj, (EAttribute) feature, obj.eGet(feature)));
							}
						}
						if (feature instanceof EReference) {
							if (feature.isMany()) {
								events.add(new AddToEReferenceEvent(obj, (EReference) feature, obj.eGet(feature)));
								@SuppressWarnings("unchecked")
								EList<EObject> eList = (EList<EObject>) obj.eGet(feature);
								for (EObject eObject : eList) {
									if (!processedEObjects.contains(eObject)) {
										handleEObject(eObject);
										processedEObjects.add(eObject);
									}
								}
							} else {
								events.add(new SetEReferenceEvent(obj, (EReference) feature, obj.eGet(feature)));
								EObject eObject = (EObject) obj.eGet(feature);
								if (!processedEObjects.contains(eObject)) {
									handleEObject(eObject);
									processedEObjects.add(eObject);
								}
							}
						}
					}
				}
			}
		}
	}
}
