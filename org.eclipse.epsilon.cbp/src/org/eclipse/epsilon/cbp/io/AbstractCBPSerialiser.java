package org.eclipse.epsilon.cbp.io;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;

public abstract class AbstractCBPSerialiser {

	//format id
	protected String FORMAT_ID = "CBP_TEXT"; 
	
	//version number (wtf?)
	protected double VERSION = 1.0;
	
	//event list
    protected List<Event> eventList;
    
    //persistence manager
	protected PersistenceManager manager;
	
	//change log
	protected Changelog changelog; 
	
	//epackage element map
	protected ModelElementIDMap ePackageElementsNamesMap;
	
	//common simple type name
	protected TObjectIntMap<String> commonsimpleTypeNameMap;
	
	//tet simple type name
	protected TObjectIntMap<String> textSimpleTypeNameMap;

	
	public abstract void serialise(Map<?,?> options);
	
	protected abstract void handleAddToResourceEvent(AddEObjectsToResourceEvent e, PrintWriter out);
	protected abstract void handleRemoveFromResourceEvent(RemoveFromResourceEvent e, PrintWriter out);

	protected abstract void handleSetEAttributeEvent(EAttributeEvent e, PrintWriter out);
	protected abstract void handleAddToEAttributeEvent(EAttributeEvent e, PrintWriter out);
	
	protected abstract void handleSetEReferenceEvent(SetEReferenceEvent e, PrintWriter out);
	protected abstract void handleAddToEReferenceEvent(AddToEReferenceEvent e, PrintWriter out);
	
	protected abstract void handleRemoveFromAttributeEvent(EAttributeEvent e, PrintWriter out);
	protected abstract void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e, PrintWriter out);
	



	
	protected void serialiseHeader(PrintWriter out) 
	{
		//obj
		EObject obj = null;
		
		//get first event
		Event e = eventList.get(0);
		
		if(e instanceof ResourceEvent)
		{
			obj = ((ResourceEvent)e).getEObjectList().get(0);
		}
		else //throw tantrum
		{
			try 
			{
				System.out.println("CBPTextSerialiser: "+e.getEventType());
				throw new Exception("Error! first item in events list is not a ResourceEvent.");
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
				System.exit(0);
			}
		}
		
		if(obj == null) //TBR
		{
			System.out.println("CBPTextSerialiser: "+e.getEventType());
			System.exit(0);
		}
		
		out.println(FORMAT_ID+" "+VERSION);
		out.println("NAMESPACE_URI "+obj.eClass().getEPackage().getNsURI());
	}
	
	
	protected int getTypeID(EDataType type)
	{
		if(commonsimpleTypeNameMap.containsKey(type.getName()))
    	{
			return commonsimpleTypeNameMap.get(type.getName());
    	}
		else if(textSimpleTypeNameMap.containsKey(type.getName()))
		{
			return textSimpleTypeNameMap.get(type.getName());
		}
    	
    	return SimpleType.COMPLEX_TYPE;
    }
}
