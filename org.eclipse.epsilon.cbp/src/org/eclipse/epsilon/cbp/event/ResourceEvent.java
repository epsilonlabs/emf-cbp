package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

public abstract class ResourceEvent extends Event
{
	protected List<EObject> eObjectList = new ArrayList<EObject>();

    @SuppressWarnings("unchecked")
	public ResourceEvent(int eventType, Object value)
    {
        super(eventType);
        
        if(value instanceof Collection)
        {
        	this.eObjectList = (List<EObject>) value;
        }
        else
        {
        	this.eObjectList.add((EObject) value);
        }
    }
    
    @SuppressWarnings("unchecked")
	public ResourceEvent(int eventType, Notification n)
    {
    	super(eventType);
    	
    	if(eventType == Event.ADD_EOBJ_TO_RESOURCE)
    	{
    		if(n.getNewValue() instanceof Collection)
    		{
    			this.eObjectList = (List<EObject>) n.getNewValue();
    		}
    		else
    		{//n.getNewValue() ! instanceof Collection
    			this.eObjectList.add((EObject) n.getNewValue());
    		}
    	}
    	else //eventType == Event.REMOVE_EOBJECTS_FROM_RESOURCE_EVENT
    	{
    		if(n.getOldValue() instanceof Collection)
    		{
    			this.eObjectList = (List<EObject>) n.getOldValue();
    		}
    		else //n.getNewValue() ! instanceof Collection
    		{
    			this.eObjectList.add((EObject) n.getOldValue());
    		}
    	}	
    }
}

