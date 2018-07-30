package org.eclipse.epsilon.cbp.hybrid.xmi;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.hybrid.HybridChangeEventAdapter;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.hybrid.event.xmi.DeleteEObjectEvent;

public class HybridXMIChangeEventAdapter extends HybridChangeEventAdapter {

	public HybridXMIChangeEventAdapter(HybridResource hybridResource) {
		super(hybridResource);
	}

	// @Override
	// public void handleDeletedEObject(ChangeEvent<?> event, EObject
	// removedObject, Object parent, Object feature) {
	// String id = ((XMIResource)
	// resource.getStateBasedResource()).getID(removedObject);
	// super.handleDeletedEObject(event, removedObject, parent, feature);
	// }

	@Override
	public void notifyChanged(Notification n) {

		// this added to save id of a removed object into the deletedEObjectToIdMap
		// so that it can be assigned again to the same object when added again
		// otherwise internal mechanism of XMIResourceImpl will be used
		switch (n.getEventType()) {
		case Notification.REMOVING_ADAPTER: {
			if (n.getNotifier() != null && n.getNotifier() instanceof EObject) {
				EObject obj = (EObject) n.getNotifier();
				Resource resource = obj.eResource();
				if (resource != null) {
					String id = ((XMIResource) resource).getID(obj);
					this.resource.getDeletedEObjectToIdMap().put(obj, id);
				}
			}
		}
			break;
		case Notification.ADD: {
			if (n.getNewValue() != null && n.getNewValue() instanceof EObject) {
				EObject obj = (EObject) n.getNewValue();
				Resource resource = obj.eResource();
				if (resource != null) {
					String id = this.resource.getDeletedEObjectToIdMap().get(obj);
					if (id != null) {
						((XMIResource) resource).setID(obj, id);
						this.resource.getDeletedEObjectToIdMap().remove(obj);
					}
				}
			}
		}
			break;

		}

		super.notifyChanged(n);

		// switch (n.getEventType()) {
		// case Notification.REMOVING_ADAPTER: {
		// if (n.getNotifier() instanceof EObject) {
		// EObject obj = (EObject) n.getNotifier();
		// if (obj.eResource() != null) {
		// String id = ((XMIResource) obj.eResource()).getID(obj);
		// this.resource.getDeletedEObjectToIdMap().put(obj, id);
		// }
		//
		// }
		// }
		// break;
		// }
	}

}