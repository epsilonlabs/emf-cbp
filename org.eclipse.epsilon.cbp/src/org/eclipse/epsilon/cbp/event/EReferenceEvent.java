package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public abstract class EReferenceEvent extends Event
{
	private EObject focusObject;
	
	private EReference eReference;
	
    @SuppressWarnings("unchecked")
	public EReferenceEvent(int eventType, EObject focusObject, EReference eReference, Object value)
    {
        super(eventType);
        
        this.focusObject = focusObject;
        
        this.eReference = eReference; 
        
        if(value instanceof Collection)
        {
        	this.eObjectList = (List<EObject>) value;
        }
        else
        {
        	this.eObjectList.add((EObject)value);
        }
    }
    
    @SuppressWarnings("unchecked")
	public EReferenceEvent(int eventType, Notification n)
    {
    	super(eventType);
    	
    	this.focusObject = (EObject) n.getNotifier();
    	
    	this.eReference = (EReference) n.getFeature();
    	
    	if(eventType == Event.ADD_TO_EREFERENCE)
    	{
    		if(n.getNewValue() instanceof Collection)
    		{
    			this.eObjectList = (List<EObject>) n.getNewValue();
    		}
    		else 
    		{
    			this.eObjectList.add((EObject) n.getNewValue());
    		}
    	}
    	else 
    	{
    		if(n.getOldValue() instanceof Collection)
    		{
    			this.eObjectList = (List<EObject>) n.getOldValue();
    		}
    		else
    		{
    			this.eObjectList.add((EObject) n.getOldValue());
    		}
    	}
    }
   
    public EObject getFocusObject()
    {
        return focusObject;
    }
    
    public EReference getEReference()
    {
        return eReference;
    }
    
  
}



