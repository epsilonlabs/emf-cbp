package org.eclipse.epsilon.cbp.driver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.Changelog;

public class ResourceContentsToEventsConverter 
{
	//change log
	Changelog changelog;
	
	//resource under question
	Resource resource;
	
	public ResourceContentsToEventsConverter(Changelog changelog, Resource resource)
	{
		this.changelog = changelog;
		this.resource = resource;
	}
	
	public void convert()
	{
		//if resource is empty, do nothing
		
		Iterator<EObject> iterator = resource.getAllContents();
		if (!iterator.hasNext()) {
			return;
		}
		
		//clear change log first
		changelog.clear();
		
		while (iterator.hasNext()) {
			EObject obj = iterator.next();
			
			//create event to add to resource
			AddEObjectsToResourceEvent e = new AddEObjectsToResourceEvent(obj);
			changelog.addEvent(e);
			
			handleAttributes(obj);
			handleReferences(obj);
		}
		
	}
	
	public void handleAttributes(EObject obj)
	{
		//for each EAttribute
		for(EAttribute attr : obj.eClass().getEAllAttributes())
		{
			//if attribute is set, changeable, non-transient and non-volatile
			if(obj.eIsSet(attr) && attr.isChangeable() && 
					!attr.isTransient() && !attr.isVolatile())
			{
				//create add to attribute event
				AddToEAttributeEvent e =
						new AddToEAttributeEvent(obj,attr,obj.eGet(attr));
				changelog.addEvent(e); 
			}
		}
	}
	
	public void handleReferences(EObject obj)
	{
		for(EReference ref : obj.eClass().getEAllReferences())
		{
			if (obj.eIsSet(ref)) {
				createAddEObjectsToEReferenceEvent(obj, obj.eGet(ref), ref);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createAddEObjectsToEReferenceEvent(EObject focusObject,Object value, EReference eRef)
	{
		//prepare an eobject list
		List<EObject> eObjectList = new ArrayList<EObject>();
		
		//if value is collection
		if(value instanceof Collection)
		{
			 eObjectList = (List<EObject>) value;
		}
		//if value is single object
		else
		{
			eObjectList.add((EObject) value);
		}
		
		//for each obj in the list, create add to ereference event
		for(EObject obj : eObjectList)
		{
			AddToEReferenceEvent e = 
					new AddToEReferenceEvent(focusObject,obj,eRef);
			changelog.addEvent(e);
		}
	}

}
