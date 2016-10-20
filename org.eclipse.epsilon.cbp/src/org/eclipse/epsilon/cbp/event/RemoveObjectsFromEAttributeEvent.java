package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public class RemoveObjectsFromEAttributeEvent extends EAttributeEvent
{
	public RemoveObjectsFromEAttributeEvent(EObject focusObject, EAttribute eAttribute, Object oldValue)
    {
       super(Event.REMOVE_FROM_EATTRIBUTE, focusObject, eAttribute, oldValue);  
    }
    
    public RemoveObjectsFromEAttributeEvent(Notification n)
    {
        this((EObject) n.getNotifier(),(EAttribute) n.getFeature(),n.getOldValue());
    }
}
