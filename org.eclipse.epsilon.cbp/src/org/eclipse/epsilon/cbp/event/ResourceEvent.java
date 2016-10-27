package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

public abstract class ResourceEvent extends Event
{
	//EObject list
	protected List<EObject> eObjectList = new ArrayList<EObject>();

    @SuppressWarnings("unchecked")
	public ResourceEvent(int eventType, Object value)
    {
        super(eventType);
        
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
	public ResourceEvent(int eventType, Notification n)
    {
    	super(eventType);
    	
    	//if event is add to resource
    	if(eventType == Event.ADD_EOBJ_TO_RESOURCE)
    	{
    		if(n.getNewValue() instanceof Collection)
    		{
    			eObjectList.addAll((List<EObject>) n.getNewValue());
    		}
    		//n.getNewValue() ! instanceof Collection
    		else
    		{
    			eObjectList.add((EObject) n.getNewValue());
    		}
    	}
    	else if (eventType == Event.REMOVE_EOBJ_FROM_RESOURCE) 
    	{
    		if(n.getOldValue() instanceof Collection)
    		{
    			eObjectList.addAll((List<EObject>) n.getOldValue());
    		}
    		else //n.getNewValue() ! instanceof Collection
    		{
    			eObjectList.add((EObject) n.getOldValue());
    		}
    	}	
    }
}

