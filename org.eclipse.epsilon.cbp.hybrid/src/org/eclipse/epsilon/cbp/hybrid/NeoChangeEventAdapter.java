package org.eclipse.epsilon.cbp.hybrid;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.ChangeEventAdapter;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;

import fr.inria.atlanmod.neoemf.resource.PersistentResource;

public class NeoChangeEventAdapter extends ChangeEventAdapter {

	private PersistentResource resource;

	public NeoChangeEventAdapter(CBPResource resource) {
		super(resource);
		// TODO Auto-generated constructor stub
	}

	public NeoChangeEventAdapter(PersistentResource resource) {
		super(null);
		this.resource = resource;

	}

	@Override
	public void notifyChanged(Notification n) {
		super.notifyChanged(n);
	}

	@Override
	public void handleDeletedEObject(EObject removedObject, Object parent, Object feature) {
		boolean isDeleted = true;

		String y = resource.getURIFragment(removedObject);

		TreeIterator<EObject> eAllContents = resource.getAllContents();
		while (eAllContents.hasNext()) {
			EObject eObject = eAllContents.next();
			if (eObject.equals(removedObject)) {
				isDeleted = false;
				break;
			}
		}

		if (isDeleted == true) {
			String id = resource.getURIFragment(removedObject);
			ChangeEvent<?> e = new DeleteEObjectEvent(removedObject, id);
			deleteCount++;
			changeEvents.add(e);
		}
	}

	@Override
	public void handleEPackageOf(EObject eObject) {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleCreateEObject(EObject obj) {

		ChangeEvent<?> event = new CreateEObjectEvent(obj, resource.getURIFragment(obj));
		createCount++;
		changeEvents.add(event);

		// Include prior attribute values into the resource
		for (EAttribute eAttr : obj.eClass().getEAllAttributes()) {
			if (eAttr.isChangeable() && obj.eIsSet(eAttr) && !eAttr.isDerived()) {
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
					}
				} else {
					Object value = obj.eGet(eAttr);

					SetEAttributeEvent e = new SetEAttributeEvent();
					setAttCount++;
					e.setEStructuralFeature(eAttr);
					e.setValue(value);
					e.setTarget(obj);
					changeEvents.add(e);
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
						addRefCount++;
						e.setEStructuralFeature(eRef);
						e.setValue(value);
						e.setTarget(obj);
						e.setPosition(i++);
						changeEvents.add(e);
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
					changeEvents.add(e);
				}
			}
		}
	}

}