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

	// flag adapter_enabled
	private boolean enabled = true;

	protected HashSet<EPackage> ePackages = new HashSet<EPackage>();

	protected EPackage currentEPackage = null;

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

		// if n is touch and no adapter enabled, return
		if (n.isTouch() || !enabled) {
			return;
		}

		// switch by event type
		switch (n.getEventType()) {
		// if event is ADD
		case Notification.ADD: {
			if (n.getNotifier() instanceof Resource) {
				if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
					// create add to resource event
					EObject newValue = (EObject) n.getNewValue();

					handleEPackageOf(newValue);
					
					// add AddEObjectsTOResourceEvent
					events.add(new AddToResourceEvent(n));

					// handle properties recursively
					handleEObject(newValue);
				}
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() != null) {
					// add to processedEObjects
					// processedEObjects.add((EObject) n.getNotifier());

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

		// if event is SET
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

		// if event is add many
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
						List<Object> list = (List<Object>) n.getNewValue();
						for (Object obj : list) {
							events.add(new AddToEAttributeEvent(focusObject, attribute, obj));
						}
					}
				} else if (n.getFeature() instanceof EReference) {
					EReference eReference = (EReference) n.getFeature();
					if (eReference.isChangeable() && !eReference.isDerived()) {
						List<EObject> list = (List<EObject>) n.getNewValue();
						for (EObject obj : list) {
							handleEPackageOf(obj);
							events.add(new AddToEReferenceEvent(focusObject, eReference, obj));
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
			List<Object> list = (List<Object>) n.getOldValue();
			if (list.size() == 0) {

			} else {
				if (n.getNotifier() instanceof Resource) {
					for (Object obj : list) {
						events.add(new RemoveFromResourceEvent((EObject) obj));
					}
				} else if (n.getNotifier() instanceof EObject) {
					EObject focusObject = (EObject) n.getNotifier();
					if (n.getFeature() instanceof EAttribute) {
						EAttribute eAttribute = (EAttribute) n.getFeature();
						for (Object obj : list) {
							events.add(new RemoveFromEAttributeEvent(focusObject, eAttribute, obj));
						}
					} else if (n.getFeature() instanceof EReference) {
						EReference eReference = (EReference) n.getFeature();
						for (Object obj : list) {
							events.add(new RemoveFromEReferenceEvent(focusObject, obj, eReference));
						}
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

	/*
	 * The following code (which allows subclass EContentAdapter to receive
	 * notifications across non containment references was copied (almost, see
	 * setTarget) verbatim from :
	 * http://wiki.eclipse.org/EMF/Recipes#Recipe:_Subclass_EContentAdapter
	 * _to_receive_notifications_across_non-containment_references
	 */

	/**
	 * By default, all cross document references are followed. Usually this is
	 * not a great idea so this class can be subclassed to customize.
	 * 
	 * @param feature
	 *            a cross document reference
	 * @return whether the adapter should follow it
	 */
	protected boolean shouldAdapt(EStructuralFeature feature) {
		return true;
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
			EStructuralFeature feature = featureIterator.feature();
			if (shouldAdapt(feature)) {
				addAdapter(notifier);
			}
		}
	}

	@Override
	protected void unsetTarget(EObject target) {
		super.unsetTarget(target);
		for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target
				.eCrossReferences().iterator(); featureIterator.hasNext();) {
			Notifier notifier = featureIterator.next();
			EStructuralFeature feature = featureIterator.feature();
			if (shouldAdapt(feature)) {
				removeAdapter(notifier);
			}
		}
	}

	@Override
	protected void selfAdapt(Notification notification) {
		super.selfAdapt(notification);
		if (notification.getNotifier() instanceof EObject) {
			Object feature = notification.getFeature();
			if (feature instanceof EReference) {
				EReference eReference = (EReference) feature;
				if (!eReference.isContainment() && shouldAdapt(eReference)) {
					handleContainment(notification);
				}
			}
		}
	}
	
	/*
	public Changelog getChangelog() {
		return changelog;
	}*/

	public void handleEPackageOf(EObject eObject) {
		EPackage ePackage = eObject.eClass().getEPackage();
		if (currentEPackage == null) {
			currentEPackage = ePackage;
			ePackages.add(ePackage);
			events.add(new RegisterEPackageEvent(ePackage));
		} else {
			if (!ePackage.equals(currentEPackage)) {
				if (ePackages.contains(ePackage)) {
					currentEPackage = ePackage;
				} else {
					ePackages.add(ePackage);
					currentEPackage = ePackage;
					events.add(new RegisterEPackageEvent(ePackage));
				}
			}
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
