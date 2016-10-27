
package org.eclipse.epsilon.cbp.driver;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.Changelog;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.util.EPackageElementsNamesMap;

public class CBPTextSerializer 
{
	
	//dont know why you need classname
	private final String classname = this.getClass().getSimpleName();
	
	//format id
	private final String FORMAT_ID = "CBP_TEXT"; 
	
	//version number (wtf?)
	private final double VERSION = 1.0;
	
	//event list
    private final List<Event> eventList;
    
    //persistence manager
	private final PersistenceManager manager;
	
	//change log
	private final Changelog changelog; 
	
	//epackage element map
	private final EPackageElementsNamesMap ePackageElementsNamesMap;
	
	//common simple type name
	private final HashMap<String, Integer> commonsimpleTypeNameMap;
	
	//tet simple type name
	private final HashMap<String, Integer> textSimpleTypeNameMap;
	
	public CBPTextSerializer(PersistenceManager manager, Changelog aChangelog, EPackageElementsNamesMap 
			ePackageElementsNamesMap)
	{
		this.manager =  manager;
		this.changelog = aChangelog;
		this.ePackageElementsNamesMap = ePackageElementsNamesMap;
		
		this.eventList = manager.getChangelog().getEventsList();
		
		this.commonsimpleTypeNameMap = manager.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = manager.getTextSimpleTypesMap();
	}
	
	public void save(Map<?,?> options) //tbr
	{
		if(eventList.isEmpty()) //tbr
		{
			System.out.println(classname+" no events found, returning!");
			return;
		}
			
		
		PrintWriter printWriter = null;
		//setup printwriter
	    try
        {
	    	BufferedWriter bw = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(manager.getURI().path(), manager.isResume()), manager.STRING_ENCODING));
            printWriter = new PrintWriter(bw);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
		
		//if we're not in resume mode, serialise initial entry
		if(!manager.isResume())
			serialiseHeader(printWriter);
		
		for(Event e : eventList)
		{
			switch(e.getEventType())
			{
			case Event.ADD_EOBJ_TO_RESOURCE:
				writeEObjectAdditionEvent((AddEObjectsToResourceEvent)e, printWriter);
				break;
			case Event.ADD_TO_EREFERENCE:
				writeEObjectAdditionEvent((AddToEReferenceEvent)e, printWriter);
				break;
			case Event.ADD_TO_EATTRIBUTE:
			case Event.REMOVE_FROM_EATTRIBUTE:
				writeEAttributeEvent((EAttributeEvent)e, printWriter);
				break;
			case Event.REMOVE_EOBJ_FROM_RESOURCE:
				writeEObjectRemovalEvent((RemoveFromResourceEvent)e, printWriter);
				break;
			case Event.REMOVE_FROM_EREFERENCE:
				writeEObjectRemovalEvent((RemoveFromEReferenceEvent)e, printWriter);
				break;
			}
		}
		
		changelog.clearEvents();
		
