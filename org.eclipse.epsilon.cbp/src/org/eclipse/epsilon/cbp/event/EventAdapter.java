package org.eclipse.epsilon.cbp.event;

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
import org.eclipse.epsilon.cbp.util.Changelog;

public class EventAdapter extends EContentAdapter {
	// change log
	protected final Changelog changelog;

	// flag adapter_enabled
	private boolean adapter_enabled = true;

	protected HashSet<EPackage> ePackages = new HashSet<EPackage>();

	protected EPackage currentEPackage = null;

	private HashSet<EObject> processedEObjects = new HashSet<EObject>();

	// constructor
	public EventAdapter(Changelog aChangelog) {
		super();
		this.changelog = aChangelog;
	}

	public void showLog() {
		changelog.printLog();
	}

	@Override
	public void notifyChanged(Notification n) {
		super.notifyChanged(n);

		// if n is touch and no adapter enabled, return
		if (n.isTouch() || !adapter_enabled) {
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

					// if EPackage is not registered before, add event
					EPackage ePackage = newValue.eClass().getEPackage();
					handleEPackage(ePackage);

					// add AddEObjectsTOResourceEvent
					changelog.addEvent(new AddEObjectsToResourceEvent(n));

					// handle properties recursively
					handleEObject(newValue);
				}
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() != null) {
					// add to processedEObjects
					processedEObjects.add((EObject) n.getNotifier());

					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature instanceof EAttribute) {
						if (feature.isMany()) {
							// create add to attribute event
							changelog.addEvent(new AddToEAttributeEvent(n));
						} else {
							changelog.addEvent(new SetEAttributeEvent(n));
						}

					} else if (n.getFeature() instanceof EReference) {
						if (feature.isMany()) {
							// create add to reference event
							changelog.addEvent(new AddToEReferenceEvent(n));

							EObject obj = (EObject) n.getNotifier();
							EList<EObject> eList = (EList<EObject>) obj.eGet(feature);
							for (EObject eObject : eList) {
								handleEObject(eObject);
							}
						} else {
							changelog.addEvent(new SetEReferenceEvent(n));
							EObject obj = (EObject) n.getNotifier();
							EObject eObject = (EObject) obj.eGet(feature);
							handleEObject(eObject);
						}
					}
				} else {
					// should not happen in here
					System.err.println("EventAdapter: remove action in ADD");
				}
			}
			break;
		}

		case Notification.UNSET: {
			if (n.getNotifier() instanceof EObject) {
				EStructuralFeature feature = (EStructuralFeature) n.getFeature();
				if (feature != null) {
					if (feature instanceof EAttribute) {
						changelog.addEvent(new RemoveFromEAttributeEvent(n));
					} else if (feature instanceof EReference) {
						changelog.addEvent(new RemoveFromEReferenceEvent(n));
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

						// if EPackage is not registered before, add event
						EPackage ePackage = newValue.eClass().getEPackage();
						handleEPackage(ePackage);

						// create addEObjectsToResourceEvent
						changelog.addEvent(new AddEObjectsToResourceEvent(n));

						// handle properties recursively
						handleEObject(newValue);
					}
				} else {
					changelog.addEvent(new RemoveFromResourceEvent(n));
				}
			} else if (n.getNotifier() instanceof EObject) {
				processedEObjects.add((EObject) n.getNotifier());

				if (n.getNewValue() != null) {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature != null) {
						if (feature instanceof EAttribute) {
							if (feature.isMany()) {
								// this should not happen, in theory
								changelog.addEvent(new AddToEAttributeEvent(n));
							} else {
								changelog.addEvent(new SetEAttributeEvent(n));
							}

						} else if (feature instanceof EReference) {
							if (feature.isMany()) {
								changelog.addEvent(new AddToEReferenceEvent(n));
								EObject obj = (EObject) n.getNotifier();
								EList<EObject> eList = (EList<EObject>) obj.eGet(feature);
								for (EObject eObject : eList) {
									handleEObject(eObject);
								}

							} else {
								changelog.addEvent(new SetEReferenceEvent(n));
								EObject obj = (EObject) n.getNotifier();
								EObject eObject = (EObject) obj.eGet(feature);
								handleEObject(eObject);
							}
						}
					}
				} else {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature != null) {
						if (feature instanceof EAttribute) {
							changelog.addEvent(new RemoveFromEAttributeEvent(n));
						} else if (feature instanceof EReference) {
							changelog.addEvent(new RemoveFromEReferenceEvent(n));
						}
					}
				}
			}
			break;
		}

		// if event is add many
		case Notification.ADD_MANY: {
			processedEObjects.add((EObject) n.getNotifier());
			if (n.getNotifier() instanceof Resource) {
				EList<EObject> list = (EList<EObject>) n.getNewValue();
				for (EObject obj : list) {
					EPackage ePackage = obj.eClass().getEPackage();
					handleEPackage(ePackage);
					changelog.addEvent(new AddEObjectsToResourceEvent(obj));
					handleEObject(obj);
				}
			} else if (n.getNotifier() instanceof EObject) {
				EObject focusObject = (EObject) n.getNotifier();
				if (n.getFeature() != null) {
					if (n.getFeature() instanceof EAttribute) {
						EAttribute attribute = (EAttribute) n.getFeature();
						List<Object> list = (List<Object>) n.getNewValue();
						for (Object obj : list) {
							changelog.addEvent(new AddToEAttributeEvent(focusObject, attribute, obj));
						}
					} else if (n.getFeature() instanceof EReference) {
						EReference eReference = (EReference) n.getFeature();
						List<EObject> list = (List<EObject>) n.getNewValue();
						for (EObject obj : list) {
							EPackage ePackage = obj.eClass().getEPackage();
							handleEPackage(ePackage);
							changelog.addEvent(new AddToEReferenceEvent(focusObject, eReference, obj));
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
					changelog.addEvent(new RemoveFromResourceEvent(n));
				} else if (n.getNotifier() instanceof EObject) {
					changelog.addEvent(new RemoveFromEReferenceEvent(n));
				}
			} else if (n.getFeature() instanceof EAttribute) {
				changelog.addEvent(new RemoveFromEAttributeEvent(n));
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
						changelog.addEvent(new RemoveFromResourceEvent(obj));
					}
				} else if (n.getNotifier() instanceof EObject) {
					EObject focusObject = (EObject) n.getNotifier();
					if (n.getFeature() instanceof EAttribute) {
						EAttribute eAttribute = (EAttribute) n.getFeature();
						for (Object obj : list) {
							changelog.addEvent(new RemoveFromEAttributeEvent(focusObject, eAttribute, obj));
						}
					} else if (n.getFeature() instanceof EReference) {
						EReference eReference = (EReference) n.getFeature();
						for (Object obj : list) {
							changelog.addEvent(new RemoveFromEReferenceEvent(focusObject, obj, eReference));
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
		adapter_enabled = bool;
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

	public Changelog getChangelog() {
		return changelog;
	}

	public void handleEPackage(EPackage ePackage) {
		if (currentEPackage == null) {
			currentEPackage = ePackage;
			ePackages.add(ePackage);
			changelog.addEvent(new EPackageRegistrationEvent(Event.REGISTER_EPACKAGE, ePackage));
		} else {
			if (!ePackage.equals(currentEPackage)) {
				if (ePackages.contains(ePackage)) {
					currentEPackage = ePackage;
				} else {
					ePackages.add(ePackage);
					currentEPackage = ePackage;
					changelog.addEvent(new EPackageRegistrationEvent(Event.REGISTER_EPACKAGE, ePackage));
				}
			}
		}
	}

	public void handleEObject(EObject obj) {
		if (processedEObjects.contains(obj)) {
			
		}
		else {
			//add current object to processed objects
			processedEObjects.add(obj);
			
			for (EStructuralFeature feature : obj.eClass().getEAllStructuralFeatures()) {
				if (obj.eIsSet(feature)) {
					if (feature instanceof EAttribute) {
						if (feature.isMany()) {
							changelog.addEvent(new AddToEAttributeEvent(obj, (EAttribute) feature, obj.eGet(feature)));
						} else {
							changelog.addEvent(new SetEAttributeEvent(obj, (EAttribute) feature, obj.eGet(feature)));
						}
					}
					if (feature instanceof EReference) {
						if (feature.isMany()) {
							changelog.addEvent(new AddToEReferenceEvent(obj, (EReference) feature, obj.eGet(feature)));
							@SuppressWarnings("unchecked")
							EList<EObject> eList = (EList<EObject>) obj.eGet(feature);
							for (EObject eObject : eList) {
								if (!processedEObjects.contains(eObject)) {
									handleEObject(eObject);
									processedEObjects.add(eObject);
								}
							}
						} else {
							changelog.addEvent(new SetEReferenceEvent(obj, (EReference) feature, obj.eGet(feature)));
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
