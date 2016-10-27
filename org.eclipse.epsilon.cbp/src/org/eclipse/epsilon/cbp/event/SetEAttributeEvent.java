package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public class SetEAttributeEvent extends EAttributeEvent{

	public SetEAttributeEvent(EObject focusObject, EAttribute eAttribute, Object newValue)
    {
       super(Event.SET_EATTRIBUTE, focusObject, eAttribute, newValue);  
    }
    
    public SetEAttributeEvent(Notification n)
    {
        this((EObject) n.getNotifier(),(EAttribute) n.getFeature(),n.getNewValue());
    }

}