		printWriter.close();
		manager.setResume(true);
	}
	
	private void writeEAttributeEvent(EAttributeEvent e, PrintWriter out) 
	{
		EObject focusObject = e.getFocusObject();
		
		EAttribute eAttribute = e.getEAttribute();
		
		EDataType eDataType = eAttribute.getEAttributeType();
		
		int serializationType = PersistenceManager.SET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES;
		
		if(e.getEventType() == Event.REMOVE_FROM_EATTRIBUTE)
			serializationType = PersistenceManager.UNSET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES;
		
		out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
				ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));

		String newValue ;
		String delimiter ="";
		
		if(getTypeID(eDataType) != PersistenceManager.COMPLEX_TYPE )
		{
			for(Object obj: e.getEAttributeValuesList())
			{
				if(obj!= null)
				{
					newValue = String.valueOf(obj);
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
				}
				else
				{
					newValue = manager.NULL_STRING;
				}
				
				out.print(delimiter+newValue);	
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		else //all other datatypes
		{
			
			for(Object obj: e.getEAttributeValuesList())
			{
				newValue = (EcoreUtil.convertToString(eDataType, obj));
				
				if(newValue!= null)
				{
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
				}
				else
				{
					newValue = manager.NULL_STRING;
				}
				
				out.print(delimiter+newValue);	
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		
		out.println();
	}
	
	private int getTypeID(EDataType type)
	{
		if(commonsimpleTypeNameMap.containsKey(type.getName()))
    	{
			return commonsimpleTypeNameMap.get(type.getName());
    	}
		else if(textSimpleTypeNameMap.containsKey(type.getName()))
		{
			return textSimpleTypeNameMap.get(type.getName());
		}
    	
    	return PersistenceManager.COMPLEX_TYPE;
    }
	
	private void writeEObjectAdditionEvent(AddToEReferenceEvent e, PrintWriter out)
	{
		writeEObjectAdditionEvent(e.getEObjectList(),e.getFocusObject(),e.getEReference(),false,out);
	}
	
	private void writeEObjectAdditionEvent(AddEObjectsToResourceEvent e, PrintWriter out)
	{
		writeEObjectAdditionEvent(e.getEObjectList(),null,null,true,out);
	}
	
	private void writeEObjectAdditionEvent(List<EObject> eObjectsList, EObject focusObject, 
					EReference eReference, boolean isAddToResource, PrintWriter out)
	{
		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();
    	
    	for(EObject obj : eObjectsList)
    	{
    		//if obj is not added already
    		if(changelog.addObjectToMap(obj))
    		{
    			//add to object-to-create-list
    			eObjectsToCreateList.add(ePackageElementsNamesMap.getID(obj.eClass().getName())); 
    			
    			//eObjectsToCreateList.add(changelog.getObjectId(obj));
    			
    		}
    		else
    		{
    			eObjectsToAddList.add(changelog.getObjectId(obj));
    		}
    	}
    	
    	String delimiter= "";
		if(!eObjectsToCreateList.isEmpty())
		{
			if(isAddToResource)
			{
				out.print(PersistenceManager.CREATE_AND_ADD_EOBJECTS_TO_RESOURCE+" [");
			}
			else 
			{
				out.print(PersistenceManager.CREATE_EOBJECTS_AND_SET_EREFERENCE_VALUES+" "+
						changelog.getObjectId(focusObject)+" "+
						ePackageElementsNamesMap.getID(eReference.getName())+" [");
				
			}
			
	        int index = 0;
    		for(int i = 0; i < (eObjectsToCreateList.size() / 2); i++)
    		{
    			out.print(delimiter+eObjectsToCreateList.get(index)+" "+eObjectsToCreateList.get(index+1));
    			
    			delimiter = PersistenceManager.DELIMITER;
    			
    			index = index + 2;
    		}
    		
    		out.print("]");
		}
		
		if(!eObjectsToAddList.isEmpty())
		{
			if(isAddToResource)
			{
				out.print(PersistenceManager.ADD_EOBJECTS_TO_RESOURCE+" [");
			}
			else 
			{
				out.print(PersistenceManager.SET_EOBJECT_EREFERENCE_VALUES+" "+
						changelog.getObjectId(focusObject)+" "+
						ePackageElementsNamesMap.getID(eReference.getName())+
						" [");
			}
			
			delimiter="";
			for(Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();)
			{
				out.print(delimiter+it.next());
				delimiter = PersistenceManager.DELIMITER;
			}
			
			out.print("]");	
		}
		out.println();
	}

	private void writeEObjectRemovalEvent(RemoveFromEReferenceEvent e, PrintWriter out)
	{
		writeEObjectRemovalEvent(e.getEObjectList(),e.getFocusObject(),e.getEReference(),false,out);
	}
	
	private void writeEObjectRemovalEvent(RemoveFromResourceEvent e, PrintWriter out)
	{
		writeEObjectRemovalEvent(e.getEObjectList(),null,null,true,out);
	}
	
	private void writeEObjectRemovalEvent(List<EObject> removedEObjectsList,EObject focusObject, 
			EReference eReference,boolean isRemoveFromResource, PrintWriter out)
	{
		if(isRemoveFromResource)
		{
			out.print(PersistenceManager.REMOVE_EOBJECTS_FROM_RESOURCE+" [");
		}
		else 
		{
			out.print(PersistenceManager.UNSET_EOBJECT_EREFERENCE_VALUES+" "+
					changelog.getObjectId(focusObject)+" "+
                    (ePackageElementsNamesMap.getID(eReference.getName())+" ["));
		}
		
		String delimiter = "";
		
		for(EObject obj : removedEObjectsList)
		{
			out.print(delimiter + changelog.getObjectId(obj));
			delimiter = PersistenceManager.DELIMITER;
		}
		out.print("]");
		out.println();
	}

	private void serialiseHeader(PrintWriter out) 
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
				System.out.println(classname+" "+e.getEventType());
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
			System.out.println(classname+" "+e.getEventType());
			System.exit(0);
		}
		
		out.println(FORMAT_ID+" "+VERSION);
		out.println("NAMESPACE_URI "+obj.eClass().getEPackage().getNsURI());
	}
}
