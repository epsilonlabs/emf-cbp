package org.eclipse.epsilon.cbp.event;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public abstract class EReferenceEvent extends Event
{
	//object under question
	private EObject focusObject;
	
	//EReference related to this event
	private EReference eReference;
	
    @SuppressWarnings("unchecked")
	public EReferenceEvent(int eventType, EObject focusObject, EReference eReference, Object value)
    {
        super(eventType);
        
        this.focusObject = focusObject;
        
        this.eReference = eReference; 
        
        //if value is a collection
        if(value instanceof Collection)
        {
        	eObjectList.addAll((Collection<? extends EObject>) value);
        }
        else
        {
        	eObjectList.add((EObject)value);
        }
    }
    
    @SuppressWarnings("unchecked")
	public EReferenceEvent(int eventType, Notification n)
    {
    	super(eventType);
    	
    	//get notifier
    	this.focusObject = (EObject) n.getNotifier();
    	
    	//get feature
    	this.eReference = (EReference) n.getFeature();
    	
    	//if event is add
    	if(eventType == Event.ADD_TO_EREFERENCE)
    	{
    		//if new value is collection
    		if(n.getNewValue() instanceof Collection)
    		{
    			eObjectList.addAll((List<EObject>) n.getNewValue());
    		}
    		else 
    		{
    			eObjectList.add((EObject) n.getNewValue());
    		}
    	}
    	//if event is not add
    	else 
    	{
    		//if old value is collection
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
   
    public EObject getFocusObject()
    {
        return focusObject;
    }
    
    public EReference getEReference()
    {
        return eReference;
    }
}



