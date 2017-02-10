package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

public abstract class ResourceEvent extends Event
{

    @SuppressWarnings("unchecked")
	public ResourceEvent(Object value)
    {
        if(value instanceof Collection)
        {
        	eObjectList.addAll((List<EObject>) value);
        }
        else
        {
        	eObjectList.add((EObject) value);
        }
    }
    
    @SuppressWarnings("unchecked")
	public ResourceEvent(Notification n)
    {
 
    	
    	//if event is add to resource
    	if(this instanceof AddEObjectsToResourceEvent)
    	{
    		if(n.getNewValue() instanceof Collection)
    		{
    			eObjectList.addAll((List<EObject>) n.getNewValue());
    		}
    		else
    		{
    			eObjectList.add((EObject) n.getNewValue());
    		}
    	}
    	else if (this instanceof RemoveFromResourceEvent) 
    	{
    		if(n.getOldValue() instanceof Collection)
    		{
    			eObjectList.addAll((List<EObject>) n.getOldValue());
    		}
    		else
    		{
    			eObjectList.add((EObject) n.getOldValue());
    		}
    	}	
    }
}

