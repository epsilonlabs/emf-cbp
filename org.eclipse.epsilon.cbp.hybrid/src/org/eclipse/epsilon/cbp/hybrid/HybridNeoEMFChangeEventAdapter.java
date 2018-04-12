package org.eclipse.epsilon.cbp.hybrid;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;

import fr.inria.atlanmod.neoemf.core.DefaultPersistentEObject;

public class HybridNeoEMFChangeEventAdapter extends HybridChangeEventAdapter {

	public HybridNeoEMFChangeEventAdapter(HybridNeoEMFResourceImpl hybridResource) {
		super(hybridResource);
	}

	@Override
	public void notifyChanged(Notification n) {
		super.notifyChanged(n);

//		System.out.println(" n = " + n);
//		if (n.getClass().getName().equals(ResourceImpl.class.getName() + "$9")) {
//			System.out.println(n.getNotifier());
//			System.out.println(n.getFeature());
//		} 
		if (n instanceof ENotificationImpl) {

			if (n.getEventType() == Notification.ADD_MANY) {
				@SuppressWarnings("unchecked")
				Collection<Object> values = (Collection<Object>) n.getNewValue();
				int position = n.getPosition();
				for (Object value : values) {
					ChangeEvent<?> evt = null;
					if (n.getNotifier() instanceof DefaultPersistentEObject) {
						evt = new AddToResourceEvent();
						addResCount++;
						evt.setValues(value);
						addEventToList(evt, n, position++);
					}
				}
			}

			// ------

		}
	}

}