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
		if(resource.getContents().isEmpty())
		{
			return;
		}
		
		//clear change log first
		changelog.clear();
		
		//create event to add to resource
		AddEObjectsToResourceEvent e = new AddEObjectsToResourceEvent (resource.getContents());
		changelog.addEvent(e);
		
		for(EObject obj : resource.getContents())
		{
			createSetAttributeEntries(obj);
			//TODO: check performance
			handleReferences(obj);
		}
	}
	
	
	private void handleReferences(EObject root) 
	{
		//get an iterator for all contents of the root
		//TODO: not sure about eAllContents()
		for(Iterator<EObject> it = root.eAllContents(); it.hasNext();) //containment refs
		{
			//get EObject
			EObject obj = it.next();
			
			createAddEObjectsToEReferenceEvent(obj.eContainer(),obj,obj.eContainmentFeature());
		
			createSetAttributeEntries(obj);
		}
		
		//for each reference
		for(EReference rf : root.eClass().getEAllReferences())
		{
			//if is not containment
			if(!rf.isContainment())
			{
				//if is set
				if(root.eIsSet(rf))
				{
				   //create add to ERef event and add to EAttr event 
				   createAddEObjectsToEReferenceEvent(root,root.eGet(rf),rf);
				   createSetAttributeEntries(root.eGet(rf));
				   
				   //handle containments of eObjects within root.eGet
				   List <EObject> children = new ArrayList<EObject>();
				   
				   if(root.eGet(rf) instanceof Collection)
				   {
					   children = (List<EObject>) root.eGet(rf);
				   }
				   else
				   {
					   children.add((EObject)root.eGet(rf));
				   }
				   
				   for(EObject obj : children)
				   {
					   /*
					    * For opposite references, we try to always pick the same ref. We don't want to recurse 
					    * for both ends of the ref. Choose ref by comparing name lengths 
					    */
					   if(rf.getEOpposite() != null)
					   {
						   //choose based on feature id.
						   if((rf.getContainerClass().getName().length()+rf.getName().length()) > 
						   			(rf.getEOpposite().getContainerClass().getName().length()
						   					+rf.getEOpposite().getName().length()))	   
						   { 
							   if(rf.getFeatureID() > rf.getEOpposite().getFeatureID())
							   {
								   handleReferences(obj);  
							   }
						   }					   
					   }
					   else //for non opposites
					   {
						   handleReferences(obj);
					   }
				   }
				}
			}
		}
	}
	
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
	
	private void createSetAttributeEntries(Object value)
	{
		//prepare an eObject list
		List<EObject> eObjectList = new ArrayList<EObject>();
		
		//if value is a collection
		if(value instanceof Collection)
		{
			 eObjectList = (List<EObject>) value;
		}
		//if value is not a collection
		else
		{
			eObjectList.add((EObject) value);
		}
		
		//for each EObject in the list
		for(EObject obj : eObjectList)
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
		
	}
}
