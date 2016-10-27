package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class AddToEReferenceEvent extends EReferenceEvent
{
	public AddToEReferenceEvent(EObject focusObject,Object newValue,EReference eReference)
    {
        super(Event.ADD_TO_EREFERENCE, focusObject,eReference,newValue);
    }
	
    public AddToEReferenceEvent(Notification n)
    {
        super(Event.ADD_TO_EREFERENCE,n);
    } 
}


