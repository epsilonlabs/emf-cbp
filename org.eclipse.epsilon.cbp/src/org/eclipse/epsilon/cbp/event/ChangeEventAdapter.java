package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class ChangeEventAdapter extends EContentAdapter {
	
	protected List<ChangeEvent<?>> changeEvents = new ArrayList<ChangeEvent<?>>();
	protected boolean enabled = true;
	protected HashSet<EPackage> ePackages = new HashSet<EPackage>();
	protected HashSet<EObject> processedEObjects = new HashSet<EObject>();
	protected CBPResource resource = null;
	
	public ChangeEventAdapter(CBPResource resource) {
		this.resource = resource;
	}
	
	public List<ChangeEvent<?>> getChangeEvents() {
		return changeEvents;
	}
	
	@Override
	public void notifyChanged(Notification n) {
		
		super.notifyChanged(n);
		
		if (n.isTouch() || !enabled) { return; }
		
		
		if (n.getFeature() != null) {
			EStructuralFeature feature = (EStructuralFeature) n.getFeature();
			if (!feature.isChangeable() || feature.isDerived()) return;
		}
		
		ChangeEvent<?> event = null;
		
		switch (n.getEventType()) {
		
		case Notification.ADD: {
			if (n.getNotifier() instanceof Resource) {
				if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
					event = new AddToResourceEvent();
				}
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() != null) {
					EStructuralFeature feature = (EStructuralFeature) n.getFeature();
					if (feature instanceof EAttribute) {
						event = new AddToEAttributeEvent();
					} else if (n.getFeature() instanceof EReference) {
						event = new AddToEReferenceEvent();
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
				} else if (feature instanceof EReference) {
					event = new UnsetEReferenceEvent();
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
					} else if (feature instanceof EReference) {
						event = new SetEReferenceEvent();
					}
					event.setValues(n.getNewValue());
				} else {
					if (feature instanceof EAttribute) {
						event = new UnsetEAttributeEvent();
					} else if (feature instanceof EReference) {
						event = new UnsetEReferenceEvent();
					}
				}
			}
			break;
		}

		case Notification.ADD_MANY: {
			@SuppressWarnings("unchecked")
			Collection<EObject> values = (Collection<EObject>) n.getNewValue();
			if (n.getNotifier() instanceof Resource) {
				event = new AddToResourceEvent();
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() instanceof EAttribute) {
					event = new AddToEAttributeEvent();
				} else if (n.getFeature() instanceof EReference) {
					event = new AddToEReferenceEvent();
				}
			}
			event.setValues(values);
			break;
		}
		
		case Notification.REMOVE: {
			if (n.getOldValue() instanceof EObject) {
				if (n.getNotifier() instanceof Resource) {
					event = new RemoveFromResourceEvent();
				} else if (n.getNotifier() instanceof EObject) {
					event = new RemoveFromEReferenceEvent();
				}
			} else if (n.getFeature() instanceof EAttribute) {
				event = new RemoveFromEAttributeEvent();
			}
			event.setValues(n.getOldValue());
			break;
		}
		
		case Notification.REMOVE_MANY: {
			if (n.getNotifier() instanceof Resource) {
				event = new RemoveFromResourceEvent();
			} else if (n.getNotifier() instanceof EObject) {
				if (n.getFeature() instanceof EAttribute) {
					event = new RemoveFromEAttributeEvent();
				} else if (n.getFeature() instanceof EReference) {
					event = new RemoveFromEReferenceEvent();
				}
			}
			event.setValues(n.getOldValue());
			break;
		}
		default: {
			System.err.println("EventAdapter: Unhandled notification!" + n.toString());
			break;
		}
		}
		
		// Features which are not meant to be serialised are defined as "unset"
		if (event instanceof SetEReferenceEvent || event instanceof AddToEReferenceEvent) {
			EStructuralFeature feature = (EStructuralFeature) n.getFeature();
			if (!((EObject) n.getNotifier()).eIsSet(feature)) return;
		}
		
		if (event instanceof EObjectValuesEvent) {
			if (((EObjectValuesEvent) event).getValues().isEmpty()) return;
			for (EObject obj : ((EObjectValuesEvent) event).getValues()) {
				handleEPackageOf(obj);
				handleEObject(obj);
			}
		}
		
		if (event instanceof EStructuralFeatureEvent<?>) {
			((EStructuralFeatureEvent<?>) event).setEStructuralFeature((EStructuralFeature) n.getFeature());
			((EStructuralFeatureEvent<?>) event).setTarget(n.getNotifier());			
		}
		
		if (event != null) {
			event.setPosition(n.getPosition());
			changeEvents.add(event);
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
			changeEvents.add(new RegisterEPackageEvent(ePackage, this));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleEObject(EObject obj) {
		if (!resource.isRegistered(obj)) {
			changeEvents.add(new CreateEObjectEvent(obj.eClass(), resource.register(obj)));

			// Include prior attribute values into the resource
			for (EAttribute eAttr : obj.eClass().getEAllAttributes()) {
				if (eAttr.isChangeable() && obj.eIsSet(eAttr)) {
					if (eAttr.isMany()) {
						Collection<?> values = (Collection<?>) obj.eGet(eAttr);
						int i = 0;
						for (Object value : values) {
							AddToEAttributeEvent e = new AddToEAttributeEvent();
							e.setEStructuralFeature(eAttr);
							e.setValue(value);
							e.setTarget(obj);
							e.setPosition(i++);
							changeEvents.add(e);
						}
					} else {
						Object value = obj.eGet(eAttr);

						SetEAttributeEvent e = new SetEAttributeEvent();
						e.setEStructuralFeature(eAttr);
						e.setValue(value);
						e.setTarget(obj);
						changeEvents.add(e);
					}
				}
			}

			// Include prior reference values into the resource
			for (EReference eRef : obj.eClass().getEAllReferences()) {
				if (eRef.isChangeable() && obj.eIsSet(eRef)) {
					if (eRef.isMany()) {
						Collection<EObject> values = (Collection<EObject>) obj.eGet(eRef);
						int i = 0;
						for (EObject value : values) {
							if (value.eResource() == obj.eResource()) {
								handleEObject(value);
							}

							AddToEReferenceEvent e = new AddToEReferenceEvent();
							e.setEStructuralFeature(eRef);
							e.setValue(value);
							e.setTarget(obj);
							e.setPosition(i++);
							changeEvents.add(e);
						}
					} else {
						EObject value = (EObject) obj.eGet(eRef);
						if (value.eResource() == obj.eResource()) {
							handleEObject(value);
						}

						SetEReferenceEvent e = new SetEReferenceEvent();
						e.setEStructuralFeature(eRef);
						e.setValue(value);
						e.setTarget(obj);
						changeEvents.add(e);
					}
				}
			}

			//resource.register(obj);
		}
	}
}
