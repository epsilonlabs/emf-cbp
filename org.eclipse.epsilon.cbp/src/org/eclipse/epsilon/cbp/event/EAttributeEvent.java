package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public abstract class EAttributeEvent extends Event
{
	private EObject focusObject;
	
	private EAttribute eAttribute;
	
	private List<Object> eAttributeValuesList = new ArrayList<Object>();
	
	
    @SuppressWarnings("unchecked")
	public EAttributeEvent(int eventType, EObject focusObject, EAttribute eAttribute, Object value)
    {
       super(eventType);
       
       this.focusObject = focusObject;
       this.eAttribute = eAttribute;
       
       if(value instanceof Collection)
       {
    	   this.eAttributeValuesList.addAll((List<Object>) value);
       }
       else
       {
    	   this.eAttributeValuesList.add(value);
       }
    }
    
    @SuppressWarnings("unchecked")
    public EAttributeEvent(int eventType, Notification n)
    {
    	super(eventType);
    	
        Object value = null;
        
        if(eventType == Event.ADD_TO_EATTRIBUTE)
        {
        	value = n.getNewValue();
        }
        else 
        {
        	value = n.getOldValue();
        }
        
        this.focusObject = (EObject) n.getNotifier();
        
        this.eAttribute = (EAttribute) n.getFeature();
        
        if(value instanceof Collection)
        {
        	this.eAttributeValuesList = (List<Object>) value;
        }
        else
        {
        	this.eAttributeValuesList.add(value);
        }
    }
    
    public EObject getFocusObject()
    {
        return focusObject;
    }
    
    public EAttribute getEAttribute()
    {
        return eAttribute;
    }
    
    public List<Object> getEAttributeValuesList()
	{
		return eAttributeValuesList;	
	}
}
