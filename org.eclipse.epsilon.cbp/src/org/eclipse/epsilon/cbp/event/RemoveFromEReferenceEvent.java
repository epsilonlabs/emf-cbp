package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class RemoveFromEReferenceEvent extends EReferenceEvent
{
	/*EObject added to another EObject via some EReference*/
	public RemoveFromEReferenceEvent(EObject focusObject,Object oldValue,EReference eReference)
    {
        super(Event.REMOVE_FROM_EREFERENCE, focusObject, eReference, oldValue);
    }
	
    public RemoveFromEReferenceEvent(Notification n)
    {
       super(Event.REMOVE_FROM_EREFERENCE,n);
    } 
}


