package org.eclipse.epsilon.cbp.hybrid;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.ChangeEventAdapter;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.hybrid.event.xmi.CreateEObjectEvent;
import org.eclipse.epsilon.hybrid.event.xmi.DeleteEObjectEvent;

public abstract class HybridChangeEventAdapter extends ChangeEventAdapter {

    protected HybridResource resource;

    public HybridChangeEventAdapter(CBPResource resource) {
	super(resource);
	// TODO Auto-generated constructor stub
    }

    public HybridChangeEventAdapter(HybridResource resource) {
	super(null);
	this.resource = resource;
    }

    @Override
    public void notifyChanged(Notification n) {
	super.notifyChanged(n);
    }

    @Override
    public void handleEPackageOf(EObject eObject) {
//	    super.handleEPackageOf(eObject);
    }

    @Override
    public void handleDeletedEObject(ChangeEvent<?> event, EObject removedObject, Object parent, Object feature) {
	if (removedObject.eResource() == null) {
	    if (compositeId == null) {
		// to handle removed object that remove the object from the
		// resource, e.g. remove an object from a containment feature
		startCompositeOperation();
	    }
	    event.setComposite(compositeId);
	    changeEvents.add(event);
	    String id = resource.getURIFragment(removedObject);
	    ChangeEvent<?> e = new DeleteEObjectEvent(removedObject, id);
	    e.setComposite(compositeId);
	    changeEvents.add(e);
	} else {
	    changeEvents.add(event);
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleCreateEObject(EObject eObject) {
	HybridResource hybridResource = (HybridResource) resource;
	if (!hybridResource.isRegistered(eObject)) {
	    ChangeEvent<?> event = new CreateEObjectEvent(eObject, hybridResource.register(eObject));
	    changeEvents.add(event);

	    // Include prior attribute values into the resource
	    for (EAttribute eAttr : eObject.eClass().getEAllAttributes()) {
		if (eAttr.isChangeable() && eObject.eIsSet(eAttr) && !eAttr.isDerived()) {
		    if (eAttr.isMany()) {
			Collection<?> values = (Collection<?>) eObject.eGet(eAttr);
			int i = 0;
			for (Object value : values) {
			    AddToEAttributeEvent e = new AddToEAttributeEvent();
			    addAttCount++;
			    e.setEStructuralFeature(eAttr);
			    e.setValue(value);
			    e.setTarget(eObject);
			    e.setPosition(i++);
			    changeEvents.add(e);
			}
		    } else {
			Object value = eObject.eGet(eAttr);

			SetEAttributeEvent e = new SetEAttributeEvent();
			setAttCount++;
			e.setEStructuralFeature(eAttr);
			e.setValue(value);
			e.setTarget(eObject);
			changeEvents.add(e);
		    }
		}
	    }

	    // Include prior reference values into the resource
	    for (EReference eRef : eObject.eClass().getEAllReferences()) {

		if (eRef.isChangeable() && eObject.eIsSet(eRef) && !eRef.isDerived()) {
		    if (eRef.getEOpposite() != null && eRef.getEOpposite().isMany() && eRef.getEOpposite().isChangeable()) {
			// If this is the "1" side of an 1:N pair of references,
			// ignore it:
			// the "N" side has more information.
			continue;
		    }

		    if (eRef.isMany()) {
			Collection<EObject> values = (Collection<EObject>) eObject.eGet(eRef);
			int i = 0;
			for (EObject value : values) {
			    if (value.eResource() == eObject.eResource()) {
				handleCreateEObject(value);
			    }

			    AddToEReferenceEvent e = new AddToEReferenceEvent();
			    addRefCount++;
			    e.setEStructuralFeature(eRef);
			    e.setValue(value);
			    e.setTarget(eObject);
			    e.setPosition(i++);
			    changeEvents.add(e);
			}
		    } else {
			EObject value = (EObject) eObject.eGet(eRef);
			if (value.eResource() == eObject.eResource()) {
			    handleCreateEObject(value);
			}
			SetEReferenceEvent e = new SetEReferenceEvent();
			setRefCount++;
			e.setEStructuralFeature(eRef);
			e.setValue(value);
			e.setTarget(eObject);
			changeEvents.add(e);
		    }
		}
	    }
	}
    }

}