package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class SetEReferenceEvent extends EReferenceEvent{

	public SetEReferenceEvent(EObject focusObject,Object newValue,EReference eReference)
    {
        super(Event.SET_EREFERENCE, focusObject,eReference,newValue);
    }
	
    public SetEReferenceEvent(Notification n)
    {
        super(Event.SET_EREFERENCE,n);
    } 

}
